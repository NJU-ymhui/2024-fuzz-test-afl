package cn.edu.nju.modules.mutate;

import cn.edu.nju.util.manager.ResourcesManager;
import cn.edu.nju.util.Log;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Random;

public class MutationImpl implements Mutation {
    private ResourcesManager resourcesManager;
    private static final Random RANDOM = new Random();

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    @Override
    public String mutate() {
        try {
            // 获取当前种子路径
            String currentSeed = resourcesManager.getCurrentMutatedSeedPath();
            System.out.println("Current seed: " + currentSeed);
            byte[] data = Files.readAllBytes(Paths.get(currentSeed));

            // 变异策略
            byte[] mutatedData = applyMutation(data);

            // 保存变异后的数据
            String newSeedPath = saveMutatedData(mutatedData);
            System.out.println("New seed: " + Arrays.toString(mutatedData));
            //不应该在这里入队
           // resourcesManager.addNewMutatedSeed(newSeedPath);


            Log.info("Mutated seed saved to " + newSeedPath);

            return newSeedPath;
        } catch (IOException e) {
            Log.error("Mutation failed: " + e.getMessage());
        }
        return null;
    }


    private byte[] applyMutation(byte[] data) {
        // 定义变异算子权重（可根据需要调整）
        // 比例：
        // Bitflip: 30%
        // Arithmetic: 20%
        // Interesting Values: 20%
        // Splice: 20%
        // Havoc: 10%
        double choice = RANDOM.nextDouble();
        if (choice < 0.30) {
            return bitFlipMutation(data);
        } else if (choice < 0.50) {
            return arithmeticMutation(data);
        } else if (choice < 0.70) {
            return interestingValuesMutation(data);
        } else if (choice < 0.90) {
            return spliceMutation(data);
        } else {
            return havocMutation(data);
        }
    }

    // 1. Bitflip mutation
    private byte[] bitFlipMutation(byte[] data) {
        byte[] mutated = data.clone();
        int pos = RANDOM.nextInt(mutated.length);
        int bit = RANDOM.nextInt(8);
        mutated[pos] ^= (1 << bit);
        Log.info(String.format("Bitflip Mutation: Flipped bit %d at byte position %d", bit, pos));
        return mutated;
    }

    // 2. Arithmetic mutation
    private byte[] arithmeticMutation(byte[] data) {
        byte[] mutated = data.clone();
        if (mutated.length < 4) {
            // Not enough data for integer operations; fallback to bitflip
            Log.info("Arithmetic Mutation: Data too short for arithmetic mutation. Falling back to bitflip.");
            return bitFlipMutation(data);
        }
        // 随机选择一个整数开始的位置（4字节对齐）
        int pos = RANDOM.nextInt((mutated.length / 4)) * 4;
        // 读取一个4字节整数（大端格式）
        int value = ((mutated[pos] & 0xFF) << 24) | ((mutated[pos + 1] & 0xFF) << 16) |
                ((mutated[pos + 2] & 0xFF) << 8) | (mutated[pos + 3] & 0xFF);
        // 随机加减一个小值
        int delta = RANDOM.nextBoolean() ? 1 : -1;
        value += delta;
        // 写回变异后的值
        mutated[pos] = (byte) ((value >> 24) & 0xFF);
        mutated[pos + 1] = (byte) ((value >> 16) & 0xFF);
        mutated[pos + 2] = (byte) ((value >> 8) & 0xFF);
        mutated[pos + 3] = (byte) (value & 0xFF);
        Log.info(String.format("Arithmetic Mutation: Adjusted integer at position %d by %d", pos, delta));
        return mutated;
    }

    // 3. Interesting Values mutation
    private byte[] interestingValuesMutation(byte[] data) {
        byte[] mutated = data.clone();
        // 定义一些“有趣”的字节值
        byte[] interestingBytes = {(byte) 0x00, (byte) 0xFF, (byte) 0x7F, (byte) 0x80};
        // 随机选择一个位置和一个有趣的值
        int pos = RANDOM.nextInt(mutated.length);
        byte newValue = interestingBytes[RANDOM.nextInt(interestingBytes.length)];
        byte oldValue = mutated[pos];
        mutated[pos] = newValue;
        Log.info(String.format("Interesting Values Mutation: Changed byte at position %d from 0x%02X to 0x%02X", pos, oldValue, newValue));
        return mutated;
    }

    // 4. Splice mutation
    private byte[] spliceMutation(byte[] data) {
        byte[] mutated = data.clone();
        String otherSeedPath = resourcesManager.getRandomSeedPath();
        if (otherSeedPath == null || otherSeedPath.equals(resourcesManager.getCurrentMutatedSeedPath())) {
            // No other seed available; fallback to bitflip
            Log.info("Splice Mutation: No other seed available or same seed selected. Falling back to bitflip.");
            return bitFlipMutation(data);
        }
        try {
            byte[] otherData = Files.readAllBytes(Paths.get(otherSeedPath));
            if (otherData.length == 0) {
                Log.info("Splice Mutation: Other seed data is empty. Falling back to bitflip.");
                return bitFlipMutation(data);
            }
            // 随机选择一个切片长度
            int sliceLen = RANDOM.nextInt(mutated.length / 2) + 1;
            // 随机选择切片位置
            int slicePos = RANDOM.nextInt(mutated.length - sliceLen);
            // 选择另一个种子从中获取切片
            int otherSlicePos = RANDOM.nextInt(otherData.length - sliceLen);
            // 替换切片
            System.arraycopy(otherData, otherSlicePos, mutated, slicePos, sliceLen);
            Log.info(String.format("Splice Mutation: Replaced %d bytes starting at %d with bytes from seed %s", sliceLen, slicePos, otherSeedPath));
            return mutated;
        } catch (IOException e) {
            Log.error("Splice Mutation: Failed to read other seed - " + e.getMessage());
            // Fallback to bitflip
            return bitFlipMutation(data);
        }
    }

    // 5. Havoc mutation
    private byte[] havocMutation(byte[] data) {
        byte[] mutated = data.clone();
        int num_mutations = RANDOM.nextInt(4) + 1; // 1-4 mutations
        for (int i = 0; i < num_mutations; i++) {
            double choice = RANDOM.nextDouble();
            if (choice < 0.40) {
                mutated = bitFlipMutation(mutated);
            } else if (choice < 0.70) {
                mutated = arithmeticMutation(mutated);
            } else {
                mutated = interestingValuesMutation(mutated);
            }
        }
        Log.info(String.format("Havoc Mutation: Applied %d random mutations", num_mutations));
        return mutated;
    }

    private String saveMutatedData(byte[] data) throws IOException {
        String mutatedDir = "fuzz_outputs/mutated_seeds";
        Files.createDirectories(Paths.get(mutatedDir));
        String filename = "mutated_" + System.currentTimeMillis() + ".bin";
        String path = Paths.get(mutatedDir, filename).toString();
        Files.write(Paths.get(path), data);
        return path;
    }
}

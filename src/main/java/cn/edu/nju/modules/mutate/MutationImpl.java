package cn.edu.nju.modules.mutate;

import cn.edu.nju.util.manager.ResourcesManager;
import cn.edu.nju.util.Log;

import java.io.*;
import java.nio.file.*;
import java.util.Random;

public class MutationImpl implements Mutation {
    private ResourcesManager resourcesManager;
    private static final Random RANDOM = new Random();

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    @Override
    public void mutate() {
        try {
            // 获取当前种子路径
            String currentSeed = resourcesManager.getCurrentSeedPath();
            byte[] data = Files.readAllBytes(Paths.get(currentSeed));

            // 应用变异操作（示例为位翻转）
            byte[] mutatedData = bitFlipMutation(data);

            // 保存变异后的数据
            String newSeedPath = saveMutatedData(mutatedData);
            resourcesManager.addNewMutatedSeed(newSeedPath);

            Log.info("Mutated seed saved to " + newSeedPath);
        } catch (IOException e) {
            Log.error("Mutation failed: " + e.getMessage());
        }
    }

    private byte[] bitFlipMutation(byte[] data) {
        byte[] mutated = data.clone();
        // 以1%的概率翻转每一位
        for (int i = 0; i < mutated.length; i++) {
            for (int bit = 0; bit < 8; bit++) {
                if (RANDOM.nextDouble() < 0.01) {
                    mutated[i] ^= (1 << bit);
                }
            }
        }
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

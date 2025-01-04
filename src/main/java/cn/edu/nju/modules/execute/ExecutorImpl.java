package cn.edu.nju.modules.execute;

import cn.edu.nju.util.manager.ResourcesManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ExecutorImpl implements Executor {
    private ResourcesManager resourcesManager;

    @Override
    public boolean execute(String programPath, String inputFilePath, long timeoutMillis, String... additionalArgs) {
        if (programPath == null || programPath.isEmpty()) {
            throw new IllegalArgumentException("Program path cannot be null or empty.");
        }
        if (inputFilePath == null || inputFilePath.isEmpty()) {
            throw new IllegalArgumentException("Input file path cannot be null or empty.");
        }
        System.out.println("Program file path: " + programPath);
        System.out.println("Input file path: " + inputFilePath);

        try {
            // Step 1: 调用 shm_writer 创建共享内存并运行插桩程序
            int shmId = runShmWriter(programPath, inputFilePath);
            if (shmId == -1) {
                System.err.println("Failed to execute shm_writer.");
                return false;
            }

            // Step 2: 调用 shm_reader 读取共享内存内容
            String output = runShmReader(shmId);
            if (output == null) {
                System.err.println("Failed to execute shm_reader.");
                return false;
            }

            // 打印结果到控制台
            System.out.println("Program Output:\n" + output);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getResultFromConsole() {
        return "Use shm_reader to retrieve results from shared memory.";
    }

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    /**
     * 调用 shm_writer 创建共享内存并运行插桩程序
     *
     * @param programPath 插桩程序路径
     * @param inputFilePath 输入文件路径
     * @return 创建的共享内存 ID，失败时返回 -1
     */
    private int runShmWriter(String programPath, String inputFilePath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("./src/main/java/cn/edu/nju/modules/execute/shm_writer", programPath, inputFilePath);
        pb.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("shm_writer output: " + line);
                if (line.startsWith("Created shared memory ID:")) {
                    return Integer.parseInt(line.split(":")[1].trim());
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("shm_writer failed with exit code: " + exitCode);
        }
        return -1;
    }

    /**
     * 调用 shm_reader 读取共享内存内容
     *
     * @param shmId 共享内存 ID
     * @return 共享内存中的内容，失败时返回 null
     */
    private String runShmReader(int shmId) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("./src/main/java/cn/edu/nju/modules/execute/shm_reader", String.valueOf(shmId));
        pb.redirectErrorStream(true); // 合并标准输出和错误输出
        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.err.println("shm_reader failed with exit code: " + exitCode);
            return null;
        }

        return output.toString();
    }
}

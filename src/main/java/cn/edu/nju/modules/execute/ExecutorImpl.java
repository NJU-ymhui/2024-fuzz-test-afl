package cn.edu.nju.modules.execute;

import cn.edu.nju.util.manager.ResourcesManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExecutorImpl implements Executor {
    private static final int SHM_SIZE = 65536; // 共享内存大小 (64KB)
    private ResourcesManager resourcesManager;
    private StringBuilder consoleOutput; // 用于存储程序输出
    private int shmId; // 共享内存 ID
    private String shmPath; // 共享内存路径

    public ExecutorImpl() {
        this.consoleOutput = new StringBuilder();
        this.shmId = -1;
        this.shmPath = null;
    }

    @Override
    public boolean execute(String programPath, String inputFilePath, long timeoutMillis, String... additionalArgs) {
        if (programPath == null || programPath.isEmpty()) {
            throw new IllegalArgumentException("Program path cannot be null or empty.");
        }

        try {
            // 1. 使用 ipcmk 创建共享内存
            createSharedMemory();

            // 2. 构建命令
            List<String> command = new ArrayList<>();
            command.add(programPath);

            // 添加输入文件路径
            if (inputFilePath != null && !inputFilePath.isEmpty()) {
                command.add(inputFilePath);
            }

            // 添加额外参数
            if (additionalArgs != null && additionalArgs.length > 0) {
                command.addAll(Arrays.asList(additionalArgs));
            }

            // 使用 ProcessBuilder 构建并启动进程
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true); // 将错误输出合并到标准输出

            // 设置共享内存 ID 环境变量
            pb.environment().put("AFL_SHM_ID", String.valueOf(shmId));

            Process process = pb.start();

            // 捕获标准输出
            consoleOutput.setLength(0); // 清空之前的输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    consoleOutput.append(line).append("\n");
                }
            }

            // 设置超时
            if (!process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
                process.destroy(); // 强制终止
                consoleOutput.append("Execution timed out.\n");
                return false;
            }

            // 3. 读取共享内存中的覆盖率数据
            readSharedMemory();

            // 返回执行状态
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            consoleOutput.append("Error during execution: ").append(e.getMessage());
            return false;
        } finally {
            // 4. 清理共享内存
            cleanupSharedMemory();
        }
    }

    @Override
    public String getResultFromConsole() {
        return consoleOutput.toString();
    }

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    /**
     * 使用 ipcmk 创建共享内存段
     */
    private void createSharedMemory() throws IOException {
        // 使用 ipcmk 命令创建共享内存
        ProcessBuilder pb = new ProcessBuilder("ipcmk", "-M", String.valueOf(SHM_SIZE));
        pb.redirectErrorStream(true); // 合并错误流到标准输出
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Shared memory id:")) {
                    // 从输出中提取共享内存 ID
                    shmId = Integer.parseInt(line.split(":")[1].trim());
                    shmPath = "/dev/shm/afl_shm_" + shmId;
                    System.out.println("Created shared memory ID: " + shmId);
                    return;
                }
            }
        }

        // 如果没有正确提取到共享内存 ID，则抛出异常
        throw new IOException("Failed to create shared memory using ipcmk.");
    }

    /**
     * 读取共享内存中的覆盖率数据
     */
    private void readSharedMemory() {
        if (shmPath == null) {
            consoleOutput.append("Shared memory path is invalid.\n");
            return;
        }

        try (RandomAccessFile shmFile = new RandomAccessFile(shmPath, "r")) {
            FileChannel channel = shmFile.getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, SHM_SIZE);

            int coveredEdges = 0;
            for (int i = 0; i < SHM_SIZE; i++) {
                if ((buffer.get(i) & 0xFF) > 0) { // 转换为无符号整型
                    coveredEdges++;
                }
            }
            consoleOutput.append("Covered edges: ").append(coveredEdges).append("\n");
        } catch (IOException e) {
            consoleOutput.append("Failed to read shared memory: ").append(e.getMessage()).append("\n");
        }
    }

    /**
     * 清理共享内存段
     */
    private void cleanupSharedMemory() {
        if (shmId != -1) {
            try {
                ProcessBuilder pb = new ProcessBuilder("ipcrm", "-m", String.valueOf(shmId));
                pb.redirectErrorStream(true);
                Process process = pb.start();
                process.waitFor();
                consoleOutput.append("Shared memory cleaned up successfully.\n");
            } catch (IOException | InterruptedException e) {
                consoleOutput.append("Failed to clean up shared memory: ").append(e.getMessage()).append("\n");
            }
        }
    }
}

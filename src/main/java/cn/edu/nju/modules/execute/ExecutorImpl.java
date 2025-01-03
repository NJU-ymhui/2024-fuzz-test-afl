package cn.edu.nju.modules.execute;

import cn.edu.nju.util.manager.ResourcesManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExecutorImpl implements Executor {
    private ResourcesManager resourcesManager;
    private StringBuilder consoleOutput; // 用于存储程序输出

    public ExecutorImpl() {
        this.consoleOutput = new StringBuilder();
    }

    @Override
    public boolean execute(String programPath, String inputFilePath, long timeoutMillis, String... additionalArgs) {
        if (programPath == null || programPath.isEmpty()) {
            throw new IllegalArgumentException("Program path cannot be null or empty.");
        }

        try {
            // 构建命令
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

            // 返回执行状态
            return process.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            consoleOutput.append("Error during execution: ").append(e.getMessage());
            return false;
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
}

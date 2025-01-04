package cn.edu.nju.modules.execute;

import cn.edu.nju.util.Log;
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
    private StringBuilder consoleOutput;
    private List<Double> coverageData; // 保存覆盖率数据

    // 指定 executor.py 的路径
    private static final String EXECUTOR_PY_PATH = "src/main/java/cn/edu/nju/modules/execute/executor.py";

    public ExecutorImpl() {
        this.consoleOutput = new StringBuilder();
        this.coverageData = new ArrayList<>();
    }

    @Override
    public boolean execute(String programPath, String inputFilePath, List<String> cmdOptions, long timeoutMillis, String... additionalArgs) {
        if (programPath == null || programPath.isEmpty()) {
            throw new IllegalArgumentException("Program path cannot be null or empty.");
        }
        if (inputFilePath == null || inputFilePath.isEmpty()) {
            throw new IllegalArgumentException("Input file path cannot be null or empty.");
        }

        try {
            // 调用 executor.py 脚本
            String output = runExecutorScript(programPath, inputFilePath, cmdOptions, additionalArgs, timeoutMillis);
            if (output == null) {
                Log.error("Executor: Failed to execute executor.py.");
                return false;
            }

            // 打印结果
            Log.info("Executor: Program Output:\n" + output);
            consoleOutput.append(output);
            return true;

        } catch (Exception e) {
            Log.error("Executor: Error during execution." + e);
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

    /**
     * 获取解析后的覆盖率数据
     * @return List<Double> 类型的覆盖率数据
     */
    public List<Double> getCoverageData() {
        return coverageData;
    }

    /**
     * 调用 executor.py 脚本
     */
    private String runExecutorScript(String programPath, String inputFilePath, List<String> cmdOptions,
                                     String[] additionalArgs, long timeoutMillis) throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("python3"); // Python 解释器
        command.add(EXECUTOR_PY_PATH); // Python 脚本路径
        command.add(programPath); // 插桩程序路径
        command.add(inputFilePath); // 输入文件路径
        command.addAll(cmdOptions); // 命令行选项
        if (additionalArgs != null) {
            command.addAll(Arrays.asList(additionalArgs)); // 额外参数
        }

        Log.info("Executor: Running executor.py with command: " + String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true); // 合并标准输出和错误输出

        Process process = null;
        StringBuilder output = new StringBuilder();
        try {
            process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    Log.info("executor.py output: " + line);

                    // 尝试解析覆盖率数据并保存到 coverageData
                    parseCoverageLine(line);
                }
            }

            if (!process.waitFor(timeoutMillis, TimeUnit.MILLISECONDS)) {
                process.destroy();
                Log.error("Executor: executor.py timed out.");
                return null;
            }

            if (process.exitValue() != 0) {
                Log.error("Executor: executor.py failed with exit code: " + process.exitValue());
                return null;
            }
        } catch (IOException e) {
            Log.error("Executor: Failed to start executor.py process." + e);
            throw e;
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return output.toString();
    }

    /**
     * 解析覆盖率数据并保存到 coverageData
     * @param line 输出的每一行
     */
    private void parseCoverageLine(String line) {
        try {
            // 假设覆盖率是以百分比形式输出，例如 "10.45%"
            if (line.trim().endsWith("%")) {
                double coverage = Double.parseDouble(line.replace("%", "").trim());
                coverageData.add(coverage);
            }
        } catch (NumberFormatException e) {
            Log.error("Executor: Failed to parse coverage percentage from line: " + line);
        }
    }
}

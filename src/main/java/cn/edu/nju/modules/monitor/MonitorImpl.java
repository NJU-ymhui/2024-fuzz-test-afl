package cn.edu.nju.modules.monitor;

import cn.edu.nju.util.Log;
import cn.edu.nju.util.manager.ResourcesManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MonitorImpl is responsible for monitoring the execution results of the fuzzing process.
 * It logs coverage data, execution speed, and saves special test cases.
 *
 * Now it also stores coverage with iteration index.
 */
public class MonitorImpl implements Monitor {
    private ResourcesManager resourcesManager;
    private BufferedWriter logWriter;
    private long startTime;
    private long endTime;

    // 记录迭代轮次 -> 覆盖率
    private final Map<Integer, Double> iterationCoverageMap = new LinkedHashMap<>();
    // 用于累加轮次数
    private int iterationCounter = 0;

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    @Override
    public void setUp() {
        try {
            String logFilePath = resourcesManager.getLogPath();
            logWriter = new BufferedWriter(new FileWriter(logFilePath, true));
            startTime = System.currentTimeMillis();
            log("Monitoring started at " + LocalDateTime.now());
        } catch (IOException e) {
            Log.error("Failed to initialize log writer: " + e.getMessage());
        }
    }

    @Override
    public void tearDown() {
        endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log("Monitoring ended at " + LocalDateTime.now());
        log("Total execution time: " + duration + " ms");
        logCoverageData();
        closeLogWriter();
    }

    /**
     * 新增方法：
     * 由外部传入 executor.getCoverageData() 的结果，
     * 以便在本类中进行「轮次数 + 覆盖率」记录和解析
     *
     * @param coverageList Executor中获取的覆盖率列表（百分比）
     */
    public void parseCoverageData(List<Double> coverageList) {
        if (coverageList == null || coverageList.isEmpty()) {
            // 没有覆盖率数据，则不记录
            return;
        }

        // 假设 coverageList 中有多个数据，则逐条记录
        for (Double coverage : coverageList) {
            iterationCounter++;
            iterationCoverageMap.put(iterationCounter, coverage);
        }
    }

    /**
     * 实现接口中要求返回的 map
     */
    @Override
    public Map<Integer, Double> getCoverageMapByIteration() {
        return iterationCoverageMap;
    }

    /**
     * Logs a message to the log file.
     */
    private void log(String message) {
        try {
            if (logWriter != null) {
                logWriter.write(message);
                logWriter.newLine();
                logWriter.flush();
            }
            Log.info(message);
        } catch (IOException e) {
            Log.error("Failed to write to log: " + e.getMessage());
        }
    }

    /**
     * Logs coverage data and execution speed.
     */
    private void logCoverageData() {
        try {
            log("=== Coverage Data ===");

            // 这里仅示例打印出 ResourcesManager 里的 coverage 情况
            // 如果要打印 iterationCoverageMap，请自行处理
            resourcesManager.getCoverageData().forEach((seedPath, coverage) -> {
                log("Seed: " + seedPath + " | Coverage: " + coverage);
            });

            // Calculate execution speed (executions per second)
            long durationSeconds = (endTime - startTime) / 1000;
            if (durationSeconds == 0) durationSeconds = 1; // Avoid division by zero
            int totalExecutions = resourcesManager.getCoverageData().size(); // Placeholder
            double executionsPerSecond = (double) totalExecutions / durationSeconds;
            log("Execution Speed: " + executionsPerSecond + " executions/second");

            // Save special test cases if any
            if (!resourcesManager.getVulnerabilities().isEmpty()) {
                log("=== Vulnerabilities Detected ===");
                resourcesManager.getVulnerabilities().forEach(vuln -> {
                    log("Vulnerability: " + vuln);
                });
            }
        } catch (Exception e) {
            Log.error("Failed to log coverage data: " + e.getMessage());
        }
    }

    /**
     * Closes the log writer safely.
     */
    private void closeLogWriter() {
        try {
            if (logWriter != null) {
                logWriter.close();
            }
        } catch (IOException e) {
            Log.error("Failed to close log writer: " + e.getMessage());
        }
    }
}

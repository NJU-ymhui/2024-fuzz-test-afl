package cn.edu.nju;

import cn.edu.nju.modules.evaluate.EvaluatorImpl;
import cn.edu.nju.modules.execute.ExecutorImpl;
import cn.edu.nju.modules.monitor.MonitorImpl;
import cn.edu.nju.modules.mutate.MutationImpl;
import cn.edu.nju.modules.rank.SeedsManagerImpl;
import cn.edu.nju.modules.schedule.SchedulerImpl;
import cn.edu.nju.util.Log;
import cn.edu.nju.util.manager.FuzzingManager;
import cn.edu.nju.util.manager.ResourcesManager;

public class Fuzzing {
    // Coverage-guided mutation-based fuzzing tool
    public static void main(String[] args) {
        // 创建 ResourcesManager 实例
        ResourcesManager resourcesManager = new ResourcesManager();

        // 创建 FuzzingManager 实例并分配资源
        FuzzingManager fuzzingManager = new FuzzingManager();
        fuzzingManager.allocateResource(resourcesManager);

        // 设置循环次数，例如10次
        fuzzingManager.setLoopCount(10);

        // 创建要测试的模块实例
        SeedsManagerImpl seedsManager = new SeedsManagerImpl();
        SchedulerImpl scheduler = new SchedulerImpl();
        MutationImpl mutation = new MutationImpl();
        ExecutorImpl executor = new ExecutorImpl();
        MonitorImpl monitor = new MonitorImpl();
        EvaluatorImpl evaluator = new EvaluatorImpl();

        // 注册模块
        fuzzingManager.register(seedsManager, scheduler, mutation, executor, monitor, evaluator);

        // 指定目标可执行文件的路径
        String path = System.getProperty("user.dir") + "test/resources/others/elf/small_exec.elf";

        // 运行模糊测试
        fuzzingManager.runOn(path);

        Log.info("Fuzzing: Fuzzing process completed.");
    }
}
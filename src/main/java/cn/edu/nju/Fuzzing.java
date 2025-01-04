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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fuzzing {
    // Coverage-guided mutation-based fuzzing tool
    public static void main(String[] args) {
        // java -jar target.jar cmd1 cmd2
        // cmd1 is the executable file path
        // cmd2 is the initial seeds path
        if (args.length < 2) {
            Log.error("Usage: java -jar target.jar <executable-file-path-and-cmd> <initial-seeds-path>");
            return;
        }
        String executableFilePath = args[0], initialSeedsPath = args[args.length - 1];  // 可执行文件路径和初始种子路径, 正式运行的时候替换最下面的那个path
        List<String> cmdOptions = new ArrayList<>(Arrays.asList(args).subList(1, args.length - 1));
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

        // 运行模糊测试
        fuzzingManager.runOn(System.getProperty("user.dir") + "/" + executableFilePath,
                System.getProperty("user.dir") + "/" + initialSeedsPath, cmdOptions);

        Log.info("Fuzzing: Fuzzing process completed.");
    }
}
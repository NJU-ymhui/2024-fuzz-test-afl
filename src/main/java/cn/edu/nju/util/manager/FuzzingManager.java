package cn.edu.nju.util.manager;

import cn.edu.nju.modules.evaluate.Evaluator;
import cn.edu.nju.modules.execute.Executor;
import cn.edu.nju.modules.monitor.Monitor;
import cn.edu.nju.modules.mutate.Mutation;
import cn.edu.nju.modules.rank.SeedsManager;
import cn.edu.nju.modules.rank.SeedsManagerImpl;
import cn.edu.nju.modules.schedule.Scheduler;
import cn.edu.nju.util.Log;

public class FuzzingManager {
    private int loopCount;
    private Evaluator evaluator;
    private Executor executor;
    private Monitor monitor;
    private Mutation mutation ;
    private SeedsManager seedsManager  ;
    private Scheduler scheduler;
    private ResourcesManager resourcesManager;

    public FuzzingManager() {
        this.loopCount = 0;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public void allocateResource(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    public void register(Object... modules) {
        for (Object module : modules) {
            if (module instanceof Evaluator) {
                this.evaluator = (Evaluator) module;
                this.evaluator.register(resourcesManager);
            } else if (module instanceof Executor) {
                this.executor = (Executor) module;
                this.executor.register(resourcesManager);
            } else if (module instanceof Monitor) {
                this.monitor = (Monitor) module;
                this.monitor.register(resourcesManager);
            } else if (module instanceof Mutation) {
                this.mutation = (Mutation) module;
                this.mutation.register(resourcesManager);
            } else if (module instanceof SeedsManager) {
                this.seedsManager = (SeedsManager) module;
                this.seedsManager.register(resourcesManager);
            } else if (module instanceof Scheduler) {
                this.scheduler = (Scheduler) module;
                this.scheduler.register(resourcesManager);
            } else {
                Log.error("Unknown module type");
                throw new RuntimeException("Unknown module type");
            }
        }
    }

    /***
     *
     * @param objPath 源代码构建出来的可执行文件路径
     */
    public void runOn(String objPath) {
        if (mutation == null || seedsManager == null || scheduler == null||resourcesManager == null||monitor == null||executor == null||evaluator==null) {
            Log.error("Some modules are not registered");
            throw new RuntimeException("Some modules are not registered");
        }
        int loops = loopCount;

            while (loops-- > 0) {
                resourcesManager.loadInitialSeeds(objPath);
                monitor.setUp(); // 开始监控
                seedsManager.register(this.resourcesManager);
                scheduler.register(this.resourcesManager);

                seedsManager.sort(); // 种子排序
                scheduler.schedule(); // 能量调度

                // 获取下一个种子
                SeedsManagerImpl.Seed seed = seedsManager.getNextSeed();
                if (seed == null) {
                    Log.error("No seeds available to mutate.");
                    break;
                }

                // 设置当前要变异的种子路径
                resourcesManager.setCurrentMutatedSeedPath(seed.getFilepath());
                Log.info("FuzzingManager: Current seed set to " + seed.getFilepath());

                mutation.register(this.resourcesManager);
                mutation.mutate(); // 变异
                 if (!executor.execute(objPath)) { // TODO 可能还要别的参数
                Log.error(executor.getResultFromConsole());
                }
                evaluator.eval(); // 评估
                monitor.tearDown(); // 结束监控
            }


    }
}

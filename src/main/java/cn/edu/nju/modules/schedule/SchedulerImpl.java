package cn.edu.nju.modules.schedule;

import cn.edu.nju.modules.rank.SeedsManagerImpl.Seed;
import cn.edu.nju.util.manager.ResourcesManager;
import cn.edu.nju.util.Log;

public class SchedulerImpl implements Scheduler {
    private ResourcesManager resourcesManager;

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    @Override
    public void schedule() {
        // 遍历所有种子，调整其能量
        for (Seed seed : resourcesManager.getAllSeeds()) {
            int currentEnergy = seed.getEnergy();
            int newCoverage = seed.getCoverage();

            // 简单的能量调度策略：覆盖率越高，能量越高
            if (newCoverage > currentEnergy) {
                seed.setEnergy(currentEnergy + 1);
            }

            // 确保能量有一个上限
            if (seed.getEnergy() > 100) {
                seed.setEnergy(100);
            }
        }
        Log.info("Energy scheduling completed.");
        //
    }
}

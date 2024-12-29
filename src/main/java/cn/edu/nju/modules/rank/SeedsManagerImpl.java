package cn.edu.nju.modules.rank;

import cn.edu.nju.util.manager.ResourcesManager;
import cn.edu.nju.util.Log;

import java.util.*;

public class SeedsManagerImpl implements SeedsManager {
    private ResourcesManager resourcesManager;
    private PriorityQueue<Seed> seedQueue;

    public SeedsManagerImpl() {
        // 按照覆盖率从高到低排序
        this.seedQueue = new PriorityQueue<>(Comparator.comparingInt(Seed::getCoverage).reversed());
    }

    @Override
    public void register(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
        loadInitialSeeds();
    }

    private void loadInitialSeeds() {
        List<String> initialSeeds = resourcesManager.getInitialSeeds();
        for (String seedPath : initialSeeds) {
            seedQueue.add(new Seed(seedPath, 0,1)); // 初始化覆盖率为0
        }
        Log.info("Loaded " + initialSeeds.size() + " initial seeds.");
    }

    @Override
    public void sort() {
        // 由于使用PriorityQueue，种子已经按覆盖率排序，无需额外实现
        // 如果使用其他数据结构，可以在此进行排序
        Log.info("Seeds sorted by coverage.");
    }

    @Override
    public void addSeed(String seedPath, int coverage,int energy) {
        seedQueue.add(new Seed(seedPath, coverage,energy));
        Log.info("Added new seed: " + seedPath + " with coverage: " + coverage);
    }

    public Seed getNextSeed() {
        return seedQueue.poll();
    }

    // 内部种子类
    public static class Seed {
        private String filepath;
        private int coverage;
        private int energy; // 添加能量属性

        public Seed(String filepath, int coverage,int energy) {
            this.filepath = filepath;
            this.coverage = coverage;
            this.energy = energy;
        }

        public String getFilepath() {
            return filepath;
        }

        public int getCoverage() {
            return coverage;
        }

        public void setCoverage(int coverage) {
            this.coverage = coverage;
        }

        public int getEnergy() {
            return this.energy;
        }

        public void setEnergy(int i) {
            this.energy = i;
        }
    }
}

package cn.edu.nju.util.manager;

import cn.edu.nju.modules.rank.SeedsManagerImpl.Seed;

import java.util.*;


public class ResourcesManager {
    private List<String> initialSeeds;//用于存放原始种子
    private List<Seed> seedList;//用于存放种子
    private Map<String, Integer> coverageData;//用于存放能量
    private List<String> vulnerabilities;
    private String currentMutatedSeedPath;//现在的最佳选择
    private String logPath;//原始种子的位置

    public ResourcesManager() {
        this.initialSeeds = new ArrayList<>();
        this.seedList = new ArrayList<>();
        this.coverageData = new HashMap<>();
        this.vulnerabilities = new ArrayList<>();
        this.logPath = "fuzzing.log";
    }

    public void loadInitialSeeds(String Path) {
        initialSeeds.add(Path);
    }

    public List<String> getInitialSeeds() {
        return initialSeeds;
    }

    //加入新的变异种子
    public void addNewMutatedSeed(String seedPath) {
        seedList.add(new Seed(seedPath, 0, 1)); // 新变异种子的初始能量为1
        currentMutatedSeedPath = seedPath;
    }

    //该函数失效
    /*public String getCurrentSeedPath() {
        if (!seedList.isEmpty()) {
            return seedList.get(0).getFilepath();
        }
        return null;
    }*/

    public void setCurrentMutatedSeedPath(String path) {
        this.currentMutatedSeedPath = path;
    }

    public String getCurrentMutatedSeedPath() {
        return currentMutatedSeedPath;
    }

    public void updateSeedCoverage(String seedPath, int coverage) {
        coverageData.put(seedPath, coverage);
        for (Seed seed : seedList) {
            if (seed.getFilepath().equals(seedPath)) {
                seed.setCoverage(coverage);
                break;
            }
        }
    }

    public Map<String, Integer> getCoverageData() {
        return coverageData;
    }

    public void addVulnerability(String vulnerability) {
        vulnerabilities.add(vulnerability);
    }

    public List<String> getVulnerabilities() {
        return vulnerabilities;
    }

    public String getLogPath() {
        return logPath;
    }

    public Collection<Seed> getAllSeeds() {
        return seedList;
    }
}

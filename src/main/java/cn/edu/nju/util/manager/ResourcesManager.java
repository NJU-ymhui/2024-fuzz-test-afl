package cn.edu.nju.util.manager;

import cn.edu.nju.modules.rank.SeedsManagerImpl.Seed;

import java.util.*;


public class ResourcesManager {
    private List<String> initialSeeds;
    private List<Seed> seedList;
    private Map<String, Integer> coverageData;
    private List<String> vulnerabilities;
    private String currentMutatedSeedPath;
    private String logPath;

    public ResourcesManager() {
        this.initialSeeds = new ArrayList<>();
        this.seedList = new ArrayList<>();
        this.coverageData = new HashMap<>();
        this.vulnerabilities = new ArrayList<>();
        this.logPath = "fuzzing.log";
        loadInitialSeeds();
    }

    private void loadInitialSeeds() {
        initialSeeds.add("D:\\test\\testcases\\testcases\\others\\elf\\small_exec.elf");
    }

    public List<String> getInitialSeeds() {
        return initialSeeds;
    }

    public void addNewMutatedSeed(String seedPath) {
        seedList.add(new Seed(seedPath, 0, 1)); // 新变异种子的初始能量为1
        currentMutatedSeedPath = seedPath;
    }

    public String getCurrentSeedPath() {
        if (!seedList.isEmpty()) {
            return seedList.get(0).getFilepath();
        }
        return null;
    }

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

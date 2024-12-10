package cn.edu.nju;

import cn.edu.nju.util.manager.FuzzingManager;
import cn.edu.nju.util.manager.ResourcesManager;

public class Fuzzing {
    // Coverage-guided mutation-based fuzzing tool
    public static void main(String[] args) {
        ResourcesManager resourcesManager = new ResourcesManager();
        // TODO update resourcesManager

        FuzzingManager fuzzingManager = new FuzzingManager();
        fuzzingManager.allocateResource(resourcesManager);
        fuzzingManager.setLoopCount(10);
        // TODO register modules

        String path = ""; // TODO get real path
        fuzzingManager.runOn(path);
    }
}
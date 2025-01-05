package cn.edu.nju.modules.execute;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

import java.util.List;

public interface Executor {
    // TODO
    @UnmodifiableSignature
    boolean execute(String programPath, String inputFilePath, List<String> cmdOptions, long timeoutMillis, String... additionalArgs); // 请自行设计接口规约

    @UnmodifiableSignature
    String getResultFromConsole();

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager

    List<Double> getCoverageData() ;

    Integer getCrash();
}

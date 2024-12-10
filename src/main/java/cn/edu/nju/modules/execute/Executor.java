package cn.edu.nju.modules.execute;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

public interface Executor {
    // TODO
    @UnmodifiableSignature
    boolean execute(Object... args); // 请自行设计接口规约

    @UnmodifiableSignature
    String getResultFromConsole();

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager
}

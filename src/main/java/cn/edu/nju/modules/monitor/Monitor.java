package cn.edu.nju.modules.monitor;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

public interface Monitor {
    @UnmodifiableSignature
    void setUp();

    @UnmodifiableSignature
    void tearDown();

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager

}

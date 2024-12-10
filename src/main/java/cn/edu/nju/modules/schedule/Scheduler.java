package cn.edu.nju.modules.schedule;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

public interface Scheduler {
    @UnmodifiableSignature
    void schedule();

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager
}

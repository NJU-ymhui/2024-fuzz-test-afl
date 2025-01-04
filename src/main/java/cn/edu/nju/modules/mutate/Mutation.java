package cn.edu.nju.modules.mutate;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

public interface Mutation {
    // TODO
    @UnmodifiableSignature
    String mutate(); // 变异逻辑，如需额外方法，请新建方法并使用该方法调用

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager
}

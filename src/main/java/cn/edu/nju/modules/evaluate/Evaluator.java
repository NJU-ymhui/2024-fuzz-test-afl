package cn.edu.nju.modules.evaluate;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

public interface Evaluator {
    // TODO
    @UnmodifiableSignature
    void eval(Object... args); // 占位参数，可根据需要设定

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager

}

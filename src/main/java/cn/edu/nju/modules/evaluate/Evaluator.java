package cn.edu.nju.modules.evaluate;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

import java.util.Map;

public interface Evaluator {
    // TODO
    /***
     * @param epoch2Coverage: key: 轮数, value: 覆盖率
     */
    @UnmodifiableSignature
    void eval(Map<Integer, Double> epoch2Coverage); // 占位参数，可根据需要设定

    /**
     * @param msg: 监控信息, 可以传测试过程中pb进程的控制台输出 / shm内容
     */
    void monitor(String msg);

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager

}

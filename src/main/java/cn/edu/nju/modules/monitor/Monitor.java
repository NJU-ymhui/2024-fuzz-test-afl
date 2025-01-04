package cn.edu.nju.modules.monitor;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

import java.util.Map;

public interface Monitor {
    @UnmodifiableSignature
    void setUp();

    @UnmodifiableSignature
    void tearDown();

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager

    /**
     * 新增方法：
     * 从 Executor 获取的覆盖率列表中，解析并返回 (iteration -> coverage) 的映射。
     * @return Map，其中 key 为迭代轮次，value 为覆盖率（百分比）。
     */
    Map<Integer, Double> getCoverageMapByIteration();

}

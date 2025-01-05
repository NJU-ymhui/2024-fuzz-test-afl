package cn.edu.nju.modules.monitor;

import cn.edu.nju.modules.execute.Executor;
import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

import java.util.List;
import java.util.Map;

public interface Monitor {
    @UnmodifiableSignature
    void setUp(String outputPath);

    @UnmodifiableSignature
    void tearDown();

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager

    void register(Executor executor);

    /**
     * 新增方法：
     * 从 Executor 获取的覆盖率列表中，解析并返回 (iteration -> coverage) 的映射。
     * @return Map，其中 key 为迭代轮次，value 为覆盖率（百分比）。
     */
    Map<Integer, Double> getCoverageMapByIteration();

    Map<Integer, Integer> getCrashDate();

    void parseCoverageData(List<Double> coverageList);

    void parseCrashData(Integer crash);

}

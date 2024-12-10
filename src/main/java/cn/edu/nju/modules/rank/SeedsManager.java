package cn.edu.nju.modules.rank;

import cn.edu.nju.util.annotations.UnmodifiableSignature;
import cn.edu.nju.util.manager.ResourcesManager;

public interface SeedsManager {
    // TODO 可能需要维护一个种子队列, 而这个种子队列又是全局的，很多组件都可能去修改
    @UnmodifiableSignature
    void sort(); // 对种子进行排序

    @UnmodifiableSignature
    void register(ResourcesManager resourcesManager); // implement class have this resource manager
}

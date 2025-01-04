# 能量调度组件
## 接口描述
### 方法：register
- public void register(ResourcesManager resourcesManager);
- 描述：该方法用于注册一个 ResourcesManager 实例，以便能够获取所有种子的相关信息。
- 参数： resourcesManager：ResourcesManager 类型，负责管理资源的实例。
### 方法：schedule
- public void schedule();
- 描述：调度所有种子的能量。遍历所有种子并根据其覆盖率调整能量值。
- 处理逻辑： 遍历所有种子并获取其当前能量 (currentEnergy) 和覆盖率 (newCoverage)。
   如果种子的覆盖率 (newCoverage) 高于其当前能量 (currentEnergy)，则将种子的能量增加1。
   保证能量值不超过上限100。
- 日志记录：在调度完成后，通过 Log 类记录“Energy scheduling completed.”的信息，用于监控调度过程的执行情况。
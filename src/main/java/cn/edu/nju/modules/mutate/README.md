# 变异组件
- 在该组件内实现了通过栈顶的种子变异生成变异测试文件，将该文件地址返回
## 接口描述
### 方法：register 
- public void register(ResourcesManager resourcesManager);
- 描述:该方法用于注册一个 ResourcesManager 实例，该实例用于获取当前种子路径和管理变异后的种子。
- 参数: resourcesManager：ResourcesManager 类型，负责管理资源的实例。
### 方法：mutate
- public String mutate();
  - 描述:执行数据变异操作，从当前种子路径读取数据，应用随机数选择变异策略，然后将变异后的数据保存到指定位置。
- 输出:返回一个字符串，表示保存变异后数据的路径。如果变异过程失败，则返回 null。
- 异常处理:捕获 IOException，并记录变异失败的信息。 
- 变异策略方法:bitFlipMutation
- 数据保存方法:saveMutatedData
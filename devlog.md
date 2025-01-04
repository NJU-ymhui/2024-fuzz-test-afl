# 开发日志
**注：实现时请尽量不要改变基础接口**
<br>
即：可以添加新方法，但尽量不要改变被`@UnmodifiableSignature`修饰的方法及签名，如果必须修改
请在此处说明修改原因及如何更改（删除or更新）
## 基础接口`UnModifiableSignature`变更


## 进度记录
### 2024/12/10   余明晖
- 实现了`@UnmodifiableSignature`的注解
- 构建框架，主要包括主要包的结构，主类
- 声明接口`Evaluator`, `Executor`, `Monitor`, `Mutation`, `SeedsManager`, `Scheduler`
- 初步构建全局管理类`FuzzingManager`, `ResourcesManager`

### 2024/12/29   王荣铮
- 初步实现了`Mutation`, `SeedsManager`, `Scheduler`
- 初步实现了全局管理类 `ResourcesManager`
- 变异算子设置为位翻转

### 2024/1/3  王荣铮
- 初步实现了`FuzzingManager`种子生成执行流程

### 2024/1/4 王荣铮
- 发现种子地址传递逻辑问题，修改完成
- 添加变异策略
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
### 2024/1/4   余明晖
- 实现工具命令行参数解析
- 实现评估组件`Evaluator`，可以绘制折线图记录覆盖率
- 更新主类，实现参数传递与模糊器启动
- 修缮代码结构和简单框架
- 完成本地化`Docker`构建，不需要额外镜像下载，一键构建
### 2024/12/20   王丁涵
- 实现`MonitorImpl`，负责记录覆盖率、执行速度及特殊测试用例
- 更新`ResourcesManager`，添加对监控数据的支持
- 编写`Monitor`模块的详细`README.md`

### 2024/1/3  王荣铮
- 初步实现了`FuzzingManager`种子生成执行流程

### 2024/1/4 王荣铮
- 发现种子地址传递逻辑问题，修改完成
- 添加变异策略
### 2024/12/25   王丁涵
- 集成`MonitorImpl`到主流程，确保模糊测试过程中的数据被正确记录
- 测试监控组件在不同模糊测试场景下的表现，修复发现的问题
- 准备初步的统计图表，验证监控数据的准确性

### 2024/12/30   王丁涵
- 优化`MonitorImpl`的日志记录机制，提升性能
- 完善监控数据的收集和存储，确保数据完整性
- 更新`README.md`和`devlog.md`，记录最新的开发进展
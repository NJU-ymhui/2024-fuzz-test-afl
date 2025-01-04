# 2024-fuzz-test-afl
南京大学软件学院2024秋软件测试代码大作业——覆盖率引导的变异式模糊测试工具

## 开发者须知
请在开发过程中自行完善`devlog.md`, 记录进度、任务分配、遇到的难题和解决的过程等（主要是作业要求），
忘了不好补，只能根据git-log回溯，太麻烦了

## 项目结构
- `src/`：源代码
- `cn.edu.nju.modules`: 组件模块
  - `cn.edu.nju.modules.rank`: 种子排序组件
  - `cn.edu.nju.modules.schedule`: 能量调度组件
  - `cn.edu.nju.modules.mutate`: 变异组件
  - `cn.edu.nju.modules.execute`: 测试执行组件
  - `cn.edu.nju.modules.monitor`: 执行结果监控组件
  - `cn.edu.nju.modules.evaluate`: 评估组件

## 开箱即用
执行如下指令
```shell
./build.sh
```  
构建docker镜像
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
<br>
具体各组件的使用方法与实现架构、框架见包内`README`

## 构建环境
*请确保执行环境为bash*

方法一 (推荐)：maven构建，执行如下命令
```shell
apt install maven
chmod a+x mvn_build.sh
./mvn_build.sh
```
完成构建jar包`fuzz-test-afl-2024-1.0-SNAPSHOT.jar`，可参考**如何使用**执行命令

方法二：Docker构建，执行如下指令，执行前需要在Dockerfile最后修改具体的执行参数（参数形式见**如何使用**）
```shell
./build.sh
```  
完成构建docker镜像

## 如何使用
1. 构建环境
2. 准备资源：待测可执行文件和初始种子
3. 传递参数
```shell
java -jar fuzz-test-afl-2024-1.0-SNAPSHOT.jar <test-file-path> [<options>] <initial-seeds-path> -o <output-path>
```
示例：
```shell
java -jar fuzz-test-afl-2024-1.0-SNAPSHOT.jar src/test/resources/testcases/T02/readelf -a src/test/resources/initial-seeds/T02/small_exec.elf -o src/test/resources/output/T02
```
4. 等待结果输出
5. 查看日志

## 结果文件（日志 + 可视化图表）
`src/test/resources/output`

## 演示视频
【软件测试课程代码大作业——覆盖率引导的模糊测试工具演示视频】 
<br>
https://www.bilibili.com/video/BV1FbrTYaEf7/?share_source=copy_web&vd_source=12654aed32292ea1c09af2a25c2f3116

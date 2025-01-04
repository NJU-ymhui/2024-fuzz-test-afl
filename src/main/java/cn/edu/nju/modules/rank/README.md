# 种子排序组件
## 接口描述
### 构造函数
- public SeedsManagerImpl();
- 描述：构造函数初始化一个优先队列，用于按种子的覆盖率进行排序。种子覆盖率较高的种子优先级更高。
### 方法：register
- public void register(ResourcesManager resourcesManager);
- 描述:用于注册 ResourcesManager 实例，加载初始种子。
- 参数:resourcesManager：ResourcesManager 类型，负责管理资源的实例。 
### 方法：loadInitialSeeds
- private void loadInitialSeeds();
- 描述:私有方法，用于从 ResourcesManager 中加载初始种子，并将它们添加到优先队列中。每个种子的初始覆盖率设置为0。
### 方法：sort
- public void sort();
- 描述:当前通过优先队列自动维护种子的排序，无需额外实现排序逻辑。此方法提供日志信息表示种子已按覆盖率排序。 
### 方法：addSeed
- public void addSeed(String seedPath, int coverage, int energy);
- 描述:向种子队列中添加新的种子，并记录其路径、覆盖率和能量值。
- 参数: seedPath：String 类型，表示新种子的路径。 coverage：int 类型，表示新种子的覆盖率。 energy：int 类型，表示新种子的能量值。
### 方法：getNextSeed
- public Seed getNextSeed();
- 描述:返回优先级最高的种子（覆盖率最高的种子），但不移除该种子。
## 内部类
### Seed 类
- 描述:内部类 Seed 用于表示种子的相关信息，包括文件路径、覆盖率和能量。

- 属性:
  - filepath：String 类型，表示种子的文件路径。
  - coverage：int 类型，表示种子的覆盖率。
  - energy：int 类型，表示种子的能量值。
- 方法
  - 构造函数
  - 方法：getFilepath 返回种子的文件路径。
  - 方法：getCoverage 返回种子的覆盖率。
  - 方法：setCoverage 设置种子的覆盖率。
  - 方法：getEnergy 返回种子的能量值。
  - 方法：setEnergy 设置种子的能量值。
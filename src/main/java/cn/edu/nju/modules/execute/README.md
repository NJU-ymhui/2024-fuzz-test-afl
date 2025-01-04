# 测试执行组件

### **概述**

该系统通过 `Executor` 接口与实现类 `ExecutorImpl` 调用 Python 脚本 `executor.py`，实现对插桩程序的执行和覆盖率统计。核心功能包括：

1. **插桩程序的执行**
2. **共享内存覆盖率数据的读取**
3. **按位覆盖率的统计**

------

## **文件说明**

### **1. `Executor` 接口**

#### **文件路径**

```
cn.edu.nju.modules.execute.Executor
```

#### **功能**

定义了一个统一的执行接口，便于不同的实现方式扩展和替换。

#### **接口方法**

1. **`execute`**

    - **功能**：执行插桩程序，并通过共享内存统计覆盖率。

    - 参数

      ：

        - `programPath`：插桩程序路径。
        - `inputFilePath`：输入文件路径。
        - `cmdOptions`：命令行选项。
        - `timeoutMillis`：执行超时时间。
        - `additionalArgs`：额外参数。

    - **返回值**：`true` 表示执行成功，`false` 表示失败。

2. **`getResultFromConsole`**

    - **功能**：获取执行结果的控制台输出。
    - **返回值**：执行结果字符串。

3. **`register`**

    - **功能**：注册 `ResourcesManager` 资源管理器。

------

### **2. `ExecutorImpl` 实现类**

#### **文件路径**

```
cn.edu.nju.modules.execute.ExecutorImpl
```

#### **功能**

通过调用 `executor.py` 脚本完成插桩程序的执行和覆盖率统计。

#### **方法说明**

1. **`execute`**

    - 实现功能

      ：

        - 检查输入参数是否合法。
        - 调用 Python 脚本 `executor.py`。
        - 捕获脚本的输出，返回执行结果。

    - 逻辑流程

      ：

        - 构建调用命令。
        - 使用 `ProcessBuilder` 执行 Python 脚本。
        - 捕获 Python 脚本的输出。
        - 处理异常与超时。

2. **`getResultFromConsole`**

    - **功能**：返回 Python 脚本的输出内容。

3. **`runExecutorScript`**

    - **功能**：封装了对 `executor.py` 脚本的调用逻辑。

    - 关键点

      ：

        - 调用 Python 解释器运行脚本。
        - 捕获脚本的标准输出。
        - 处理脚本执行中的异常或超时。
4. **方法：`getCoverageData`**

   - **功能**：

       - 返回在执行插桩程序时解析并存储的覆盖率数据。
       - 覆盖率数据以百分比形式存储，每次执行的覆盖率数据以列表形式返回。

   - **方法签名**：

     ```
     public List<Double> getCoverageData();
     ```

   - **返回值**：

       - 类型：`List<Double>`
       - 每个元素表示一次插桩程序执行的覆盖率百分比。
------

### **3. `executor.py` 脚本**

#### **文件路径**

```
src/main/java/cn/edu/nju/modules/execute/executor.py
```

#### **功能**

执行插桩程序、读取共享内存覆盖率数据，并按位统计覆盖率。

#### **主要逻辑**

1. **创建共享内存**

    - 使用 `sysv_ipc` 创建大小为 `65536 * 8` 位的共享内存。
    - 将共享内存 ID 设置为环境变量 `__AFL_SHM_ID`。

2. **执行插桩程序**

    - 使用 `subprocess.run` 调用插桩程序，将环境变量传递给插桩程序。

3. **读取共享内存**

    - 通过共享内存读取覆盖率数据。
    - 按位统计非零值（覆盖路径）和零值（未覆盖路径）。

4. **计算覆盖率**

    - 计算覆盖率为非零位数占总位数的百分比：

      ```python
      coverage_percentage = (total_ones / total_bits) * 100
      ```

5. **输出覆盖率**

    - 直接打印覆盖率结果到控制台。

6. **释放共享内存**

    - 删除共享内存，避免资源泄漏。

------

### **使用说明**

#### **执行流程**

1. 编译插桩程序（确保程序支持 `__AFL_SHM_ID` 环境变量）。
2. 调用 `Executor` 接口，运行插桩程序并统计覆盖率。


### **依赖与环境要求**

1. **语言与工具**

    - Java 11+
    - Python 3.6+
    - Python 库 `sysv_ipc`

2. **安装依赖**

   ```bash
   pip install sysv_ipc
   ```

3. **系统要求**

    - 支持共享内存的操作系统（Linux/Unix）。

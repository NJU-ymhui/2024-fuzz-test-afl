# 使用 Ubuntu 22.04 作为基础镜像
FROM ubuntu:22.04

# 设置非交互模式，避免某些安装过程中需要用户输入
ENV DEBIAN_FRONTEND=noninteractive

# 安装 Java 运行时环境和构建工具
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    maven \
    curl \
    git \
    python \
    && rm -rf /var/lib/apt/lists/*

RUN pip install sysv_ipc

# 设置 Java 环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# 创建应用目录
WORKDIR /app

# 复制项目文件到容器
COPY . /app

# 如果是 Maven 项目，先编译和打包
RUN mvn clean install

# 默认运行命令行工具
ENTRYPOINT ["java", "-jar", "target/fuzz-test-afl-2024-1.0-SNAPSHOT.jar"]

# 如果有需要的参数，可以在这里指定
# CMD ["arg1", "arg2"]

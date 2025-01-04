#!/bin/bash

# 设置变量
IMAGE_TAR_FILE="ubuntu-22.04.tar"  # 要加载的镜像文件
DOCKERFILE_DIR="."  # Dockerfile 所在的目录（默认为当前目录）
IMAGE_NAME="my-afl-tool"  # 构建的镜像名称
IMAGE_TAG="latest"  # 镜像标签

# 检查镜像文件是否存在
if [ ! -f "$IMAGE_TAR_FILE" ]; then
  echo "Error: 镜像文件 '$IMAGE_TAR_FILE' 不存在."
  exit 1
fi

# Step 1: 加载镜像
echo "正在加载镜像 '$IMAGE_TAR_FILE' ..."
docker load -i "$IMAGE_TAR_FILE"

# 检查 docker load 是否成功
if [ $? -ne 0 ]; then
  echo "Error: 加载镜像 '$IMAGE_TAR_FILE' 失败."
  exit 1
fi

echo "镜像加载完成."

# Step 2: 构建镜像
echo "正在构建镜像 '$IMAGE_NAME:$IMAGE_TAG' ..."
docker build -t "$IMAGE_NAME:$IMAGE_TAG" "$DOCKERFILE_DIR"

# 检查 docker build 是否成功
if [ $? -ne 0 ]; then
  echo "Error: 构建镜像 '$IMAGE_NAME:$IMAGE_TAG' 失败."
  exit 1
fi

echo "镜像构建完成：$IMAGE_NAME:$IMAGE_TAG"

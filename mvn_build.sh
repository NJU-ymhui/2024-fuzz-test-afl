#!/bin/bash

echo "构建项目..."

mvn package
cp target/*.jar .

echo "构建完成"
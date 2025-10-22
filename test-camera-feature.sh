#!/bin/bash

echo "=== 摄像头管理功能测试脚本 ==="

# 检查数据库连接
echo "1. 检查数据库连接..."
if command -v mysql &> /dev/null; then
    echo "MySQL客户端已安装"
else
    echo "警告: 未找到MySQL客户端"
fi

# 检查后端Java环境
echo "2. 检查Java环境..."
if command -v java &> /dev/null; then
    java -version
else
    echo "错误: 未找到Java环境"
    exit 1
fi

# 检查Maven
echo "3. 检查Maven..."
if command -v mvn &> /dev/null; then
    mvn -version
else
    echo "错误: 未找到Maven"
    exit 1
fi

# 检查Node.js环境
echo "4. 检查Node.js环境..."
if command -v node &> /dev/null; then
    node -version
    npm -version
else
    echo "错误: 未找到Node.js环境"
    exit 1
fi

# 编译后端
echo "5. 编译后端项目..."
mvn clean compile -DskipTests

if [ $? -eq 0 ]; then
    echo "后端编译成功"
else
    echo "后端编译失败"
    exit 1
fi

# 检查前端依赖
echo "6. 检查前端依赖..."
cd yulo-ui
if [ -d "node_modules" ]; then
    echo "前端依赖已安装"
else
    echo "安装前端依赖..."
    npm install
fi

echo "=== 测试准备完成 ==="
echo "请按以下步骤进行测试："
echo "1. 执行 sql/camera.sql 初始化数据库"
echo "2. 启动后端服务: mvn spring-boot:run"
echo "3. 启动前端服务: cd yulo-ui && npm run dev"
echo "4. 访问系统并测试摄像头管理功能"
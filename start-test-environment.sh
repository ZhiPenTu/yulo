#!/bin/bash

echo "=== 摄像头拉流功能测试环境启动脚本 ==="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到Java环境"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到Maven"
    exit 1
fi

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到Node.js环境"
    exit 1
fi

echo "✅ 环境检查通过"
echo ""

# 编译后端项目
echo "📦 编译后端项目..."
mvn clean compile -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 后端编译失败"
    exit 1
fi
echo "✅ 后端编译成功"
echo ""

# 检查前端依赖
echo "📦 检查前端依赖..."
cd yulo-ui
if [ ! -d "node_modules" ]; then
    echo "📥 安装前端依赖..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 前端依赖安装失败"
        exit 1
    fi
fi
cd ..
echo "✅ 前端依赖就绪"
echo ""

echo "🚀 环境准备完成！"
echo ""
echo "接下来请按以下步骤启动服务："
echo ""
echo "1. 启动后端服务："
echo "   mvn spring-boot:run"
echo ""
echo "2. 启动前端服务（新终端窗口）："
echo "   cd yulo-ui && npm run dev"
echo ""
echo "3. 测试ZLMediaKit连接："
echo "   curl http://35.46.5.76:8080/index/api/getServerConfig?secret=MKjn33mg82zrW8TIleMSDlbdxhmhP6yZ"
echo ""
echo "4. 运行API测试："
echo "   ./test-camera-stream-api.sh"
echo ""
echo "5. 打开可视化测试页面："
echo "   open test-camera-stream.html"
echo ""
echo "📋 测试清单："
echo "□ 后端服务启动成功"
echo "□ 前端服务启动成功"
echo "□ ZLMediaKit服务可访问"
echo "□ 摄像头管理页面正常显示"
echo "□ 拉流功能测试通过"
echo "□ API接口测试通过"
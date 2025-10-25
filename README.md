<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">智能视频监控管理系统 v3.9.0</h1>
<h4 align="center">基于RuoYi框架的智能摄像头监控与AI分析管理平台</h4>
<p align="center">
	<a href="#"><img src="https://img.shields.io/badge/Java-1.8+-blue.svg"></a>
	<a href="#"><img src="https://img.shields.io/badge/SpringBoot-2.5.15-brightgreen.svg"></a>
	<a href="#"><img src="https://img.shields.io/badge/Vue-3.0+-green.svg"></a>
	<a href="#"><img src="https://img.shields.io/badge/License-MIT-yellow.svg"></a>
</p>

## 系统简介

智能视频监控管理系统是基于RuoYi框架开发的企业级摄像头监控与AI智能分析平台。系统集成了摄像头设备管理、视频流处理、AI智能检测、区域绘制配置等核心功能，为用户提供完整的智能监控解决方案。

### 技术架构
* **后端框架**：Spring Boot 2.5.15 + Spring Security + MyBatis
* **前端框架**：Vue 3 + Element Plus + Vite
* **数据库**：MySQL 8.0+
* **缓存**：Redis
* **认证**：JWT Token
* **媒体服务**：ZLMediaKit
* **AI分析**：支持多种智能检测算法

## 核心功能

### 🎥 摄像头设备管理
1. **设备管理**：摄像头设备的增删改查、状态监控、批量操作
2. **在线状态检测**：实时监控摄像头在线状态，定时检查设备连通性
3. **设备分组**：支持按区域、类型等维度对摄像头进行分组管理
4. **设备配置**：摄像头参数配置、网络设置、视频参数调整

### 📹 视频流管理
5. **视频拉流**：基于ZLMediaKit的RTSP视频流拉取和转换
6. **多格式支持**：支持HTTP-FLV、HLS、RTMP等多种视频流格式
7. **流媒体控制**：视频流的启动、停止、状态查询和管理
8. **视频预览**：实时视频预览和播放功能

### 🤖 AI智能分析
9. **人机非行为分析**：违法停车、违法倒车、超速、低速、事故检测等15种检测类型
10. **场景分析**：施工区域、遗留物、火灾、烟雾、花屏等6种场景检测
11. **AI配置管理**：灵活的AI检测功能开关和参数配置
12. **检测结果处理**：AI检测结果的存储、查询和告警

### 🎯 区域绘制配置
13. **多边形绘制**：在视频画面上绘制检测区域（车道区、禁行区、拥堵区等）
14. **区域类型管理**：支持8种不同类型的检测区域配置
15. **可视化编辑**：直观的区域绘制界面，支持实时预览和编辑
16. **区域数据管理**：区域配置的保存、加载和批量管理

### 🔧 系统管理功能
17. **用户权限管理**：用户、角色、权限的精细化管理
18. **系统监控**：服务器性能监控、日志管理、缓存监控
19. **定时任务**：摄像头状态检查、数据清理等定时任务管理
20. **系统配置**：系统参数配置、字典管理、通知公告

## 快速开始

### 环境要求
- JDK 1.8+
- MySQL 8.0+
- Redis 3.0+
- Node.js 16+
- ZLMediaKit 媒体服务器

### 安装部署

1. **克隆项目**
```bash
git clone [项目地址]
cd [项目目录]
```

2. **数据库初始化**
```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE yulo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 导入数据库脚本
mysql -u root -p yulo_db < sql/camera.sql
```

3. **后端启动**
```bash
# 修改配置文件
vim src/main/resources/application.yml

# 启动后端服务
mvn spring-boot:run
```

4. **前端启动**
```bash
cd yulo-ui
npm install
npm run dev
```

5. **媒体服务器配置**
```bash
# 启动ZLMediaKit服务
./start-test-environment.sh
```

### 默认账号
- 用户名：admin
- 密码：admin123

## 功能特色

### 🎯 智能检测能力
- **15种行为检测**：违法停车、违法倒车、超速、低速、事故、拥堵等
- **6种场景检测**：施工区域、遗留物、火灾、烟雾、花屏等
- **实时分析**：毫秒级检测响应，支持高并发处理
- **精准识别**：基于深度学习算法，检测准确率高

### 📹 视频流处理
- **多协议支持**：RTSP、RTMP、HTTP-FLV、HLS等主流协议
- **实时转码**：自动转码适配不同播放终端
- **流媒体管理**：统一的流媒体服务管理和监控
- **低延迟传输**：优化的传输协议，延迟控制在秒级

### 🎨 可视化配置
- **区域绘制**：直观的多边形区域绘制工具
- **实时预览**：配置过程中实时预览检测效果
- **模板管理**：支持区域配置模板的保存和复用
- **批量操作**：支持多摄像头的批量配置

### 🔧 运维管理
- **设备监控**：实时监控摄像头在线状态和健康度
- **自动巡检**：定时检查设备状态，异常自动告警
- **日志审计**：完整的操作日志和系统日志记录
- **性能监控**：系统资源使用情况实时监控


## 项目结构

```
智能视频监控管理系统/
├── src/                          # 后端源码
│   ├── main/java/com/ruoyi/
│   │   ├── project/device/       # 设备管理模块
│   │   ├── framework/task/       # 定时任务
│   │   └── ...
│   └── main/resources/
│       ├── mybatis/device/       # MyBatis映射文件
│       └── application.yml       # 配置文件
├── yulo-ui/                      # 前端源码
│   ├── src/views/
│   │   ├── deviceManage/         # 设备管理页面
│   │   └── AIsSettings/          # AI设置页面
│   └── src/api/                  # API接口
├── sql/                          # 数据库脚本
├── docker/                       # Docker配置
├── doc/                          # 项目文档
└── test-*.html                   # 测试页面
```

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yulo_db
    username: root
    password: password
```

### ZLMediaKit配置
```yaml
zlmediakit:
  server:
    url: http://35.46.5.76:8080
    secret: MKjn33mg82zrW8TIleMSDlbdxhmhP6yZ
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: 
```

## 使用文档

- [AI设置功能快速开始指南](./AI设置功能快速开始指南.md)
- [区域绘制功能快速开始指南](./区域绘制功能快速开始指南.md)
- [摄像头拉流功能测试说明](./摄像头拉流功能测试说明.md)
- [摄像头状态定时检查功能验证报告](./摄像头状态定时检查功能验证报告.md)

## 许可证

本项目基于 [MIT License](LICENSE) 开源协议。

## 技术支持

如有问题或建议，请查看相关文档或提交Issue。
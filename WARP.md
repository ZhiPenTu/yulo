# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## 常用命令（后端/前端/环境）

- 后端（Maven / Spring Boot）
  - 开发运行
    ```bash path=null start=null
    mvn spring-boot:run
    ```
  - 构建可执行包（跳过测试）
    ```bash path=null start=null
    mvn -DskipTests clean package
    ```
  - 运行所有测试（当前仓库未包含测试用例，但命令可用）
    ```bash path=null start=null
    mvn -q test
    ```
  - 运行单个测试类或方法
    ```bash path=null start=null
    mvn -q -Dtest=ClassName test
    mvn -q -Dtest=ClassName#methodName test
    ```

- 前端（Vite / Vue3）
  - 开发运行
    ```bash path=null start=null
    cd yulo-ui && npm install && npm run dev
    ```
  - 构建产物与本地预览
    ```bash path=null start=null
    cd yulo-ui && npm run build:prod
    cd yulo-ui && npm run preview
    ```

- 基础设施（本地容器）
  - 启动 MySQL、Redis、Nginx、后端等服务
    ```bash path=null start=null
    docker compose -f docker/docker-compose.yml up -d
    ```
  - 停止并清理
    ```bash path=null start=null
    docker compose -f docker/docker-compose.yml down
    ```

- 数据库初始化（示例）
  ```bash path=null start=null
  mysql -u <user> -p<password> -h <host> -P <port> < <repo_root>/sql/camera.sql
  ```

- Lint/格式化
  - 本仓库未在 Maven 或前端脚本中配置显式的 Lint/格式化命令；如需引入，请在后续变更中添加对应插件/脚本。

## 代码与架构总览

- 总体形态
  - 单体后端：Spring Boot 2.5.x（基于 RuoYi），持久层 MyBatis + MySQL，缓存 Redis，认证 JWT，调度 Quartz，接口文档 Swagger（可通过 application.yml 开关与前缀控制）。
  - 前端：Vue3 + Element Plus + Vite，位于 `yulo-ui/`。
  - 容器化与本地依赖：`docker/docker-compose.yml` 提供 MySQL、Redis、Nginx、后端服务编排；`sql/` 目录提供初始化/迁移脚本。

- 启动入口与配置
  - 后端主类：`src/main/java/com/ruoyi/RuoYiApplication.java`。
  - 主要配置：`src/main/resources/application.yml`、`application-druid.yml`（数据源 Druid，Redis、Swagger、MyBatis、Token、ZLMediaKit 等在此配置，通过环境变量注入数据库/缓存参数）。

- 分层与通用能力
  - 控制器（controller）→ 服务（service）→ 持久化（mapper + `resources/mybatis/**/**Mapper.xml`）的典型分层；领域对象置于各模块的 `domain/`。
  - `com.ruoyi.framework`：应用级基础设施（安全配置 SecurityConfig/JWT、AOP 日志与数据源切换、全局异常、线程池、Redis 封装、Swagger、MyBatis、XSS、防重复提交、国际化、静态资源等）。
  - `com.ruoyi.common`：常量、异常定义、工具方法（Excel、HTTP、签名、反射、分页、IP/地址、文件等）。

- 业务模块（`com.ruoyi.project` 下）
  - `device`：摄像头设备与流媒体集成
    - 控制器：`device/controller/CameraController`
    - 服务：`device/service/ICameraService`、`IZLMediaKitService` 及其实现 `impl/*`
    - 定时任务：`com.ruoyi.framework.task.CameraStatusTask`（设备在线状态巡检等）
    - 持久化：`resources/mybatis/device/*.xml`
  - `eventManage`：事件上报/查询
    - 控制器/服务/Mapper 与 `resources/mybatis/eventManage/*.xml` 配套
  - `system`：用户、角色、菜单、字典、组织等基础系统能力；`SecurityConfig` 与 `TokenService` 提供认证授权、权限判断
  - `monitor`：计划任务、登录/操作日志、在线用户、服务器信息等运行时监控
  - `tool/gen`：代码生成（表与字段元数据、模板与工具）

- 持久化与配置要点
  - MyBatis 映射 XML 按模块归档于 `src/main/resources/mybatis/**`，别名包 `com.ruoyi.project.**.domain`；全局配置 `mybatis/mybatis-config.xml`。
  - 数据源与连接池在 `application-druid.yml` 下通过环境变量控制（主库、连接池与监控控制台等）。

- 前端要点
  - 脚本由 `yulo-ui/package.json` 提供（`dev`、`build:prod`、`build:stage`、`preview`）。业务视图位于 `yulo-ui/src/views/`，API 调用位于 `yulo-ui/src/api/`，与后端接口一一对应。

- 本地开发依赖
  - 依赖外部 MySQL、Redis、ZLMediaKit；可用 `docker-compose` 快速就绪 MySQL/Redis/Nginx/后端。ZLMediaKit 需在应用配置中设置服务地址与密钥后方可联通。

## 与文档的衔接

- 根目录 `README.md` 提供了快速开始、技术栈、核心功能与项目结构示例，以及前后端启动指引与 SQL 初始化路径；上述命令与架构概览与其一致并做了凝练汇总。

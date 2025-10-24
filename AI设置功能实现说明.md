# AI设置功能实现说明

## 功能概述

AI设置页面用于配置摄像头的AI检测功能，包括人机非行为分析和场景分析两大类功能。管理员可以为每个摄像头单独配置需要启用的AI检测项目，并查看摄像头的偏移对照图。

## 实现的功能

### 1. 摄像头列表展示
- 显示所有摄像头的基本信息（编号、名称、状态）
- 支持按摄像头编号或名称搜索
- 显示摄像头在线/离线状态

### 2. AI功能配置

#### 2.1 人机非行为分析
支持以下检测项目的配置：
- 违法停车 (parking)
- 违法倒车 (reverse)
- 货车高速 (truckHighSpeed)
- 占用应急车道 (emergencyLane)
- 违法变道 (changeLane)
- 导流区 (guideArea)
- 压实线 (crossSolidLine)
- 拥堵 (congestion)
- 收费站拥堵 (congestionToll)
- 超速 (overSpeed)
- 低速 (lowSpeed)
- 事故 (accident)
- 行人入侵 (personInvasion)
- 摩托车入侵 (motorbikeInvasion)
- 交通流量 (trafficFlow)

#### 2.2 场景分析
支持以下检测项目的配置：
- 施工区域 (constructionArea)
- 遗留物 (remnants)
- 火灾 (fire)
- 烟雾 (smoke)
- 花屏 (flowerScreen)
- 事故 (accident)

### 3. 相机偏移情况查看
- 点击图片图标可查看摄像头的偏移对照图
- 支持图片预览和放大查看
- 如果摄像头没有偏移对照图，按钮将被禁用

### 4. 实时保存
- AI功能配置修改后自动保存到数据库
- 使用 JSON 格式存储配置信息

## 数据库设计

### 新增字段

在 `camera` 表中新增以下字段：

| 字段名 | 类型 | 说明 | 示例 |
|--------|------|------|------|
| ai_function | text | AI检测配置 | JSON格式的配置数据 |
| regions | text | 区域绘制范围数据 | 暂未使用 |
| camera_url | varchar(255) | 摄像头直播地址 | http://35.46.5.76:8080/live/xxx.live.mp4 |
| background_snap | varchar(255) | 偏移对照图 | http://35.46.5.88:19000/camera-background-snap/xxx.jpg |
| lanes | text | 车道信息 | 暂未使用 |

### ai_function 字段格式

```json
{
  "carPersonMotor": {
    "enable": true,
    "attributes": {
      "parking": true,
      "reverse": true,
      "congestion": true,
      "lowSpeed": true,
      "personInvasion": true,
      "trafficFlow": true
    }
  },
  "scene": {
    "enable": true,
    "attributes": {
      "constructionArea": true,
      "remnants": true,
      "fire": true,
      "smoke": true,
      "accident": true
    }
  }
}
```

## 文件修改清单

### 前端文件

1. **yulo-ui/src/views/AIsSettings/index.vue** (新建)
   - AI设置页面主组件
   - 实现摄像头列表展示
   - 实现AI功能配置界面
   - 实现偏移对照图查看功能

### 后端文件

1. **src/main/java/com/ruoyi/project/device/domain/Camera.java**
   - 新增 aiFunction 字段
   - 新增 regions 字段
   - 新增 cameraUrl 字段
   - 新增 backgroundSnap 字段
   - 新增 lanes 字段

2. **src/main/resources/mybatis/device/CameraMapper.xml**
   - 更新 resultMap 映射
   - 更新 selectCameraVo SQL
   - 更新 insertCamera 语句
   - 更新 updateCamera 语句

### 数据库文件

1. **sql/camera.sql**
   - 更新表结构定义
   - 更新示例数据

2. **sql/camera_update.sql** (新建)
   - 提供现有数据库的升级脚本
   - 添加新字段的 ALTER TABLE 语句

## 使用说明

### 1. 数据库升级

如果是现有系统，需要执行升级脚本：

```bash
mysql -u root -p your_database < sql/camera_update.sql
```

如果是新系统，直接执行：

```bash
mysql -u root -p your_database < sql/camera.sql
```

### 2. 访问页面

在系统菜单中添加 AI设置 菜单项，路由配置为：

```javascript
{
  path: '/ai-settings',
  component: () => import('@/views/AIsSettings/index'),
  name: 'AIsSettings',
  meta: { title: 'AI设置', icon: 'setting' }
}
```

### 3. 配置AI功能

1. 进入 AI设置 页面
2. 找到需要配置的摄像头
3. 点击"人机非行为分析"或"场景分析"前的复选框启用功能
4. 点击右侧箭头展开详细配置项
5. 勾选需要启用的具体检测项目
6. 配置会自动保存

### 4. 查看偏移对照图

1. 在操作列找到图片图标按钮
2. 点击按钮打开偏移对照图预览
3. 可以点击图片进行放大查看

## 注意事项

1. **区域绘制功能暂未实现**：当前版本中"区域绘制"按钮点击后仅显示提示信息，实际功能待后续开发。

2. **AI功能配置格式**：ai_function 字段必须是有效的 JSON 格式，否则会导致解析失败。

3. **偏移对照图**：background_snap 字段存储的是图片的完整 URL 地址，需要确保图片服务器可访问。

4. **权限控制**：建议为 AI设置 页面配置适当的访问权限，避免普通用户误操作。

5. **性能优化**：如果摄像头数量较多，建议添加分页功能以提升页面加载速度。

## 后续优化建议

1. 实现区域绘制功能，支持在视频画面上绘制检测区域
2. 添加批量配置功能，支持一次性为多个摄像头配置相同的AI功能
3. 添加配置模板功能，支持保存和应用常用配置
4. 添加配置历史记录，支持查看和回滚配置变更
5. 添加AI检测效果预览，实时显示检测结果

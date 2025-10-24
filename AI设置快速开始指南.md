# AI设置功能 - 快速开始指南

## 🚀 5分钟快速部署

### 步骤 1: 更新数据库 (2分钟)

```bash
# 进入项目根目录
cd /path/to/your/project

# 执行数据库升级脚本（现有系统）
mysql -u root -p yulo_db < sql/camera_update.sql

# 或者重新创建表（新系统）
mysql -u root -p yulo_db < sql/camera.sql
```

### 步骤 2: 重启后端服务 (1分钟)

```bash
# 停止现有服务
# 然后重新启动
./ry.sh

# 或者
java -jar target/ruoyi-admin.jar
```

### 步骤 3: 配置前端路由 (1分钟)

在系统管理后台添加菜单，或执行以下SQL：

```sql
-- 添加AI设置菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('AI设置', (SELECT menu_id FROM sys_menu WHERE menu_name = '设备管理' AND parent_id = 0), 2, 'ai-settings', 'AIsSettings/index', 1, 0, 'C', '0', '0', 'device:ai:list', 'setting', 'admin', NOW(), 'AI设置菜单');
```

### 步骤 4: 访问页面 (1分钟)

1. 登录系统
2. 在侧边栏找到"设备管理" -> "AI设置"
3. 开始配置AI功能

## ✅ 功能验证清单

- [ ] 页面可以正常打开
- [ ] 显示摄像头列表
- [ ] 可以勾选AI功能
- [ ] 可以展开详细配置
- [ ] 配置可以保存成功
- [ ] 可以查看偏移对照图（如果有）

## 📁 文件位置

### 前端文件
```
yulo-ui/src/views/AIsSettings/index.vue
```

### 后端文件
```
src/main/java/com/ruoyi/project/device/domain/Camera.java
src/main/resources/mybatis/device/CameraMapper.xml
```

### 数据库文件
```
sql/camera.sql          # 完整表结构
sql/camera_update.sql   # 升级脚本
```

### 文档文件
```
AI设置功能实现说明.md    # 详细实现文档
AI设置功能测试说明.md    # 测试指南
AI设置功能完成总结.md    # 功能总结
AI设置路由配置说明.md    # 路由配置详解
AI设置快速开始指南.md    # 本文档
```

## 🎯 核心功能

### 1. 人机非行为分析 (15项)
- 违法停车、违法倒车、货车高速
- 占用应急车道、违法变道、导流区
- 压实线、拥堵、收费站拥堵
- 超速、低速、事故
- 行人入侵、摩托车入侵、交通流量

### 2. 场景分析 (6项)
- 施工区域、遗留物、火灾
- 烟雾、花屏、事故

### 3. 偏移对照图查看
- 点击图片图标查看摄像头偏移情况
- 支持图片预览和放大

## 💡 使用技巧

### 快速配置
1. 勾选"人机非行为分析"启用整个功能模块
2. 点击右侧箭头展开详细配置
3. 勾选需要的检测项目
4. 配置自动保存，无需手动提交

### 批量查看
使用搜索框快速定位摄像头：
- 输入摄像头编号精确查找
- 输入名称关键字模糊查找

### 状态识别
- 🟢 绿色图标 = 摄像头在线
- 🔴 红色图标 = 摄像头离线
- "working" 标签 = AI功能已启用

## ⚠️ 注意事项

1. **区域绘制功能暂未实现**
   - 按钮已预留，点击显示提示
   - 待后续版本开发

2. **偏移对照图**
   - 需要在数据库中配置 background_snap 字段
   - 格式：完整的图片URL地址

3. **AI功能配置**
   - 配置以JSON格式存储在 ai_function 字段
   - 修改后自动保存，无需手动提交

4. **权限要求**
   - 需要 device:ai:list 权限查看
   - 需要 device:ai:edit 权限修改

## 🐛 常见问题

### Q: 页面打不开？
**A**: 检查路由配置和菜单权限

### Q: 配置保存失败？
**A**: 检查后端服务是否正常，查看浏览器控制台错误

### Q: 偏移对照图不显示？
**A**: 检查 background_snap 字段是否有值，图片URL是否可访问

### Q: 刷新后配置丢失？
**A**: 检查数据库连接，确认 ai_function 字段已更新

## 📞 获取帮助

遇到问题？查看详细文档：

1. **实现细节** → 查看《AI设置功能实现说明.md》
2. **测试指南** → 查看《AI设置功能测试说明.md》
3. **路由配置** → 查看《AI设置路由配置说明.md》
4. **功能总结** → 查看《AI设置功能完成总结.md》

## 🎉 开始使用

现在你已经准备好了！

1. ✅ 数据库已更新
2. ✅ 后端服务已重启
3. ✅ 路由已配置
4. ✅ 权限已分配

**立即登录系统，开始配置你的AI检测功能吧！** 🚀

---

**版本**: v1.0  
**更新时间**: 2025年10月23日  
**适用系统**: 若依框架 + Vue3 + Element Plus

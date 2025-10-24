# AI设置路由配置说明

## 前端路由配置

### 方式一：在路由文件中添加

找到前端路由配置文件（通常是 `yulo-ui/src/router/index.js`），添加以下路由配置：

```javascript
{
  path: '/ai-settings',
  component: Layout,
  hidden: false,
  children: [
    {
      path: 'index',
      component: () => import('@/views/AIsSettings/index'),
      name: 'AIsSettings',
      meta: { 
        title: 'AI设置', 
        icon: 'setting',
        noCache: true
      }
    }
  ]
}
```

### 方式二：通过后台菜单管理添加

如果系统支持动态菜单，可以在后台菜单管理中添加：

1. 登录系统管理后台
2. 进入 系统管理 -> 菜单管理
3. 点击"新增"按钮
4. 填写以下信息：

| 字段 | 值 |
|------|-----|
| 菜单名称 | AI设置 |
| 父菜单 | 设备管理（或根据实际情况选择） |
| 显示排序 | 2 |
| 路由地址 | ai-settings |
| 组件路径 | AIsSettings/index |
| 菜单类型 | 菜单 |
| 菜单图标 | setting |
| 是否外链 | 否 |
| 是否缓存 | 否 |
| 显示状态 | 显示 |
| 菜单状态 | 正常 |

5. 点击"确定"保存

## 数据库菜单配置（若依框架）

如果使用若依框架，可以执行以下SQL添加菜单：

```sql
-- 添加AI设置菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('AI设置', (SELECT menu_id FROM sys_menu WHERE menu_name = '设备管理' AND parent_id = 0), 2, 'ai-settings', 'AIsSettings/index', 1, 0, 'C', '0', '0', 'device:ai:list', 'setting', 'admin', sysdate(), '', NULL, 'AI设置菜单');

-- 获取刚插入的菜单ID
SET @parentId = LAST_INSERT_ID();

-- 添加按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('AI配置查询', @parentId, 1, '#', '', 1, 0, 'F', '0', '0', 'device:ai:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('AI配置修改', @parentId, 2, '#', '', 1, 0, 'F', '0', '0', 'device:ai:edit', '#', 'admin', sysdate(), '', NULL, '');
```

## 权限配置

### 1. 角色权限分配

在系统管理 -> 角色管理中，为需要访问AI设置的角色分配权限：

1. 找到目标角色（如：管理员、设备管理员）
2. 点击"修改"
3. 在菜单权限中勾选"AI设置"
4. 保存

### 2. 用户权限验证

确保用户具有以下权限之一：
- `device:ai:list` - 查看AI设置列表
- `device:ai:query` - 查询AI配置
- `device:ai:edit` - 修改AI配置

## 菜单图标说明

推荐使用的图标（Element Plus Icons）：

- `setting` - 设置图标（推荐）
- `tools` - 工具图标
- `cpu` - CPU图标（表示AI）
- `monitor` - 监控图标

如果使用自定义图标，需要在项目中引入对应的图标文件。

## 访问路径

配置完成后，可以通过以下路径访问：

- **开发环境**: `http://localhost:8080/#/ai-settings`
- **生产环境**: `http://your-domain.com/#/ai-settings`

## 面包屑导航

页面访问后，面包屑导航应显示为：

```
首页 / 设备管理 / AI设置
```

如果面包屑不正确，检查以下配置：

1. 父菜单是否正确设置
2. 路由 meta 信息是否完整
3. 面包屑组件是否正确引用

## 侧边栏菜单

配置完成后，侧边栏菜单应显示：

```
设备管理
  ├─ 摄像头管理
  └─ AI设置  ← 新增菜单
```

## 常见问题

### Q1: 菜单不显示？

**解决方案**:
1. 检查菜单状态是否为"正常"
2. 检查显示状态是否为"显示"
3. 检查用户角色是否有该菜单权限
4. 清除浏览器缓存，重新登录

### Q2: 点击菜单报404错误？

**解决方案**:
1. 检查组件路径是否正确：`AIsSettings/index`
2. 检查文件是否存在：`yulo-ui/src/views/AIsSettings/index.vue`
3. 检查路由配置是否正确

### Q3: 页面显示但功能不可用？

**解决方案**:
1. 检查后端接口是否正常
2. 检查数据库表结构是否已更新
3. 检查浏览器控制台是否有错误信息

### Q4: 权限不足无法访问？

**解决方案**:
1. 检查用户角色权限配置
2. 检查菜单权限标识是否正确
3. 联系管理员分配权限

## 测试验证

配置完成后，按以下步骤验证：

1. ✅ 登录系统
2. ✅ 在侧边栏找到"AI设置"菜单
3. ✅ 点击菜单，页面正常加载
4. ✅ 面包屑导航显示正确
5. ✅ 页面功能正常使用

## 配置检查清单

- [ ] 前端路由配置已添加
- [ ] 数据库菜单记录已插入
- [ ] 角色权限已分配
- [ ] 用户权限已验证
- [ ] 菜单图标显示正常
- [ ] 页面可以正常访问
- [ ] 功能测试通过

---

**配置完成后，请参考《AI设置功能测试说明.md》进行完整的功能测试。**

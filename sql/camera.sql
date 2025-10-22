-- ----------------------------
-- 摄像头表
-- ----------------------------
drop table if exists camera;
create table camera (
  id                bigint(20)      not null auto_increment    comment '主键ID',
  camera_code       varchar(50)     not null                   comment '摄像头编号',
  camera_name       varchar(100)    not null                   comment '摄像头名称',
  video_source      varchar(500)    not null                   comment '摄像头视频源',
  media_proxy       varchar(100)    not null                   comment '媒体代理',
  radar_code        varchar(50)     default null               comment '关联雷达编号',
  coordinates       varchar(200)    default null               comment '经纬度,偏北角',
  camera_info       text            default null               comment '摄像头属性信息',
  status            char(1)         default '1'                comment '状态（0离线 1在线）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime        default null               comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime        default null               comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (id),
  unique key uk_camera_code (camera_code)
) engine=innodb auto_increment=1 comment = '摄像头表';

-- ----------------------------
-- 初始化摄像头数据
-- ----------------------------
insert into camera values(1, '350000007132901001', 'AK10+720 A2 去向-枪机', 'rtsp://admin:HTcf@2022!@35.46.9.130:554/video2', 'rotanova_server_node', 'B02BM15620115948870287381', '119.3038766,25.8409609,159.6942999', '{"min_distance": 20, "max_distance": 200, "direction": "up", "over_speed_threshold":160, "low_speed_threshold": 40, "congestion_speed_threshold": 30, "congestion_thread_num": 10, "parking_speed_threshold": 2, "radar_enable": 1}', '1', 'admin', sysdate(), '', null, '摄像头测试数据');
insert into camera values(2, '350000007132901002', 'AK10+720 A3 去向-枪机', 'rtsp://admin:HTcf@2022!@35.46.9.130:554/video3', 'rotanova_server_node', 'B02BM15620115948870287382', '119.3038766,25.8409609,159.6942999', '{"min_distance": 20, "max_distance": 200, "direction": "up", "over_speed_threshold":160, "low_speed_threshold": 40}', '1', 'admin', sysdate(), '', null, '摄像头测试数据');
insert into camera values(3, '350000007132901003', 'AK10+720 A3 来向-枪机', 'rtsp://admin:HTcf@2022!@35.46.9.130:554/video4', 'rotanova_server_node', 'B02BM15620115948870287383', '119.3038766,25.8409609,159.6942999', '{"min_distance": 20, "max_distance": 200, "direction": "down"}', '1', 'admin', sysdate(), '', null, '摄像头测试数据');
-- ---
-------------------------
-- 菜单 SQL
-- ----------------------------
-- 父菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('设备管理', 0, 5, 'device', NULL, 1, 0, 'M', '0', '0', '', 'system', 'admin', sysdate(), '', NULL, '设备管理目录');

-- 子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('摄像头管理', (SELECT menu_id FROM sys_menu WHERE menu_name = '设备管理' AND parent_id = 0), 1, 'camera', 'deviceManage/camar/index', 1, 0, 'C', '0', '0', 'device:camera:list', 'monitor', 'admin', sysdate(), '', NULL, '摄像头管理菜单');

-- 按钮父菜单ID
SET @parentId = (SELECT menu_id FROM sys_menu WHERE menu_name = '摄像头管理' AND parent_id != 0);

-- 按钮 SQL
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('摄像头查询', @parentId, 1, '#', '', 1, 0, 'F', '0', '0', 'device:camera:query', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('摄像头新增', @parentId, 2, '#', '', 1, 0, 'F', '0', '0', 'device:camera:add', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('摄像头修改', @parentId, 3, '#', '', 1, 0, 'F', '0', '0', 'device:camera:edit', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('摄像头删除', @parentId, 4, '#', '', 1, 0, 'F', '0', '0', 'device:camera:remove', '#', 'admin', sysdate(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('摄像头导出', @parentId, 5, '#', '', 1, 0, 'F', '0', '0', 'device:camera:export', '#', 'admin', sysdate(), '', NULL, '');
-- ----------------------------
-- 摄像头表新增字段
-- ----------------------------

-- 添加 AI 检测配置字段
ALTER TABLE camera ADD COLUMN ai_function text DEFAULT NULL COMMENT 'AI 检测配置';

-- 添加区域绘制范围数据字段
ALTER TABLE camera ADD COLUMN regions text DEFAULT NULL COMMENT '区域绘制范围数据';

-- 添加摄像头直播地址字段
ALTER TABLE camera ADD COLUMN camera_url varchar(255) DEFAULT NULL COMMENT '摄像头直播地址';

-- 添加偏移对照图字段
ALTER TABLE camera ADD COLUMN background_snap varchar(255) DEFAULT NULL COMMENT '偏移对照图';

-- 添加车道信息字段
ALTER TABLE camera ADD COLUMN lanes text DEFAULT NULL COMMENT '车道信息';

-- 更新示例数据
UPDATE camera SET 
  ai_function = '{"carPersonMotor":{"enable":true,"attributes":{"parking":true,"reverse":true,"truckHighSpeed":false,"emergencyLane":false,"changeLane":false,"guideArea":false,"crossSolidLine":false,"congestion":true,"congestionToll":false,"overSpeed":false,"lowSpeed":true,"accident":false,"personInvasion":true,"motorbikeInvasion":false,"trafficFlow":true}},"scene":{"enable":true,"attributes":{"constructionArea":true,"remnants":true,"fire":true,"smoke":true,"flowerScreen":false,"accident":true}}}',
  camera_url = 'http://35.46.5.76:8080/live/350000007132901001.live.mp4',
  background_snap = 'http://35.46.5.88:19000/camera-background-snap/35000000071329010001.jpg'
WHERE camera_code = '350000007132901001';

UPDATE camera SET 
  ai_function = '{"carPersonMotor":{"enable":false,"attributes":{}},"scene":{"enable":false,"attributes":{}}}',
  camera_url = CONCAT('http://35.46.5.76:8080/live/', camera_code, '.live.mp4')
WHERE camera_code IN ('350000007132901002', '350000007132901003');

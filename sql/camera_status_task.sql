-- 摄像头状态检查定时任务配置
-- 每5分钟执行一次摄像头状态检查

-- 注意：需要先确保sys_job表存在，这是若依框架的定时任务配置表
-- 如果使用的是Quartz调度，需要通过系统管理-定时任务菜单添加

-- 定时任务配置说明：
-- 任务名称：摄像头状态检查
-- 任务组名：DEFAULT
-- 调用目标字符串：cameraStatusTask.checkAllCameraStatus
-- cron表达式：0 0/5 * * * ? (每5分钟执行一次)
-- 执行策略：立即执行
-- 是否并发：否
-- 状态：正常

-- 如果sys_job表存在，可以使用以下SQL插入：
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
VALUES 
('摄像头状态检查', 'DEFAULT', 'cameraStatusTask.checkAllCameraStatus', '0 0/5 * * * ?', '1', '0', '0', 'admin', sysdate(), '每5分钟检查一次所有摄像头的在线状态');

-- cron表达式说明：
-- 0 0/5 * * * ?  表示每5分钟执行一次
-- 0 0/10 * * * ? 表示每10分钟执行一次
-- 0 0/30 * * * ? 表示每30分钟执行一次
-- 0 0 * * * ?    表示每小时执行一次
-- 0 0 0/2 * * ?  表示每2小时执行一次

-- 可以根据实际需求调整cron表达式

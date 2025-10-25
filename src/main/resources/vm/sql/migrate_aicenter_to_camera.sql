-- ===================================================================
-- 数据迁移脚本: aicenter -> camera
-- 说明: 将 aicenter 表中的数据迁移到 camera 表
-- 创建时间: 2025-10-24
-- ===================================================================

-- 字段映射关系:
-- aicenter.camera_id        -> camera.camera_code
-- aicenter.camera_name      -> camera.camera_name
-- aicenter.camera_url       -> camera.camera_url (直播地址)
-- aicenter.src_url          -> camera.video_source (视频源)
-- aicenter.media_worker     -> camera.media_proxy
-- aicenter.radar_code       -> camera.radar_code
-- aicenter.location         -> camera.coordinates
-- aicenter.attributes       -> camera.camera_info
-- aicenter.ai_function      -> camera.ai_function
-- aicenter.regions          -> camera.regions
-- aicenter.background_snap  -> camera.background_snap
-- aicenter.lanes            -> camera.lanes

-- 开始迁移数据
INSERT INTO camera (
    camera_code,
    camera_name,
    video_source,
    media_proxy,
    radar_code,
    coordinates,
    camera_info,
    status,
    create_by,
    create_time,
    update_by,
    update_time,
    remark,
    ai_function,
    regions,
    camera_url,
    background_snap,
    lanes
)
SELECT 
    a.camera_id AS camera_code,
    a.camera_name,
    COALESCE(a.src_url, '') AS video_source,
    COALESCE(a.media_worker, '') AS media_proxy,
    a.radar_code,
    a.location AS coordinates,
    a.attributes AS camera_info,
    '1' AS status,
    'admin' AS create_by,
    NOW() AS create_time,
    '' AS update_by,
    NULL AS update_time,
    '从 aicenter 表迁移' AS remark,
    a.ai_function,
    a.regions,
    a.camera_url,
    a.background_snap,
    a.lanes
FROM aicenter a
WHERE NOT EXISTS (
    SELECT 1 FROM camera c WHERE c.camera_code COLLATE utf8mb4_unicode_ci = a.camera_id
);

-- 显示迁移结果
SELECT 
    COUNT(*) AS total_migrated,
    '数据迁移完成' AS message
FROM camera 
WHERE remark = '从 aicenter 表迁移';

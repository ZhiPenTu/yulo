package com.ruoyi.project.device.mapper;

import java.util.List;
import com.ruoyi.project.device.domain.Camera;

/**
 * 摄像头Mapper接口
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public interface CameraMapper 
{
    /**
     * 查询摄像头
     * 
     * @param id 摄像头主键
     * @return 摄像头
     */
    public Camera selectCameraById(Long id);

    /**
     * 查询摄像头列表
     * 
     * @param camera 摄像头
     * @return 摄像头集合
     */
    public List<Camera> selectCameraList(Camera camera);

    /**
     * 新增摄像头
     * 
     * @param camera 摄像头
     * @return 结果
     */
    public int insertCamera(Camera camera);

    /**
     * 修改摄像头
     * 
     * @param camera 摄像头
     * @return 结果
     */
    public int updateCamera(Camera camera);

    /**
     * 删除摄像头
     * 
     * @param id 摄像头主键
     * @return 结果
     */
    public int deleteCameraById(Long id);

    /**
     * 批量删除摄像头
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCameraByIds(Long[] ids);

    /**
     * 校验摄像头编号是否唯一
     * 
     * @param cameraCode 摄像头编号
     * @return 结果
     */
    public Camera checkCameraCodeUnique(String cameraCode);
}
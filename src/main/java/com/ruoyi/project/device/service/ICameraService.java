package com.ruoyi.project.device.service;

import java.util.List;
import com.ruoyi.project.device.domain.Camera;

/**
 * 摄像头Service接口
 *
 * @author ruoyi
 * @date 2025-10-21
 */
public interface ICameraService
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
     * 批量删除摄像头
     *
     * @param ids 需要删除的摄像头主键集合
     * @return 结果
     */
    public int deleteCameraByIds(Long[] ids);

    /**
     * 删除摄像头信息
     *
     * @param id 摄像头主键
     * @return 结果
     */
    public int deleteCameraById(Long id);

    /**
     * 校验摄像头编号是否唯一
     *
     * @param camera 摄像头信息
     * @return 结果
     */
    public boolean checkCameraCodeUnique(Camera camera);
}

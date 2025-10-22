package com.ruoyi.project.device.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.constant.UserConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.device.mapper.CameraMapper;
import com.ruoyi.project.device.domain.Camera;
import com.ruoyi.project.device.service.ICameraService;

/**
 * 摄像头Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-21
 */
@Service
public class CameraServiceImpl implements ICameraService
{
    @Autowired
    private CameraMapper cameraMapper;

    /**
     * 查询摄像头
     *
     * @param id 摄像头主键
     * @return 摄像头
     */
    @Override
    public Camera selectCameraById(Long id)
    {
        return cameraMapper.selectCameraById(id);
    }

    /**
     * 查询摄像头列表
     *
     * @param camera 摄像头
     * @return 摄像头
     */
    @Override
    public List<Camera> selectCameraList(Camera camera)
    {
        return cameraMapper.selectCameraList(camera);
    }

    /**
     * 新增摄像头
     *
     * @param camera 摄像头
     * @return 结果
     */
    @Override
    public int insertCamera(Camera camera)
    {
        camera.setCreateTime(DateUtils.getNowDate());
        return cameraMapper.insertCamera(camera);
    }

    /**
     * 修改摄像头
     *
     * @param camera 摄像头
     * @return 结果
     */
    @Override
    public int updateCamera(Camera camera)
    {
        camera.setUpdateTime(DateUtils.getNowDate());
        return cameraMapper.updateCamera(camera);
    }

    /**
     * 批量删除摄像头
     *
     * @param ids 需要删除的摄像头主键
     * @return 结果
     */
    @Override
    public int deleteCameraByIds(Long[] ids)
    {
        return cameraMapper.deleteCameraByIds(ids);
    }

    /**
     * 删除摄像头信息
     *
     * @param id 摄像头主键
     * @return 结果
     */
    @Override
    public int deleteCameraById(Long id)
    {
        return cameraMapper.deleteCameraById(id);
    }

    /**
     * 校验摄像头编号是否唯一
     *
     * @param camera 摄像头信息
     * @return 结果
     */
    @Override
    public boolean checkCameraCodeUnique(Camera camera)
    {
        Long cameraId = StringUtils.isNull(camera.getId()) ? -1L : camera.getId();
        Camera info = cameraMapper.checkCameraCodeUnique(camera.getCameraCode());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != cameraId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}

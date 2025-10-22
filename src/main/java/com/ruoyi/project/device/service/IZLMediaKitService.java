package com.ruoyi.project.device.service;

import com.ruoyi.project.device.domain.dto.StreamRequestDto;
import com.ruoyi.project.device.domain.dto.StreamStatusDto;

/**
 * ZLMediaKit媒体服务接口
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public interface IZLMediaKitService
{
    /**
     * 开始拉流
     * 
     * @param streamRequest 拉流请求参数
     * @return 操作结果
     */
    boolean startStream(StreamRequestDto streamRequest);

    /**
     * 停止拉流
     * 
     * @param streamRequest 拉流请求参数
     * @return 操作结果
     */
    boolean stopStream(StreamRequestDto streamRequest);

    /**
     * 查询拉流状态
     * 
     * @param cameraCode 摄像头编号
     * @return 拉流状态
     */
    StreamStatusDto getStreamStatus(String cameraCode);
}
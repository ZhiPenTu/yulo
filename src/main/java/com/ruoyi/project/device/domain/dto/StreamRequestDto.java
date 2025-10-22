package com.ruoyi.project.device.domain.dto;

/**
 * 拉流请求DTO
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public class StreamRequestDto
{
    /** 摄像头编号 */
    private String cameraCode;

    /** 视频源地址 */
    private String videoSource;

    /** 媒体代理 */
    private String mediaProxy;

    public StreamRequestDto() {}

    public StreamRequestDto(String cameraCode, String videoSource, String mediaProxy) {
        this.cameraCode = cameraCode;
        this.videoSource = videoSource;
        this.mediaProxy = mediaProxy;
    }

    public String getCameraCode() {
        return cameraCode;
    }

    public void setCameraCode(String cameraCode) {
        this.cameraCode = cameraCode;
    }

    public String getVideoSource() {
        return videoSource;
    }

    public void setVideoSource(String videoSource) {
        this.videoSource = videoSource;
    }

    public String getMediaProxy() {
        return mediaProxy;
    }

    public void setMediaProxy(String mediaProxy) {
        this.mediaProxy = mediaProxy;
    }

    @Override
    public String toString() {
        return "StreamRequestDto{" +
                "cameraCode='" + cameraCode + '\'' +
                ", videoSource='" + videoSource + '\'' +
                ", mediaProxy='" + mediaProxy + '\'' +
                '}';
    }
}
package com.ruoyi.project.device.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.framework.aspectj.lang.annotation.Excel;
import com.ruoyi.framework.web.domain.BaseEntity;

/**
 * 摄像头对象 camera
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public class Camera extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 摄像头编号 */
    @Excel(name = "摄像头编号")
    private String cameraCode;

    /** 摄像头名称 */
    @Excel(name = "摄像头名称")
    private String cameraName;

    /** 摄像头视频源 */
    @Excel(name = "摄像头视频源")
    private String videoSource;

    /** 媒体代理 */
    @Excel(name = "媒体代理")
    private String mediaProxy;

    /** 关联雷达编号 */
    @Excel(name = "关联雷达编号")
    private String radarCode;

    /** 经纬度,偏北角 */
    @Excel(name = "经纬度,偏北角")
    private String coordinates;

    /** 摄像头属性信息 */
    @Excel(name = "摄像头属性信息")
    private String cameraInfo;

    /** 状态（0离线 1在线） */
    @Excel(name = "状态", readConverterExp = "0=离线,1=在线")
    private String status;

    /** AI 检测配置 */
    @Excel(name = "AI 检测配置")
    private String aiFunction;

    /** 区域绘制范围数据 */
    @Excel(name = "区域绘制范围数据")
    private String regions;

    /** 摄像头直播地址 */
    @Excel(name = "摄像头直播地址")
    private String cameraUrl;

    /** 偏移对照图 */
    @Excel(name = "偏移对照图")
    private String backgroundSnap;

    /** 车道信息 */
    @Excel(name = "车道信息")
    private String lanes;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setCameraCode(String cameraCode) 
    {
        this.cameraCode = cameraCode;
    }

    public String getCameraCode() 
    {
        return cameraCode;
    }
    public void setCameraName(String cameraName) 
    {
        this.cameraName = cameraName;
    }

    public String getCameraName() 
    {
        return cameraName;
    }
    public void setVideoSource(String videoSource) 
    {
        this.videoSource = videoSource;
    }

    public String getVideoSource() 
    {
        return videoSource;
    }
    public void setMediaProxy(String mediaProxy) 
    {
        this.mediaProxy = mediaProxy;
    }

    public String getMediaProxy() 
    {
        return mediaProxy;
    }
    public void setRadarCode(String radarCode) 
    {
        this.radarCode = radarCode;
    }

    public String getRadarCode() 
    {
        return radarCode;
    }
    public void setCoordinates(String coordinates) 
    {
        this.coordinates = coordinates;
    }

    public String getCoordinates() 
    {
        return coordinates;
    }
    public void setCameraInfo(String cameraInfo) 
    {
        this.cameraInfo = cameraInfo;
    }

    public String getCameraInfo() 
    {
        return cameraInfo;
    }
    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setAiFunction(String aiFunction) 
    {
        this.aiFunction = aiFunction;
    }

    public String getAiFunction() 
    {
        return aiFunction;
    }

    public void setRegions(String regions) 
    {
        this.regions = regions;
    }

    public String getRegions() 
    {
        return regions;
    }

    public void setCameraUrl(String cameraUrl) 
    {
        this.cameraUrl = cameraUrl;
    }

    public String getCameraUrl() 
    {
        return cameraUrl;
    }

    public void setBackgroundSnap(String backgroundSnap) 
    {
        this.backgroundSnap = backgroundSnap;
    }

    public String getBackgroundSnap() 
    {
        return backgroundSnap;
    }

    public void setLanes(String lanes) 
    {
        this.lanes = lanes;
    }

    public String getLanes() 
    {
        return lanes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("cameraCode", getCameraCode())
            .append("cameraName", getCameraName())
            .append("videoSource", getVideoSource())
            .append("mediaProxy", getMediaProxy())
            .append("radarCode", getRadarCode())
            .append("coordinates", getCoordinates())
            .append("cameraInfo", getCameraInfo())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("aiFunction", getAiFunction())
            .append("regions", getRegions())
            .append("cameraUrl", getCameraUrl())
            .append("backgroundSnap", getBackgroundSnap())
            .append("lanes", getLanes())
            .toString();
    }
}
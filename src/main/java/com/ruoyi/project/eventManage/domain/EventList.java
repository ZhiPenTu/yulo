package com.ruoyi.project.eventManage.domain;

import java.util.Date;

/**
 * 事件图片实体，对应表 event_list
 */
public class EventList {
    private String eventCode;
    private String eventType;
    private String eventPicurl;
    private String eventDetail;
    private Date startTime;
    private Date endTime;
    private String linkCameraId;
    private Integer mecEventId;
    private String workerId;
    private Integer falseReport;
    private String videoUrl;
    private String eventPicurlGallery;
    private String location;
    private Integer triggerType;
    private String reportDec;

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getEventPicurl() { return eventPicurl; }
    public void setEventPicurl(String eventPicurl) { this.eventPicurl = eventPicurl; }

    public String getEventDetail() { return eventDetail; }
    public void setEventDetail(String eventDetail) { this.eventDetail = eventDetail; }

    public Date getStartTime() { return startTime; }
    public void setStartTime(Date startTime) { this.startTime = startTime; }

    public Date getEndTime() { return endTime; }
    public void setEndTime(Date endTime) { this.endTime = endTime; }

    public String getLinkCameraId() { return linkCameraId; }
    public void setLinkCameraId(String linkCameraId) { this.linkCameraId = linkCameraId; }

    public Integer getMecEventId() { return mecEventId; }
    public void setMecEventId(Integer mecEventId) { this.mecEventId = mecEventId; }

    public String getWorkerId() { return workerId; }
    public void setWorkerId(String workerId) { this.workerId = workerId; }

    public Integer getFalseReport() { return falseReport; }
    public void setFalseReport(Integer falseReport) { this.falseReport = falseReport; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getEventPicurlGallery() { return eventPicurlGallery; }
    public void setEventPicurlGallery(String eventPicurlGallery) { this.eventPicurlGallery = eventPicurlGallery; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getTriggerType() { return triggerType; }
    public void setTriggerType(Integer triggerType) { this.triggerType = triggerType; }

    public String getReportDec() { return reportDec; }
    public void setReportDec(String reportDec) { this.reportDec = reportDec; }
}
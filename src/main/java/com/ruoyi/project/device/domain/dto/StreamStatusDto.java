package com.ruoyi.project.device.domain.dto;

/**
 * 拉流状态响应DTO
 * 
 * @author ruoyi
 * @date 2025-10-21
 */
public class StreamStatusDto
{
    /** 拉流状态 */
    private String status;

    /** 流ID */
    private String streamId;

    /** 状态描述 */
    private String message;

    /** 拉流地址 */
    private String streamUrl;

    public StreamStatusDto() {}

    public StreamStatusDto(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public void setStreamUrl(String streamUrl) {
        this.streamUrl = streamUrl;
    }

    @Override
    public String toString() {
        return "StreamStatusDto{" +
                "status='" + status + '\'' +
                ", streamId='" + streamId + '\'' +
                ", message='" + message + '\'' +
                ", streamUrl='" + streamUrl + '\'' +
                '}';
    }
}
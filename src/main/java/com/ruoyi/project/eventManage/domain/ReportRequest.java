package com.ruoyi.project.eventManage.domain;

import java.util.List;

/**
 * 误报信息提交请求
 */
public class ReportRequest {
    private String eventCode;
    private List<String> reportDec;

    public String getEventCode() { return eventCode; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }

    public List<String> getReportDec() { return reportDec; }
    public void setReportDec(List<String> reportDec) { this.reportDec = reportDec; }
}
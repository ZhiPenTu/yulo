package com.ruoyi.project.eventManage.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.project.eventManage.domain.EventList;

public interface IEventListService {
    String uploadImage(MultipartFile file) throws Exception;
    int saveEvent(EventList event);
    List<EventList> list(EventList query);
    int submitReport(String eventCode, String reportDec);
}
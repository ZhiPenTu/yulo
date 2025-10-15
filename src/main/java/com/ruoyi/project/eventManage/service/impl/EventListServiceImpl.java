package com.ruoyi.project.eventManage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.framework.config.RuoYiConfig;
import com.ruoyi.project.eventManage.domain.EventList;
import com.ruoyi.project.eventManage.mapper.EventListMapper;
import com.ruoyi.project.eventManage.service.IEventListService;

@Service
public class EventListServiceImpl implements IEventListService {

    @Autowired
    private EventListMapper eventListMapper;

    @Override
    public String uploadImage(MultipartFile file) throws Exception {
        String uploadDir = RuoYiConfig.getUploadPath();
        return FileUploadUtils.upload(uploadDir, file);
    }

    @Override
    public int saveEvent(EventList event) {
        if (eventListMapper.selectByCode(event.getEventCode()) == null) {
            return eventListMapper.insert(event);
        } else {
            return eventListMapper.update(event);
        }
    }

    @Override
    public List<EventList> list(EventList query) {
        return eventListMapper.selectList(query);
    }

    @Override
    public int submitReport(String eventCode, String reportDec) {
        EventList event = new EventList();
        event.setEventCode(eventCode);
        event.setReportDec(reportDec);
        return eventListMapper.update(event);
    }
}
package com.ruoyi.project.eventManage.mapper;

import java.util.List;
import com.ruoyi.project.eventManage.domain.EventList;

public interface EventListMapper {
    int insert(EventList event);
    int update(EventList event);
    EventList selectByCode(String eventCode);
    List<EventList> selectList(EventList query);
}
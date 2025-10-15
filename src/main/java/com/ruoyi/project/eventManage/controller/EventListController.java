package com.ruoyi.project.eventManage.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.project.eventManage.domain.EventList;
import com.ruoyi.project.eventManage.service.IEventListService;
import com.ruoyi.project.eventManage.domain.ReportRequest;

@RestController
@RequestMapping("/eventManage/event")
public class EventListController extends BaseController {

    @Autowired
    private IEventListService eventListService;

    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) throws Exception {
        String url = eventListService.uploadImage(file);
        return AjaxResult.success("上传成功", url);
    }

    @PostMapping("/save")
    public AjaxResult save(@RequestBody EventList event) {
        int rows = eventListService.saveEvent(event);
        return toAjax(rows);
    }

    @GetMapping("/list")
    public TableDataInfo list(EventList query) {
        startPage();
        List<EventList> list = eventListService.list(query);
        return getDataTable(list);
    }

    /**
     * 提交误报信息（多选类型）
     */
    @PostMapping("/report")
    public AjaxResult report(@RequestBody ReportRequest req) {
        String reportDecStr = (req.getReportDec() == null || req.getReportDec().isEmpty())
                ? null
                : String.join(",", req.getReportDec());
        int rows = eventListService.submitReport(req.getEventCode(), reportDecStr);
        return toAjax(rows);
    }
}
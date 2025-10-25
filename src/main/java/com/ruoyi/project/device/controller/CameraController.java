package com.ruoyi.project.device.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.framework.aspectj.lang.annotation.Log;
import com.ruoyi.framework.aspectj.lang.enums.BusinessType;
import com.ruoyi.project.device.domain.Camera;
import com.ruoyi.project.device.service.ICameraService;
import com.ruoyi.framework.web.controller.BaseController;
import com.ruoyi.framework.web.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.web.page.TableDataInfo;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.project.device.domain.dto.StreamRequestDto;
import com.ruoyi.project.device.domain.dto.StreamStatusDto;
import com.ruoyi.project.device.service.IZLMediaKitService;

/**
 * 摄像头Controller
 *
 * @author ruoyi
 * @date 2025-10-21
 */
@RestController
@RequestMapping("/device/camera")
public class CameraController extends BaseController
{
    @Autowired
    private ICameraService cameraService;

    @Autowired
    private IZLMediaKitService zlMediaKitService;

    /**
     * 查询摄像头列表
     */
    @PreAuthorize("@ss.hasPermi('device:camera:list')")
    @GetMapping("/list")
    public TableDataInfo list(Camera camera)
    {
        startPage();
        List<Camera> list = cameraService.selectCameraList(camera);
        return getDataTable(list);
    }

    /**
     * 导出摄像头列表
     */
    @PreAuthorize("@ss.hasPermi('device:camera:export')")
    @Log(title = "摄像头", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Camera camera)
    {
        List<Camera> list = cameraService.selectCameraList(camera);
        ExcelUtil<Camera> util = new ExcelUtil<Camera>(Camera.class);
        util.exportExcel(response, list, "摄像头数据");
    }

    /**
     * 获取摄像头详细信息
     */
    @PreAuthorize("@ss.hasPermi('device:camera:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(cameraService.selectCameraById(id));
    }

    /**
     * 新增摄像头
     */
    @PreAuthorize("@ss.hasPermi('device:camera:add')")
    @Log(title = "摄像头", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Camera camera)
    {
        if (UserConstants.NOT_UNIQUE == cameraService.checkCameraCodeUnique(camera))
        {
            return AjaxResult.error("新增摄像头'" + camera.getCameraName() + "'失败，摄像头编号已存在");
        }
        return toAjax(cameraService.insertCamera(camera));
    }

    /**
     * 修改摄像头
     */
    @PreAuthorize("@ss.hasPermi('device:camera:edit')")
    @Log(title = "摄像头", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Camera camera)
    {
        if (UserConstants.NOT_UNIQUE == cameraService.checkCameraCodeUnique(camera))
        {
            return AjaxResult.error("修改摄像头'" + camera.getCameraName() + "'失败，摄像头编号已存在");
        }
        return toAjax(cameraService.updateCamera(camera));
    }

    /**
     * 删除摄像头
     */
    @PreAuthorize("@ss.hasPermi('device:camera:remove')")
    @Log(title = "摄像头", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(cameraService.deleteCameraByIds(ids));
    }

    /**
     * 开始拉流
     */
    @PreAuthorize("@ss.hasPermi('device:camera:stream')")
    @Log(title = "摄像头拉流", businessType = BusinessType.OTHER)
    @PostMapping("/startStream")
    public AjaxResult startStream(@RequestBody StreamRequestDto streamRequest)
    {
        if (streamRequest.getCameraCode() == null || streamRequest.getVideoSource() == null) {
            return AjaxResult.error("摄像头编号和视频源不能为空");
        }
        
        boolean result = zlMediaKitService.startStream(streamRequest);
        if (result) {
            return AjaxResult.success("开始拉流成功");
        } else {
            return AjaxResult.error("开始拉流失败");
        }
    }

    /**
     * 停止拉流
     */
    @PreAuthorize("@ss.hasPermi('device:camera:stream')")
    @Log(title = "摄像头拉流", businessType = BusinessType.OTHER)
    @PostMapping("/stopStream")
    public AjaxResult stopStream(@RequestBody StreamRequestDto streamRequest)
    {
        if (streamRequest.getCameraCode() == null) {
            return AjaxResult.error("摄像头编号不能为空");
        }
        
        boolean result = zlMediaKitService.stopStream(streamRequest);
        if (result) {
            return AjaxResult.success("停止拉流成功");
        } else {
            return AjaxResult.error("停止拉流失败");
        }
    }

    /**
     * 查询拉流状态
     */
    @PreAuthorize("@ss.hasPermi('device:camera:query')")
    @GetMapping("/streamStatus/{cameraCode}")
    public AjaxResult getStreamStatus(@PathVariable("cameraCode") String cameraCode)
    {
        StreamStatusDto status = zlMediaKitService.getStreamStatus(cameraCode);
        return AjaxResult.success(status);
    }

    /**
     * 检查摄像头在线状态
     */
    @PreAuthorize("@ss.hasPermi('device:camera:query')")
    @GetMapping("/checkOnlineStatus/{id}")
    public AjaxResult checkOnlineStatus(@PathVariable("id") Long id)
    {
        Camera camera = cameraService.selectCameraById(id);
        if (camera == null) {
            return AjaxResult.error("摄像头不存在");
        }
        
        boolean isOnline = cameraService.checkCameraOnlineStatus(camera);
        return AjaxResult.success(isOnline);
    }

    /**
     * 批量检查摄像头在线状态
     */
    @PreAuthorize("@ss.hasPermi('device:camera:query')")
    @PostMapping("/batchCheckOnlineStatus")
    public AjaxResult batchCheckOnlineStatus(@RequestBody Long[] ids)
    {
        return AjaxResult.success(cameraService.batchCheckOnlineStatus(ids));
    }
}

package com.ruoyi.project.device.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.constant.UserConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.device.mapper.CameraMapper;
import com.ruoyi.project.device.domain.Camera;
import com.ruoyi.project.device.service.ICameraService;

/**
 * 摄像头Service业务层处理
 *
 * @author ruoyi
 * @date 2025-10-21
 */
@Service
public class CameraServiceImpl implements ICameraService
{
    @Autowired
    private CameraMapper cameraMapper;

    /**
     * 查询摄像头
     *
     * @param id 摄像头主键
     * @return 摄像头
     */
    @Override
    public Camera selectCameraById(Long id)
    {
        return cameraMapper.selectCameraById(id);
    }

    /**
     * 查询摄像头列表
     *
     * @param camera 摄像头
     * @return 摄像头
     */
    @Override
    public List<Camera> selectCameraList(Camera camera)
    {
        return cameraMapper.selectCameraList(camera);
    }

    /**
     * 新增摄像头
     *
     * @param camera 摄像头
     * @return 结果
     */
    @Override
    public int insertCamera(Camera camera)
    {
        camera.setCreateTime(DateUtils.getNowDate());
        return cameraMapper.insertCamera(camera);
    }

    /**
     * 修改摄像头
     *
     * @param camera 摄像头
     * @return 结果
     */
    @Override
    public int updateCamera(Camera camera)
    {
        camera.setUpdateTime(DateUtils.getNowDate());
        return cameraMapper.updateCamera(camera);
    }

    /**
     * 批量删除摄像头
     *
     * @param ids 需要删除的摄像头主键
     * @return 结果
     */
    @Override
    public int deleteCameraByIds(Long[] ids)
    {
        return cameraMapper.deleteCameraByIds(ids);
    }

    /**
     * 删除摄像头信息
     *
     * @param id 摄像头主键
     * @return 结果
     */
    @Override
    public int deleteCameraById(Long id)
    {
        return cameraMapper.deleteCameraById(id);
    }

    /**
     * 校验摄像头编号是否唯一
     *
     * @param camera 摄像头信息
     * @return 结果
     */
    @Override
    public boolean checkCameraCodeUnique(Camera camera)
    {
        Long cameraId = StringUtils.isNull(camera.getId()) ? -1L : camera.getId();
        Camera info = cameraMapper.checkCameraCodeUnique(camera.getCameraCode());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != cameraId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 检查摄像头在线状态
     *
     * @param camera 摄像头信息
     * @return 是否在线
     */
    @Override
    public boolean checkCameraOnlineStatus(Camera camera)
    {
        if (camera == null || StringUtils.isEmpty(camera.getVideoSource()))
        {
            return false;
        }

        String ip = extractIpFromVideoSource(camera.getVideoSource());
        if (StringUtils.isEmpty(ip))
        {
            return false;
        }

        return pingHost(ip);
    }

    /**
     * 批量检查摄像头在线状态（并发优化版本）
     *
     * @param ids 摄像头ID数组
     * @return 在线状态Map
     */
    @Override
    public java.util.Map<Long, Boolean> batchCheckOnlineStatus(Long[] ids)
    {
        java.util.Map<Long, Boolean> statusMap = new java.util.concurrent.ConcurrentHashMap<>();

        if (ids == null || ids.length == 0)
        {
            return statusMap;
        }

        // 使用并行流提高批量检测性能
        java.util.Arrays.stream(ids)
            .parallel()
            .forEach(id -> {
                try
                {
                    Camera camera = selectCameraById(id);
                    if (camera != null)
                    {
                        boolean isOnline = checkCameraOnlineStatus(camera);
                        statusMap.put(id, isOnline);

                        // 更新数据库中的状态
                        if ((isOnline && !"1".equals(camera.getStatus())) ||
                            (!isOnline && !"0".equals(camera.getStatus())))
                        {
                            camera.setStatus(isOnline ? "1" : "0");
                            updateCamera(camera);
                        }
                    }
                }
                catch (Exception e)
                {
                    // 记录错误但不中断其他检测
                    statusMap.put(id, false);
                }
            });

        return statusMap;
    }

    /**
     * 从视频源URL中提取IP地址
     *
     * @param videoSource 视频源URL
     * @return IP地址
     */
    private String extractIpFromVideoSource(String videoSource)
    {
        if (StringUtils.isEmpty(videoSource))
        {
            return null;
        }

        try
        {
            // 支持多种格式：
            // 1. rtsp://192.168.1.100:554/stream
            // 2. http://192.168.1.100/stream
            // 3. rtsp://admin:password@192.168.1.100:554/stream (带用户名密码)
            // 4. rtsp://admin:HTcf@YNpj2@35.46.9.135:554/video2 (密码中包含@符号)
            
            // 方法：先匹配协议，然后找到最后一个@符号后面的IP地址
            // 如果没有@符号，则直接匹配协议后的IP地址
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?:rtsp|http|https)://(?:.*@)?([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
            java.util.regex.Matcher matcher = pattern.matcher(videoSource);

            if (matcher.find())
            {
                return matcher.group(1);
            }
            
            // 如果上面的正则没匹配到，尝试更宽松的匹配（处理特殊情况）
            // 直接查找IP地址模式
            pattern = java.util.regex.Pattern.compile(
                "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
            matcher = pattern.matcher(videoSource);
            
            if (matcher.find())
            {
                String ip = matcher.group(1);
                // 验证IP地址的有效性
                String[] parts = ip.split("\\.");
                for (String part : parts)
                {
                    int num = Integer.parseInt(part);
                    if (num < 0 || num > 255)
                    {
                        return null;
                    }
                }
                return ip;
            }
        }
        catch (Exception e)
        {
            // 记录错误但不抛出异常
            System.err.println("提取IP地址失败: " + videoSource + ", 错误: " + e.getMessage());
        }

        return null;
    }

    /**
     * Ping主机检查是否在线
     *
     * @param ip IP地址
     * @return 是否在线
     */
    private boolean pingHost(String ip)
    {
        if (StringUtils.isEmpty(ip))
        {
            return false;
        }

        try
        {
            java.net.InetAddress address = java.net.InetAddress.getByName(ip);
            // 超时时间设置为1.5秒，提高检测效率
//            boolean reachable = address.isReachable(1500);
//
//            if (!reachable)
//            {
                // 如果第一次失败，尝试使用系统 ping 命令（某些系统需要管理员权限才能使用 ICMP）
                return pingWithCommand(ip);
//            }

//            return true;
        }
        catch (Exception e)
        {
            // 如果 Java ping 失败，尝试系统命令
            return pingWithCommand(ip);
        }
    }

    /**
     * 使用系统命令 ping 主机
     *
     * @param ip IP地址
     * @return 是否在线
     */
    private boolean pingWithCommand(String ip)
    {
        Process process = null;
        try
        {
            String os = System.getProperty("os.name").toLowerCase();
            String command;

            if (os.contains("win"))
            {
                // Windows: ping -n 1 -w 800 IP (减少超时时间到800ms)
                command = "ping -n 1 -w 800 " + ip;
            }
            else
            {
                // Linux/Mac: ping -c 1 -W 1 IP
                command = "ping -c 1 -W 1 " + ip;
            }

            process = Runtime.getRuntime().exec(command);
            
            // 设置超时等待2秒，避免进程hang住
            boolean finished = process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS);
            
            if (!finished)
            {
                // 超时则强制终止进程
                process.destroyForcibly();
                return false;
            }
            
            int exitCode = process.exitValue();
            return exitCode == 0;
        }
        catch (Exception e)
        {
            return false;
        }
        finally
        {
            // 确保进程被清理
            if (process != null && process.isAlive())
            {
                process.destroyForcibly();
            }
        }
    }
}

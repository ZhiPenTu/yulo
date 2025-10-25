package com.ruoyi.framework.task;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.project.device.domain.Camera;
import com.ruoyi.project.device.service.ICameraService;

/**
 * 摄像头状态检查定时任务
 * 
 * @author ruoyi
 */
@Component("cameraStatusTask")
public class CameraStatusTask
{
    private static final Logger log = LoggerFactory.getLogger(CameraStatusTask.class);

    @Autowired
    private ICameraService cameraService;

    // 线程池大小，根据实际情况调整（增加到20以提高并发度）
    private static final int THREAD_POOL_SIZE = 20;
    
    // 超时时间（秒）- 增加到300秒（5分钟）以确保所有摄像头都能检测完成
    private static final int TIMEOUT_SECONDS = 300;
    
    // 线程池关闭等待时间（秒）
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 30;

    /**
     * 检查所有摄像头的在线状态（并发批量检测）
     */
    public void checkAllCameraStatus()
    {
        long startTime = System.currentTimeMillis();
        log.info("========== 开始执行摄像头状态检查定时任务 ==========");
        
        ExecutorService executorService = null;
        
        try
        {
            // 查询所有摄像头（不带任何过滤条件，确保获取所有数据）
            Camera queryCamera = new Camera();
            List<Camera> cameraList = cameraService.selectCameraList(queryCamera);
            
            if (cameraList == null || cameraList.isEmpty())
            {
                log.warn("没有找到任何摄像头，跳过状态检查");
                return;
            }
            
            int totalCount = cameraList.size();
            log.info("成功查询到 {} 个摄像头，开始并发检测状态（线程池大小：{}）", totalCount, THREAD_POOL_SIZE);
            
            // 创建固定大小的线程池
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            
            // 使用原子计数器统计结果
            AtomicInteger processedCount = new AtomicInteger(0);
            AtomicInteger onlineCount = new AtomicInteger(0);
            AtomicInteger offlineCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);
            AtomicInteger updatedCount = new AtomicInteger(0);
            
            // 创建异步任务列表
            List<CompletableFuture<Void>> futures = new java.util.ArrayList<>();
            
            // 为每个摄像头创建异步检测任务
            for (int i = 0; i < cameraList.size(); i++)
            {
                final Camera camera = cameraList.get(i);
                final int index = i + 1;
                
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try
                    {
                        log.debug("开始检测摄像头 [{}/{}]: {} ({})", 
                            index, totalCount, camera.getCameraName(), camera.getCameraCode());
                        
                        boolean isOnline = cameraService.checkCameraOnlineStatus(camera);
                        String newStatus = isOnline ? "1" : "0";
                        String oldStatus = camera.getStatus();
                        
                        // 只有状态发生变化时才更新数据库
                        if (!newStatus.equals(oldStatus))
                        {
                            camera.setStatus(newStatus);
                            cameraService.updateCamera(camera);
                            updatedCount.incrementAndGet();
                            log.info("摄像头 [{}/{}] [{}] 状态已更新：{} -> {}", 
                                index, totalCount,
                                camera.getCameraName(), 
                                "1".equals(oldStatus) ? "在线" : "离线",
                                isOnline ? "在线" : "离线");
                        }
                        else
                        {
                            log.debug("摄像头 [{}/{}] [{}] 状态未变化：{}", 
                                index, totalCount, camera.getCameraName(), 
                                isOnline ? "在线" : "离线");
                        }
                        
                        if (isOnline)
                        {
                            onlineCount.incrementAndGet();
                        }
                        else
                        {
                            offlineCount.incrementAndGet();
                        }
                        
                        int processed = processedCount.incrementAndGet();
                        if (processed % 20 == 0)
                        {
                            log.info("检测进度：{}/{} ({}%)", 
                                processed, totalCount, (processed * 100 / totalCount));
                        }
                    }
                    catch (Exception e)
                    {
                        errorCount.incrementAndGet();
                        processedCount.incrementAndGet();
                        log.error("检查摄像头 [{}/{}] [{}] 状态时发生错误：{}", 
                            index, totalCount, camera.getCameraName(), e.getMessage(), e);
                    }
                }, executorService);
                
                futures.add(future);
            }
            
            log.info("已提交 {} 个检测任务到线程池，等待执行完成（超时时间：{}秒）...", 
                futures.size(), TIMEOUT_SECONDS);
            
            // 等待所有任务完成，设置超时时间
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
            );
            
            try
            {
                allFutures.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
                log.info("所有检测任务已完成");
            }
            catch (java.util.concurrent.TimeoutException e)
            {
                log.error("摄像头状态检查超时！已处理：{}/{}，部分检测可能未完成", 
                    processedCount.get(), totalCount);
            }
            catch (Exception e)
            {
                log.error("等待检测任务完成时发生错误", e);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("========== 摄像头状态检查完成 ==========");
            log.info("总数：{}，已处理：{}，在线：{}，离线：{}，错误：{}，更新：{}，耗时：{}ms ({}秒)", 
                totalCount,
                processedCount.get(),
                onlineCount.get(), 
                offlineCount.get(), 
                errorCount.get(),
                updatedCount.get(),
                duration,
                duration / 1000);
            
            // 检查是否所有摄像头都被处理
            if (processedCount.get() < totalCount)
            {
                log.warn("警告：有 {} 个摄像头未被处理！", (totalCount - processedCount.get()));
            }
        }
        catch (Exception e)
        {
            log.error("执行摄像头状态检查定时任务时发生严重错误", e);
        }
        finally
        {
            // 关闭线程池
            if (executorService != null)
            {
                log.info("开始关闭线程池...");
                executorService.shutdown();
                try
                {
                    if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                    {
                        log.warn("线程池在 {} 秒内未能正常关闭，强制关闭", SHUTDOWN_TIMEOUT_SECONDS);
                        executorService.shutdownNow();
                        
                        if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
                        {
                            log.error("线程池强制关闭失败");
                        }
                    }
                    else
                    {
                        log.info("线程池已正常关闭");
                    }
                }
                catch (InterruptedException e)
                {
                    log.error("关闭线程池时被中断", e);
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 检查指定摄像头的在线状态
     * 
     * @param cameraId 摄像头ID
     */
    public void checkCameraStatusById(Long cameraId)
    {
        log.info("开始检查摄像头[ID:{}]的状态", cameraId);
        
        try
        {
            Camera camera = cameraService.selectCameraById(cameraId);
            if (camera == null)
            {
                log.warn("摄像头[ID:{}]不存在", cameraId);
                return;
            }
            
            boolean isOnline = cameraService.checkCameraOnlineStatus(camera);
            String newStatus = isOnline ? "1" : "0";
            
            if (!newStatus.equals(camera.getStatus()))
            {
                camera.setStatus(newStatus);
                cameraService.updateCamera(camera);
                log.info("摄像头[{}]状态已更新：{}", camera.getCameraName(), isOnline ? "在线" : "离线");
            }
        }
        catch (Exception e)
        {
            log.error("检查摄像头[ID:{}]状态时发生错误", cameraId, e);
        }
    }
}

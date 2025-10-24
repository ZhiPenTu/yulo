package com.ruoyi.project.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.project.device.domain.dto.StreamRequestDto;
import com.ruoyi.project.device.domain.dto.StreamStatusDto;
import com.ruoyi.project.device.service.IZLMediaKitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * ZLMediaKit媒体服务实现类
 *
 * @author ruoyi
 * @date 2025-10-21
 */
@Service
public class ZLMediaKitServiceImpl implements IZLMediaKitService
{
    private static final Logger log = LoggerFactory.getLogger(ZLMediaKitServiceImpl.class);

    @Value("${zlmediakit.server.url:http://35.46.5.76:8080}")
    private String zlmediakitUrl;

    @Value("${zlmediakit.server.secret:MKjn33mg82zrW8TIleMSDlbdxhmhP6yZ}")
    private String zlmediakitSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean startStream(StreamRequestDto streamRequest)
    {
        try {
            String url = zlmediakitUrl + "/index/api/addStreamProxy";

            Map<String, Object> params = new HashMap<>();
            params.put("secret", zlmediakitSecret);
            params.put("vhost", "__defaultVhost__");
            params.put("app", "live");
            params.put("stream", streamRequest.getCameraCode());
            params.put("url", streamRequest.getVideoSource());
            params.put("enable_rtsp", 0);
            params.put("enable_rtmp", 1);
            params.put("enable_hls", 0);
            params.put("enable_mp4", 0);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject result = JSON.parseObject(response.getBody());
                Integer code = result.getInteger("code");
                if (code != null && code == 0) {
                    log.info("开始拉流成功: {}", streamRequest.getCameraCode());
                    return true;
                } else {
                    log.error("开始拉流失败: {}, 错误信息: {}", streamRequest.getCameraCode(), result.getString("msg"));
                }
            }
        } catch (Exception e) {
            log.error("开始拉流异常: {}", streamRequest.getCameraCode(), e);
        }
        return false;
    }

    @Override
    public boolean stopStream(StreamRequestDto streamRequest)
    {
        try {
            String url = zlmediakitUrl + "/index/api/delStreamProxy";

//            http://35.46.5.76:8080/index/api/delStreamProxy?secret=MKjn33mg82zrW8TIleMSDlbdxhmhP6yZ&key=__defaultVhost__/live/350000007132901002
            Map<String, Object> params = new HashMap<>();
            params.put("secret", zlmediakitSecret);
            params.put("key","__defaultVhost__/live/" + streamRequest.getCameraCode());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject result = JSON.parseObject(response.getBody());
                Integer code = result.getInteger("code");
                if (code != null && code == 0) {
                    log.info("停止拉流成功: {}", streamRequest.getCameraCode());
                    return true;
                } else {
                    log.error("停止拉流失败: {}, 错误信息: {}", streamRequest.getCameraCode(), result.getString("msg"));
                }
            }
        } catch (Exception e) {
            log.error("停止拉流异常: {}", streamRequest.getCameraCode(), e);
        }
        return false;
    }

    @Override
    public StreamStatusDto getStreamStatus(String cameraCode)
    {
        try {
            String url = zlmediakitUrl + "/index/api/getMediaList";

            Map<String, Object> params = new HashMap<>();
            params.put("secret", zlmediakitSecret);
            params.put("schema", "");
            params.put("vhost", "__defaultVhost__");
            params.put("app", "live");
            params.put("stream", cameraCode);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject result = JSON.parseObject(response.getBody());
                Integer code = result.getInteger("code");
                if (code != null && code == 0) {
                    Object data = result.get("data");
                    if (data != null && data.toString().contains(cameraCode)) {
                        StreamStatusDto statusDto = new StreamStatusDto("active", "拉流中");
                        statusDto.setStreamId(cameraCode);
                        statusDto.setStreamUrl(zlmediakitUrl + "/live/" + cameraCode + ".flv");
                        return statusDto;
                    }
                }
            }
        } catch (Exception e) {
            log.error("查询拉流状态异常: {}", cameraCode, e);
        }

        return new StreamStatusDto("inactive", "未拉流");
    }
}

#!/bin/bash

# 摄像头拉流功能API测试脚本

API_BASE="http://localhost:8080"
CAMERA_CODE="test_camera_001"
VIDEO_SOURCE="rtsp://admin:password@192.168.1.100:554/stream1"
MEDIA_PROXY="rotanova_server_node"

echo "=== 摄像头拉流功能API测试 ==="
echo "API地址: $API_BASE"
echo "摄像头编号: $CAMERA_CODE"
echo "视频源: $VIDEO_SOURCE"
echo ""

# 测试开始拉流
echo "1. 测试开始拉流..."
START_RESPONSE=$(curl -s -X POST "$API_BASE/device/camera/startStream" \
  -H "Content-Type: application/json" \
  -d "{
    \"cameraCode\": \"$CAMERA_CODE\",
    \"videoSource\": \"$VIDEO_SOURCE\",
    \"mediaProxy\": \"$MEDIA_PROXY\"
  }")

echo "开始拉流响应: $START_RESPONSE"
echo ""

# 等待2秒
sleep 2

# 测试查询拉流状态
echo "2. 测试查询拉流状态..."
STATUS_RESPONSE=$(curl -s -X GET "$API_BASE/device/camera/streamStatus/$CAMERA_CODE")
echo "拉流状态响应: $STATUS_RESPONSE"
echo ""

# 等待3秒
sleep 3

# 测试停止拉流
echo "3. 测试停止拉流..."
STOP_RESPONSE=$(curl -s -X POST "$API_BASE/device/camera/stopStream" \
  -H "Content-Type: application/json" \
  -d "{
    \"cameraCode\": \"$CAMERA_CODE\",
    \"mediaProxy\": \"$MEDIA_PROXY\"
  }")

echo "停止拉流响应: $STOP_RESPONSE"
echo ""

# 再次查询状态确认停止
echo "4. 确认拉流已停止..."
FINAL_STATUS=$(curl -s -X GET "$API_BASE/device/camera/streamStatus/$CAMERA_CODE")
echo "最终状态响应: $FINAL_STATUS"
echo ""

echo "=== 测试完成 ==="
echo ""
echo "如需测试视频播放，请："
echo "1. 确保ZLMediaKit服务正常运行"
echo "2. 打开 test-camera-stream.html 进行可视化测试"
echo "3. 或直接访问播放地址: http://35.46.5.76:8080/live/$CAMERA_CODE.flv"
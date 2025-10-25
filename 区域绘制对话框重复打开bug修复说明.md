# 区域绘制对话框重复打开Bug修复说明

## 问题描述

第一次打开"区域绘制"对话框时，区域标记显示正常。但关闭对话框后再次打开，区域标记消失了，即使数据库中有数据。

## 问题原因

1. **事件监听器重复添加**：每次打开对话框都会添加新的 `loadedmetadata` 事件监听器，但没有移除旧的监听器，导致事件处理函数被多次调用。

2. **数据污染**：在坐标转换时直接修改了 `regions.value`，将归一化坐标转换为像素坐标后，原始数据被污染。第二次打开时，数据已经是像素坐标，但代码仍然尝试转换，导致坐标错误。

3. **状态未清理**：关闭对话框时没有清理拖拽相关的状态变量。

## 修复方案

### 1. 提取事件处理函数
将 `loadedmetadata` 事件的处理逻辑提取为独立函数 `handleVideoMetadataLoaded`，便于管理和移除。

```javascript
const handleVideoMetadataLoaded = () => {
  const video = videoRef.value;
  const canvas = canvasRef.value;
  
  if (!video || !canvas) return;
  
  canvas.width = video.videoWidth || 1920;
  canvas.height = video.videoHeight || 1080;
  
  // 转换归一化坐标为像素坐标（创建新数组，不修改原数据）
  regions.value = regions.value.map(region => {
    // ... 坐标转换逻辑
    return { ...region }; // 返回副本而不是原对象
  });
  
  drawAllRegions();
};
```

### 2. 正确管理事件监听器
在 `initVideoAndCanvas` 函数中：
- 先移除旧的事件监听器
- 再添加新的事件监听器
- 检查视频是否已加载，如果已加载则直接处理

```javascript
const initVideoAndCanvas = () => {
  if (!videoRef.value || !canvasRef.value) return;
  
  const video = videoRef.value;
  
  // 移除旧的事件监听器（如果存在）
  video.removeEventListener('loadedmetadata', handleVideoMetadataLoaded);
  
  // 添加新的事件监听器
  video.addEventListener('loadedmetadata', handleVideoMetadataLoaded);
  
  // 如果视频已经加载，直接处理
  if (video.readyState >= 1) {
    handleVideoMetadataLoaded();
  }
};
```

### 3. 深拷贝区域数据
在 `handleEdit` 函数中使用深拷贝，避免修改原始数据：

```javascript
// 深拷贝区域数据，避免修改原始数据
regions.value = JSON.parse(JSON.stringify(parsedRegions || []));
```

### 4. 清理状态
在 `cancelDrawing` 函数中清理所有状态：

```javascript
const cancelDrawing = () => {
  // 清理视频事件监听器
  if (videoRef.value) {
    videoRef.value.removeEventListener('loadedmetadata', handleVideoMetadataLoaded);
  }
  
  regionDialogVisible.value = false;
  isDrawing.value = false;
  currentPoints.value = [];
  isDragging.value = false;
  dragPointIndex.value = null;
  dragRegionIndex.value = null;
};
```

## 修复效果

- ✅ 可以多次打开和关闭对话框，区域标记始终正常显示
- ✅ 不会污染原始数据
- ✅ 事件监听器正确管理，不会重复添加
- ✅ 所有状态正确清理

## 测试建议

1. 打开区域绘制对话框，查看已有区域是否正常显示
2. 关闭对话框
3. 重新打开对话框，确认区域仍然正常显示
4. 重复步骤2-3多次，确保稳定性
5. 测试新增、编辑、删除区域功能是否正常
6. 测试拖拽顶点功能是否正常

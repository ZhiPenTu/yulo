<template>
  <div class="ai-settings-container">
    <!-- 表格 -->
    <div class="table-container">
      <el-table
        :data="cameraList"
        v-loading="loading"
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        
        <el-table-column label="摄像头情况" width="180" align="center">
          <template #default="scope">
            <div class="camera-status">
              <el-icon class="status-icon" :class="scope.row.status === '1' ? 'online' : 'offline'">
                <VideoCamera />
              </el-icon>
              <div class="status-text">
                <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" size="small">
                  {{ scope.row.status === '1' ? '在线' : '离线' }}
                </el-tag>
              </div>
              <el-button
                v-if="scope.row.backgroundSnap"
                type="text"
                class="snap-icon-btn"
                @click="handleViewBackgroundSnap(scope.row)"
                title="查看相机偏移情况"
              >
                <el-icon :size="20">
                  <Picture />
                </el-icon>
              </el-button>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="相机编号情况" width="250" align="center">
          <template #default="scope">
            <div class="camera-info">
              <div class="camera-id">
                <el-icon><VideoCamera /></el-icon>
                <span>{{ scope.row.cameraCode }}</span>
              </div>
              <div class="camera-detail">
                (AK10+720 A2 去向-枪机)
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="人机非事件" width="400" align="center">
          <template #default="scope">
            <div class="ai-function-section">
              <div class="section-header">
                <el-checkbox
                  :model-value="scope.row.carPersonMotorEnable"
                  @change="(val) => handleEnableChange(scope.row, 'carPersonMotor', val)"
                  @click.stop
                >
                  人机非行为分析
                </el-checkbox>
                <span class="status-text">{{ scope.row.carPersonMotorEnable ? 'working' : '' }}</span>
                <el-icon 
                  class="expand-icon" 
                  :class="{ expanded: scope.row.expandCarPersonMotor }"
                  @click="toggleSection(scope.row, 'carPersonMotor')"
                >
                  <ArrowRight />
                </el-icon>
              </div>
              
              <div v-show="scope.row.expandCarPersonMotor" class="section-content">
                <el-checkbox-group 
                  :model-value="scope.row.carPersonMotorAttributes"
                  @change="(val) => handleAttributesChange(scope.row, 'carPersonMotor', val)"
                >
                  <el-checkbox label="parking">违法停车</el-checkbox>
                  <el-checkbox label="reverse">违法倒车</el-checkbox>
                  <el-checkbox label="truckHighSpeed">货车高速</el-checkbox>
                  <el-checkbox label="emergencyLane">占用应急车道</el-checkbox>
                  <el-checkbox label="changeLane">违法变道</el-checkbox>
                  <el-checkbox label="guideArea">导流区</el-checkbox>
                  <el-checkbox label="crossSolidLine">压实线</el-checkbox>
                  <el-checkbox label="congestion">拥堵</el-checkbox>
                  <el-checkbox label="congestionToll">收费站拥堵</el-checkbox>
                  <el-checkbox label="overSpeed">超速</el-checkbox>
                  <el-checkbox label="lowSpeed">低速</el-checkbox>
                  <el-checkbox label="accident">事故</el-checkbox>
                  <el-checkbox label="personInvasion">行人入侵</el-checkbox>
                  <el-checkbox label="motorbikeInvasion">摩托车入侵</el-checkbox>
                  <el-checkbox label="trafficFlow">交通流量</el-checkbox>
                </el-checkbox-group>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="场景分析" width="400" align="center">
          <template #default="scope">
            <div class="ai-function-section">
              <div class="section-header">
                <el-checkbox
                  :model-value="scope.row.sceneEnable"
                  @change="(val) => handleEnableChange(scope.row, 'scene', val)"
                  @click.stop
                >
                  场景分析
                </el-checkbox>
                <span class="status-text">{{ scope.row.sceneEnable ? 'working' : '' }}</span>
                <el-icon 
                  class="expand-icon" 
                  :class="{ expanded: scope.row.expandScene }"
                  @click="toggleSection(scope.row, 'scene')"
                >
                  <ArrowRight />
                </el-icon>
              </div>
              
              <div v-show="scope.row.expandScene" class="section-content">
                <el-checkbox-group 
                  :model-value="scope.row.sceneAttributes"
                  @change="(val) => handleAttributesChange(scope.row, 'scene', val)"
                >
                  <el-checkbox label="constructionArea">施工区域</el-checkbox>
                  <el-checkbox label="remnants">遗留物</el-checkbox>
                  <el-checkbox label="fire">火灾</el-checkbox>
                  <el-checkbox label="smoke">烟雾</el-checkbox>
                  <el-checkbox label="flowerScreen">花屏</el-checkbox>
                  <el-checkbox label="accident">事故</el-checkbox>
                </el-checkbox-group>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" align="center" fixed="right" width="120">
          <template #default="scope">
            <div class="action-buttons">
              <el-button 
                type="text"
                class="edit-btn"
                @click="handleEdit(scope.row)"
                :loading="loading"
              >
                区域绘制
              </el-button>  
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
      />
    </div>

    <!-- 背景快照图片预览对话框 -->
    <el-dialog
      v-model="backgroundSnapDialogVisible"
      title="相机偏移情况"
      width="800px"
      append-to-body
    >
      <div class="background-snap-container">
        <el-image
          :src="currentBackgroundSnap"
          fit="contain"
          style="width: 100%; max-height: 600px;"
          :preview-src-list="[currentBackgroundSnap]"
    >
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
              <span>图片加载失败</span>
          </div>
</template>
        </el-image>
      </div>
    </el-dialog>

    <!-- 区域命名对话框 -->
    <el-dialog
      v-model="regionNameDialogVisible"
      :title="isEditingRegion ? '编辑区域' : '区域命名'"
      width="500px"
      :close-on-click-modal="false"
      append-to-body
    >
      <el-form :model="regionNameForm" label-width="90px">
        <el-form-item label="区域类型" required>
          <el-input v-model="regionNameForm.typeName" disabled />
        </el-form-item>
        <el-form-item label="区域名称" required>
          <el-input 
            v-model="regionNameForm.name" 
            placeholder="请输入区域名称，如：1、2、99等"
            @input="regionNameForm.value = regionNameForm.name"
          />
        </el-form-item>
        
        <!-- 车道区特有字段 -->
        <template v-if="regionNameForm.type === 'Lane'">
          <el-form-item label="车道方向" required>
            <el-select v-model="regionNameForm.laneDirection" placeholder="请选择车道方向">
              <el-option label="向上" value="up" />
              <el-option label="向下" value="down" />
              <el-option label="向右" value="right" />
              <el-option label="向左" value="left" />
            </el-select>
          </el-form-item>
          <el-form-item label="车道类型" required>
            <el-select v-model="regionNameForm.laneType" placeholder="请选择车道类型">
              <el-option label="行车道" value="drivingLane" />
              <el-option label="应急车道" value="emergencyLane" />
              <el-option label="导流区" value="driverslon" />
            </el-select>
          </el-form-item>
          <el-form-item label="最低限速">
            <el-input 
              v-model.number="regionNameForm.minSpeed" 
              type="number" 
              placeholder="请输入最低限速"
            />
          </el-form-item>
          <el-form-item label="最高限速">
            <el-input 
              v-model.number="regionNameForm.maxSpeed" 
              type="number" 
              placeholder="请输入最高限速"
            />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="regionNameDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddRegion">确定</el-button>
      </template>
    </el-dialog>

    <!-- 区域绘制对话框 -->
    <el-dialog
      v-model="regionDialogVisible"
      title="绘制区域"
      width="1400px"
      :close-on-click-modal="false"
      append-to-body
    >
      <div style="display: flex; gap: 20px; height: 600px;">
        <!-- 左侧：视频播放区域 -->
        <div style="flex: 1; display: flex; flex-direction: column; background: #000; border-radius: 4px; overflow: hidden;">
          <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px 20px; background: rgba(0, 0, 0, 0.8); color: #fff;">
            <span style="font-size: 18px; font-weight: bold;">{{ currentCamera?.cameraCode }}</span>
            <span style="font-size: 16px;">{{ new Date().toLocaleString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit' }).replace(/\//g, '-') }}</span>
          </div>
          <div style="flex: 1; position: relative; display: flex; align-items: center; justify-content: center; background: #000;">
            <video
              ref="videoRef"
              :src="currentCamera?.cameraUrl"
              autoplay
              muted
              loop
              style="max-width: 100%; max-height: 100%; object-fit: contain;"
            ></video>
            <canvas
              ref="canvasRef"
              style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); cursor: crosshair; max-width: 100%; max-height: 100%;"
              @click="handleCanvasClick"
              @mousedown="handleCanvasMouseDown"
              @mousemove="handleCanvasMouseMove"
              @mouseup="handleCanvasMouseUp"
              @mouseleave="handleCanvasMouseLeave"
            ></canvas>
          </div>
        </div>

        <!-- 右侧：控制面板 -->
        <div style="width: 320px; display: flex; flex-direction: column; gap: 15px; padding: 20px; background: #f5f7fa; border-radius: 4px; overflow-y: auto;">
          <!-- 区域类型选择 -->
          <div style="display: flex; flex-direction: column; gap: 10px;">
            <span style="font-size: 14px; color: #606266; font-weight: 500;">当前绘制区域：</span>
            <el-select v-model="selectedRegionType" placeholder="请选择区域类型" :disabled="isDrawing">
              <el-option v-for="type in regionTypes" :key="type.value" :label="type.label" :value="type.value" />
            </el-select>
            <el-button type="primary" :icon="Plus" @click="addRegion" :disabled="isDrawing">新增</el-button>
          </div>

          <!-- 区域列表 -->
          <div style="display: flex; flex-wrap: wrap; gap: 10px; padding: 15px; background: #fff; border-radius: 4px; min-height: 100px;">
            <div
              v-for="(region, index) in regions"
              :key="index"
              :style="{
                position: 'relative',
                width: '70px',
                height: '70px',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                background: selectedRegionIndex === index ? '#9b59b6' : (isDrawing && selectedRegionIndex !== index ? '#909399' : '#409eff'),
                color: '#fff',
                borderRadius: '4px',
                cursor: isDrawing && selectedRegionIndex !== index ? 'not-allowed' : 'pointer',
                fontSize: '14px',
                fontWeight: 'bold',
                boxShadow: selectedRegionIndex === index ? '0 0 0 3px rgba(155, 89, 182, 0.3)' : 'none',
                padding: '4px',
                opacity: isDrawing && selectedRegionIndex !== index ? 0.5 : 1
              }"
              @click="selectRegion(index)"
              :title="`名称: ${region.name}\n类型: ${regionTypes.find(t => t.value === region.type)?.label || region.type}`"
            >
              <div style="font-size: 20px;">{{ region.name }}</div>
              <el-icon
                :style="{
                  position: 'absolute',
                  top: '-8px',
                  right: '-8px',
                  width: '20px',
                  height: '20px',
                  background: '#f56c6c',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '12px'
                }"
                @click.stop="deleteRegion(index)"
              >
                <Close />
              </el-icon>
              <el-icon
                v-if="region.points && region.points.length > 0"
                :style="{
                  position: 'absolute',
                  bottom: '-8px',
                  left: '-8px',
                  width: '20px',
                  height: '20px',
                  background: '#909399',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '12px'
                }"
                @click.stop="resetRegion(index)"
                title="重置区域"
              >
                <RefreshLeft />
              </el-icon>
              <el-icon
                :style="{
                  position: 'absolute',
                  bottom: '-8px',
                  right: '-8px',
                  width: '20px',
                  height: '20px',
                  background: '#e6a23c',
                  borderRadius: '50%',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '12px'
                }"
                @click.stop="editRegionName(index)"
                title="编辑名称"
              >
                <Edit />
              </el-icon>
            </div>
          </div>

          <!-- 绘制控制 -->
          <div style="display: flex; gap: 10px;">
            <el-button type="primary" :disabled="selectedRegionIndex === null || isDrawing" @click="startDrawing" style="flex: 1;">开始绘制</el-button>
            <el-button type="warning" :disabled="!isDrawing" @click="endDrawing" style="flex: 1;">结束绘制</el-button>
          </div>

          <!-- 底部按钮 -->
          <div style="display: flex; gap: 10px; margin-top: auto; padding-top: 15px; border-top: 1px solid #dcdfe6;">
            <el-button @click="cancelDrawing" style="flex: 1;">取消</el-button>
            <el-button type="success" @click="saveRegions" :disabled="isDrawing" style="flex: 1;">保存区域</el-button>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="AIsSettings">
import { ref, reactive, onMounted } from 'vue';
import { listCamera, updateCamera } from "@/api/deviceManage/camera";
import { ElMessage } from 'element-plus';
import { Plus, Warning, VideoCamera, ArrowRight, Picture, Close, Edit, RefreshLeft } from '@element-plus/icons-vue';

const showRegionPropertiesDialog = ref(false);

const loading = ref(false);
const cameraList = ref([]);
const searchKeyword = ref('');
const eventKeyword = ref('');
const backgroundSnapDialogVisible = ref(false);
const currentBackgroundSnap = ref('');
const total = ref(0);

// 查询参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10
});

// 获取摄像头列表
const getList = () => {
  loading.value = true;
  listCamera(queryParams.value).then(response => {
    cameraList.value = response.rows.map(camera => {
      // 解析 ai_function 字段
      let aiFunction = {};
      if (camera.aiFunction) {
        try {
          aiFunction = typeof camera.aiFunction === 'string' 
            ? JSON.parse(camera.aiFunction) 
            : camera.aiFunction;
        } catch (e) {
          console.error('解析 aiFunction 失败:', e);
          aiFunction = getDefaultAiFunction();
        }
      } else {
        aiFunction = getDefaultAiFunction();
      }
      
      // 提取 carPersonMotor 配置
      const carPersonMotor = aiFunction.carPersonMotor || { enable: false, attributes: {} };
      const carPersonMotorAttributes = [];
      if (carPersonMotor.attributes) {
        Object.keys(carPersonMotor.attributes).forEach(key => {
          if (carPersonMotor.attributes[key] === true) {
            carPersonMotorAttributes.push(key);
          }
        });
      }
      
      // 提取 scene 配置
      const scene = aiFunction.scene || { enable: false, attributes: {} };
      const sceneAttributes = [];
      if (scene.attributes) {
        Object.keys(scene.attributes).forEach(key => {
          if (scene.attributes[key] === true) {
            sceneAttributes.push(key);
          }
        });
      }
      
      return {
        ...camera,
        aiFunction,
        expandCarPersonMotor: false,
        expandScene: false,
        carPersonMotorEnable: carPersonMotor.enable,
        carPersonMotorAttributes,
        sceneEnable: scene.enable,
        sceneAttributes
      };
    });
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
};

// 默认 AI 功能配置
const getDefaultAiFunction = () => {
  return {
    carPersonMotor: {
      enable: false,
      attributes: {}
    },
    scene: {
      enable: false,
      attributes: {}
    }
  };
};

// 切换展开/收起
const toggleSection = (row, type) => {
  if (type === 'carPersonMotor') {
    row.expandCarPersonMotor = !row.expandCarPersonMotor;
  } else if (type === 'scene') {
    row.expandScene = !row.expandScene;
  }
};

// 处理启用/禁用变更
const handleEnableChange = (row, type, value) => {
  if (type === 'carPersonMotor') {
    row.carPersonMotorEnable = value;
  } else if (type === 'scene') {
    row.sceneEnable = value;
  }
  saveAiFunction(row);
};

// 处理属性变更
const handleAttributesChange = (row, type, value) => {
  if (type === 'carPersonMotor') {
    row.carPersonMotorAttributes = value;
  } else if (type === 'scene') {
    row.sceneAttributes = value;
  }
  saveAiFunction(row);
};

// 保存 AI 功能配置
const saveAiFunction = (row) => {
  // 构建 carPersonMotor 配置
  const carPersonMotorAttributes = {};
  row.carPersonMotorAttributes.forEach(attr => {
    carPersonMotorAttributes[attr] = true;
  });
  
  // 构建 scene 配置
  const sceneAttributes = {};
  row.sceneAttributes.forEach(attr => {
    sceneAttributes[attr] = true;
  });
  
  const aiFunction = {
    carPersonMotor: {
      enable: row.carPersonMotorEnable,
      attributes: carPersonMotorAttributes
    },
    scene: {
      enable: row.sceneEnable,
      attributes: sceneAttributes
    }
  };
  
  // 更新到数据库
  const updateData = {
    id: row.id,
    aiFunction: JSON.stringify(aiFunction)
  };
  
  updateCamera(updateData).then(() => {
    ElMessage.success('AI 功能配置更新成功');
    row.aiFunction = aiFunction;
  }).catch((error) => {
    ElMessage.error('AI 功能配置更新失败');
    console.error('保存失败:', error);
  });
};

// 查看背景快照
const handleViewBackgroundSnap = (row) => {
  if (row.backgroundSnap) {
    currentBackgroundSnap.value = row.backgroundSnap;
    backgroundSnapDialogVisible.value = true;
  } else {
    ElMessage.warning('该摄像头暂无偏移对照图');
  }
};

// 区域绘制相关状态
const regionDialogVisible = ref(false);
const currentCamera = ref(null);
const regionTypes = [
  { label: '车道区', value: 'Lane' },
  { label: '实线', value: 'solidLine' },
  { label: '拥堵触发区域', value: 'CongestionTriggerRegion' },
  { label: '禁行区', value: 'ProhibitedRegion' },
  { label: '人、非机屏蔽区', value: 'ShieldRegion' },
  { label: '车辆屏蔽区', value: 'VehicleShieldRegion' },
  { label: '车流统计区', value: 'TrafficFlowCountRegion' },
  { label: '遗撒物屏蔽区', value: 'remnantsShieldRegion' }
];
const selectedRegionType = ref('');
const regions = ref([]);
const selectedRegionIndex = ref(null);
const isDrawing = ref(false);
const currentPoints = ref([]);
const videoRef = ref(null);
const canvasRef = ref(null);
const isDragging = ref(false);
const dragPointIndex = ref(null);
const dragRegionIndex = ref(null);
const regionNameDialogVisible = ref(false);
const regionNameForm = ref({ 
  name: '', 
  value: '', 
  type: '', 
  typeName: '',
  laneDirection: '',
  laneType: '',
  minSpeed: null,
  maxSpeed: null
});
const isEditingRegion = ref(false);
const editingRegionIndex = ref(null);

// 编辑区域绘制
const handleEdit = (row) => {
  currentCamera.value = row;
  
  // 重置所有状态
  selectedRegionType.value = '';
  selectedRegionIndex.value = null;
  isDrawing.value = false;
  currentPoints.value = [];
  isDragging.value = false;
  dragPointIndex.value = null;
  dragRegionIndex.value = null;
  
  // 解析已有的区域数据（深拷贝，避免修改原数据）
  if (row.regions) {
    try {
      const parsedRegions = typeof row.regions === 'string' 
        ? JSON.parse(row.regions) 
        : row.regions;
      
      // 深拷贝区域数据，避免修改原始数据
      regions.value = JSON.parse(JSON.stringify(parsedRegions || []));
    } catch (e) {
      console.error('解析区域数据失败:', e);
      regions.value = [];
    }
  } else {
    regions.value = [];
  }
  
  regionDialogVisible.value = true;
  
  // 等待DOM更新后初始化视频和画布
  setTimeout(() => {
    initVideoAndCanvas();
  }, 100);
};

// 视频元数据加载处理函数
const handleVideoMetadataLoaded = () => {
  const video = videoRef.value;
  const canvas = canvasRef.value;
  
  if (!video || !canvas) return;
  
  canvas.width = video.videoWidth || 1920;
  canvas.height = video.videoHeight || 1080;
  
  // 转换归一化坐标为像素坐标（创建新数组，不修改原数据）
  regions.value = regions.value.map(region => {
    if (region.points && region.points.length > 0) {
      // 检查是否是归一化坐标（值在0-1之间）
      const firstPoint = region.points[0];
      const isNormalized = (firstPoint.x !== undefined && firstPoint.x <= 1) || 
                         (Array.isArray(firstPoint) && firstPoint[0] <= 1);
      
      if (isNormalized) {
        return {
          ...region,
          points: region.points.map(p => {
            if (p.x !== undefined) {
              return [p.x * canvas.width, p.y * canvas.height];
            } else {
              return [p[0] * canvas.width, p[1] * canvas.height];
            }
          })
        };
      }
    }
    return { ...region }; // 返回副本而不是原对象
  });
  
  drawAllRegions();
};

// 初始化视频和画布
const initVideoAndCanvas = () => {
  if (!videoRef.value || !canvasRef.value) return;
  
  const video = videoRef.value;
  const canvas = canvasRef.value;
  
  // 移除旧的事件监听器（如果存在）
  video.removeEventListener('loadedmetadata', handleVideoMetadataLoaded);
  
  // 添加新的事件监听器
  video.addEventListener('loadedmetadata', handleVideoMetadataLoaded);
  
  // 如果视频已经加载，直接处理
  if (video.readyState >= 1) {
    handleVideoMetadataLoaded();
  }
};

// 新增区域
const addRegion = () => {
  if (!selectedRegionType.value) {
    ElMessage.warning('请先选择区域类型');
    return;
  }
  
  if (isDrawing.value) {
    ElMessage.warning('请先结束当前绘制');
    return;
  }
  
  // 打开命名对话框
  const typeName = regionTypes.find(t => t.value === selectedRegionType.value)?.label || '';
  regionNameForm.value = { 
    name: '', 
    value: '', 
    type: selectedRegionType.value,
    typeName: typeName,
    laneDirection: '',
    laneType: '',
    minSpeed: null,
    maxSpeed: null
  };
  isEditingRegion.value = false;
  editingRegionIndex.value = null;
  regionNameDialogVisible.value = true;
};

// 确认添加/编辑区域
const confirmAddRegion = () => {
  if (!regionNameForm.value.name) {
    ElMessage.warning('请输入区域名称');
    return;
  }
  
  // 如果是车道区，验证必填字段
  if (regionNameForm.value.type === 'Lane') {
    if (!regionNameForm.value.laneDirection) {
      ElMessage.warning('请选择车道方向');
      return;
    }
    if (!regionNameForm.value.laneType) {
      ElMessage.warning('请选择车道类型');
      return;
    }
  }
  
  if (isEditingRegion.value) {
    // 编辑模式
    regions.value[editingRegionIndex.value].name = regionNameForm.value.name;
    regions.value[editingRegionIndex.value].value = regionNameForm.value.name; // name 和 value 使用同一个值
    
    // 如果是车道区，更新车道属性
    if (regionNameForm.value.type === 'Lane') {
      regions.value[editingRegionIndex.value].attributes = {
        lane_direction: regionNameForm.value.laneDirection,
        lane_type: regionNameForm.value.laneType,
        min_speed: regionNameForm.value.minSpeed,
        max_speed: regionNameForm.value.maxSpeed
      };
    }
    
    regionNameDialogVisible.value = false;
    drawAllRegions(); // 重新绘制以更新显示的名称
    ElMessage.success('区域修改成功');
  } else {
    // 新增模式
    const attributes = {};
    
    // 如果是车道区，添加车道属性
    if (regionNameForm.value.type === 'Lane') {
      attributes.lane_direction = regionNameForm.value.laneDirection;
      attributes.lane_type = regionNameForm.value.laneType;
      if (regionNameForm.value.minSpeed !== null && regionNameForm.value.minSpeed !== '') {
        attributes.min_speed = regionNameForm.value.minSpeed;
      }
      if (regionNameForm.value.maxSpeed !== null && regionNameForm.value.maxSpeed !== '') {
        attributes.max_speed = regionNameForm.value.maxSpeed;
      }
    }
    
    const newRegion = {
      name: regionNameForm.value.name,
      value: regionNameForm.value.name, // name 和 value 使用同一个值
      type: selectedRegionType.value,
      attributes: attributes,
      points: []
    };
    
    regions.value.push(newRegion);
    selectedRegionIndex.value = regions.value.length - 1;
    regionNameDialogVisible.value = false;
    ElMessage.success('区域添加成功，请选中后开始绘制');
  }
};

// 编辑区域名称
const editRegionName = (index) => {
  if (isDrawing.value) {
    ElMessage.warning('请先结束当前绘制');
    return;
  }
  
  const region = regions.value[index];
  const typeName = regionTypes.find(t => t.value === region.type)?.label || '';
  
  regionNameForm.value = {
    name: region.name,
    value: region.value,
    type: region.type,
    typeName: typeName,
    laneDirection: region.attributes?.lane_direction || '',
    laneType: region.attributes?.lane_type || '',
    minSpeed: region.attributes?.min_speed || null,
    maxSpeed: region.attributes?.max_speed || null
  };
  
  isEditingRegion.value = true;
  editingRegionIndex.value = index;
  regionNameDialogVisible.value = true;
};

// 选中区域
const selectRegion = (index) => {
  if (isDrawing.value) {
    ElMessage.warning('请先结束当前绘制');
    return;
  }
  selectedRegionIndex.value = index;
  drawAllRegions(); // 重新绘制以更新选中状态的颜色
};

// 开始绘制
const startDrawing = () => {
  if (selectedRegionIndex.value === null) {
    ElMessage.warning('请先选择一个区域');
    return;
  }
  
  if (isDrawing.value) {
    ElMessage.warning('已经在绘制中');
    return;
  }
  
  isDrawing.value = true;
  currentPoints.value = [];
  ElMessage.success('开始绘制，点击视频画面标记区域顶点');
};

// 结束绘制
const endDrawing = () => {
  if (!isDrawing.value) {
    ElMessage.warning('当前没有在绘制');
    return;
  }
  
  if (currentPoints.value.length < 3) {
    ElMessage.warning('至少需要标记3个点');
    return;
  }
  
  // 保存当前绘制的点到选中的区域
  regions.value[selectedRegionIndex.value].points = [...currentPoints.value];
  
  isDrawing.value = false;
  currentPoints.value = [];
  
  drawAllRegions();
  ElMessage.success('绘制完成，已缓存到本地');
};

// 获取鼠标在画布上的坐标
const getCanvasCoordinates = (event) => {
  const canvas = canvasRef.value;
  const rect = canvas.getBoundingClientRect();
  const scaleX = canvas.width / rect.width;
  const scaleY = canvas.height / rect.height;
  
  return {
    x: (event.clientX - rect.left) * scaleX,
    y: (event.clientY - rect.top) * scaleY
  };
};

// 检查鼠标是否在某个顶点附近
const getPointAtPosition = (x, y) => {
  const threshold = 10; // 点击阈值（像素）
  
  // 检查已绘制的区域
  for (let regionIndex = 0; regionIndex < regions.value.length; regionIndex++) {
    const region = regions.value[regionIndex];
    if (region.points && region.points.length > 0) {
      for (let pointIndex = 0; pointIndex < region.points.length; pointIndex++) {
        const point = region.points[pointIndex];
        const px = Array.isArray(point) ? point[0] : point.x;
        const py = Array.isArray(point) ? point[1] : point.y;
        
        const distance = Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2));
        if (distance <= threshold) {
          return { regionIndex, pointIndex, isCurrentDrawing: false };
        }
      }
    }
  }
  
  // 检查正在绘制的点
  if (isDrawing.value && currentPoints.value.length > 0) {
    for (let pointIndex = 0; pointIndex < currentPoints.value.length; pointIndex++) {
      const point = currentPoints.value[pointIndex];
      const distance = Math.sqrt(Math.pow(x - point[0], 2) + Math.pow(y - point[1], 2));
      if (distance <= threshold) {
        return { regionIndex: null, pointIndex, isCurrentDrawing: true };
      }
    }
  }
  
  return null;
};

// 画布鼠标按下事件
const handleCanvasMouseDown = (event) => {
  const { x, y } = getCanvasCoordinates(event);
  const pointInfo = getPointAtPosition(x, y);
  
  if (pointInfo) {
    // 开始拖拽
    isDragging.value = true;
    dragPointIndex.value = pointInfo.pointIndex;
    if (pointInfo.isCurrentDrawing) {
      dragRegionIndex.value = -1; // 表示正在绘制的点
    } else {
      dragRegionIndex.value = pointInfo.regionIndex;
    }
    event.preventDefault();
  }
};

// 画布鼠标移动事件
const handleCanvasMouseMove = (event) => {
  if (!isDragging.value) {
    // 检查鼠标是否在顶点附近，改变鼠标样式
    const { x, y } = getCanvasCoordinates(event);
    const pointInfo = getPointAtPosition(x, y);
    const canvas = canvasRef.value;
    canvas.style.cursor = pointInfo ? 'move' : (isDrawing.value ? 'crosshair' : 'default');
    return;
  }
  
  // 拖拽中
  const { x, y } = getCanvasCoordinates(event);
  
  if (dragRegionIndex.value === -1) {
    // 拖拽正在绘制的点
    currentPoints.value[dragPointIndex.value] = [x, y];
  } else {
    // 拖拽已保存区域的点
    const region = regions.value[dragRegionIndex.value];
    if (Array.isArray(region.points[dragPointIndex.value])) {
      region.points[dragPointIndex.value] = [x, y];
    } else {
      region.points[dragPointIndex.value] = { x, y };
    }
  }
  
  drawAllRegions();
  event.preventDefault();
};

// 画布鼠标释放事件
const handleCanvasMouseUp = (event) => {
  isDragging.value = false;
  dragPointIndex.value = null;
  dragRegionIndex.value = null;
};

// 画布鼠标离开事件
const handleCanvasMouseLeave = (event) => {
  isDragging.value = false;
  dragPointIndex.value = null;
  dragRegionIndex.value = null;
};

// 画布点击事件
const handleCanvasClick = (event) => {
  // 如果刚刚完成拖拽，不添加新点
  if (isDragging.value) return;
  
  if (!isDrawing.value) return;
  
  const { x, y } = getCanvasCoordinates(event);
  
  // 检查是否点击在已有顶点上，如果是则不添加新点
  const pointInfo = getPointAtPosition(x, y);
  if (pointInfo) return;
  
  currentPoints.value.push([x, y]);
  drawAllRegions();
};

// 绘制所有区域
const drawAllRegions = () => {
  if (!canvasRef.value) return;
  
  const canvas = canvasRef.value;
  const ctx = canvas.getContext('2d');
  
  // 清空画布
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  
  // 绘制已保存的区域
  regions.value.forEach((region, index) => {
    if (region.points && region.points.length > 0) {
      // 转换点格式：从 [x, y] 转为 {x, y}
      const formattedPoints = region.points.map(p => 
        Array.isArray(p) ? p : [p.x, p.y]
      );
      drawPolygon(ctx, formattedPoints, index === selectedRegionIndex.value, region.name);
    }
  });
  
  // 绘制当前正在绘制的点
  if (isDrawing.value && currentPoints.value.length > 0) {
    drawPolygon(ctx, currentPoints.value, true, null, true);
  }
};

// 绘制多边形
const drawPolygon = (ctx, points, isSelected, regionName, isDrawing = false) => {
  if (points.length === 0) return;
  
  ctx.beginPath();
  ctx.moveTo(points[0][0], points[0][1]);
  
  for (let i = 1; i < points.length; i++) {
    ctx.lineTo(points[i][0], points[i][1]);
  }
  
  if (!isDrawing) {
    ctx.closePath();
  }
  
  // 填充 - 选中时使用紫色，未选中时使用蓝色
  ctx.fillStyle = isSelected ? 'rgba(155, 89, 182, 0.3)' : 'rgba(64, 158, 255, 0.2)';
  ctx.fill();
  
  // 边框 - 选中时使用紫色，未选中时使用蓝色
  ctx.strokeStyle = isSelected ? '#9b59b6' : '#409EFF';
  ctx.lineWidth = isSelected ? 3 : 2; // 选中时线条更粗
  ctx.stroke();
  
  // 绘制顶点
  points.forEach((point, index) => {
    ctx.beginPath();
    ctx.arc(point[0], point[1], isSelected ? 8 : 6, 0, 2 * Math.PI); // 选中时顶点更大
    ctx.fillStyle = isSelected ? '#9b59b6' : '#409EFF';
    ctx.fill();
    
    // 添加白色边框使顶点更明显
    ctx.strokeStyle = '#fff';
    ctx.lineWidth = 2;
    ctx.stroke();
    
    // 绘制顶点序号
    if (isDrawing) {
      ctx.fillStyle = '#fff';
      ctx.font = 'bold 12px Arial';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText(index + 1, point[0], point[1]);
    }
  });
  
  // 绘制区域名称
  if (regionName && points.length > 0) {
    const centerX = points.reduce((sum, p) => sum + p[0], 0) / points.length;
    const centerY = points.reduce((sum, p) => sum + p[1], 0) / points.length;
    
    ctx.fillStyle = '#fff';
    ctx.font = 'bold 16px Arial';
    ctx.textAlign = 'center';
    ctx.textBaseline = 'middle';
    ctx.fillText(regionName, centerX, centerY);
  }
};

// 删除区域
const deleteRegion = (index) => {
  // 如果删除的是正在绘制的区域，需要结束绘制状态
  if (isDrawing.value && selectedRegionIndex.value === index) {
    isDrawing.value = false;
    currentPoints.value = [];
    ElMessage.info('已取消当前绘制');
  }
  
  regions.value.splice(index, 1);
  if (selectedRegionIndex.value === index) {
    selectedRegionIndex.value = null;
  }
  drawAllRegions();
};

// 重置区域
const resetRegion = (index) => {
  if (isDrawing.value && selectedRegionIndex.value === index) {
    ElMessage.warning('请先结束当前绘制');
    return;
  }
  
  regions.value[index].points = [];
  drawAllRegions();
  ElMessage.success('区域已重置');
};

// 保存区域到数据库
const saveRegions = () => {
  if (isDrawing.value) {
    ElMessage.warning('请先结束当前绘制');
    return;
  }
  
  if (regions.value.length === 0) {
    ElMessage.warning('请先添加并绘制区域');
    return;
  }
  
  // 检查是否所有区域都已绘制
  const undrawnRegions = regions.value.filter(r => !r.points || r.points.length === 0);
  if (undrawnRegions.length > 0) {
    ElMessage.warning('还有区域未绘制完成');
    return;
  }
  
  // 转换数据格式以匹配 typejson 格式
  const formattedRegions = regions.value.map(region => {
    // 获取视频尺寸用于归一化坐标
    const canvas = canvasRef.value;
    const videoWidth = canvas.width || 1920;
    const videoHeight = canvas.height || 1080;
    
    return {
      name: region.name,
      value: region.value,
      type: region.type,
      attributes: region.attributes || {},
      points: region.points.map(point => {
        // 如果点已经是归一化格式，直接使用；否则进行归一化
        if (Array.isArray(point)) {
          return {
            x: point[0] / videoWidth,
            y: point[1] / videoHeight
          };
        } else if (point.x !== undefined && point.x <= 1) {
          // 已经是归一化格式
          return { x: point.x, y: point.y };
        } else {
          // 需要归一化
          return {
            x: point.x / videoWidth,
            y: point.y / videoHeight
          };
        }
      })
    };
  });
  
  const updateData = {
    id: currentCamera.value.id,
    regions: JSON.stringify(formattedRegions)
  };
  
  updateCamera(updateData).then(() => {
    ElMessage.success('区域保存成功');
    currentCamera.value.regions = JSON.stringify(formattedRegions);
    regionDialogVisible.value = false;
  }).catch((error) => {
    ElMessage.error('区域保存失败');
    console.error('保存失败:', error);
  });
};

// 取消绘制
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

onMounted(() => {
  getList();
});
</script>

<style scoped lang="scss">
.ai-settings-container {
  padding: 20px;
  background: #f0f2f5;
  min-height: 100vh;

  .search-bar {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 16px 20px;
    background: white;
    border-radius: 4px;
    margin-bottom: 16px;

    .search-item {
      display: flex;
      align-items: center;
      gap: 10px;

      .label {
        font-size: 14px;
        color: #606266;
        white-space: nowrap;
      }

      .add-btn {
        display: flex;
        align-items: center;
        gap: 4px;
      }

      .config-count {
        color: #409eff;
        font-weight: 500;
      }
    }
  }

  .warning-tip {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 16px;
    background: #fff7e6;
    border: 1px solid #ffd591;
    border-radius: 4px;
    margin-bottom: 16px;
    color: #d46b08;

    .warning-icon {
      font-size: 18px;
      color: #faad14;
    }
  }

  .table-container {
    background: white;
    border-radius: 4px;
    padding: 16px;

    .camera-status {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;

      .status-icon {
        font-size: 32px;
        
        &.online {
          color: #52c41a;
        }
        
        &.offline {
          color: #ff4d4f;
        }
      }

      .snap-icon-btn {
        padding: 4px;
        margin-top: 4px;
        
        &:hover {
          color: #409eff;
        }
      }
    }

    .camera-info {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      gap: 4px;

      .camera-id {
        display: flex;
        align-items: center;
        gap: 4px;
        font-weight: 500;
      }

      .camera-detail {
        font-size: 12px;
        color: #8c8c8c;
      }
    }

    .ai-function-section {
      text-align: left;

      .section-header {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px;
        user-select: none;

        .status-text {
          font-size: 12px;
          color: #52c41a;
        }

        .expand-icon {
          margin-left: auto;
          cursor: pointer;
          transition: transform 0.3s;
          
          &:hover {
            color: #409eff;
          }
          
          &.expanded {
            transform: rotate(90deg);
          }
        }
      }

      .section-content {
        padding: 12px 24px;
        background: #fafafa;
        border-radius: 4px;
        margin-top: 8px;

        :deep(.el-checkbox-group) {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 12px;

          .el-checkbox {
            margin-right: 0;
          }
        }
      }
    }

    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 8px;
      align-items: center;

      .edit-btn {
        color: #1890ff;
      }
    }
  }

  .background-snap-container {
    display: flex;
    justify-content: center;
    align-items: center;

    .image-error {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      color: #909399;
      padding: 40px;

      .el-icon {
        font-size: 48px;
      }
    }
  }
}
</style>

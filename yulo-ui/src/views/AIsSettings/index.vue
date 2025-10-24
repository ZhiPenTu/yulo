<template>
  <div class="ai-settings-container">
    <!-- 提示信息 -->
    <div class="warning-tip">
      <el-icon class="warning-icon"><Warning /></el-icon>
      <span>有1个摄像头的视频源中断，该设备的AI检测将停止运行 (仅当摄像头全部正常时AI检测才能正常运行)</span>
    </div>

    <!-- 表格 -->
    <div class="table-container">
      <el-table
        :data="filteredCameraList"
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
              <div class="camera-code">
                <el-tag type="success" size="small">在线中</el-tag>
              </div>
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

        <el-table-column label="操作" align="center" fixed="right">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="text" class="edit-btn" @click="handleEdit(scope.row)">
                区域绘制
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
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
  </div>
</template>

<script setup name="AIsSettings">
import { ref, reactive, computed, onMounted } from 'vue';
import { listCamera, updateCamera } from "@/api/deviceManage/camera";
import { ElMessage } from 'element-plus';
import { Plus, Warning, VideoCamera, ArrowRight, Picture } from '@element-plus/icons-vue';

const loading = ref(false);
const cameraList = ref([]);
const searchKeyword = ref('');
const eventKeyword = ref('');
const backgroundSnapDialogVisible = ref(false);
const currentBackgroundSnap = ref('');

// 获取摄像头列表
const getList = () => {
  loading.value = true;
  listCamera({}).then(response => {
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

// 编辑区域绘制
const handleEdit = (row) => {
  ElMessage.info('区域绘制功能暂未实现');
};

// 过滤摄像头列表
const filteredCameraList = computed(() => {
  if (!searchKeyword.value) {
    return cameraList.value;
  }
  return cameraList.value.filter(camera => {
    return camera.cameraCode.includes(searchKeyword.value) || 
           camera.cameraName.includes(searchKeyword.value);
  });
});

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

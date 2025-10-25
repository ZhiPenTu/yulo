<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" v-show="showSearch" :inline="true" label-width="100px">
      <el-form-item label="摄像头编号" prop="cameraCode">
        <el-input
          v-model="queryParams.cameraCode"
          placeholder="请输入摄像头编号"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="摄像头名称" prop="cameraName">
        <el-input
          v-model="queryParams.cameraName"
          placeholder="请输入摄像头名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="摄像头状态"
          clearable
          style="width: 240px"
        >
          <el-option label="在线" value="1" />
          <el-option label="离线" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['device:camera:add']"
        >新增摄像头</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['device:camera:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['device:camera:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="cameraList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" type="index" width="50" align="center" />
      <el-table-column label="摄像头编号" align="center" prop="cameraCode" />
      <el-table-column label="摄像头名称" align="center" prop="cameraName" />
      <el-table-column label="视频源" align="center" prop="videoSource">
        <template #default="scope">
          <span>{{ scope.row.videoSource }}</span>
          <el-button
           v-if="scope.row.streamStatus == 'active'"
            type="text"
            icon="VideoPlay"
            @click="handlePlayVideo(scope.row)"
            style="margin-left: 8px;"
            title="播放视频直播"
          />
        </template>
      </el-table-column>
      <el-table-column label="媒体代理" align="center" prop="mediaProxy" />
      <el-table-column label="关联雷达编号" align="center" prop="radarCode" />
      <el-table-column label="经纬度,偏北角" align="center" prop="coordinates" show-overflow-tooltip />
      <el-table-column label="设备状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'">
            {{ scope.row.status === '1' ? '在线' : '离线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="拉流状态" align="center" prop="streamStatus">
        <template #default="scope">
          <el-tag :type="getStreamStatusType(scope.row.streamStatus)">
            {{ getStreamStatusText(scope.row.streamStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template #default="scope">
          <el-button
            type="text"
            icon="VideoPlay"
            @click="handleStartStream(scope.row)"
            v-hasPermi="['device:camera:stream']"
            v-if="scope.row.streamStatus !== 'active'"
          >开始拉流</el-button>
          <el-button
            type="text"
            icon="VideoPause"
            @click="handleStopStream(scope.row)"
            v-hasPermi="['device:camera:stream']"
            v-if="scope.row.streamStatus == 'active'"
          >停止拉流</el-button>
          <el-button
            type="text"
            icon="Edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['device:camera:edit']"
          >修改</el-button>
          <el-button
            text
            type="danger"
            icon="Delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['device:camera:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 视频播放对话框 -->
    <el-dialog 
      title="视频直播" 
      v-model="videoDialogOpen" 
      width="800px" 
      append-to-body
      @close="handleCloseVideo"
    >
      <div class="video-container" style="text-align: center;">
        <video
          ref="videoPlayer"
          :src="videoUrl"
          controls
          autoplay
          style="width: 100%; max-height: 500px;"
          @error="handleVideoError"
        >
          您的浏览器不支持视频播放
        </video>
        <div style="margin-top: 10px; color: #666;">
          <p>摄像头: {{ currentCamera.cameraName }}</p>
          <p>直播地址: {{ videoUrl }}</p>
        </div>
      </div>
    </el-dialog>

    <!-- 添加或修改摄像头对话框 -->
    <el-dialog :title="title" v-model="open" width="600px" append-to-body>
      <el-form ref="cameraRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="摄像头编号" prop="cameraCode">
          <el-input v-model="form.cameraCode" placeholder="请输入摄像头编号" :disabled="form.id != null" />
        </el-form-item>
        <el-form-item label="摄像头名称" prop="cameraName">
          <el-input v-model="form.cameraName" placeholder="请输入摄像头名称" />
        </el-form-item>
        <el-form-item label="摄像头视频源" prop="videoSource">
          <el-input v-model="form.videoSource" placeholder="请输入摄像头视频源" />
        </el-form-item>
        <el-form-item label="媒体代理" prop="mediaProxy">
          <el-select v-model="form.mediaProxy" placeholder="请选择媒体代理" style="width: 100%">
            <el-option label="rotanova_server_node" value="rotanova_server_node" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联雷达编号" prop="radarCode">
          <el-input v-model="form.radarCode" placeholder="请输入关联雷达编号" />
        </el-form-item>
        <el-form-item label="经纬度,偏北角" prop="coordinates">
          <el-input v-model="form.coordinates" placeholder="请输入经纬度,偏北角" />
        </el-form-item>
        <el-form-item label="摄像头属性信息" prop="cameraInfo">
          <el-input
            v-model="form.cameraInfo"
            type="textarea"
            :rows="4"
            placeholder="请输入摄像头属性信息(JSON格式)"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">提 交</el-button>
          <el-button @click="cancel">重 置</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Camera">
import { listCamera, getCamera, delCamera, addCamera, updateCamera, startStream, stopStream, getStreamStatus } from "@/api/deviceManage/camera";

const { proxy } = getCurrentInstance();

const cameraList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// 视频播放相关
const videoDialogOpen = ref(false);
const videoUrl = ref("");
const currentCamera = ref({});
const videoPlayer = ref(null);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    cameraCode: null,
    cameraName: null,
    status: null
  },
  rules: {
    cameraCode: [
      { required: true, message: "摄像头编号不能为空", trigger: "blur" }
    ],
    cameraName: [
      { required: true, message: "摄像头名称不能为空", trigger: "blur" }
    ],
    videoSource: [
      { required: true, message: "摄像头视频源不能为空", trigger: "blur" }
    ],
    mediaProxy: [
      { required: true, message: "媒体代理不能为空", trigger: "change" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询摄像头列表 */
function getList() {
  loading.value = true;
  listCamera(queryParams.value).then(response => {
    cameraList.value = response.rows;
    total.value = response.total;
    // 查询每个摄像头的拉流状态
    updateStreamStatus();
    loading.value = false;
  });
}

/** 更新拉流状态 */
function updateStreamStatus() {
  cameraList.value.forEach(camera => {
    if (camera.cameraCode) {
      getStreamStatus(camera.cameraCode).then(response => {
        camera.streamStatus = response.data?.status || 'inactive';
      }).catch(() => {
        camera.streamStatus = 'inactive';
      });
    }
  });
}

/** 获取拉流状态显示文本 */
function getStreamStatusText(status) {
  switch (status) {
    case 'active':
      return '拉流中';
    case 'inactive':
      return '未拉流';
    case 'error':
      return '拉流异常';
    default:
      return '未知';
  }
}

/** 获取拉流状态标签类型 */
function getStreamStatusType(status) {
  switch (status) {
    case 'active':
      return 'success';
    case 'inactive':
      return 'info';
    case 'error':
      return 'danger';
    default:
      return 'warning';
  }
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    cameraCode: null,
    cameraName: null,
    videoSource: null,
    mediaProxy: null,
    radarCode: null,
    coordinates: null,
    cameraInfo: null,
    status: "1"
  };
  proxy.resetForm("cameraRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "新增摄像头";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const id = row.id || ids.value;
  getCamera(id).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "编辑摄像头";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["cameraRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateCamera(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addCamera(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const cameraIds = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除摄像头编号为"' + (row.cameraCode || ids.value) + '"的数据项？').then(function() {
    return delCamera(cameraIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 开始拉流操作 */
function handleStartStream(row) {
  if (!row.cameraCode || !row.videoSource) {
    proxy.$modal.msgError("摄像头编号或视频源不能为空");
    return;
  }
  
  const streamData = {
    cameraCode: row.cameraCode,
    videoSource: row.videoSource,
    mediaProxy: row.mediaProxy || 'rotanova_server_node'
  };
  
  proxy.$modal.confirm('是否确认开始拉流摄像头"' + row.cameraName + '"？').then(function() {
    return startStream(streamData);
  }).then(() => {
    proxy.$modal.msgSuccess("开始拉流成功");
    // 更新当前行的拉流状态
    row.streamStatus = 'active';
    // 可选：重新查询列表以获取最新状态
    // getList();
  }).catch((error) => {
    proxy.$modal.msgError("开始拉流失败：" + (error.msg || error.message || "未知错误"));
  });
}

/** 停止拉流操作 */
function handleStopStream(row) {
  if (!row.cameraCode) {
    proxy.$modal.msgError("摄像头编号不能为空");
    return;
  }
  
  const streamData = {
    cameraCode: row.cameraCode,
    mediaProxy: row.mediaProxy || 'rotanova_server_node'
  };
  
  proxy.$modal.confirm('是否确认停止拉流摄像头"' + row.cameraName + '"？').then(function() {
    return stopStream(streamData);
  }).then(() => {
    proxy.$modal.msgSuccess("停止拉流成功");
    // 更新当前行的拉流状态
    row.streamStatus = 'inactive';
    // 可选：重新查询列表以获取最新状态
    // getList();
  }).catch((error) => {
    proxy.$modal.msgError("停止拉流失败：" + (error.msg || error.message || "未知错误"));
  });
}

/** 播放视频直播 */
function handlePlayVideo(row) {
  if (!row.cameraCode) {
    proxy.$modal.msgError("摄像头编号不能为空");
    return;
  }
  
  currentCamera.value = row;
  videoUrl.value = `http://35.46.5.76:8080/live/${row.cameraCode}.live.mp4`;
  videoDialogOpen.value = true;
}

/** 关闭视频播放 */
function handleCloseVideo() {
  videoDialogOpen.value = false;
  if (videoPlayer.value) {
    videoPlayer.value.pause();
    videoPlayer.value.currentTime = 0;
  }
  videoUrl.value = "";
  currentCamera.value = {};
}

/** 视频播放错误处理 */
function handleVideoError(event) {
  console.error('视频播放错误:', event);
  proxy.$modal.msgError("视频播放失败，请检查摄像头是否在线或直播地址是否正确");
}

getList();
</script>
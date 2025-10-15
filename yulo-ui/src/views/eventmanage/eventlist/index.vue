<template>
    <div class="event-list-page">
        <el-card class="toolbar" shadow="never">
            <el-form :inline="true" :model="query" label-width="80px">
                <el-form-item label="事件类型">
                    <el-select v-model="query.eventType" style="width: 180px;" placeholder="选择事件类型" clearable>
                        <el-option v-for="item in eventTypeOptions" :key="item.id" :label="item.name"
                            :value="item.id" />
                    </el-select>
                </el-form-item>
                <!-- <el-form-item label="摄像头">
                    <el-input v-model="query.linkCameraId" placeholder="摄像头ID" clearable />
                </el-form-item> -->
                <el-form-item label="开始时间">
                    <el-date-picker
                        v-model="query.startTime"
                        type="datetime"
                        placeholder="选择开始时间"
                        style="width: 200px;"
                        clearable
                        value-format="YYYY-MM-DD HH:mm:ss"
                    />
                </el-form-item>
                <el-form-item label="结束时间">
                    <el-date-picker
                        v-model="query.endTime"
                        type="datetime"
                        placeholder="选择结束时间"
                        style="width: 200px;"
                        clearable
                        value-format="YYYY-MM-DD HH:mm:ss"
                    />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="handleQuery">查询</el-button>
                    <el-button @click="resetQuery">重置</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card class="list" shadow="never">
            <el-table :data="list" style="width: 100%" v-loading="loading">
                <!-- <el-table-column prop="eventCode" label="事件编码" min-width="280" /> -->
                <el-table-column prop="eventType" label="类型" width="200">
                    <template #default="{ row }">
                        {{ row.eventType + "「" + formatEventType(row.eventType)+ "」" }}
                    </template>
                </el-table-column>
                <el-table-column label="路方目标类型" width="120">
                    <template #default="{ row }">
                        <el-tag
                            v-if="row.eventDetail.length > 0 && row.eventDetail[0].classification_category == 'roadCrew'"
                            type="primary">路方车辆</el-tag>
                        <el-tag
                            v-else-if="row.eventDetail.length > 0 && row.eventDetail[0].classification_category == 'socialVehicle'"
                            type="primary">社会车辆</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="laneId" label="车道号" width="120">
                    <template #default="{ row }">
                        <el-tag v-for="item in row.eventDetail" :key="item.track_id" type="primary">{{ item.lane_name
                        }}</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="事件图片" width="160">
                    <template #default="{ row }">
                        <el-image v-if="row.eventPicurl" :src="row.eventPicurl" fit="cover"
                            style="width: 140px; height: 80px; cursor: pointer;"
                            :preview-src-list="buildPreviewList(row)" @click="openPreview(row)">
                            <template #error>
                                <div class="image-slot">加载失败</div>
                            </template>
                        </el-image>
                    </template>
                </el-table-column>
                <el-table-column prop="triggerType" label="触发类型" width="120">
                    <template #default="{ row }">
                        <el-tag v-if="row.triggerType == 1" type="primary">纯视觉</el-tag>
                        <el-tag v-else-if="row.triggerType == 4" type="success">雷视融合</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="eventType" label="视频" width="120">
                    <template #default="{ row }">
                        <el-tooltip content="预览视频" placement="top">
                            <span class="video-trigger" :class="{ disabled: !row.videoUrl }"
                                @click="openVideo(row.videoUrl)">
                                <el-icon>
                                    <VideoCamera />
                                </el-icon>
                            </span>
                        </el-tooltip>
                    </template>
                </el-table-column>
                <el-table-column prop="startTime" label="开始时间" width="180">
                    <template #default="{ row }">
                        {{ formatTime(row.startTime) }}
                    </template>
                </el-table-column>
                <el-table-column prop="endTime" label="结束时间" width="180">
                    <template #default="{ row }">
                        <span v-if="row.endTime">{{ formatTime(row.endTime) }}</span>
                        <el-icon v-else style="font-size: 18px;" class="is-loading">
                            <Loading />
                        </el-icon>
                    </template>

                </el-table-column>
                <el-table-column prop="location" label="事件地点" min-width="160">
                    <template #default="{ row }">
                        {{ row.location ?? '-' }}
                    </template>
                </el-table-column>

                <el-table-column label="操作" min-width="200">
                    <template #default="{ row }">
                        <!-- <el-button type="primary" link @click="openPreview(row)">下载原图</el-button>
            <el-button type="danger" link @click="downloadImage(row)">详情</el-button> -->
                        <el-button type="danger" link @click="openReportDialog(row)">误报</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination :current-page="pageNum" :page-size="pageSize" :page-sizes="[10, 20, 50]"
                    layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
                    @current-change="handlePageChange" />
            </div>
        </el-card>

        <el-dialog v-model="previewVisible" title="图片预览" width="60%" :destroy-on-close="true">
            <div class="gallery">
                <el-image v-for="(u, idx) in previewList" :key="idx" :src="u" fit="contain"
                    style="width: 100%; margin-bottom: 8px;" />
            </div>
        </el-dialog>

        <!-- 视频预览对话框 -->
        <el-dialog v-model="videoVisible" title="视频预览" width="60%" :destroy-on-close="true" @close="onVideoDialogClose">
            <div class="video-wrapper">
                <video ref="videoRef" :src="currentVideoUrl" controls autoplay preload="metadata"
                    style="width: 100%; background: #000;" />
            </div>
        </el-dialog>

        <!-- 误报信息提交对话框 -->
        <el-dialog v-model="reportDialogVisible" title="误报信息提交" width="480px" :destroy-on-close="true">
            <el-form label-width="20px">
                <el-form-item>
                    <el-checkbox-group v-model="reportTypes">
                        <el-checkbox label="事件检出错误" value="eventTypeError" />
                        <el-checkbox label="目标类型错误" value="targetTypeError" />
                        <el-checkbox label="所在车道错误" value="laneError" />
                    </el-checkbox-group>
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="reportDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="submitReport">提交</el-button>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
// @ts-ignore
import { listEvents, uploadEventImage, saveEvent, submitFalseReport } from '@/api/eventManage/event'
// @ts-ignore
import request from '@/utils/request'

const loading = ref(false)
const list = ref<any[]>([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

const query = reactive({
    eventType: '',
    linkCameraId: '',
    startTime: '',
    endTime: ''
})

const eventTypeOptions = ref([
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "100",
        "name": "交通事故"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "222",
        "name": "烟雾"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "301",
        "name": "暴雨"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "304",
        "name": "强风"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "305",
        "name": "大雾"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "331",
        "name": "能见度低"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "401",
        "name": "道路遗撒"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "405",
        "name": "行人闯入"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "421",
        "name": "压实线"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "422",
        "name": "超速驾驶"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "423",
        "name": "低速驾驶"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "424",
        "name": "异常停车"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "425",
        "name": "占用应急车道"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "426",
        "name": "货车驶入快车道"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "427",
        "name": "穿越导流线区域"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "451",
        "name": "非机动车闯入"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "501",
        "name": "占道施工"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "708",
        "name": "交通拥堵"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "721",
        "name": "车辆逆行"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "723",
        "name": "频繁变道"
    },
    {
        "createBy": null,
        "createTime": null,
        "updateBy": null,
        "updateTime": null,
        "remark": null,
        "id": "801",
        "name": "危化品车驶入"
    }
])
/**
 * 根据 eventType 值在 eventTypeOptions 中查找对应 name
 * @param eventType 事件类型 id
 * @returns 对应 name，未找到则返回原值
 */
const formatEventType = (eventType: string | number): string => {
    const item = eventTypeOptions.value.find(opt => opt.id == eventType)
    return item ? item.name : String(eventType)
}




const previewVisible = ref(false)
const previewList = ref<string[]>([])
const videoVisible = ref(false)
const currentVideoUrl = ref('')
const videoRef = ref<HTMLVideoElement | null>(null)

// 误报弹窗相关
const reportDialogVisible = ref(false)
const reportTypes = ref<string[]>([])
const currentReportRow = ref<any | null>(null)

const formatProfileUrl = (url: string) => {
    if (!url) return ''
    // RuoYi配置静态资源前缀 /profile
    return url.startsWith('/profile') ? url : `/profile${url}`
}

const buildPreviewList = (row: any) => {
    const items: string[] = []
    if (row.eventPicurl) items.push(row.eventPicurl)
    return items
}

const openPreview = (row: any) => {
    previewList.value = buildPreviewList(row)
    previewVisible.value = true
}


const openVideo = (url?: string) => {
    if (!url) {
        ElMessage.warning('暂无视频可预览')
        return
    }
    currentVideoUrl.value = url
    videoVisible.value = true
    nextTick(() => {
        const v = videoRef.value
        if (v) {
            try {
                v.load()
                // play 返回 Promise，捕获以避免未处理拒绝
                v.play()?.catch(() => { })
            } catch { }
        }
    })
}

const onVideoDialogClose = () => {
    const v = videoRef.value
    if (v) {
        try {
            v.pause()
            v.currentTime = 0
            v.src = ''
        } catch { }
    }
    currentVideoUrl.value = ''
}

const onVideoError = () => {
    ElMessage.error('视频加载失败')
}

const openReportDialog = (row: any) => {
    currentReportRow.value = row
    reportTypes.value = []
    reportDialogVisible.value = true
}

const submitReport = async () => {
    if (!currentReportRow.value) return
    if (!reportTypes.value.length) {
        ElMessage.warning('请至少选择一个误报类型')
        return
    }
    try {
        await submitFalseReport({
            eventCode: currentReportRow.value.eventCode,
            reportDec: reportTypes.value
        })
        ElMessage.success('误报信息已提交')
        reportDialogVisible.value = false
    } catch (e) {
        ElMessage.error('提交失败')
    }
}

const downloadImage = async (row: any) => {
    if (!row.eventPicurl) return
    const url = formatProfileUrl(row.eventPicurl)
    try {
        const res = await request({ url, method: 'get', responseType: 'blob' })
        const blob = new Blob([res])
        const a = document.createElement('a')
        a.href = URL.createObjectURL(blob)
        a.download = `${row.eventCode || 'event'}.jpg`
        a.click()
        URL.revokeObjectURL(a.href)
    } catch (e) {
        ElMessage.error('下载失败')
    }
}

const getList = async () => {
    loading.value = true
    try {
        const params = { ...query, pageNum: pageNum.value, pageSize: pageSize.value }
        const { rows, total: t } = await listEvents(params)
        for (const row of rows || []) {
            try {
                row.eventDetail = JSON.parse(row.eventDetail)
            } catch (e) {
                row.eventDetail = []
            }
            console.log(row.eventDetail)
        }
        list.value = rows || []
        total.value = t || 0
    } finally {
        loading.value = false
    }
}

const handleQuery = () => {
    pageNum.value = 1
    getList()
}

const resetQuery = () => {
    query.eventType = ''
    query.linkCameraId = ''
    query.startTime = ''
    query.endTime = ''
    handleQuery()
}

const customUpload = async (options: any) => {
    const { file, onError, onSuccess } = options
    const formData = new FormData()
    formData.append('file', file)
    try {
        const url = await uploadEventImage(formData)
        // 将上传成功的图片保存为事件，可由后端生成eventCode或前端提供
        const event = {
            eventCode: `${Date.now()}`,
            eventType: query.eventType || 'congestion',
            eventPicurl: url?.data || url,
            startTime: new Date().toISOString(),
            linkCameraId: query.linkCameraId || 'camera-1'
        }
        await saveEvent(event)
        ElMessage.success('上传并保存成功')
        getList()
        onSuccess(url)
    } catch (e) {
        ElMessage.error('上传失败')
        onError(e)
    }
}

const formatTime = (time: string) => {
    if (!time) return ''
    return new Date(time).toLocaleString()
}

const handlePageChange = (p: number) => {
    pageNum.value = p
    getList()
}

const handleSizeChange = (s: number) => {
    pageSize.value = s
    pageNum.value = 1
    getList()
}

setInterval(() => {
    getList()
}, 5000)

onMounted(() => {
    getList()
})
</script>

<style scoped>
.event-list-page {
    padding: 12px;
}

.toolbar {
    margin-bottom: 12px;
}

.pagination {
    margin-top: 12px;
    display: flex;
    justify-content: flex-end;
}

.image-slot {
    width: 140px;
    height: 80px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f5f7fa;
    color: #909399;
}

.video-trigger {
    cursor: pointer;
    display: inline-flex;
    align-items: center;
}

.video-trigger.disabled {
    cursor: not-allowed;
    opacity: 0.4;
}

.video-wrapper {
    width: 100%;
}
</style>
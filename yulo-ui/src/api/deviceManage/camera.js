import request from '@/utils/request'

// 查询摄像头列表
export function listCamera(query) {
  return request({
    url: '/device/camera/list',
    method: 'get',
    params: query
  })
}

// 查询摄像头详细
export function getCamera(id) {
  return request({
    url: '/device/camera/' + id,
    method: 'get'
  })
}

// 新增摄像头
export function addCamera(data) {
  return request({
    url: '/device/camera',
    method: 'post',
    data: data
  })
}

// 修改摄像头
export function updateCamera(data) {
  return request({
    url: '/device/camera',
    method: 'put',
    data: data
  })
}

// 删除摄像头
export function delCamera(id) {
  return request({
    url: '/device/camera/' + id,
    method: 'delete'
  })
}

// 开始拉流
export function startStream(data) {
  return request({
    url: '/device/camera/startStream',
    method: 'post',
    data: data
  })
}

// 停止拉流
export function stopStream(data) {
  return request({
    url: '/device/camera/stopStream',
    method: 'post',
    data: data
  })
}

// 查询拉流状态
export function getStreamStatus(cameraCode) {
  return request({
    url: '/device/camera/streamStatus/' + cameraCode,
    method: 'get'
  })
}
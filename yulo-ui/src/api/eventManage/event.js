import request from '@/utils/request'

export function uploadEventImage(data) {
  return request({
    url: '/eventManage/event/upload',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data
  })
}

export function saveEvent(data) {
  return request({
    url: '/eventManage/event/save',
    method: 'post',
    data
  })
}

export function listEvents(params) {
  return request({
    url: '/eventManage/event/list',
    method: 'get',
    params
  })
}

export function submitFalseReport(data) {
  return request({
    url: '/eventManage/event/report',
    method: 'post',
    data
  })
}
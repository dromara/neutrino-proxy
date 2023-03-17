import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/port-group/page',
    method: 'get',
    params: query
  })
}

export function portGroupList() {
  return request({
    url: '/port-group/list',
    method: 'get'
  })
}

export function updateEnableStatus(id, enable) {
  return request({
    url: '/port-group/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function createPortGroup(data) {
  return request({
    url: '/port-group/create',
    method: 'post',
    data
  })
}

export function deleteGroup(id) {
  return request({
    url: '/port-group/delete',
    method: 'post',
    params: {
      id: id
    }
  })
}

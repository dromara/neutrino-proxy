import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/port-pool/page',
    method: 'get',
    params: query
  })
}

export function portPoolList() {
  return request({
    url: '/port-pool/list',
    method: 'get'
  })
}
export function availablePortList(query) {
  return request({
    url: '/port-pool/get-available-port-list',
    method: 'get',
    params: query
  })
}

export function updateEnableStatus(id, enable) {
  return request({
    url: '/port-pool/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function createPortPool(data) {
  return request({
    url: '/port-pool/create',
    method: 'post',
    data
  })
}

export function updatePortPool(data) {
  return request({
    url: '/port-pool/update',
    method: 'post',
    data
  })
}

export function deletePortPool(id) {
  return request({
    url: '/port-pool/delete',
    method: 'post',
    params: {
      id: id
    }
  })
}

export function deleteBatchPortPool(ids) {
  return request({
    url: '/port-pool/delete-batch',
    method: 'post',
    data: {
      ids: ids
    }
  })
}

export function portAvailable(query) {
  return request({
    url: '/port-pool/port-available',
    method: 'get',
    params: query
  })
}

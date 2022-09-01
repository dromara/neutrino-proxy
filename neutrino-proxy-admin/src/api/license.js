import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/license/page',
    method: 'get',
    params: query
  })
}

export function licenseList() {
  return request({
    url: '/license/list',
    method: 'get'
  })
}

export function createLicense(data) {
  return request({
    url: '/license/create',
    method: 'post',
    data
  })
}

export function updateLicense(data) {
  return request({
    url: '/license/update',
    method: 'post',
    data
  })
}

export function updateEnableStatus(id, enable) {
  return request({
    url: '/license/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function deleteLicense(id) {
  return request({
    url: '/license/delete',
    method: 'post',
    params: {
      id: id
    }
  })
}

import request from '@/utils/request'

export function domainNameBindInfo(query) {
  return request({
    url: '/domain-name/bind-info',
    method: 'get',
    params: query
  })
}
export function fetchList(query) {
  return request({
    url: '/domain/page',
    method: 'get',
    params: query
  })
}

export function updateEnableStatus(id, enable) {
  return request({
    url: '/domain/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function createDomain(data) {
  return request({
    url: '/domain/create',
    method: 'post',
    data
  })
}

export function updateDomain(data) {
  return request({
    url: '/domain/update',
    method: 'post',
    data
  })
}

export function deleteDomain(id) {
  return request({
    url: '/domain/delete',
    method: 'post',
    params: {
      id: id
    }
  })
}

export function updateDefaultStatus(id, isDefault) {
  return request({
    url: '/domain/update/default-status',
    method: 'post',
    data: {
      id: id,
      isDefault: isDefault
    }
  })
}

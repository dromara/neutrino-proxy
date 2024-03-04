import request from '@/utils/request'

export function domainNameBindInfo(query) {
  return request({
    url: '/domain/bind-info',
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


export function mappingModify(data) {
  return request({
    url: '/port-mapping/update',
    method: 'post',
    data
  })
}

export function deleteMapping(id) {
  return request({
    url: '/port-mapping/delete',
    method: 'post',
    params: {
      id: id
    }
  })
}

export function updateEnableStatus(id, enable) {
  return request({
    url: '/port-mapping/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function portMappingBindSecurityGroup(id, securityGroupId) {
  return request({
    url: '/port-mapping/bind/security-group',
    method: 'post',
    data: {
      id: id,
      securityGroupId: securityGroupId
    }
  })
}

export function portMappingUnbindSecurityGroup(id) {
  return request({
    url: `/port-mapping/unbind/security-group?id=${id}`,
    method: 'post'
  })
}


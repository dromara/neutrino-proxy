import request from '@/utils/request'


export function fetchList(query) {
  return request({
    url: '/domain/page',
    method: 'get',
    params: query
  })
}

export function mappingModify(data) {
  return request({
    url: '/domain/modify',
    method: 'post',
    data
  })
}

export function deleteMapping(id) {
  return request({
    url: '/domain/delete/' + id,
    method: 'get'
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

export function portMappingBindSecurityGroup(id, securityGroupId) {
  return request({
    url: '/domain/bind/security-group',
    method: 'post',
    data: {
      id: id,
      securityGroupId: securityGroupId
    }
  })
}

export function portMappingUnbindSecurityGroup(id) {
  return request({
    url: `/domain/unbind/security-group?id=${id}`,
    method: 'post'
  })
}

export function domainAvailable(query) {
  return request({
    url: '/domain/available',
    method: 'get',
    params: query
  })
}

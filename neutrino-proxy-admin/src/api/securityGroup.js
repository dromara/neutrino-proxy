import request from '@/utils/request'

export function fetchGroupPage(query) {
  return request({
    url: '/security/group/page',
    method: 'get',
    params: query
  })
}

export function fetchGroupList() {
  return request({
    url: '/security/group/list',
    method: 'get'
  })
}

export function fetchGroupDetail(query) {
  return request({
    url: '/security/group/detail',
    method: 'get',
    params: query
  })
}

export function createGroup(data) {
  return request({
    url: `/security/group/create`,
    method: 'post',
    data
  })
}

export function updateGroup(data) {
  return request({
    url: `/security/group/update`,
    method: 'post',
    data
  })
}

export function deleteGroup(data) {
  return request({
    url: '/security/group/delete',
    method: 'post',
    data
  })
}

export function updateGroupEnableStatus(id, enable) {
  return request({
    url: '/security/group/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function enableGroup(query) {
  return request({
    url: '/security/group/enable',
    method: 'post',
    params: query
  })
}

export function disableGroup(query) {
  return request({
    url: '/security/group/disable',
    method: 'post',
    params: query
  })
}

export function fetchRuleList(query) {
  return request({
    url: '/security/rule/list',
    method: 'get',
    params: query
  })
}

export function createRule(data) {
  return request({
    url: `/security/rule/create`,
    method: 'post',
    data
  })
}

export function updateRule(data) {
  return request({
    url: `/security/rule/update`,
    method: 'post',
    data
  })
}
export function deleteRule(query) {
  return request({
    url: '/security/rule/delete',
    method: 'post',
    params: query
  })
}

export function enableRule(ruleId) {
  return request({
    url: `/security/rule/enable?ruleId=${ruleId}`,
    method: 'post'
  })
}

export function disableRule(ruleId) {
  return request({
    url: `/security/rule/disable?ruleId=${ruleId}`,
    method: 'post'
  })
}

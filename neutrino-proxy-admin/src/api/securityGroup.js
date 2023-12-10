import request from '@/utils/request'

const baseUri = '/security';

export function fetchGroupList() {
  return request({
    url: `${baseUri}/group/list`,
    method: 'get'
  })
}

export function fetchGroupOne(groupId) {
  return request({
    url: `${baseUri}/group/detail?groupId=${groupId}`,
    method: 'get'
  })
}

export function createGroup(data) {
  return request({
    url: `${baseUri}/group/create`,
    method: 'post',
    data
  })
}

export function updateGroup(data) {
  return request({
    url: `${baseUri}/group/update`,
    method: 'post',
    data
  })
}

export function deleteGroup(groupId) {
  return request({
    url: `${baseUri}/group/delete?groupId=${groupId}`,
    method: 'post'
  })
}

export function enableGroup(groupId) {
  return request({
    url: `${baseUri}/group/enable?groupId=${groupId}`,
    method: 'post'
  })
}

export function disableGroup(groupId) {
  return request({
    url: `${baseUri}/group/disable?groupId=${groupId}`,
    method: 'post'
  })
}

export function fetchRuleList(groupId) {
  return request({
    url: `${baseUri}/rule/list?groupId=${groupId}`,
    method: 'get'
  })
}

export function createRule(data) {
  return request({
    url: `${baseUri}/rule/create`,
    method: 'post',
    data
  })
}

export function updateRule(data) {
  return request({
    url: `${baseUri}/rule/update`,
    method: 'post',
    data
  })
}
export function deleteRule(ruleId) {
  return request({
    url: `${baseUri}/rule/delete?ruleId=${ruleId}`,
    method: 'post'
  })
}

export function enableRule(ruleId) {
  return request({
    url: `${baseUri}/rule/enable?ruleId=${ruleId}`,
    method: 'post'
  })
}

export function disableRule(ruleId) {
  return request({
    url: `${baseUri}/rule/disable?ruleId=${ruleId}`,
    method: 'post'
  })
}

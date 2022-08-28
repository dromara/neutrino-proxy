import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/user/page',
    method: 'get',
    params: query
  })
}

export function fetchUser() {
  return request({
    url: '/user/detail',
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/user/create',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/user/update',
    method: 'post',
    data
  })
}

export function currentUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

export function updateEnableStatus(id, enable) {
  return request({
    url: '/user/update/enable-status',
    method: 'post',
    data: {
      id: id,
      enable: enable
    }
  })
}

export function hello() {
  return request({
    url: '/test1/hello',
    method: 'GET'
  })
}


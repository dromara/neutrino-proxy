import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/protocal/page',
    method: 'get',
    params: query
  })
}

export function protocalList(query) {
  return request({
    url: '/protocal/list',
    method: 'get',
    params: query
  })
}

import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/user-login-record/page',
    method: 'get',
    params: query
  })
}

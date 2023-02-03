import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/client-connect-record/page',
    method: 'get',
    params: query
  })
}

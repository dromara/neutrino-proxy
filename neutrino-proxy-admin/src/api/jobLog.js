import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/job-log/page',
    method: 'get',
    params: query
  })
}

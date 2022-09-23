import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/job-Log/page',
    method: 'get',
    params: query
  })
}

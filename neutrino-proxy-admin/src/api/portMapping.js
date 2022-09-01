import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/port-mapping/page',
    method: 'get',
    params: query
  })
}

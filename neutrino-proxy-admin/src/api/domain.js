import request from '@/utils/request'

export function domainNameBindInfo(query) {
  return request({
    url: '/domain/bind-info',
    method: 'get',
    params: query
  })
}

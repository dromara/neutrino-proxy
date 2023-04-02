import request from '@/utils/request'

export function domainNameBindInfo(query) {
  return request({
    url: '/domain-name/bind-info',
    method: 'get',
    params: query
  })
}

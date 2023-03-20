import request from '@/utils/request'
export function fetchUserFlowReportList(query) {
  return request({
    url: '/report/user/flow-report/page',
    method: 'get',
    params: query
  })
}

export function fetchLicenseFlowReportList(query) {
  return request({
    url: '/report/license/flow-report/page',
    method: 'get',
    params: query
  })
}

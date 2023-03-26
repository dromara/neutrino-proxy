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

export function fetchUserFlowMonthReportList(query) {
  return request({
    url: '/report/user/flow-month-report/page',
    method: 'get',
    params: query
  })
}

export function fetchLicenseFlowMonthReportList(query) {
  return request({
    url: '/report/license/flow-month-report/page',
    method: 'get',
    params: query
  })
}

export function homeData(query) {
  return request({
    url: '/report/home/data-view',
    method: 'get',
    params: query
  })
}

import request from '@/utils/request'

export function loginByUsername(loginName, loginPassword) {
  const data = {
    loginName,
    loginPassword
  }
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

export function getUserInfo(token) {
  return request({
    url: '/user/info',
    method: 'get',
    params: { token }
  })
}


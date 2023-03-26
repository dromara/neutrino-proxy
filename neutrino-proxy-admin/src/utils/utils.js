const SIZE_UNINTS = ['B', 'KB', 'MB', 'GB', 'TB']
const SIZE_SYSTEM = 1024
/**
 * 根据字节数获取大小描述
 * 1、小于1024字节的以B为单位
 * 2、小于1024KB的以KB为单位
 * 3、小于1024M的以MB为单位
 * 4、小于1024G的以GB为单位
 * 5、其他以TB为单位
 */
export function getSizeDescByByteCount(byteCount) {
  if (byteCount <= 0) {
    return '0B'
  }

  let res = byteCount
  let index = 0
  while (index < SIZE_UNINTS.length && res >= SIZE_SYSTEM) {
    res /= SIZE_SYSTEM
    index++
  }

  if (index >= SIZE_UNINTS.length) {
    index = SIZE_UNINTS.length - 1
    res *= 1024
  }

  return parseFloat(res.toFixed(2)) + SIZE_UNINTS[index]
}

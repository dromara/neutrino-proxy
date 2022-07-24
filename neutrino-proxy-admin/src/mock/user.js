
const List = [
  {
    id: 1,
    userName: '张三',
    license: '79419a1a8691413aa5e845b9e3e90051',
    createTime: 1658666005316,
    updateTime: 1658666005316,
    status: 1
  },
  {
    id: 2,
    userName: '李四',
    license: '9352b1c25f564c81a5677131d7769876',
    createTime: 1658666329809,
    updateTime: 1658666329809,
    status: 2
  }
]

export default {
  getList: config => {
    console.log('111---', config)
    return {
      total: List.length,
      items: List
    }
  },
  getUser: () => ({
    id: 120000000001,
    author: { key: 'mockPan' },
    source_name: '原创作者',
    category_item: [{ key: 'global', name: '全球' }],
    comment_disabled: true,
    content: '<p>我是测试数据我是测试数据</p><p><img class="wscnph" src="https://wpimg.wallstcn.com/4c69009c-0fd4-4153-b112-6cb53d1cf943" data-wscntype="image" data-wscnh="300" data-wscnw="400" data-mce-src="https://wpimg.wallstcn.com/4c69009c-0fd4-4153-b112-6cb53d1cf943"></p>"',
    content_short: '我是测试数据',
    display_time: +new Date(),
    image_uri: 'https://wpimg.wallstcn.com/e4558086-631c-425c-9430-56ffb46e70b3',
    platforms: ['a-platform'],
    source_uri: 'https://github.com/PanJiaChen/vue-element-admin',
    status: 'published',
    tags: [],
    title: 'vue-element-admin'
  }),
  createUser: () => ({
    data: 'success'
  }),
  updateUser: () => ({
    data: 'success'
  })
}

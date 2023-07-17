# 1.x规划
- Bug
- 优化
  - 添加端口映射时，验证端口是否被其他服务占用
  - 拉下搜索license，支持模糊搜索
  - license下拉用户搜索，支持模糊搜索
- UDP支持
- 官网文档完善
  - HTTPS配置说明
- 协议重构
- 代码重构

# 1.8.6
- [x] 端口映射选择端口支持分页
- [ ] 新增/更新端口映射，增加端口占用检测
- [ ] 增加服务端/客户端jar式一键部署脚本
- [ ] 拉下搜索license，支持模糊搜索
- [ ] license下拉用户搜索，支持模糊搜索
- [ ] 排查解决问题：https://gitee.com/dromara/neutrino-proxy/issues/I7LGLB

# Bug
- 指令通达被close的问题，org.dromara.neutrinoproxy.server.proxy.core.ProxyTunnelChannelHandler.channelInactive 
- windows环境下直接运行发布版的jar包，日志输出乱码
- 代理mysql时，使用未开启远程访问的账号走代理访问mysql，代理客户端出现断开现象
- 客户端连接映射某个端口以后，如果在服务器端禁用了，没有立刻反映出来，要灯客户端重连以后才会屏蔽被禁用的端口。

# 2.x规划
- 插件开发
  - [ ] neutrino-proxy-solon-plugin
  - [ ] neutrino-proxy-jetbrains-plugin
  - [ ] neutrino-proxy-starter (SpringBoot)
- 基础优化
  - [ ] 支持批量端口映射
  - [ ] 代理协议规范化，方便后续更好扩展、支持不同语言客户端接入
- 监控运维
  - [ ] 监控服务端状态
  - [ ] 支持调整服务端端口、证书
  - [ ] 服务端版本、协议版本、客户端版本管理
  - [ ] 服务端日志
  - [ ] 支持持管理后台动态调整日志级别。
  - [ ] 动态调整转发、报文、心跳输出
- client+计划启动
  - [ ] 安卓客户端
  - [ ] 基于electron-egg的多平台客户端

# 3.x规划
- [ ] 支持针对用户限速、限流
- [ ] 支持P2P穿透
- [ ] 支持原生编译

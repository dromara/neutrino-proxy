# 1.x剩余规划
- [ ] 增加针对https的支持

# Bug
- 指令通达被close的问题，org.dromara.neutrinoproxy.server.proxy.core.ProxyTunnelChannelHandler.channelInactive 
- windows环境下直接运行发布版的jar包，日志输出乱码
- 代理mysql时，使用未开启远程访问的账号走代理访问mysql，代理客户端出现断开现象

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
- client+计划启动
  - [ ] 安卓客户端
  - [ ] 基于electron-egg的多平台客户端

# 3.x规划
- [ ] 支持针对用户限速、限流
- [ ] 支持P2P穿透
- [ ] 支持原生编译

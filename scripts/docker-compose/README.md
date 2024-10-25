## 项目简介
- [中微子代理(neutrino-proxy)](https://gitee.com/dromara/neutrino-proxy) 是一款基于Solon、Netty的内网穿透神器。该项目采用最为宽松的MIT协议，因此您可以对它进行复制、修改、传播并用于任何个人或商业行为。
- 市面上基于内网穿透的常见产品有：花生壳、TeamView、cpolar等。
- 常见的使用场景：
    - 本地开发调试第三方回调
    - 本地开发异地接口连调
    - 远程登录内网windows机器
    - 将本地服务映射到外网，用于演示
- Gitee仓库：https://gitee.com/dromara/neutrino-proxy
- GitCode仓库：https://gitcode.com/dromara/neutrino-proxy
- GitHub仓库：https://github.com/dromara/neutrino-proxy
- 官网地址: https://neutrino-proxy.dromara.org

## 主要特点：
- 1、流量监控：首页图表、报表管理多维度流量监控。全方位掌握实时、历史代理数据。
- 2、用户/License：支持多用户、多客户端使用。后台禁用实时生效。
- 3、端口池：对外端口统一管理，支持用户、License独占端口。
- 4、端口映射：新增、编辑、删除、禁用实时生效。
- 5、Docker：服务端/客户端支持Docker一键部署。
- 6、SSL证书：隧道通信支持SSL加密，保护您的数据安全。
- 7、域名映射：支持绑定子域名，方便本地调试三方回调
- 8、多种协议：支持代理TCP、HTTP、HTTPS、UDP协议
- 9、原生部署：支持编译为原生可执行文件，更低部署门槛、更少内存占用
- 10、安全组：支持黑/白名单IP访问限制
- 11、限速：支持对License、端口映射限制上传/下载速度
- 12、采用最为宽松的MIT协议，免去你的后顾之忧

## 镜像版本&地址
- 版本：当前最新版本为`2.0.1`，可直接使用`latest`拉取最新镜像
- neutrino-proxy-server
	- DockerHub镜像地址：aoshiguchen/neutrino-proxy-server:latest
	- 阿里云镜像地址：neutrino-proxy registry.cn-hangzhou.aliyuncs.com/asgc/neutrino-proxy:latest
- neutrino-proxy-client
	- DockerHub镜像地址：aoshiguchen/neutrino-proxy-client:latest
	- 阿里云镜像地址：registry.cn-hangzhou.aliyuncs.com/asgc/neutrino-proxy-client:latest

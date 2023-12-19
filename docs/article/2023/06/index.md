# NeutrinoProxy2.0.1发布，新增IP访问拦截+限速支持

## 项目简介
- [中微子代理(neutrino-proxy)](https://gitee.com/asgc/neutrino-proxy) 是一款基于netty的内网穿透神器。该项目采用最为宽松的MIT协议，因此您可以对它进行复制、修改、传播并用于任何个人或商业行为。
- 市面上基于内网穿透的常见产品有：花生壳、TeamView、cpolar等。
- 常见的使用场景：
    - 本地开发调试第三方回调
    - 本地开发异地接口连调
    - 远程登录内网windows机器
    - 将本地服务映射到外网，用于演示
- Gitee仓库：https://gitee.com/dromara/neutrino-proxy
- Github仓库：https://github.com/dromara/neutrino-proxy
- 官网地址1: https://neutrino-proxy.dromara.org
- 官网地址2: https://dromara.gitee.io/neutrino-proxy

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

## 本次更新内容
- jdk版本升级为21
- 新增安全组模块，支持黑名单、白名单限制
- 支持对用户、license限速
- 修复HTTP(S)映射时使用tcp端口访问正常，使用域名访问偶现一直loading的问题
- 修复HTTP(S)映射时使用映射的域名上传文件时，连接中断的问题

## 安装使用说明
- 快速上手：https://neutrino-proxy.dromara.org/neutrino-proxy/pages/793dcb/
- 升级须知： 
  - jdk版本升级为了jdk21，jar部署时请注意
  - 涉及到表结构变更，执行增量SQL：https://gitee.com/dromara/neutrino-proxy/blob/master/neutrino-proxy-server/src/main/resources/sql/mysql/update/UPDATE-20231215.SQL

## 运行示例
#### License限速
<img src="./license2.png" width="100%"/>

#### 端口映射限速
<img src="./port-mapping2.png" width="100%"/>

#### 安全组
<img src="./security-group1.png" width="100%"/>
<img src="./security-rule1.png" width="100%"/>

## 联系我们
笔者时间、能力有限，且开源项目非一朝一夕之事，存在众多问题亦在所难免。使用、学习过程中有任何问题欢迎大家与我联系。

对项目有什么想法或者建议，可以加我微信拉交流群，或者创建[issues](https://gitee.com/dromara/neutrino-proxy/issues)，一起完善项目

- 微信号：yuyunshize
- Email: aoshiguchen@dromara.org
- 微信二维码（添加时请备注"中微子进群"）：
  <img src="MyWeChatQRCode.jpeg" width="100%"/>

package org.dromara.neutrinoproxy.client.starter.ssh;

import lombok.Data;

@Data
public class SSHProxy {
    private String sshId;

    //跳板机服务器公网IP
    private String host;
    //跳板机服务器登录名
    private String username;
    //跳板机登陆密码
    private String password;
    //本地的端口
    private int localPort;

    //需代理服务器端口号
    private int remotePort;
    //需代理服务器ip
    private String remoteHost;

}
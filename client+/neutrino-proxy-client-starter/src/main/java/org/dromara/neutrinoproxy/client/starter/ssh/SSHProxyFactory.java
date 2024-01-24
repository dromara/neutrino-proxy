package org.dromara.neutrinoproxy.client.starter.ssh;

public interface SSHProxyFactory {
    void openTunnel();
    void closeTunnel();
}
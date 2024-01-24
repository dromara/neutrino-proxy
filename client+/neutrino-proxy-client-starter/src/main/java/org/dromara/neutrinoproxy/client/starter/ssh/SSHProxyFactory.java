package org.dromara.neutrinoproxy.client.starter.ssh;
/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
public interface SSHProxyFactory {
    void openTunnel();
    void closeTunnel();
}
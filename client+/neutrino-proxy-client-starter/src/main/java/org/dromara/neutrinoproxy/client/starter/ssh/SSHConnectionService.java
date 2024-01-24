package org.dromara.neutrinoproxy.client.starter.ssh;

/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
public interface SSHConnectionService {
    String addConnection(String host, String username, String password, int localPort, int remotePort,String remoteHost);
    void openTunnel(String sshId);
    void closeTunnel(String sshId);
    void closeAllTunnels();
}
package org.dromara.neutrinoproxy.client.starter.ssh;

/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
public class SSHConnectionFactory {
    public static final SSHConnectionService factory = new SSHConnectionServiceImpl();
}

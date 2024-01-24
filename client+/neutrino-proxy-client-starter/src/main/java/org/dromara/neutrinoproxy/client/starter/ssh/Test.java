package org.dromara.neutrinoproxy.client.starter.ssh;
/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
public class Test {
    public static void main(String[] args) {

        // 添加SSH连接
        String sshId = SSHConnectionFactory.factory.addConnection("194.36.209.398", "root", "xxxxxx", 1234, 6379,"xx.xx.xx.xx");
        SSHConnectionFactory.factory.openTunnel(sshId);
        try {
            Thread.sleep(100000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // 关闭SSH隧道
        SSHConnectionFactory.factory.closeTunnel(sshId);

    }
}

package org.dromara.neutrinoproxy.client.starter.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
/**
 *
 * @author: gc.x
 * @date: 2024/1/21
 */
@Slf4j
public class SSHProxyFactoryImpl implements SSHProxyFactory {
    private SSHProxy connection;
    private Session session;

    public SSHProxyFactoryImpl(SSHProxy connection) {
        this.connection = connection;
    }

    @Override
    public void openTunnel() {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(connection.getUsername(), connection.getHost(), 22);
            session.setPassword(connection.getPassword());
            // 开启调试模式，打印更多详细日志
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            config.put("userauth.gssapi-with-mic", "no");
            config.put("compression.s2c", "zlib@openssh.com,zlib,none");
            config.put("compression.c2s", "zlib@openssh.com,zlib,none");
            session.setConfig(config);
            session.setDaemonThread(true);
            JSch.setLogger(new com.jcraft.jsch.Logger() {
                public boolean isEnabled(int level) {
                    return true;
                }
                public void log(int level, String message) {
                    log.info("JSch - " + level + ": " + message);
                }
            });

            int assignedPort = session.setPortForwardingL(connection.getLocalPort(), connection.getRemoteHost(), connection.getRemotePort());
            session.connect(10000);
            if (assignedPort != -1) {
                log.info("SSH connection established successfully!");
            }  else {
                log.info("Failed to establish SSH connection!");
            }
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
        }
    }

    @Override
    public void closeTunnel() {
        if (session != null && session.isConnected()) {
            session.disconnect();
            log.info("SSH connection closed successfully!");
        }
    }
}
package org.dromara.neutrinoproxy.client.starter.ssh;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SSHConnectionServiceImpl implements SSHConnectionService {

    private Map<String, SSHProxy> connections;
    private Map<String, SSHProxyFactory> factories;

    public SSHConnectionServiceImpl() {
        connections = new HashMap<>();
        factories = new HashMap<>();
    }

    @Override
    public String addConnection(String host, String username, String password, int localPort, int remotePort,String remoteHost) {
        SSHProxy connection = new SSHProxy();
        String sshId = UUID.randomUUID().toString();
        connection.setSshId(sshId);
        connection.setHost(host);
        connection.setUsername(username);
        connection.setPassword(password);
        connection.setLocalPort(localPort);
        connection.setRemotePort(remotePort);
        connection.setRemoteHost(remoteHost);
        connections.put(connection.getSshId(), connection);
        return sshId;
    }

    @Override
    public void openTunnel(String sshId) {
        SSHProxy connection = connections.get(sshId);
        if (connection != null && !factories.containsKey(sshId)) {
            SSHProxyFactory factory = new SSHProxyFactoryImpl(connection);
            factory.openTunnel();
            factories.put(sshId, factory);
        }
    }

    @Override
    public void closeTunnel(String sshId) {
        SSHProxyFactory factory = factories.get(sshId);

        if (factory != null) {
            factory.closeTunnel();
            factories.remove(sshId);
        }
    }

    @Override
    public void closeAllTunnels() {
        for (Map.Entry<String, SSHProxyFactory> entry : factories.entrySet()) {
            SSHProxyFactory factory = entry.getValue();
            factory.closeTunnel();
        }
        factories.clear();
    }
}
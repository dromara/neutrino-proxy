package org.dromara.neutrinoproxy.server.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 检查端口是否被占用
 * 文章参考：https://blog.csdn.net/xingluxiaoseng/article/details/40148527
 */
public class PortAvailableUtil {

    private static void bindPort(String host, int port) throws IOException {
        Socket s = new Socket();
        s.bind(new InetSocketAddress(host, port));
        s.close();
    }

    /**
     * 端口占用判断，若是端口被占用，则会抛出IOException异常，表示端口被占用
     * @param port
     * @return
     */
    public static boolean isPortAvailable(int port) {
        try {
            bindPort("0.0.0.0", port);
            bindPort(InetAddress.getLocalHost().getHostAddress(), port);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("端口被占用："+isPortAvailable(9527));
    }

}

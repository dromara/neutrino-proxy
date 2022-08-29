/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fun.asgc.neutrino.proxy.server.base.proxy;

import fun.asgc.neutrino.proxy.core.ProxyClientConfig;
import fun.asgc.neutrino.proxy.server.dal.entity.PortMappingDO;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class ProxyServerConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 更新配置后保证在其他线程即时生效
     */
    private static ProxyServerConfig instance = new ProxyServerConfig();;

    /**
     * 代理服务器为各个代理客户端（key）开启对应的端口列表（value）
     */
    private volatile Map<String, List<Integer>> clientInetPortMapping = new HashMap<String, List<Integer>>();

    /**
     * 代理服务器上的每个对外端口（key）对应的代理客户端背后的真实服务器信息（value）
     */
    private volatile Map<Integer, String> inetPortLanInfoMapping = new HashMap<Integer, String>();

    public void addClientConfig(String licenseKey, List<PortMappingDO> portMappingList) {
        String clientKey = licenseKey;
        List<Integer> ports = new ArrayList<>();
        for (PortMappingDO portMapping : portMappingList) {
            ports.add(portMapping.getServerPort());
            inetPortLanInfoMapping.put(portMapping.getServerPort(), portMapping.getClientIp() + ":" + portMapping.getClientPort());
        }
        clientInetPortMapping.put(clientKey, ports);
    }

    /**
     * 获取代理客户端对应的代理服务器端口
     *
     * @param clientKey
     * @return
     */
    public List<Integer> getClientInetPorts(String clientKey) {
        return clientInetPortMapping.get(clientKey);
    }

    /**
     * 根据代理服务器端口获取后端服务器代理信息
     *
     * @param port
     * @return
     */
    public String getLanInfo(Integer port) {
        return inetPortLanInfoMapping.get(port);
    }

    /**
     * 返回需要绑定在代理服务器的端口（用于用户请求）
     *
     * @return
     */
    public List<Integer> getUserPorts() {
        List<Integer> ports = new ArrayList<Integer>();
        Iterator<Integer> ite = inetPortLanInfoMapping.keySet().iterator();
        while (ite.hasNext()) {
            ports.add(ite.next());
        }

        return ports;
    }

    public static ProxyServerConfig getInstance() {
        return instance;
    }


    /**
     * 代理客户端与其后面真实服务器映射关系
     *
     * @author fengfei
     *
     */
    @Data
    public static class ClientProxyMapping {

        /**
         * 代理服务器端口
         */
        private Integer inetPort;

        /**
         * 需要代理的网络信息（代理客户端能够访问），格式 192.168.1.99:80 (必须带端口)
         */
        private String lan;

    }
}

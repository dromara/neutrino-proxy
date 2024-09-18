package org.dromara.neutrinoproxy.server.proxy.enhance;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.solon.annotation.Db;
import org.dromara.neutrinoproxy.server.dal.DomainMapper;
import org.dromara.neutrinoproxy.server.dal.entity.DomainNameDO;
import org.dromara.neutrinoproxy.server.util.ProxyUtil;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.event.EventListener;

import javax.net.ssl.KeyManagerFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 域名到SSL上下文
 * @author: Mirac
 * @date: 2024/8/25
 */
@Slf4j
@Component
@Data
public class SslContextManager implements EventListener<AppLoadEndEvent> {

    @Db
    private DomainMapper domainMapper;

    // 初始化时加载所有域名的 SSL 上下文
    @Override
    public void onEvent(AppLoadEndEvent appLoadEndEvent) throws Throwable {
        try {
            initializeSslContexts();
        } catch (Exception e) {
            log.error("initialize SSL handler failed", e);
            e.printStackTrace();
        }
    }

    // 维护域名到 SslContext 的映射
    private final ConcurrentHashMap<String, SslContext> domainSslContexts = new ConcurrentHashMap<>();

    // 初始化所有域名的 SSL 上下文
    private void initializeSslContexts() throws Exception {
        List<DomainNameDO> domainNameDOS = domainMapper.selectList(Wrappers.<DomainNameDO>lambdaQuery()
            .isNotNull(DomainNameDO::getDomain)
            .isNotNull(DomainNameDO::getJks)
            .isNotNull(DomainNameDO::getKeyStorePassword));

        for (DomainNameDO domainNameDO : domainNameDOS) {
            if (domainNameDO.getDomain() != null && domainNameDO.getKeyStorePassword() != null && domainNameDO.getJks() != null) {
                String domain = domainNameDO.getDomain();
                byte[] jks = domainNameDO.getJks();// 假设数据库中存储了每个域名对应的 JKS 路径
                String keyStorePassword = domainNameDO.getKeyStorePassword();
                SslContext sslContext = loadSslContextFromJks(jks, keyStorePassword);
                domainSslContexts.put(domain, sslContext);
            }
        }
    }

    // 动态添加新的域名和证书
    public void addDomainAndCert(String domain, byte[] jks, String keyStorePassword) throws Exception {
        SslContext sslContext = loadSslContextFromJks(jks, keyStorePassword);
        domainSslContexts.put(domain, sslContext);
    }

    // 使用 JKS 文件加载 SSL 上下文，禁用客户端认证并设置为服务器模式
    private SslContext loadSslContextFromJks(byte[] jks, String keyStorePassword) throws Exception {
        InputStream jksInputStream = new ByteArrayInputStream(jks);
        // 初始化 KeyStore
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(jksInputStream, keyStorePassword.toCharArray());

        // 初始化 KeyManagerFactory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, keyStorePassword.toCharArray());

        // 创建 SslContext 并禁用客户端认证
        return SslContextBuilder.forServer(kmf)
            .clientAuth(ClientAuth.NONE)  // 禁用客户端认证
            .build();
    }

    public SslContext getSslContextByDomain(String domain) {
        return domainSslContexts.get(domain);
    }


    public void removeSslContextByDomain(String domain) {
        domainSslContexts.remove(domain);
    }

    public SslContext getSslContextByFullDomain(String fullDomain) {
        String domain = ProxyUtil.getDomainNameByFullDomain(fullDomain);
        if (StrUtil.isBlank(domain)) {
            return null;
        }
        return domainSslContexts.get(domain);
    }
}

package org.dromara.neutrinoproxy.client.config;

import org.dromara.neutrinoproxy.client.sdk.config.ProxyConfig;
import org.noear.solon.annotation.Component;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;

/**
 * @author songyinyin
 * @since 2023/10/21 22:50
 */
@Component
public class NeutrinoClientRuntimeNativeRegistrar implements RuntimeNativeRegistrar {
    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
        metadata.registerResourceInclude("test.jks");

        // sdk - ProxyConfig
        metadata.registerReflection(ProxyConfig.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Protocol.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Client.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Tunnel.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Tcp.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Udp.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Reconnection.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

        // client - SolonProxyConfig
        metadata.registerReflection(SolonProxyConfig.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(SolonProxyConfig.Protocol.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(SolonProxyConfig.Client.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(SolonProxyConfig.Tunnel.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(SolonProxyConfig.Tcp.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(SolonProxyConfig.Udp.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(SolonProxyConfig.Reconnection.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

    }
}

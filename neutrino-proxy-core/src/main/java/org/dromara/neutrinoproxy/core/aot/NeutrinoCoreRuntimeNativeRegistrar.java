package org.dromara.neutrinoproxy.core.aot;

import cn.hutool.core.util.ClassUtil;
import io.netty.channel.SimpleChannelInboundHandler;
import org.dromara.neutrinoproxy.core.ProxyMessage;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;

import java.util.Set;

/**
 * @author songyinyin
 * @since 2023/10/23 21:33
 */
public class NeutrinoCoreRuntimeNativeRegistrar implements RuntimeNativeRegistrar {
    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {
        Set<Class<?>> channelInboundClasses = ClassUtil.scanPackageBySuper("org.dromara.neutrinoproxy", SimpleChannelInboundHandler.class);
        for (Class<?> clazz : channelInboundClasses) {
            metadata.registerReflection(clazz, MemberCategory.INVOKE_DECLARED_METHODS);
        }

        metadata.registerReflection(ProxyMessage.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        metadata.registerReflection(ProxyMessage.UdpBaseInfo.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_DECLARED_METHODS, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        metadata.registerArg("--add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-exports=java.base/jdk.internal.misc=ALL-UNNAMED");
    }
}

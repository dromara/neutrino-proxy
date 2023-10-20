package org.dromara.neutrinoproxy.server.base;

import org.apache.ibatis.io.DefaultVFS;
import org.apache.ibatis.io.JBoss6VFS;
import org.dromara.neutrinoproxy.server.base.proxy.ProxyConfig;
import org.dromara.neutrinoproxy.server.base.rest.ResponseBody;
import org.dromara.neutrinoproxy.server.dal.ClientConnectRecordMapper;
import org.dromara.neutrinoproxy.server.dal.FlowReportDayMapper;
import org.dromara.neutrinoproxy.server.dal.FlowReportHourMapper;
import org.dromara.neutrinoproxy.server.dal.FlowReportMinuteMapper;
import org.dromara.neutrinoproxy.server.dal.FlowReportMonthMapper;
import org.dromara.neutrinoproxy.server.dal.JobInfoMapper;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.PortMappingMapper;
import org.dromara.neutrinoproxy.server.dal.PortPoolMapper;
import org.dromara.neutrinoproxy.server.dal.UserLoginRecordMapper;
import org.dromara.neutrinoproxy.server.dal.UserMapper;
import org.dromara.neutrinoproxy.server.dal.UserTokenMapper;
import org.dromara.neutrinoproxy.server.service.ClientConnectRecordService;
import org.dromara.neutrinoproxy.server.service.JobInfoService;
import org.dromara.neutrinoproxy.server.service.JobLogService;
import org.dromara.neutrinoproxy.server.service.LicenseService;
import org.dromara.neutrinoproxy.server.service.PortGroupService;
import org.dromara.neutrinoproxy.server.service.PortMappingService;
import org.dromara.neutrinoproxy.server.service.PortPoolService;
import org.dromara.neutrinoproxy.server.service.ReportService;
import org.dromara.neutrinoproxy.server.service.UserLoginRecordService;
import org.dromara.neutrinoproxy.server.service.UserService;
import org.dromara.solonplugins.job.JobBean;
import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.RuntimeNativeRegistrar;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author songyinyin
 * @since 2023/10/20 11:24
 */
@Component
public class NeutrinoRuntimeNativeRegistrar implements RuntimeNativeRegistrar {
    @Override
    public void register(AppContext context, RuntimeNativeMetadata metadata) {

        // 使用 MP lambda 的类，需要注册序列化
        metadata.registerLambdaSerialization(ClientConnectRecordService.class);
        metadata.registerLambdaSerialization(JobInfoService.class);
        metadata.registerLambdaSerialization(JobLogService.class);
        metadata.registerLambdaSerialization(LicenseService.class);
        metadata.registerLambdaSerialization(PortGroupService.class);
        metadata.registerLambdaSerialization(PortMappingService.class);
        metadata.registerLambdaSerialization(PortPoolService.class);
        metadata.registerLambdaSerialization(ReportService.class);
        metadata.registerLambdaSerialization(UserLoginRecordService.class);
        metadata.registerLambdaSerialization(UserService.class);

        metadata.registerLambdaSerialization(LicenseMapper.class);
        metadata.registerLambdaSerialization(ClientConnectRecordMapper.class);
        metadata.registerLambdaSerialization(FlowReportDayMapper.class);
        metadata.registerLambdaSerialization(FlowReportHourMapper.class);
        metadata.registerLambdaSerialization(FlowReportMinuteMapper.class);
        metadata.registerLambdaSerialization(FlowReportMonthMapper.class);
        metadata.registerLambdaSerialization(JobInfoMapper.class);
        metadata.registerLambdaSerialization(PortMappingMapper.class);
        metadata.registerLambdaSerialization(PortPoolMapper.class);
        metadata.registerLambdaSerialization(UserLoginRecordMapper.class);
        metadata.registerLambdaSerialization(UserMapper.class);
        metadata.registerLambdaSerialization(UserTokenMapper.class);


        // mybatis
        metadata.registerReflection(JBoss6VFS.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        metadata.registerReflection(DefaultVFS.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        Collection<Class<?>> classes = ResourceUtil.scanClasses("org.dromara.neutrinoproxy.server.dal.entity.*");
        for (Class<?> clazz : classes) {
            metadata.registerReflection(clazz, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
        }

        Solon.context().methodForeach(method -> {
            ParamWrap[] paramWraps = method.getParamWraps();
            for (ParamWrap paramWrap : paramWraps) {
                Class<?> paramType = paramWrap.getType();
                metadata.registerReflection(paramType, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

                Type genericType = paramWrap.getGenericType();
                metadata.registerReflection(genericType.getTypeName(), MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
            }

            Class<?> returnType = method.getReturnType();
            metadata.registerReflection(returnType, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

        });

        metadata.registerReflection(ResponseBody.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerSerialization(ResponseBody.class);

        metadata.registerReflection(ProxyConfig.Protocol.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Server.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Tunnel.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Tcp.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        metadata.registerReflection(ProxyConfig.Udp.class, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);

        metadata.registerReflection(JobBean.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

    }
}

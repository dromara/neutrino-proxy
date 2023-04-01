package org.dromara.neutrinoproxy.server.base.rest.interceptor;

import cn.hutool.core.util.StrUtil;
import org.dromara.neutrinoproxy.server.base.rest.*;
import org.dromara.neutrinoproxy.server.constant.EnableStatusEnum;
import org.dromara.neutrinoproxy.server.constant.ExceptionConstant;
import org.dromara.neutrinoproxy.server.dal.entity.UserDO;
import org.dromara.neutrinoproxy.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dromara.neutrinoproxy.server.base.rest.*;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

import java.lang.reflect.Method;

/**
 * @author: aoshiguchen
 * @date: 2023/3/9
 */
@Slf4j
@Component
public class BaseAuthInterceptor implements RouterInterceptor {

    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (!(mainHandler instanceof Action)) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }
        Action action = (Action) mainHandler;
        Method targetMethod = action.method().getMethod();

        SystemContext systemContext = new SystemContext();
        SystemContextHolder.set(systemContext);
        systemContext.setIp(ctx.realIp());

        Authorization authorization = targetMethod.getAnnotation(Authorization.class);
        if (null == authorization || authorization.login()) {
            String authorize = ctx.header("Authorize");
            if (StrUtil.isEmpty(authorize)) {
                throw ServiceException.create(ExceptionConstant.USER_NOT_LOGIN);
            }
            UserDO userDO = Solon.context().getBean(UserService.class).findByToken(authorize);
            if (null == userDO) {
                throw ServiceException.create(ExceptionConstant.USER_NOT_LOGIN);
            }
            if (EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
                throw ServiceException.create(ExceptionConstant.USER_DISABLE);
            }
            if (null != authorization && authorization.onlyAdmin() && !userDO.getLoginName().equals("admin")) {
                throw ServiceException.create(ExceptionConstant.NO_PERMISSION_VISIT);
            }

            systemContext.setToken(authorize);
            systemContext.setUser(userDO);

            // token续期
            Solon.context().getBean(UserService.class).updateTokenExpirationTime(authorize);
        }
        chain.doIntercept(ctx, mainHandler);
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        SystemContextHolder.remove();
        if (result instanceof ResponseBody) {
            return result;
        }

        if (result instanceof Throwable) {
            log.error("全局异常", (Throwable) result);

            if (result instanceof ServiceException) {
                ServiceException exception = (ServiceException) result;
                return new ResponseBody<>()
                        .setCode(exception.getCode())
                        .setMsg(exception.getMsg());
            }

            return new ResponseBody<>()
                    .setCode(ExceptionConstant.SYSTEM_ERROR.getCode())
                    .setMsg(ExceptionConstant.SYSTEM_ERROR.getMsg())
                    .setStack(ExceptionUtils.getStackTrace((Throwable) result));
        }

        return new ResponseBody<>()
                .setCode(0)
                .setData(result);
    }
}

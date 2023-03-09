package fun.asgc.neutrino.proxy.server.base.rest.interceptor;

import fun.asgc.neutrino.proxy.server.base.rest.ResponseBody;
import fun.asgc.neutrino.proxy.server.base.rest.ServiceException;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

/**
 * @author: aoshiguchen
 * @date: 2023/3/9
 */
@Component
public class GlobalExceptionFilter implements Filter {

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            chain.doFilter(ctx);
        } catch (Throwable e) {
            if (e instanceof ServiceException) {
                ServiceException serviceException = (ServiceException)e;
                ctx.render(new ResponseBody<>()
                        .setCode(serviceException.getCode())
                        .setMsg(serviceException.getMsg()));
                return;
            }
            ctx.render(new ResponseBody<>()
                    .setCode(ExceptionConstant.SYSTEM_ERROR.getCode())
                    .setMsg(ExceptionConstant.SYSTEM_ERROR.getMsg())
                    .setStack(ExceptionUtils.getStackTrace(e)));
        }
    }
}

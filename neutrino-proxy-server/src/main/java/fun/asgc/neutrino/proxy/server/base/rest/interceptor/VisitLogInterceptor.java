package fun.asgc.neutrino.proxy.server.base.rest.interceptor;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContext;
import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.boot.jlhttp.HTTPServer;
import org.noear.solon.boot.jlhttp.JlHttpContext;
import org.noear.solon.core.handle.Action;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.route.RouterInterceptor;
import org.noear.solon.core.route.RouterInterceptorChain;

import java.util.Date;

/**
 * @author: aoshiguchen
 * @date: 2023/3/12
 */
@Slf4j
@Component(index = 1)
public class VisitLogInterceptor implements RouterInterceptor {
    @Override
    public void doIntercept(Context ctx, Handler mainHandler, RouterInterceptorChain chain) throws Throwable {
        if (!(mainHandler instanceof Action)) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }
        SystemContext systemContext = SystemContextHolder.getContext();
        if (null == systemContext) {
            chain.doIntercept(ctx, mainHandler);
            return;
        }
        Action action = (Action) mainHandler;
        systemContext.setAction(action);
        systemContext.setReceiveTime(new Date());
        chain.doIntercept(ctx, mainHandler);
    }

    @Override
    public Object postResult(Context ctx, Object result) throws Throwable {
        SystemContext systemContext = SystemContextHolder.getContext();
        if (null != systemContext) {
            // 如果更换了其他插件，则此处需要调整
            JlHttpContext jlHttpContext = (JlHttpContext) ctx;
            HTTPServer.Request request = (HTTPServer.Request) jlHttpContext.request();

            Date receiveTime = SystemContextHolder.getContext().getReceiveTime();
            Date now = new Date();
            long elapsedTime = now.getTime() - receiveTime.getTime();
            log.info("\n-----------------------------------------------------------------接口请求日志：\n{} url:{} 执行耗时:{}\n请求体参数:{}\n响应结果:{}\n客户端IP:{}\n",
                    request.getMethod(), request.getPath(), getElapsedTimeStr(elapsedTime),
                    JSONObject.toJSONString(request.getParams()),
                    JSONObject.toJSONString(result),
                    SystemContextHolder.getIp()
            );
        }
        SystemContextHolder.remove();
        return RouterInterceptor.super.postResult(ctx, result);
    }

    /**
	 * 获取耗时描述
	 * @param elapsedTime
	 * @return
	 */
	private static String getElapsedTimeStr(long elapsedTime) {
		if (elapsedTime < 1000) {
			return String.format("%s毫秒", elapsedTime);
		} else if (elapsedTime < 60000) {
			return String.format("%.2f秒", (elapsedTime * 1.0) / 1000);
		}
		return String.format("%.2f分钟", (elapsedTime * 1.0) / 1000 / 60);
	}

}

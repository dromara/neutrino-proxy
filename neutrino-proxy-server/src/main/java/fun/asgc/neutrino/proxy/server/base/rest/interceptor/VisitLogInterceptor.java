package fun.asgc.neutrino.proxy.server.base.rest.interceptor;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
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

        Date startTime = new Date();

        chain.doIntercept(ctx, mainHandler);

        Date now = new Date();
        long elapsedTime = now.getTime() - startTime.getTime();
        log.info("\n-----------------------------------------------------------------接口请求日志：\n{} url:{} 执行耗时:{}\n请求体参数:{}\n响应结果:{}\n客户端IP:{}\n",
                ctx.method(), ctx.path(), getElapsedTimeStr(elapsedTime),
                JSONObject.toJSONString(ctx.paramMap()),
                JSONObject.toJSONString(JSONObject.toJSONString(ctx.result)),
                ctx.realIp()
        );
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

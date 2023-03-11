///**
// * Copyright (c) 2022 aoshiguchen
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//package fun.asgc.neutrino.proxy.server.base.rest.interceptor;
//
//import com.alibaba.fastjson.JSONObject;
//import fun.asgc.neutrino.proxy.server.base.rest.SystemContextHolder;
//import lombok.extern.slf4j.Slf4j;
//
//import java.lang.reflect.Method;
//import java.util.Date;
//
///**
// * 访问日志拦截器
// * @author: aoshiguchen
// * @date: 2022/8/14
// */
//@Slf4j
//public class VisitLogInterceptor implements HandlerInterceptor {
//
//	@Override
//	public boolean preHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod) throws Exception {
//		if (null != SystemContextHolder.getContext()) {
//			SystemContextHolder.getContext().setReceiveTime(new Date());
//		}
//		return true;
//	}
//
//	@Override
//	public void postHandle(HttpRequestWrapper requestParser, HttpResponseWrapper responseWrapper, String route, Method targetMethod, Object result) throws Exception {
//		if (null == SystemContextHolder.getContext() || null == SystemContextHolder.getContext().getReceiveTime()) {
//			return;
//		}
//		Date receiveTime = SystemContextHolder.getContext().getReceiveTime();
//		Date now = new Date();
//		long elapsedTime = now.getTime() - receiveTime.getTime();
//		log.info("\n-----------------------------------------------------------------接口请求日志：\n{} url:{} 执行耗时:{}\nURL参数:{}\n请求体参数:{}\n响应结果:{}\n客户端IP:{}\n",
//			requestParser.getMethod().name(), requestParser.getUrl(), getElapsedTimeStr(elapsedTime),
//			requestParser.getQueryString(),
//			requestParser.getContentAsString(),
//			JSONObject.toJSONString(result),
//			SystemContextHolder.getIp()
//			);
//	}
//
//	/**
//	 * 获取耗时描述
//	 * @param elapsedTime
//	 * @return
//	 */
//	private static String getElapsedTimeStr(long elapsedTime) {
//		if (elapsedTime < 1000) {
//			return String.format("%s毫秒", elapsedTime);
//		} else if (elapsedTime < 60000) {
//			return String.format("%.2f秒", (elapsedTime * 1.0) / 1000);
//		}
//		return String.format("%.2f分钟", (elapsedTime * 1.0) / 1000 / 60);
//	}
//}

/**
 * Copyright (C) 2018-2022 Zeyi information technology (Shanghai) Co., Ltd.
 * <p>
 * All right reserved.
 * <p>
 * This software is the confidential and proprietary
 * information of Zeyi Company of China.
 * ("Confidential Information"). You shall not disclose
 * such Confidential Information and shall use it only
 * in accordance with the terms of the contract agreement
 * you entered into with Zeyi inc.
 */
package fun.asgc.neutrino.core.util;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.web.context.HttpContextHolder;
import fun.asgc.neutrino.core.web.context.HttpResponseWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author: wen.y
 * @date: 2022/5/28
 */
public class HttpServerUtil {

	public static void sendResponse(HttpResponseStatus status) {
		sendResponse(status, null);
	}

	public static void sendResponse(HttpResponseStatus status, ByteBuf content) {
		HttpResponseWrapper httpResponseWrapper = HttpContextHolder.getHttpResponseWrapper();
		httpResponseWrapper.setStatus(status);
		if (null != content) {
			httpResponseWrapper.setContent(content);
		}
		httpResponseWrapper.writeAndFlush();
	}

	public static void send404Response(String url) {
		String res = String.format("404 未找到指定资源: %s", url);
		sendResponse(HttpResponseStatus.NOT_FOUND, Unpooled.wrappedBuffer(res.getBytes()));
	}

	public static void send500Response(Throwable throwable) {
		String res = String.format("500 服务异常: %s", ExceptionUtils.getStackTrace(throwable));
		sendResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(res.getBytes()));
	}

	public static void send200Response(Object o) {
		String res = String.valueOf(o);
		if (null != o && !TypeUtil.isNormalBasicType(o.getClass())) {
			res = JSONObject.toJSONString(o);
		}
		HttpResponseWrapper httpResponseWrapper = HttpContextHolder.getHttpResponseWrapper();
		httpResponseWrapper.setContent(Unpooled.wrappedBuffer(res.getBytes()));
		httpResponseWrapper.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		httpResponseWrapper.writeAndFlush();
	}

}

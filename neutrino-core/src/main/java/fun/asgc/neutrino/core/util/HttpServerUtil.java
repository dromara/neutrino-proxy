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

import com.alibaba.fastjson.JSON;
import fun.asgc.neutrino.core.web.HttpResponseEntry;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 *
 * @author: wen.y
 * @date: 2022/5/28
 */
public class HttpServerUtil {

	public static void send404Response(ChannelHandlerContext context, String url) {
		HttpResponseEntry responseEntry = new HttpResponseEntry(HttpResponseStatus.NOT_FOUND.code(), HttpResponseStatus.NOT_FOUND.reasonPhrase(), String.format("未找到指定资源: %s", url));
		String res = JSON.toJSONString(responseEntry);
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, Unpooled.wrappedBuffer(res.getBytes()));
		fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		context.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
	}

	public static void send500Response(ChannelHandlerContext context, Throwable throwable) {
		HttpResponseEntry responseEntry = new HttpResponseEntry(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), HttpResponseStatus.INTERNAL_SERVER_ERROR.reasonPhrase(), String.format("服务异常: %s", ExceptionUtils.getStackTrace(throwable)));
		String res = JSON.toJSONString(responseEntry);
		FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR, Unpooled.wrappedBuffer(res.getBytes()));
		fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		context.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE);
	}

}

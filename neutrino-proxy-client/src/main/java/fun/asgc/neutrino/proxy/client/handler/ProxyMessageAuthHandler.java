/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.proxy.client.handler;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.util.FileUtil;
import fun.asgc.neutrino.proxy.client.config.ProxyConfig;
import fun.asgc.neutrino.proxy.client.core.LicenseObtainService;
import fun.asgc.neutrino.proxy.core.Constants;
import fun.asgc.neutrino.proxy.core.ExceptionEnum;
import fun.asgc.neutrino.proxy.core.ProxyMessage;
import fun.asgc.neutrino.proxy.core.ProxyMessageHandler;
import fun.asgc.neutrino.proxy.core.dispatcher.Match;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 认证信息处理器
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@NonIntercept
@Match(type = Constants.ProxyDataTypeName.AUTH)
@Component
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	@Autowired
	private LicenseObtainService licenseObtainService;
	@Autowired
	private ProxyConfig proxyConfig;

	@Override
	public void handle(ChannelHandlerContext context, ProxyMessage proxyMessage) {
		String info = proxyMessage.getInfo();
		JSONObject data = JSONObject.parseObject(info);
		Integer code = data.getInteger("code");
		log.info("认证结果:{}", info);
		if (ExceptionEnum.SUCCESS.getCode().equals(code)) {
			ProxyConfig.authSuccess = true;
			FileUtil.write("./.neutrino-proxy-client.json", JSONObject.toJSONString(proxyConfig.getCustomConfig(), SerializerFeature.PrettyFormat));
			licenseObtainService.stop();
		}
	}
}

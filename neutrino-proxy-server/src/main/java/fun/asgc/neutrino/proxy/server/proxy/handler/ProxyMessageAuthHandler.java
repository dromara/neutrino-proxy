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

package fun.asgc.neutrino.proxy.server.proxy.handler;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Match;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.util.ChannelUtil;
import fun.asgc.neutrino.core.util.StringUtil;
import fun.asgc.neutrino.proxy.core.*;
import fun.asgc.neutrino.proxy.server.base.proxy.ProxyConfig;
import fun.asgc.neutrino.proxy.server.constant.ClientConnectTypeEnum;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.OnlineStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.SuccessCodeEnum;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.ClientConnectRecordDO;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.dal.entity.UserDO;
import fun.asgc.neutrino.proxy.server.service.*;
import fun.asgc.neutrino.proxy.server.util.ProxyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Slf4j
@NonIntercept
@Match(type = Constants.ProxyDataTypeName.AUTH)
@Component
public class ProxyMessageAuthHandler implements ProxyMessageHandler {
	@Autowired
	private ProxyConfig proxyConfig;
	@Autowired
	private LicenseService licenseService;
	@Autowired
	private UserService userService;
	@Autowired
	private PortMappingService portMappingService;
	@Autowired
	private ProxyMutualService proxyMutualService;
	@Autowired
	private FlowReportService flowReportService;
	@Autowired
	private ClientConnectRecordService clientConnectRecordService;
	@Autowired
	private LicenseMapper licenseMapper;
	@Autowired
	private VisitorChannelService visitorChannelService;

	@Override
	public void handle(ChannelHandlerContext ctx, ProxyMessage proxyMessage) {
		String ip = ChannelUtil.getIP(ctx.channel());
		Date now = new Date();

		String licenseKey = proxyMessage.getInfo();
		if (StringUtil.isEmpty(licenseKey)) {
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "license不能为空!", licenseKey));
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("license不能为空!")
					.setCreateTime(now)
			);
			return;
		}
		LicenseDO licenseDO = licenseService.findByKey(licenseKey);
		if (null == licenseDO) {
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "license不存在!", licenseKey));
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("license不存在!")
					.setCreateTime(now)
			);
			return;
		}
		if (EnableStatusEnum.DISABLE.getStatus().equals(licenseDO.getEnable())) {
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "当前license已被禁用!", licenseKey));
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setLicenseId(licenseDO.getId())
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("当前license已被禁用!")
					.setCreateTime(now));
			return;
		}
		UserDO userDO = userService.findById(licenseDO.getUserId());
		if (null == userDO || EnableStatusEnum.DISABLE.getStatus().equals(userDO.getEnable())) {
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "当前license无效!", licenseKey));
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setLicenseId(licenseDO.getId())
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("当前license无效!")
					.setCreateTime(now));
			return;
		}
		Channel cmdChannel = ProxyUtil.getCmdChannelByLicenseId(licenseDO.getId());
		if (null != cmdChannel) {
			ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.AUTH_FAILED.getCode(), "当前license已被另一节点使用!", licenseKey));
			clientConnectRecordService.add(new ClientConnectRecordDO()
					.setIp(ip)
					.setLicenseId(licenseDO.getId())
					.setType(ClientConnectTypeEnum.CONNECT.getType())
					.setMsg(licenseKey)
					.setCode(SuccessCodeEnum.FAIL.getCode())
					.setErr("当前license已被另一节点使用!")
					.setCreateTime(now));
			return;
		}
		// 发送认证成功消息
		ctx.channel().writeAndFlush(ProxyMessage.buildAuthResultMessage(ExceptionEnum.SUCCESS.getCode(), "认证成功!", licenseKey));

		clientConnectRecordService.add(new ClientConnectRecordDO()
				.setIp(ip)
				.setLicenseId(licenseDO.getId())
				.setType(ClientConnectTypeEnum.CONNECT.getType())
				.setMsg(licenseKey)
				.setCode(SuccessCodeEnum.SUCCESS.getCode())
				.setCreateTime(now));

		// 更新license在线状态
		licenseMapper.updateOnlineStatus(licenseDO.getId(), OnlineStatusEnum.ONLINE.getStatus(), now);
		// 初始化VisitorChannel
		visitorChannelService.initVisitorChannel(licenseDO.getId(), ctx.channel());
	}

	@Override
	public String name() {
		return ProxyDataTypeEnum.AUTH.getDesc();
	}
}

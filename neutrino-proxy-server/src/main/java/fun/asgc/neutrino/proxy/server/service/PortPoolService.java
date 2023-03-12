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
package fun.asgc.neutrino.proxy.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import fun.asgc.neutrino.proxy.server.base.page.PageInfo;
import fun.asgc.neutrino.proxy.server.base.page.PageQuery;
import fun.asgc.neutrino.proxy.server.constant.EnableStatusEnum;
import fun.asgc.neutrino.proxy.server.constant.ExceptionConstant;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolCreateReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolListReq;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolUpdateEnableStatusReq;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolCreateRes;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolListRes;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolUpdateEnableStatusRes;
import fun.asgc.neutrino.proxy.server.dal.PortPoolMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.PortPoolDO;
import fun.asgc.neutrino.proxy.server.util.ParamCheckUtil;
import ma.glasnost.orika.MapperFacade;
import org.apache.ibatis.solon.annotation.Db;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Component
public class PortPoolService {
	@Inject
	private MapperFacade mapperFacade;
	@Db
	private PortPoolMapper portPoolMapper;
	@Inject
	private VisitorChannelService visitorChannelService;

	public PageInfo<PortPoolListRes> page(PageQuery pageQuery, PortPoolListReq req) {
		Page<PortPoolListRes> result = PageHelper.startPage(pageQuery.getCurrent(), pageQuery.getSize());
		List<PortPoolDO> list = portPoolMapper.selectList(new LambdaQueryWrapper<PortPoolDO>()
				.orderByAsc(PortPoolDO::getId)
		);
		List<PortPoolListRes> respList = mapperFacade.mapAsList(list, PortPoolListRes.class);
		return PageInfo.of(respList, result.getTotal(), pageQuery.getCurrent(), pageQuery.getSize());
	}

	public List<PortPoolListRes> list(PortPoolListReq req) {
		List<PortPoolDO> list = portPoolMapper.selectList(new LambdaQueryWrapper<PortPoolDO>()
				.eq(PortPoolDO::getEnable, EnableStatusEnum.ENABLE.getStatus())
		);
		return mapperFacade.mapAsList(list, PortPoolListRes.class);
	}

	public PortPoolCreateRes create(PortPoolCreateReq req) {
		PortPoolDO oldPortPoolDO = portPoolMapper.findByPort(req.getPort());
		ParamCheckUtil.checkMustNull(oldPortPoolDO, ExceptionConstant.PORT_CANNOT_REPEAT);

		Date now = new Date();

		portPoolMapper.insert(new PortPoolDO()
			.setPort(req.getPort())
			.setEnable(EnableStatusEnum.ENABLE.getStatus())
			.setCreateTime(now)
			.setUpdateTime(now)
		);
		// 更新visitorChannel
		visitorChannelService.updateVisitorChannelByPortPool(req.getPort(), EnableStatusEnum.ENABLE.getStatus());

		return new PortPoolCreateRes();
	}

	public PortPoolUpdateEnableStatusRes updateEnableStatus(PortPoolUpdateEnableStatusReq req) {
		PortPoolDO portPoolDO = portPoolMapper.findById(req.getId());
		ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);
		portPoolMapper.updateEnableStatus(req.getId(), req.getEnable(), new Date());

		// 更新visitorChannel
		visitorChannelService.updateVisitorChannelByPortPool(portPoolDO.getPort(), req.getEnable());

		return new PortPoolUpdateEnableStatusRes();
	}

	public void delete(Integer id) {
		PortPoolDO portPoolDO = portPoolMapper.findById(id);
		ParamCheckUtil.checkNotNull(portPoolDO, ExceptionConstant.PORT_NOT_EXIST);

		portPoolMapper.deleteById(id);

		// 更新visitorChannel
		visitorChannelService.updateVisitorChannelByPortPool(portPoolDO.getPort(), EnableStatusEnum.DISABLE.getStatus());
	}

}

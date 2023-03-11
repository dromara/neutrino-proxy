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
package fun.asgc.neutrino.proxy.server.dal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Param;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.annotation.*;
import fun.asgc.neutrino.core.db.page.PageInfo;
import fun.asgc.neutrino.proxy.server.controller.req.PortPoolListReq;
import fun.asgc.neutrino.proxy.server.controller.res.PortPoolListRes;
import fun.asgc.neutrino.proxy.server.dal.entity.PortPoolDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/7
 */
@Intercept(ignoreGlobal = true)
@Component
@Mapper
public interface PortPoolMapper extends BaseMapper<PortPoolDO> {

	@ResultType(PortPoolListRes.class)
	@Select("select * from port_pool")
	void page(PageInfo<PortPoolListRes> pageInfo, PortPoolListReq req);

	@ResultType(PortPoolListRes.class)
	@Select("select * from port_pool where enable = 1")
	List<PortPoolListRes> list();

	@Insert("insert into port_pool(`port`,`enable`,`create_time`,`update_time`) values(:port,:enable,:createTime,:updateTime)")
	void add(PortPoolDO portPool);

	@Update("update `port_pool` set enable = :enable, update_time = :updateTime where id = :id")
	void updateEnableStatus(@Param("id") Integer id, @Param("enable") Integer enable, @Param("updateTime") Date updateTime);

	@Delete("delete from `port_pool` where id = ?")
	void delete(Integer id);

	@Select("select * from port_pool where port = ? limit 0,1")
	default PortPoolDO findByPort(Integer port) {
		return this.selectOne(new LambdaQueryWrapper<PortPoolDO>()
				.eq(PortPoolDO::getPort, port)
		);
	}

	@Select("select * from port_pool where id = ?")
	PortPoolDO findById(Integer id);
}

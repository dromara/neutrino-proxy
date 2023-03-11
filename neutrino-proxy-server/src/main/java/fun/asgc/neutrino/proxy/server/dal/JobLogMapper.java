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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.annotation.Insert;
import fun.asgc.neutrino.core.db.annotation.ResultType;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.page.PageInfo;
import fun.asgc.neutrino.proxy.server.controller.req.JobLogListReq;
import fun.asgc.neutrino.proxy.server.controller.res.JobLogListRes;
import fun.asgc.neutrino.proxy.server.dal.entity.JobLogDO;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/5
 */
@Intercept(ignoreGlobal = true)
@Component
@Mapper
public interface JobLogMapper extends BaseMapper<JobLogDO> {

    @Insert("insert into job_log(`job_id`,`handler`,`param`,`code`,`msg`,`alarm_status`,`create_time`) values(:jobId,:handler,:param,:code,:msg,:alarmStatus,:createTime)")
    void add(JobLogDO jobLog);

    @ResultType(JobLogListRes.class)
    @Select("select * from job_log order by create_time desc")
    void page(PageInfo pageInfo, JobLogListReq req);

    @ResultType(JobLogListRes.class)
    @Select("select * from job_log where job_id = :jobId order by create_time desc")
    void pageByJobId(PageInfo pageInfo, JobLogListReq req);

}

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

import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.Param;
import fun.asgc.neutrino.core.aop.Intercept;
import fun.asgc.neutrino.core.db.annotation.Insert;
import fun.asgc.neutrino.core.db.annotation.ResultType;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.mapper.SqlMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportMinuteDO;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author: aoshiguchen
 * @date: 2022/10/24
 */
@Intercept(ignoreGlobal = true)
@Component
public interface FlowReportMinuteMapper extends SqlMapper {
    @Select("select * from flow_report_minute where license_id = :licenseId and date = :date")
    FlowReportMinuteDO findOne(@Param("licenseId") Integer licenseId, @Param("date") String date);

    @ResultType(FlowReportMinuteDO.class)
    @Select("select * from flow_report_minute where license_id in (:licenseIds) and date = :date")
    List<FlowReportMinuteDO> findList(@Param("licenseIds") Set<Integer> licenseIds, @Param("date") String date);

    @ResultType(FlowReportMinuteDO.class)
    @Select("select * from flow_report_minute where date >= :startDate and date <= :endDate")
    List<FlowReportMinuteDO> findListByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Insert("insert into flow_report_minute(`user_id`,`license_id`,`write_bytes`,`read_bytes`,`date`,`date_str`,`create_time`) values(:userId,:licenseId,:writeBytes,:readBytes,:date,:dateStr,:createTime)")
    void add(FlowReportMinuteDO flowReportMinuteDO);
}

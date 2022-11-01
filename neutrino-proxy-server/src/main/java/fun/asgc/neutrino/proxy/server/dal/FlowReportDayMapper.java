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
import fun.asgc.neutrino.core.db.annotation.Delete;
import fun.asgc.neutrino.core.db.annotation.Insert;
import fun.asgc.neutrino.core.db.annotation.Select;
import fun.asgc.neutrino.core.db.mapper.SqlMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportDayDO;

/**
 * @author: aoshiguchen
 * @date: 2022/10/28
 */
@Intercept(ignoreGlobal = true)
@Component
public interface FlowReportDayMapper extends SqlMapper {
    @Select("select * from flow_report_day where license_id = :licenseId and date_str = :dateStr")
    FlowReportDayDO findOne(@Param("licenseId") Integer licenseId, @Param("dateStr") String dateStr);

    @Insert("insert into flow_report_day(`user_id`,`license_id`,`write_bytes`,`read_bytes`,`date`,`date_str`,`create_time`) values(:userId,:licenseId,:writeBytes,:readBytes,:date,:dateStr,:createTime)")
    void add(FlowReportDayDO flowReportDayDO);

    @Delete("delete from flow_report_day where date_str = :dateStr")
    void deleteByDateStr(@Param("dateStr") String dateStr);
}

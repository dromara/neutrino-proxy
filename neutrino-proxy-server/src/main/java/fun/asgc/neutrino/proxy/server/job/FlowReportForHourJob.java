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
package fun.asgc.neutrino.proxy.server.job;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.quartz.IJobHandler;
import fun.asgc.neutrino.core.quartz.annotation.JobHandler;
import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.dal.FlowReportHourMapper;
import fun.asgc.neutrino.proxy.server.dal.FlowReportMinuteMapper;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportMinuteDO;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/10/28
 */
@Slf4j
@NonIntercept
@Component
@JobHandler(name = "FlowReportForHourJob", cron = "0 0 */1 * * ?", param = "")
public class FlowReportForHourJob implements IJobHandler {
    @Autowired
    private FlowReportService flowReportService;
    @Autowired
    private LicenseMapper licenseMapper;
    @Autowired
    private FlowReportMinuteMapper flowReportMinuteMapper;
    @Autowired
    private FlowReportHourMapper flowReportHourMapper;

    @Override
    public void execute(String param) throws Exception {
        Date now = new Date();
        String dateStr = DateUtil.format(DateUtil.addDate(now, Calendar.HOUR, -1), "yyyy-MM-dd HH");
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd HH");
        Date startHourDate = DateUtil.getHourBegin(date);
        Date endHourDate = DateUtil.getHourEnd(date);

        // 删除原来的记录 TODO

        // 查询前一个小时的分钟级别统计数据
        List<FlowReportMinuteDO> flowReportMinuteDOList = flowReportMinuteMapper.findListByDateRange(startHourDate, endHourDate);
        if (CollectionUtil.isEmpty(flowReportMinuteDOList)) {
            return;
        }

        // 汇总前一个小时的小时级别统计数据 TODO
    }

}

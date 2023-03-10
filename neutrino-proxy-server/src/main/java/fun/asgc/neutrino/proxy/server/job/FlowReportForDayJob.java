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

import fun.asgc.neutrino.core.util.CollectionUtil;
import fun.asgc.neutrino.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.base.quartz.IJobHandler;
import fun.asgc.neutrino.proxy.server.base.quartz.JobHandler;
import fun.asgc.neutrino.proxy.server.dal.FlowReportDayMapper;
import fun.asgc.neutrino.proxy.server.dal.FlowReportHourMapper;
import fun.asgc.neutrino.proxy.server.dal.FlowReportMinuteMapper;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportDayDO;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportHourDO;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;

/**
 * @author: aoshiguchen
 * @date: 2022/10/28
 */
@Slf4j
@Component
@JobHandler(name = "FlowReportForDayJob", cron = "0 0 1 * * ?", param = "")
public class FlowReportForDayJob implements IJobHandler {
    @Inject
    private FlowReportService flowReportService;
    @Inject
    private LicenseMapper licenseMapper;
    @Inject
    private FlowReportMinuteMapper flowReportMinuteMapper;
    @Inject
    private FlowReportHourMapper flowReportHourMapper;
    @Inject
    private FlowReportDayMapper flowReportDayMapper;

    @Override
    public void execute(String param) throws Exception {
        Date now = new Date();
        String dateStr = DateUtil.format(DateUtil.addDate(now, Calendar.DATE, -1), "yyyy-MM-dd");
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd");
        Date startHourDate = DateUtil.getDayBegin(date);
        Date endHourDate = DateUtil.getDayEnd(date);

        // 删除原来的记录
        flowReportDayMapper.deleteByDateStr(dateStr);

        // 查询前一天的小时级别统计数据
        List<FlowReportHourDO> flowReportHourDOList = flowReportHourMapper.findListByDateRange(startHourDate, endHourDate);
        if (CollectionUtil.isEmpty(flowReportHourDOList)) {
            return;
        }

        // 汇总前一个天的天级别统计数据
        Map<Integer, FlowReportDayDO> map = new HashMap<>();
        for (FlowReportHourDO item : flowReportHourDOList) {
            FlowReportDayDO report = map.get(item.getLicenseId());
            if (null == report) {
                report = new FlowReportDayDO();
                map.put(item.getLicenseId(), report);
            }
            Long writeBytes = report.getWriteBytes() == null ? 0 : report.getWriteBytes();
            Long readBytes = report.getReadBytes() == null ? 0 : report.getReadBytes();

            report.setUserId(item.getUserId());
            report.setLicenseId(item.getLicenseId());
            report.setWriteBytes(writeBytes + item.getWriteBytes());
            report.setReadBytes(readBytes + item.getReadBytes());
            report.setDate(date);
            report.setDateStr(dateStr);
            report.setCreateTime(now);
        }

        for (FlowReportDayDO item : map.values()) {
            flowReportDayMapper.add(item);
        }
    }

}

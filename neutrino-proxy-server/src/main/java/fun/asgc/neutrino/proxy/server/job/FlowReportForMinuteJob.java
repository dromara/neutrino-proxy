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
import fun.asgc.neutrino.proxy.server.dal.FlowReportMinuteMapper;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportMinuteDO;
import fun.asgc.neutrino.proxy.server.dal.entity.LicenseDO;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流量统计报表 - 分钟级别
 * @author: aoshiguchen
 * @date: 2022/10/24
 */
@Slf4j
@NonIntercept
@Component
@JobHandler(name = "FlowReportForMinuteJob", cron = "0 */1 * * * ?", param = "")
public class FlowReportForMinuteJob implements IJobHandler {

    @Autowired
    private FlowReportService flowReportService;
    @Autowired
    private LicenseMapper licenseMapper;
    @Autowired
    private FlowReportMinuteMapper flowReportMinuteMapper;

    @Override
    public void execute(String param) throws Exception {
        List<LicenseDO> list = licenseMapper.listAll();
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        Set<Integer> licenseIds = list.stream().map(LicenseDO::getId).collect(Collectors.toSet());
        Date now = new Date();
        String dateStr = DateUtil.format(DateUtil.addDate(now, Calendar.MINUTE, -1), "yyyy-MM-dd HH:mm");
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd HH:mm");
        List<FlowReportMinuteDO> oldList = flowReportMinuteMapper.findList(licenseIds, dateStr);
        Map<Integer, FlowReportMinuteDO> oldMap = CollectionUtil.isEmpty(oldList) ? new HashMap<>() :
                oldList.stream().collect(Collectors.toMap(FlowReportMinuteDO::getLicenseId, Function.identity(), (a,b) -> a));

        for (LicenseDO item : list) {
            // 避免job重复执行导致数据重复
            if (oldMap.containsKey(item.getId())) {
                continue;
            }
            Integer writeBytes = flowReportService.getAndResetWriteByte(item.getId());
            Integer readBytes = flowReportService.getAndResetReadByte(item.getId());
            if (writeBytes == 0 && readBytes == 0) {
                continue;
            }
            FlowReportMinuteDO flowReportMinuteDO = new FlowReportMinuteDO();
            flowReportMinuteDO.setUserId(item.getUserId());
            flowReportMinuteDO.setLicenseId(item.getId());
            flowReportMinuteDO.setWriteBytes(writeBytes);
            flowReportMinuteDO.setReadBytes(readBytes);
            flowReportMinuteDO.setDate(date);
            flowReportMinuteDO.setDateStr(dateStr);
            flowReportMinuteDO.setCreateTime(now);
            flowReportMinuteMapper.add(flowReportMinuteDO);
        }
    }
}

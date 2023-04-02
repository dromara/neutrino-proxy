package org.dromara.neutrinoproxy.server.job;

import cn.hutool.core.collection.CollectionUtil;
import org.dromara.neutrinoproxy.core.util.DateUtil;
import org.dromara.neutrinoproxy.server.dal.FlowReportMinuteMapper;
import org.dromara.neutrinoproxy.server.dal.LicenseMapper;
import org.dromara.neutrinoproxy.server.dal.entity.FlowReportMinuteDO;
import org.dromara.neutrinoproxy.server.dal.entity.LicenseDO;
import org.dromara.neutrinoproxy.server.service.FlowReportService;
import fun.asgc.solon.extend.job.IJobHandler;
import fun.asgc.solon.extend.job.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 流量统计报表 - 分钟级别
 * @author: aoshiguchen
 * @date: 2022/10/24
 */
@Slf4j
@Component
@JobHandler(name = "FlowReportForMinuteJob", cron = "0 */1 * * * ?", param = "")
public class FlowReportForMinuteJob implements IJobHandler {

    @Inject
    private FlowReportService flowReportService;
    @Inject
    private LicenseMapper licenseMapper;
    @Inject
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
            FlowReportMinuteDO flowReportMinuteDO = new FlowReportMinuteDO();
            flowReportMinuteDO.setUserId(item.getUserId());
            flowReportMinuteDO.setLicenseId(item.getId());
            flowReportMinuteDO.setWriteBytes(writeBytes);
            flowReportMinuteDO.setReadBytes(readBytes);
            flowReportMinuteDO.setDate(date);
            flowReportMinuteDO.setDateStr(dateStr);
            flowReportMinuteDO.setCreateTime(now);
            flowReportMinuteMapper.insert(flowReportMinuteDO);
        }
    }
}

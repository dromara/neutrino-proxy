package fun.asgc.neutrino.proxy.server.job;

import cn.hutool.core.collection.CollectionUtil;
import fun.asgc.neutrino.proxy.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.dal.FlowReportHourMapper;
import fun.asgc.neutrino.proxy.server.dal.FlowReportMinuteMapper;
import fun.asgc.neutrino.proxy.server.dal.LicenseMapper;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportHourDO;
import fun.asgc.neutrino.proxy.server.dal.entity.FlowReportMinuteDO;
import fun.asgc.neutrino.proxy.server.service.FlowReportService;
import fun.asgc.solon.extend.job.IJobHandler;
import fun.asgc.solon.extend.job.annotation.JobHandler;
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
@JobHandler(name = "FlowReportForHourJob", cron = "0 0 */1 * * ?", param = "")
public class FlowReportForHourJob implements IJobHandler {
    @Inject
    private FlowReportService flowReportService;
    @Inject
    private LicenseMapper licenseMapper;
    @Inject
    private FlowReportMinuteMapper flowReportMinuteMapper;
    @Inject
    private FlowReportHourMapper flowReportHourMapper;

    @Override
    public void execute(String param) throws Exception {
        Date now = new Date();
        String dateStr = DateUtil.format(DateUtil.addDate(now, Calendar.HOUR, -1), "yyyy-MM-dd HH");
        Date date = DateUtil.parse(dateStr, "yyyy-MM-dd HH");
        Date startHourDate = DateUtil.getHourBegin(date);
        Date endHourDate = DateUtil.getHourEnd(date);

        // 删除原来的记录
        flowReportHourMapper.deleteByDateStr(dateStr);

        // 查询前一个小时的分钟级别统计数据
        List<FlowReportMinuteDO> flowReportMinuteDOList = flowReportMinuteMapper.findListByDateRange(startHourDate, endHourDate);
        if (CollectionUtil.isEmpty(flowReportMinuteDOList)) {
            return;
        }

        // 汇总前一个小时的小时级别统计数据
        Map<Integer, FlowReportHourDO> map = new HashMap<>();
        for (FlowReportMinuteDO item : flowReportMinuteDOList) {
            FlowReportHourDO report = map.get(item.getLicenseId());
            if (null == report) {
                report = new FlowReportHourDO();
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

        for (FlowReportHourDO item : map.values()) {
            flowReportHourMapper.insert(item);
        }
    }

}

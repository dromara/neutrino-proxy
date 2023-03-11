package fun.asgc.neutrino.proxy.server.job;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.proxy.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.dal.*;
import fun.asgc.solon.extend.job.IJobHandler;
import fun.asgc.solon.extend.job.annotation.JobHandler;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日志清理Job
 * @author: aoshiguchen
 * @date: 2022/9/17
 */
@Slf4j
@Component
@JobHandler(name = "DataCleanJob", cron = "0 0 1 * * ?")
public class DataCleanJob implements IJobHandler {
    /**
     * Job日志保存天数
     */
    private static final Integer JOB_LOG_KEEP_DAYS = 7;
    /**
     * 用户登录日志保留天数
     */
    private static final Integer USER_LOGIN_RECORD_KEEP_DAYS = 30;
    /**
     * 客户端连接记录保留天数
     */
    private static final Integer CLIENT_CONNECT_RECORD_KEEP_DAYS = 30;

    /**
     * 流量统计分钟报表记录保留天数
     */
    private static final Integer FLOW_MINUTE_REPORT_KEEP_DAYS = 2;
    /**
     * 流量统计小时报表记录保留天数
     */
    private static final Integer FLOW_HOUR_REPORT_KEEP_DAYS = 90;
    /**
     * 流量统计天报表记录保留天数
     */
    private static final Integer FLOW_DAY_REPORT_KEEP_DAYS = 400;
    @Inject
    private JobLogMapper jobLogMapper;
    @Inject
    private UserLoginRecordMapper userLoginRecordMapper;
    @Inject
    private ClientConnectRecordMapper clientConnectRecordMapper;
    @Inject
    private FlowReportMinuteMapper flowReportMinuteMapper;
    @Inject
    private FlowReportHourMapper flowReportHourMapper;
    @Inject
    private FlowReportDayMapper flowReportDayMapper;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(String s) throws Exception {
        JobParams jobParams = getParams(s);

        {
            Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getJobLogKeepDays());
            log.info("清理调度管理日志 date:{}", sdf.format(date));
            jobLogMapper.clean(date);
        }

        {
            Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getUserLoginRecordKeepDays());
            log.info("清理用户登录日志 date:{}", sdf.format(date));
            userLoginRecordMapper.clean(date);
        }

        {
            Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getClientConnectRecordKeepDays());
            log.info("清理客户端连接日志 date:{}", sdf.format(date));
            clientConnectRecordMapper.clean(date);
        }

        {
            Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getFlowMinuteReportKeepDays());
            log.info("清理流通统计分钟报表日志 date:{}", sdf.format(date));
            flowReportMinuteMapper.clean(date);
        }

        {
            Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getFlowHourReportKeepDays());
            log.info("清理流通统计小时报表日志 date:{}", sdf.format(date));
            flowReportHourMapper.clean(date);
        }

        {
            Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getFlowDayReportKeepDays());
            log.info("清理流通统计日报表日志 date:{}", sdf.format(date));
            flowReportDayMapper.clean(date);
        }
    }

    public static JobParams getParams(String s) {
        try {
            if (StringUtils.isNotBlank(s)) {
                return JSONObject.parseObject(s, JobParams.class);
            }
        } catch (Exception e) {
            // ignore
        }
        return new JobParams()
                .setJobLogKeepDays(JOB_LOG_KEEP_DAYS)
                .setUserLoginRecordKeepDays(USER_LOGIN_RECORD_KEEP_DAYS)
                .setClientConnectRecordKeepDays(CLIENT_CONNECT_RECORD_KEEP_DAYS)
                .setFlowMinuteReportKeepDays(FLOW_MINUTE_REPORT_KEEP_DAYS)
                .setFlowHourReportKeepDays(FLOW_HOUR_REPORT_KEEP_DAYS)
                .setFlowDayReportKeepDays(FLOW_DAY_REPORT_KEEP_DAYS);
    }

    @Accessors(chain = true)
    @Data
    public static class JobParams {
        private Integer jobLogKeepDays;
        private Integer userLoginRecordKeepDays;
        private Integer clientConnectRecordKeepDays;
        private Integer flowMinuteReportKeepDays;
        private Integer flowHourReportKeepDays;
        private Integer flowDayReportKeepDays;
    }
}

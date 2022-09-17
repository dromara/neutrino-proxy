package fun.asgc.neutrino.proxy.server.job;

import com.alibaba.fastjson.JSONObject;
import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Component;
import fun.asgc.neutrino.core.annotation.NonIntercept;
import fun.asgc.neutrino.core.quartz.IJobHandler;
import fun.asgc.neutrino.core.quartz.annotation.JobHandler;
import fun.asgc.neutrino.core.util.DateUtil;
import fun.asgc.neutrino.proxy.server.dal.DataCleanMapper;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日志清理Job
 * @author: aoshiguchen
 * @date: 2022/9/17
 */
@Slf4j
@NonIntercept
@Component
@JobHandler(name = "DataCleanJob", cron = "0 0 1 * * ?")
public class DataCleanJob implements IJobHandler {
    @Autowired
    private DataCleanMapper dataCleanMapper;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Job日志保存天数
     */
    private static final Integer JOB_LOG_KEEP_DAYS = 7;

    @Override
    public void execute(String s) throws Exception {
        JobParams jobParams = getParams(s);

        Date date = DateUtil.addDate(new Date(), Calendar.DATE, jobParams.getJobLogKeepDays());
        log.info("清理调度管理日志 date:{}", sdf.format(date));
        dataCleanMapper.cleanJobLog(date);
    }

    public static JobParams getParams(String s) {
        try {
            return JSONObject.parseObject(s, JobParams.class);
        } catch (Exception e) {
            // ignore
        }
        return new JobParams()
                .setJobLogKeepDays(JOB_LOG_KEEP_DAYS);
    }

    @Accessors(chain = true)
    @Data
    public static class JobParams {
        private Integer jobLogKeepDays;
    }
}

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
import org.apache.commons.lang3.StringUtils;

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

        Date date = DateUtil.addDate(new Date(), Calendar.DATE, -1 * jobParams.getJobLogKeepDays());
        log.info("清理调度管理日志 date:{}", sdf.format(date));
        dataCleanMapper.cleanJobLog(date.getTime());
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
                .setJobLogKeepDays(JOB_LOG_KEEP_DAYS);
    }

    @Accessors(chain = true)
    @Data
    public static class JobParams {
        private Integer jobLogKeepDays;
    }
}

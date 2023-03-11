package fun.asgc.solon.extend.job.impl;

import fun.asgc.solon.extend.job.IJobCallback;
import fun.asgc.solon.extend.job.JobInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: aoshiguchen
 * @date: 2023/3/12
 */
@Slf4j
public class DefaultJobCallback implements IJobCallback {

    @Override
    public void executeLog(JobInfo jobInfo, String param, Throwable throwable) {
        if (null == throwable) {
            log.debug("[Solon Plugin Job] Job执行 id:{} name:{} desc:{} param:{}", jobInfo.getId(), jobInfo.getName(), jobInfo.getDesc(), param);
        } else {
            log.error("[Solon Plugin Job] Job执行 id:{} name:{} desc:{} param:{}", jobInfo.getId(), jobInfo.getName(), jobInfo.getDesc(), param, throwable);
        }
    }

}

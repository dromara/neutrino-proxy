package fun.asgc.solon.extend.job;

import fun.asgc.solon.extend.job.annotation.EnableJob;
import fun.asgc.solon.extend.job.impl.DefaultJobCallback;
import fun.asgc.solon.extend.job.impl.DefaultJobSource;
import fun.asgc.solon.extend.job.impl.JobExecutor;
import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;

/**
 * @author: aoshiguchen
 * @date: 2023/3/11
 */
public class XPluginImp implements Plugin {

    @Override
    public void start(AopContext context) throws Throwable {
        EnableJob enableJob = Solon.app().source().getAnnotation(EnableJob.class);
        if (null == enableJob || !enableJob.value()) {
            return;
        }

        //应用加载完后，再启动任务
        Solon.app().onEvent(AppLoadEndEvent.class, e -> {
            IJobSource jobSource = context.getBean(IJobSource.class);
            IJobCallback jobCallback = context.getBean(IJobCallback.class);
            if (null == jobSource) {
                jobSource = new DefaultJobSource();
            }
            if (null == jobCallback) {
                jobCallback = new DefaultJobCallback();
            }

            JobExecutor jobExecutor = new JobExecutor();
            jobExecutor.setJobSource(jobSource);
            jobExecutor.setJobCallback(jobCallback);
            jobExecutor.start();

            context.wrapAndPut(JobExecutor.class, jobExecutor);
        });
    }

}

package fun.asgc.neutrino.proxy.server.job;

import fun.asgc.solon.extend.job.IJobHandler;
import fun.asgc.solon.extend.job.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Slf4j
@Component
@JobHandler(name = "DemoJob", cron = "0/10 * * * * ?", param = "{\"a\":1}")
public class DemoJob implements IJobHandler {

	@Override
	public void execute(String param) throws Exception {
		System.out.println("DemoJob execute param:" + param);
	}
}

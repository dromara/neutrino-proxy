package org.dromara.neutrinoproxy.server.job;

import lombok.extern.slf4j.Slf4j;
import org.dromara.solonplugins.job.IJobHandler;
import org.dromara.solonplugins.job.annotation.JobHandler;
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
		log.debug("DemoJob execute param: {}", param);
	}
}

package fun.asgc.solon.extend.job;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
@Accessors(chain = true)
@Data
public class JobInfo {
	private String id;
	private String name;
	private String desc;
	private String cron;
	private String param;
	private boolean enable;
	private Map<String, Object> extension;
}

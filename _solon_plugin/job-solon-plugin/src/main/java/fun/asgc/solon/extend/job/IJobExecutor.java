package fun.asgc.solon.extend.job;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public interface IJobExecutor {

	/**
	 * 初始化
	 * @throws JobException
	 */
	void init() throws Exception;

	/**
	 * 新增job
	 * @param jobInfo
	 */
	void add(JobInfo jobInfo);

	/**
	 * 删除job
	 * @param jobName
	 */
	void remove(String jobName);

	/**
	 * 触发
	 * @param jobName
	 * @param param
	 */
	void trigger(String jobName, String param);
}

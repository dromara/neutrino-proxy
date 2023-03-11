package fun.asgc.solon.extend.job;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public interface IJobCallback {

	/**
	 * 执行日志
	 * @param jobInfo
	 * @param param
	 * @param throwable
	 */
	void executeLog(JobInfo jobInfo, String param, Throwable throwable);
}

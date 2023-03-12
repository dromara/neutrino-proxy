package fun.asgc.solon.extend.job;

/**
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public interface IJobHandler {

	/**
	 * job执行
	 * @param param
	 * @throws Exception
	 */
	void execute(String param) throws Exception;

}

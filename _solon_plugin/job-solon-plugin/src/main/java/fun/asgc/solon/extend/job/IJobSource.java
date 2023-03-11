package fun.asgc.solon.extend.job;

import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/9/4
 */
public interface IJobSource {

	/**
	 * 获取所有job列表
	 * @return
	 */
	List<JobInfo> sourceList();
}

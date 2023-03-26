package fun.asgc.neutrino.proxy.server.controller.req.system;

import lombok.Data;

/**
 * 端口分组创建请求
 *
 *
 */
@Data
public class PortGroupCreateReq {

	/**
	 * 分组名称
	 */
	private String name ;

	/**
	 * 所有者类型 (0、全局共享 1、用户所有 2License所有)
	 */
	private Integer possessorType ;

	/**
	 * 所有者id(当type为0时 固定为-1、当type为1时为用户id 、当type为2时为licenseid)
	 */
	private Integer possessorId ;
}

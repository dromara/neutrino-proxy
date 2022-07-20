package fun.asgc.neutrino.proxy.server.service;

import fun.asgc.neutrino.core.annotation.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/20
 */
@Component
public class Test1Service {
	private static final SimpleDateFormat SDF = new SimpleDateFormat( "yyyy-MM-dd :HH:mm:ss");

	public String hello() {
		return "hello 现在时间是：" + SDF.format(new Date());
	}

}

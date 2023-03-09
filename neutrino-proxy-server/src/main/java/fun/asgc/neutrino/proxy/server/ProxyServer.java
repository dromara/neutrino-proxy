package fun.asgc.neutrino.proxy.server;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@Controller
public class ProxyServer {

	public static void main(String[] args) {
		Solon.start(ProxyServer.class, args);
	}
}

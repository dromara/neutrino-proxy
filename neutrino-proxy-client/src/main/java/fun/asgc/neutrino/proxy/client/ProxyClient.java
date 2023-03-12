package fun.asgc.neutrino.proxy.client;

import org.noear.solon.Solon;
import org.noear.solon.annotation.SolonMain;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@SolonMain
public class ProxyClient {

	public static void main(String[] args) {
		Solon.start(ProxyClient.class, args);
	}

}

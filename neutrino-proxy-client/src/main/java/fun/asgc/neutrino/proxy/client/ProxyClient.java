package fun.asgc.neutrino.proxy.client;

import fun.asgc.neutrino.core.annotation.NeutrinoApplication;
import fun.asgc.neutrino.core.context.NeutrinoLauncher;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
@NeutrinoApplication(environmentVariableKey = "NeutrinoProxyClient")
public class ProxyClient {

	public static void main(String[] args) {
		NeutrinoLauncher.run(ProxyClient.class, args);
	}

}

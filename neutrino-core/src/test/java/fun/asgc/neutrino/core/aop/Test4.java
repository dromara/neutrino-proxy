/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fun.asgc.neutrino.core.aop;

import fun.asgc.neutrino.core.aop.proxy.Proxy;
import fun.asgc.neutrino.core.base.GlobalConfig;
import org.junit.Test;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/7
 */
public class Test4 {

	static {
		// 配置输出生成代理类源码
		GlobalConfig.setIsPrintGeneratorCode(true);
	}

	@Test
	public void jdk代理1() throws Exception {
		Player player = Proxy.getProxyFactory(ProxyStrategy.JDK_DYNAMIC_PROXY).get(RadioPlayer.class, Player.class);
		player.on();
		player.play();
		player.off();
	}

	@Test
	public void jdk代理2() throws Exception {
		SoundPlayer player = Proxy.getProxyFactory(ProxyStrategy.JDK_DYNAMIC_PROXY).get(RadioPlayer.class, SoundPlayer.class);
		player.on();
		player.play();
		player.volumeReduce();
		player.off();
	}

	/**
	 * 无法被代理，应抛出异常
	 * @throws Exception
	 */
	@Test
	public void jdk代理3() throws Exception {
		DefaultPlayer player = Proxy.getProxyFactory(ProxyStrategy.JDK_DYNAMIC_PROXY).get(RadioPlayer.class, DefaultPlayer.class);
		player.on();
		player.play();
		player.off();
	}

	@Test
	public void 子类代理1() throws Exception {
		Player player = Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY).get(RadioPlayer.class, Player.class);
		player.on();
		player.play();
		player.off();
	}

	@Test
	public void 子类代理2() throws Exception {
		DefaultPlayer player = Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY).get(RadioPlayer.class, DefaultPlayer.class);
		player.on();
		player.play();
		player.off();
		System.out.println(player.getName());
	}

	@Test
	public void 子类代理3() throws Exception {
		SoundPlayer player = Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY).get(RadioPlayer.class, SoundPlayer.class);
		player.on();
		player.play();
		player.volumeIncrease();
		player.off();
	}

	@Test
	public void 子类代理4() throws Exception {
		RadioPlayer player = Proxy.getProxyFactory(ProxyStrategy.ASGC_PROXY).get(RadioPlayer.class, RadioPlayer.class);
		player.on();
		player.play();
		player.volumeIncrease();
		player.off();
	}
}

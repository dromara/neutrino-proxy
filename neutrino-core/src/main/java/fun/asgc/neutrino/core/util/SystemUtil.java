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

package fun.asgc.neutrino.core.util;

import fun.asgc.neutrino.core.base.CodeBlock;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public class SystemUtil {

	public static void addShutdownHook(Runnable runnable) {
		Runtime.getRuntime().addShutdownHook(new Thread(runnable));
	}

	/**
	 * 等待进程销毁
	 * @return
	 */
	public static RunContext waitProcessDestroy() {
		return waitProcessDestroy(null);
	}

	/**
	 * 等待进程销毁
	 * @param destroy
	 * @return
	 */
	public static RunContext waitProcessDestroy(CodeBlock destroy) {
		RunContext context = new RunContext();
		SystemUtil.addShutdownHook(() -> {
			synchronized (context) {
				if (null != destroy) {
					destroy.execute();
				}
				context.stop();
				context.notify();
			}
		});
		return context;
	}


	public static class RunContext {
		private volatile  boolean running = true;

		public void sync() {
			synchronized (this) {
				while (running) {
					try {
						this.wait();
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}

		private void stop() {
			this.running = false;
		}

	}
}

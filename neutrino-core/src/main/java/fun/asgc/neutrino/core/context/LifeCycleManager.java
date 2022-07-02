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
package fun.asgc.neutrino.core.context;

import fun.asgc.neutrino.core.base.CodeBlock;

/**
 *
 * @author: 生命周期管理
 * @date: 2022/7/2
 */
public class LifeCycleManager {
	/**
	 * 生命周期状态
	 */
	private LifeCycleStatus status;

	private LifeCycleManager() {
		this.status = LifeCycleStatus.CREATE;
	}

	public synchronized void init(CodeBlock codeBlock) {
		if (this.status == LifeCycleStatus.CREATE) {
			codeBlock.execute();
			this.status = LifeCycleStatus.INIT;
		}
	}

	public synchronized void run(CodeBlock codeBlock) {
		if (this.status == LifeCycleStatus.INIT) {
			codeBlock.execute();
			this.status = LifeCycleStatus.RUN;
		}
	}

	public synchronized void destroy(CodeBlock codeBlock) {
		if (this.status != LifeCycleStatus.DESTROY) {
			codeBlock.execute();
			this.status = LifeCycleStatus.DESTROY;
		}
	}

	public static LifeCycleManager create() {
		return new LifeCycleManager();
	}

	public LifeCycleStatus getStatus() {
		return status;
	}
}

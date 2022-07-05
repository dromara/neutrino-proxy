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
package fun.asgc.neutrino.core.bean.test1;

import fun.asgc.neutrino.core.annotation.*;
import fun.asgc.neutrino.core.bean.BeanMatchMode;
import fun.asgc.neutrino.core.bean.SimpleBeanFactory;
import fun.asgc.neutrino.core.launcher.NeutrinoLauncher;

import java.util.List;
import java.util.Set;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/4
 */
@NeutrinoApplication
public class Launcher {

	@Autowired
	private Cat cat;
	@Autowired(matchMode = BeanMatchMode.ByName, value = "cat")
	private Animal cat2;
	@Autowired(matchMode = BeanMatchMode.ByType)
	private Cat cat3;
	@Autowired(matchMode = BeanMatchMode.ByType, parameterTypes = Cat.class)
	private List<Cat> cats1;
	@Autowired(value = "cat", matchMode = BeanMatchMode.ByName, parameterTypes = Cat.class)
	private List<Cat> cats2;

	@Autowired
	private Dog dog;
	@Autowired("dog")
	private Dog dog2;
	@Autowired
	private Dog dog10;
	@Autowired(value = "dog", matchMode = BeanMatchMode.ByName, parameterTypes = Dog.class)
	private Object[] dogs1;
	@Autowired(matchMode = BeanMatchMode.ByType, parameterTypes = Dog.class)
	private Set<Dog> dogs2;
	@Autowired(matchMode = BeanMatchMode.ByType, parameterTypes = Animal.class)
	private List<Animal> list;

	@Autowired
	private SimpleBeanFactory applicationBeanFactory;

	@Init
	public void init() {
		System.out.println("初始化---");
//		cat.call();
//		dog.call();
	}

	@Destroy
	public void destroy() {
		System.out.println("销毁---");
	}

	public static void main(String[] args) {
		NeutrinoLauncher.run(Launcher.class, args).sync();
	}
}

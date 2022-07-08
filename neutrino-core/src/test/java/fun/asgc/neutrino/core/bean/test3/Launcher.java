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
package fun.asgc.neutrino.core.bean.test3;

import fun.asgc.neutrino.core.annotation.Autowired;
import fun.asgc.neutrino.core.annotation.Bean;
import fun.asgc.neutrino.core.annotation.NeutrinoApplication;
import fun.asgc.neutrino.core.context.ApplicationRunner;
import fun.asgc.neutrino.core.context.NeutrinoLauncher;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/8
 */
@NeutrinoApplication
public class Launcher implements ApplicationRunner {
	@Autowired
	private Person person;

	@Override
	public void run(String[] args) {
		person.walkTheDog();
	}

	public static void main(String[] args) {
		NeutrinoLauncher.run(Launcher.class, args).sync();
	}

	@Bean
	public Dog dog() {
		return new Dog("大黄");
	}

	@Bean
	public Person person(@Autowired("dog") Dog dog) {
		Person person = new Person("张三");
		person.setPet(dog);
		return person;
	}
}

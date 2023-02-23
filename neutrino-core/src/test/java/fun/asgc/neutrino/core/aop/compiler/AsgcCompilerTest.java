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
package fun.asgc.neutrino.core.aop.compiler;

import fun.asgc.neutrino.core.aop.compiler.internal.JarLauncher;
import fun.asgc.neutrino.core.util.ReflectUtil;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/8/17
 */
public class AsgcCompilerTest {

	@Test
	public void compileAndLoadClass1() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		AsgcCompiler compiler = new AsgcCompiler();
		String code = "package a.b;\n" +
			"public class Hello {\n" +
			"\tpublic void hello() {\n" +
			"\t\tSystem.out.println(\"hello\");\n" +
			"\t}\n" +
			"}\n";
		Class clazz = compiler.compile("a.b","Hello", code);
		Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("hello")).findFirst().get();
		Object instance = clazz.newInstance();
		method.invoke(instance);
	}

	@Test
	public void compileAndLoadClass2() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		AsgcCompiler compiler = new AsgcCompiler();
		String code = "package a.b;\n" +
			"import fun.asgc.neutrino.core.aop.compiler.Animal;\n" +
			"public class Panda implements Animal {\n" +
			"\tpublic void eat(String food) {\n" +
			"\t\tSystem.out.println(\"熊猫正在吃\" + food);\n" +
			"\t}\n" +
			"}\n";
		Class clazz = compiler.compile("a.b","Panda", code);
		Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("eat")).findFirst().get();
		Object instance = clazz.newInstance();
		method.invoke(instance, "竹子");
	}

	@Test
	public void compileAndLoadClass3() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		AsgcCompiler compiler = new AsgcCompiler();
		compiler.addClasspath("/Users/yangwen/my/tmp/java");
		String code = "package a.b;\n" +
			"import fun.asgc.cptest.Player;\n" +
			"public class RadioPlayer implements Player {\n" +
			"\tpublic void play() {\n" +
			"\t\tSystem.out.println(\"收音机播放\");\n" +
			"\t}\n" +
			"}\n";
		Class clazz = compiler.compile("a.b","RadioPlayer", code);
		Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("play")).findFirst().get();
		Object instance = clazz.newInstance();
		method.invoke(instance);
	}

	@Test
	public void compileAndLoadClass4() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		AsgcCompiler compiler = new AsgcCompiler();
		compiler.addClasspath("/Users/yangwen/my/tmp/java");
		compiler.addClasspath("/Users/yangwen/my/tmp/java/asgc-package-lab1-1.0-SNAPSHOT.jar");
		String code = "package a.b;\n" +
			"import fun.asgc.lab.pkg.lab1.Dog;\n" +
			"import fun.asgc.cptest.Player;\n" +
			"public class RadioPlayer implements Player {\n" +
			"\tpublic void play() {\n" +
			"\t\tSystem.out.println(Dog.class);\n" +
			"\t\tSystem.out.println(new Dog(\"大黄\").eat(\"骨头\"));\n" +
			"\t\tSystem.out.println(\"收音机播放\");\n" +
			"\t}\n" +
			"}\n";
//		GlobalConfig.setIsSaveGeneratorCode(true);
		Class clazz = compiler.compile("a.b","RadioPlayer", code);
		Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("play")).findFirst().get();
		Object instance = clazz.newInstance();
		method.invoke(instance);
	}

	/**
	 * jar in jar测试
	 *
	 * 使用jdk1.8跑，tools.jar放在classpath下
	 * 测试jar in jar
	 * jar结构：
	 * asgc-package-lab2.jar
	 *      - BOOT-INF/
	 *          - classes/fun/asgc/lab/pkg/lab2/
	 *              - Console.class
	 *          - lib/asgc-package-lab1-1.0-SNAPSHOT.jar
	 *              - BOOT-INF/classes/fun/asgc/lab/pkg/lab1/
	 *                  - Dog.class
	 */
	@Test
	public void compileAndLoadClass5() throws Exception {
        JarLauncher jarLauncher = new JarLauncher("/Users/yangwen/my/tmp/java/asgc-package-lab2.jar");
		AsgcCompiler compiler = new AsgcCompiler(jarLauncher.createClassLoader());
		String code = "package a.b;\n" +
				"import fun.asgc.lab.pkg.lab2.Console;\n" +
				"import fun.asgc.lab.pkg.lab1.Dog;\n" +
				"public class Test {\n" +
				"\tpublic void execute() {\n" +
				"\t\tSystem.out.println(\"1122\");\n" +
				"\t}\n" +
				"\tpublic void echo(String str){\n" +
				"\t\tConsole.println(str);\n" +
				"\t}\n" +
				"\tpublic String dogEat(String name, String food){\n" +
				"\t\treturn new Dog(name).eat(food);\n" +
				"\t}\n" +
				"}\n";
		System.out.println(code);
		Class clazz = compiler.compile("a.b","Test", code);
		Method executeMethod = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("execute")).findFirst().get();
		Method echoMethod = Stream.of(clazz.getDeclaredMethods()).filter(m -> m.getName().equals("echo")).findFirst().get();
		Method dogEatMethod = Stream.of(clazz.getDeclaredMethods()).filter(m -> m.getName().equals("dogEat")).findFirst().get();

		Object instance = clazz.newInstance();
		executeMethod.invoke(instance);
		echoMethod.invoke(instance, "哈哈哈哈");
		System.out.println(dogEatMethod.invoke(instance, "大黄", "骨头"));
	}

	private Object eval(String expression) {
		String code = "package fun.asgc.test;\n" +
			"import static java.lang.Math.*;\n" +
			"public class Calc {\n" +
			"\tpublic static Object invoke(){\n" +
			"\t\treturn " + expression + ";\n" +
			"\t}\n" +
			"}\n";
		AsgcCompiler compiler = new AsgcCompiler();
		try {
			Class clazz = compiler.compile( "fun.asgc.test","Calc", code);
			Method method = ReflectUtil.getMethods(clazz).stream().filter(m -> m.getName().equals("invoke")).findFirst().get();
			return method.invoke(null);
		} catch (ClassNotFoundException|IllegalAccessException|InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Test
	public void evalTest() {
		System.out.println(eval("1 + max(2*3, pow(2,5)) + 100 - 5 * 40 / (2 * 4)"));
	}
}

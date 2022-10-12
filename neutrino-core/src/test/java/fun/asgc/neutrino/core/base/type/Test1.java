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
package fun.asgc.neutrino.core.base.type;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * @author: aoshiguchen
 * @date: 2022/9/24
 */
public class Test1 {
    @Test
    public void test() throws NoSuchFieldException {
        Field param = GenericClazz.class.getDeclaredField("param");
        Type genericType = param.getGenericType();
        ParameterizedType type = (ParameterizedType) genericType;
        Type[] typeArguments = type.getActualTypeArguments();
        System.out.println("从 HashMap<String, List<Integer>> 中获取 String:" + typeArguments[0]);
        System.out.println("从 HashMap<String, List<Integer>> 中获取 List<Integer> :" + typeArguments[1]);
        System.out.println(
                "从 HashMap<String, List<Integer>> 中获取 List :" + ((ParameterizedType) typeArguments[1]).getRawType());
        System.out.println("从 HashMap<String, List<Integer>> 中获取 Integer:" + ((ParameterizedType) typeArguments[1])
                .getActualTypeArguments()[0]);
        System.out.println("从 HashMap<String, List<Integer>> 中获取父类型:"+param.getType().getGenericSuperclass());
    }

    public static class GenericClazz {
        private HashMap<String, List<Integer>> param;
    }
}

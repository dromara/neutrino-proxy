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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author: aoshiguchen
 * @date: 2022/9/25
 */
public interface ParameterNameDiscoverer {

    /**
     * Return parameter names for this method,
     * or {@code null} if they cannot be determined.
     * @param method method to find parameter names for
     * @return an array of parameter names if the names can be resolved,
     * or {@code null} if they cannot
     */
    String[] getParameterNames(Method method);

    /**
     * Return parameter names for this constructor,
     * or {@code null} if they cannot be determined.
     * @param ctor constructor to find parameter names for
     * @return an array of parameter names if the names can be resolved,
     * or {@code null} if they cannot
     */
    String[] getParameterNames(Constructor<?> ctor);

}

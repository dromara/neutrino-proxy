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
package fun.asgc.neutrino.core.security;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.stream.Collectors;

/**
 * @author: aoshiguchen
 * @date: 2022/9/25
 */
public class SecurityManagerTest {

    public static void main(String[] args) {
        // -Djava.security.manager -Djava.security.policy=/work/tmp/policy1.policy
        System.out.println("SecurityManager: " + System.getSecurityManager());

        try (BufferedReader br = new BufferedReader(new FileReader("/work/tmp/test.txt"))){
            System.out.println("content:\n" + br.lines().collect(Collectors.joining()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                System.out.println(System.getProperty("file.encoding"));
                return null;
            }
        });
//        System.out.println(System.getProperty("file.encoding"));
    }

}

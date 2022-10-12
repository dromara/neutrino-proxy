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

import fun.asgc.neutrino.core.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author: aoshiguchen
 * @date: 2022/9/25
 */
public abstract class ParameterizedTypeReference<T> {

    private final Type type;


    protected ParameterizedTypeReference() {
        Class<?> parameterizedTypeReferenceSubclass = findParameterizedTypeReferenceSubclass(getClass());
        Type type = parameterizedTypeReferenceSubclass.getGenericSuperclass();
        Assert.isInstanceOf(ParameterizedType.class, type, "Type must be a parameterized type");
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        Assert.isTrue(actualTypeArguments.length == 1, "Number of type arguments must be 1");
        this.type = actualTypeArguments[0];
    }

    private ParameterizedTypeReference(Type type) {
        this.type = type;
    }


    public Type getType() {
        return this.type;
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj || (obj instanceof ParameterizedTypeReference &&
                this.type.equals(((ParameterizedTypeReference<?>) obj).type)));
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public String toString() {
        return "ParameterizedTypeReference<" + this.type + ">";
    }


    /**
     * Build a {@code ParameterizedTypeReference} wrapping the given type.
     * @param type a generic type (possibly obtained via reflection,
     * e.g. from {@link java.lang.reflect.Method#getGenericReturnType()})
     * @return a corresponding reference which may be passed into
     * {@code ParameterizedTypeReference}-accepting methods
     * @since 4.3.12
     */
    public static <T> ParameterizedTypeReference<T> forType(Type type) {
        return new ParameterizedTypeReference<T>(type) {
        };
    }

    private static Class<?> findParameterizedTypeReferenceSubclass(Class<?> child) {
        Class<?> parent = child.getSuperclass();
        if (Object.class == parent) {
            throw new IllegalStateException("Expected ParameterizedTypeReference superclass");
        }
        else if (ParameterizedTypeReference.class == parent) {
            return child;
        }
        else {
            return findParameterizedTypeReferenceSubclass(parent);
        }
    }

}

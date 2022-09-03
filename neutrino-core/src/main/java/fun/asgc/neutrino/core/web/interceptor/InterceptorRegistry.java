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
package fun.asgc.neutrino.core.web.interceptor;

import fun.asgc.neutrino.core.base.OrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/7/27
 */
public class InterceptorRegistry {

	private final List<InterceptorRegistration> registrations = new ArrayList<>();

	public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor) {
		InterceptorRegistration registration = new InterceptorRegistration(interceptor);
		this.registrations.add(registration);
		Collections.sort(this.registrations, INTERCEPTOR_ORDER_COMPARATOR);
		return registration;
	}

	/**
	 * Return all registered interceptors.
	 */
	public List<Object> getInterceptors() {
		List<Object> result = new ArrayList<Object>(this.registrations.size());
		for (InterceptorRegistration registration : this.registrations) {
			result.add(registration.getInterceptor());
		}
		return result;
	}


	private static final Comparator<Object> INTERCEPTOR_ORDER_COMPARATOR =
		OrderComparator.INSTANCE.withSourceProvider(new OrderComparator.OrderSourceProvider() {
			@Override
			public Object getOrderSource(final Object object) {
				if (object instanceof InterceptorRegistration) {
					return ((InterceptorRegistration) object).toOrdered();
				}
				return null;
			}
		});

}

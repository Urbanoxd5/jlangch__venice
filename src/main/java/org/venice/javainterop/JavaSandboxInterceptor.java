/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2014-2018 Venice
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.venice.javainterop;

import org.venice.impl.types.collections.VncList;


public class JavaSandboxInterceptor extends JavaValueFilterInterceptor {
	
	public JavaSandboxInterceptor(final WhiteList whiteList) {
		this.whiteList = whiteList;
	}
	

	public Object onInvokeInstanceMethod(
			final IInvoker invoker, 
			final Object receiver, 
			final String method, 
			final Object... args
	) {
		validateAccessor(receiver, method);
	
		return super.onInvokeInstanceMethod(invoker, receiver, method, args);
	}

	public Object onInvokeStaticMethod(
			final IInvoker invoker, 
			final Class<?> receiver, 
			final String method, 
			final Object... args
	) {
		validateAccessor(receiver, method);

		return super.onInvokeStaticMethod(invoker, receiver, method, args);
	}

	public Object onInvokeConstructor(
			final IInvoker invoker, 
			final Class<?> receiver,
			final Object... args
	) {
		return super.onInvokeConstructor(invoker, receiver, args);
	}

	public Object onGetBeanProperty(
			final IInvoker invoker, 
			final Object receiver, 
			final String property
	) {
		validateAccessor(receiver, property);
		
		return super.onGetBeanProperty(invoker, receiver, property);
	}

	public Object onSetBeanProperty(
			final IInvoker invoker, 
			final Object receiver, 
			final String property, 
			final Object value
	) {
		validateAccessor(receiver, property);
		
		return super.onSetBeanProperty(invoker, receiver, property, value);
	}

	public Object onGetStaticField(
			final IInvoker invoker, 
			final Class<?> receiver, 
			final String fieldName
	) {
		validateAccessor(receiver, fieldName);
		
		return super.onGetStaticField(invoker, receiver, fieldName);
	}

	public Object onGetInstanceField(
			final IInvoker invoker, 
			final Object receiver, 
			final String fieldName
	) {
		validateAccessor(receiver, fieldName);
		
		return super.onGetInstanceField(invoker, receiver, fieldName);
	}

	public Object filter(final Object obj) {
		validateClass(obj);
		return obj;
	}

	public Object filterAccessor(final Object o, final String accessor) {
		validateAccessor(o, accessor);
		return o;
	}

	public void checkBlackListedVeniceFunction(
			final String funcName, 
			final VncList args
	) {
		if (whiteList.isBlackListedVeniceFunction(funcName, args)) {
			throw new SecurityException(String.format(
					"Venice Sandbox: Access denied to function %s", 
					funcName));
		}
	}

	private void validateClass(final Object obj) {
		if (obj != null) {
			final Class<?> clazz= getClass(obj);

			if (!whiteList.isWhiteListed(clazz)) {
				throw new SecurityException(String.format(
						"Venice Sandbox: Access denied to class %s", 
						clazz.getName()));
			}
		}
	}

	private void validateAccessor(final Object receiver, final String accessor) {
		if (receiver != null) {
			final Class<?> clazz= getClass(receiver);
			if (!whiteList.isWhiteListed(clazz, accessor)) {
				throw new SecurityException(String.format(
						"Venice Sandbox: Access denied to accessor %s::%s", 
						clazz.getName(), accessor));
			}
		}			
	}
	
	private Class<?> getClass(final Object obj) {
		if (obj != null) {
			return obj instanceof Class<?> ? (Class<?>)obj : obj.getClass();
		}
		else {
			return null;
		}
	}
	
	
	private final WhiteList whiteList;
}

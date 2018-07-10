/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2018 Venice
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
package org.venice.impl.javainterop;

import org.venice.impl.util.reflect.ReflectionAccessor;
import org.venice.javainterop.AcceptAllInterceptor;
import org.venice.javainterop.JavaInterceptor;


public class JavaInterop {

	public static void enableReflectionCache(final boolean enable) {
		ReflectionAccessor.enableCache(enable);
	}

	public static boolean isReflectionCacheEnabled() {
		return ReflectionAccessor.isCacheEnabled();
	}
	
	
	
	public static void register(final JavaInterceptor interceptor) {
		threadInterceptor.set(interceptor == null ? new AcceptAllInterceptor() : interceptor);
	}

	public static void unregister() {
		threadInterceptor.remove();
	}

	
	private static final ThreadLocal<JavaInterceptor> threadInterceptor = 
			ThreadLocal.withInitial(() -> new AcceptAllInterceptor());
	
	public static JavaInterceptor getInterceptor() {
		return threadInterceptor.get();
	}
}

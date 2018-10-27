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
package com.github.jlangch.venice.sandbox;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.Venice;
import com.github.jlangch.venice.javainterop.Interceptor;
import com.github.jlangch.venice.javainterop.SandboxInterceptor;
import com.github.jlangch.venice.javainterop.SandboxRules;


public class Sandbox_JavaDynProxy_Test {
		
	@Test
	public void test_proxy() {
		final String script =
			"(do                                                          \n" +
			"    (import :java.util.function.Predicate)                   \n" +
			"    (import :java.util.stream.Collectors)                    \n" +
			"                                                             \n" +
			"    (-> (. [1 2 3 4] :stream)                                \n" +
			"       (. :filter (proxify :Predicate { :test #(> % 2) }))   \n" +
			"        (. :collect (. :Collectors :toList))))                 ";

		
		final Interceptor interceptor = 
				new SandboxInterceptor(
						new SandboxRules()
								.withClasses(
										"java.util.ArrayList:*",
										"java.util.function.*:*",
										"java.util.stream.*:*"));				

		new Venice(interceptor).eval(script);
	}

}

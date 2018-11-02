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
package com.github.jlangch.venice.examples;

import com.github.jlangch.venice.Venice;
import com.github.jlangch.venice.javainterop.IInterceptor;
import com.github.jlangch.venice.javainterop.Interceptor;
import com.github.jlangch.venice.javainterop.SandboxInterceptor;
import com.github.jlangch.venice.javainterop.SandboxRules;


public class SandboxExample {
	
	public static void main(final String[] args) {
		// --------------------------------------------------------------
		// Reject all Java calls and all Venice IO functions
		// --------------------------------------------------------------
		try {
			sandboxing_strict();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		// --------------------------------------------------------------
		// Allow dedicated Java calls and reject all Venice IO functions 
		// --------------------------------------------------------------
		try {
			sandboxing_java_calls_with_safe_venice_func();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void sandboxing_strict() {
		// disable all Java calls and all Venice IO functions
		// like 'println', 'slurp', ...
		//
		// Note: Using the RejectAllInterceptor has the same effect
		final Interceptor interceptor = new SandboxInterceptor(
													new SandboxRules().rejectAllVeniceIoFunctions());
		
		final Venice venice = new Venice(interceptor);

		// => FAIL (Venice IO function) with Sandbox SecurityException
		venice.eval("(println 100)"); 
	}
	
	private static void sandboxing_java_calls_with_safe_venice_func() {
		final IInterceptor interceptor =
				new SandboxInterceptor(
						new SandboxRules()
								.rejectAllVeniceIoFunctions()
								.withClasses(
									"java.lang.Long",  // Math::min, Math::max arguments/return type
									"java.lang.Boolean",  // ArrayList::add return type
									"java.lang.Math:min", 
									"java.lang.Math:max", 
									"java.time.ZonedDateTime:*", 
									"java.util.ArrayList:new",
									"java.util.ArrayList:add"));

		final Venice venice = new Venice(interceptor);

		// => OK (static method)
		venice.eval("(. :java.lang.Math :min 20 30)"); 
		
		// => OK (static method)
		venice.eval("(. :java.lang.Math :max 20 30)"); 
		
		// => OK (constructor & instance method)
		venice.eval("(. (. :java.time.ZonedDateTime :now) :plusDays 5))"); 
		
		// => OK (constructor)
		venice.eval("(. :java.util.ArrayList :new)");
	
		// => OK (constructor & instance method)
		venice.eval(
				"(doto (. :java.util.ArrayList :new)  " +
				"      (. :add 1)                     " +
				"      (. :add 2))                    ");

		// => FAIL (static method) with Sandbox SecurityException
		venice.eval("(. :java.lang.System :exit 0)"); 
	}
	
}
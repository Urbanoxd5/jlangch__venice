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
package com.github.jlangch.venice.impl.functions;

import java.util.concurrent.ConcurrentHashMap;

import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;


public class Delay {

	public Delay(final VncFunction fn) {
		this.fn = fn;
	}
	
	public VncVal deref() {
		return results.computeIfAbsent("result", k -> compute()).deref();
	}
	
	private Result compute() {
		try {
			return new Result(fn.apply(new VncList()), null);
		}
		catch(RuntimeException ex) {
			return new Result(null, ex);
		}
	}
	
	private static class Result {
		public Result(final VncVal val, final RuntimeException ex) {
			this.val = val;
			this.ex = ex;
		}
		
		public VncVal deref() {
			if (val != null) {
				return val;
			}
			else {
				throw ex;
			}
		}
		
		private final VncVal val;
		private final RuntimeException ex;
	}
	
	private final VncFunction fn;
	private final ConcurrentHashMap<String,Result> results = new ConcurrentHashMap<>();
}
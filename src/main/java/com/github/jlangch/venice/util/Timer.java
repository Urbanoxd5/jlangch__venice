/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2019 Venice
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
package com.github.jlangch.venice.util;

import java.io.Serializable;


public class Timer implements Serializable {
	public Timer(final String name, final int count, final long elapsedNanos) {
		this.name = name;
		this.count = count;
		this.elapsedNanos = elapsedNanos;
	}
	
	public Timer add(final long elapsedNanos) {
		return new Timer(name, count + 1, this.elapsedNanos + elapsedNanos);
	}
	
	public final String name; 
	public final int count; 
	public final long elapsedNanos;
	
	private static final long serialVersionUID = -1;
}

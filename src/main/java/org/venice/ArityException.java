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
package org.venice;


public class ArityException extends VncException {

	public ArityException(final int arity, final String name) {
		this(arity, name, null);
	}

	public ArityException(final int actual, final String name, Throwable cause) {
		super(
			String.format("Wrong number of args %d passed to %s", actual, name), 
			cause);
		
		this.arity = actual;
		this.name = name;
	}


	public int getArity() {
		return arity;
	}

	public String getName() {
		return name;
	}

	
	private static final long serialVersionUID = 1349237272157335345L;

	private final int arity;
	private final String name;
}

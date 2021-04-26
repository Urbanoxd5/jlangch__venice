/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2021 Venice
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
package com.github.jlangch.venice.impl.repl;

import org.jline.terminal.Terminal;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.env.Env;
import com.github.jlangch.venice.impl.env.Var;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncOrderedMap;
import com.github.jlangch.venice.impl.util.ArityExceptions;


public class ReplFunctions {

	public static void register(
			final Env env,
			final Terminal terminal
	) {
		registerFn(env, createReplInfoFn(terminal));
		registerFn(env, createTermRowsFn(terminal));
		registerFn(env, createTermColsFn(terminal));
	}
	
	private static void registerFn(final Env env, final VncFunction fn) {
		env.setGlobal(
			new Var(
				 	new VncSymbol(fn.getQualifiedName()), 
				 	fn, 
				 	false));
	}
	
	private static VncFunction createReplInfoFn(final Terminal terminal) {
		return 
			new VncFunction(
					"repl/info",
					VncFunction
						.meta()
						.arglists("(repl/info)")
						.doc("Returns information on the REPL. \n\n" +
							 "E.g.: {:term-name \"JLine terminal\"\n" +
							 "       :term-type \"xterm-256color\"\n" +
							 "       :term-rows 24\n" +
							 "       :term-cols 101\n" +
							 "       :term-class :PosixSysTerminal}")
						.build()
			) {
				public VncVal apply(final VncList args) {
					ArityExceptions.assertArity(this, args, 0);
	
					try {
						return VncOrderedMap.of(
								new VncKeyword("term-name"),  new VncString(terminal.getName()),
								new VncKeyword("term-type"),  new VncString(terminal.getType()),
								new VncKeyword("term-cols"),  new VncLong(terminal.getSize().getColumns()),
								new VncKeyword("term-rows"),  new VncLong(terminal.getSize().getRows()),
								new VncKeyword("term-class"), new VncKeyword(terminal.getClass().getName()));
					}
					catch(Exception ex) {
						throw new VncException("Failed to get the REPL terminal info", ex);
					}
				}
	
				private static final long serialVersionUID = -1L;
			};
	}
	
	private static VncFunction createTermRowsFn(final Terminal terminal) {
		return 
			new VncFunction(
					"repl/term-rows",
					VncFunction
						.meta()
						.arglists("(repl/term-rows)")
						.doc("Returns number of rows in the REPL terminal.")
						.build()
			) {
				public VncVal apply(final VncList args) {
					ArityExceptions.assertArity(this, args, 0);
	
					return new VncLong(terminal.getSize().getRows());
				}
	
				private static final long serialVersionUID = -1L;
			};
	}
	
	private static VncFunction createTermColsFn(final Terminal terminal) {
		return 
			new VncFunction(
					"repl/term-cols",
					VncFunction
						.meta()
						.arglists("(repl/term-cols)")
						.doc("Returns number of columns in the REPL terminal.")
						.build()
			) {
				public VncVal apply(final VncList args) {
					ArityExceptions.assertArity(this, args, 0);
	
					return new VncLong(terminal.getSize().getColumns());
				}
	
				private static final long serialVersionUID = -1L;
			};
	}
}

/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
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
package com.github.jlangch.venice.impl.util;

import java.util.stream.Collectors;

import com.github.jlangch.venice.ArityException;
import com.github.jlangch.venice.impl.specialforms.SpecialFormsDoc;
import com.github.jlangch.venice.impl.types.IVncFunction;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.collections.VncSet;
import com.github.jlangch.venice.impl.types.collections.VncVector;


public class ArityExceptions {

	public static void assertArity(
			final VncFunction fn, 
			final VncList args, 
			final int... expectedArities
	) {
		final int arity = args.size();
		if (expectedArities.length == 1) {
			// optimization for single arity case
			if (arity == expectedArities[0]) return;
		}
		else {
			for (int a : expectedArities) {
				if (a == arity) return;
			}
		}
		
		throw new ArityException(
					formatArityExMsg(
						fn.getQualifiedName(), toFnType(fn), arity, fn.getArgLists()));
	}

	public static void assertArity(
			final IVncFunction fn, 
			final FnType fnType,
			final VncList args, 
			final int... expectedArities
	) {
		final int arity = args.size();
		if (expectedArities.length == 1) {
			// optimization for single arity case
			if (arity == expectedArities[0]) return;
		}
		else {
			for (int a : expectedArities) {
				if (a == arity) return;
			}
		}
		
		// handle arity exception
		if (fn instanceof VncFunction) {
			throw new ArityException(
						formatArityExMsg(
							((VncFunction)fn).getQualifiedName(), 
							toFnType((VncFunction)fn), 
							arity,
							fn.getArgLists()));
		}
		else if (fn instanceof VncKeyword) {
			throw new ArityException(
					formatArityExMsg(fn.toString(), fnType, arity, fn.getArgLists()));
		}
		else if (fn instanceof VncMap) {
			throw new ArityException(
					formatArityExMsg("map", fnType, arity, fn.getArgLists()));
		}
		else if (fn instanceof VncSet) {
			throw new ArityException(
					formatArityExMsg("set", fnType, arity, fn.getArgLists()));
		}
		else if (fn instanceof VncVector) {
			throw new ArityException(
					formatArityExMsg("vector", fnType, arity, fn.getArgLists()));
		}
		else {
			throw new ArityException(
					formatArityExMsg("unnamed", fnType, arity, fn.getArgLists()));
		}
	}

	public static void assertArity(
			final String fnName, 
			final FnType fnType,
			final VncList args, 
			final int... expectedArities
	) {
		final int arity = args.size();
		if (expectedArities.length == 1) {
			// optimization for single arity case
			if (arity == expectedArities[0]) return;
		}
		else {
			for (int a : expectedArities) {
				if (a == arity) return;
			}
		}
		
		// handle arity exception
		if (fnType == FnType.SpecialForm) {
			final VncFunction fn = (VncFunction)SpecialFormsDoc.ns.get(new VncSymbol(fnName));
			throw new ArityException(formatArityExMsg(fnName, fnType, arity, fn.getArgLists()));
		}
		else {
			throw new ArityException(formatArityExMsg(fnName, fnType, arity));
		}
	}
	
	public static void assertMinArity(
			final VncFunction fn, 
			final VncList args, 
			final int minArity
	) {
		final int arity = args.size();
		if (arity < minArity) {
			throw new ArityException(
						formatArityExMsg(
							fn.getQualifiedName(), toFnType(fn), arity, fn.getArgLists()));
		}
	}
	
	public static void assertMinArity(
			final String fnName, 
			final FnType fnType,
			final VncList args, 
			final int minArity
	) {
		final int arity = args.size();
		if (arity < minArity) {
			if (fnType == FnType.SpecialForm) {
				final VncFunction fn = (VncFunction)SpecialFormsDoc.ns.get(new VncSymbol(fnName));
				throw new ArityException(formatArityExMsg(fnName, fnType, arity, fn.getArgLists()));
			}
			else {
				throw new ArityException(formatArityExMsg(fnName, fnType, arity));
			}
		}
	}

	
	public static String formatArityExMsg(
			final String fnName,
			final FnType fnType,
			final int arity
	) {
		return formatArityExMsg(fnName, fnType, arity, VncList.empty());
	}

	public static String formatArityExMsg(
			final String fnName,
			final FnType fnType,
			final int arity,
			final VncList argList
	) {
		return String.format(
					"Wrong number of args (%d) passed to %s %s.%s", 
					arity, 
					toString(fnType),
					fnName,
					formatArgList(argList));
	}

	public static String formatArityExMsg(
			final String fnName,
			final FnType fnType,
			final int arity, 
			final int expectedArgs,
			final VncList argList
	) {
		return String.format(
					"Wrong number of args (%d) passed to %s %s. Expected %d arg%s.%s", 
					arity, 
					toString(fnType),
					fnName, 
					expectedArgs,
					expectedArgs == 1 ? "" : "s",
					formatArgList(argList));
	}
		
	public static String formatVariadicArityExMsg(
			final String fnName,
			final FnType fnType,
			final int arity,
			final int fixedArgsCount,
			final VncList argList
	) {
		return String.format(
					"Wrong number of args (%d) passed to the variadic %s %s that "
						+ "requires at least %d arg%s.%s", 
					arity, 
					toString(fnType),
					fnName,
					fixedArgsCount,
					fixedArgsCount == 1 ? "" : "s",
					formatArgList(argList));
	}

	
	private static String formatArgList(final VncList argList) {
		return argList.isEmpty()
				? ""
				: String.format(
					"\n\n[Arg List]\n%s", 
					argList
						.stream()
						.map(it -> "    " + it.toString())
						.collect(Collectors.joining("\n")));
	}

	private static FnType toFnType(final VncFunction fn) {
		return fn.isMacro() ? FnType.Macro : FnType.Function;
	}

	private static String toString(final FnType fnType) {
		final FnType type = fnType == null ? FnType.Function : fnType;
		
		switch(type) {
			case Function:    return "function";
			case Macro:       return "macro";
			case SpecialForm: return "special form";
			case Collection:  return "collection (as function)";
			case Keyword:     return "keyword (as function)";
			default:          return "function";
		}
	}
	
	
	public static enum FnType { Function, Macro, SpecialForm, Collection, Keyword };
}

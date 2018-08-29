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

import static com.github.jlangch.venice.impl.functions.FunctionsUtil.assertMinArity;
import static com.github.jlangch.venice.impl.types.Constants.Nil;

import java.io.File;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.javainterop.JavaInterop;
import com.github.jlangch.venice.impl.types.Coerce;
import com.github.jlangch.venice.impl.types.Types;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncThreadLocal;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncJavaObject;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.collections.VncVector;


public class ShellFunctions {

	///////////////////////////////////////////////////////////////////////////
	// Shell
	///////////////////////////////////////////////////////////////////////////



	public static VncFunction sh = new VncFunction("sh") {
		{
			setArgLists("(sh & args)");
			
			setDoc("Passes the given strings to Runtime.exec() to launch a sub-process.\n" + 
					"\n" +
					" Options are\n" + 
					"  :in      may be given followed by any legal input source for\n" + 
					"           clojure.java.io/copy, e.g. InputStream, Reader, File, byte[],\n" + 
					"           or String, to be fed to the sub-process's stdin.\n" + 
					"  :in-enc  option may be given followed by a String, used as a character\n" + 
					"           encoding name (for example \"UTF-8\" or \"ISO-8859-1\") to\n" + 
					"           convert the input string specified by the :in option to the\n" + 
					"           sub-process's stdin.  Defaults to UTF-8.\n" + 
					"           If the :in option provides a byte array, then the bytes are passed\n" + 
					"           unencoded, and this option is ignored.\n" + 
					"  :out-enc option may be given followed by :bytes or a String. If a\n" + 
					"           String is given, it will be used as a character encoding\n" + 
					"           name (for example \"UTF-8\" or \"ISO-8859-1\") to convert\n" + 
					"           the sub-process's stdout to a String which is returned.\n" + 
					"           If :bytes is given, the sub-process's stdout will be stored\n" + 
					"           in a byte array and returned.  Defaults to UTF-8.\n" + 
					"  :env     override the process env with a map.\n" + 
					"  :dir     override the process dir with a String or java.io.File.\n" + 
					"\n" +
					"You can bind :env or :dir for multiple operations using with-sh-env\n" + 
					"and with-sh-dir.\n" + 
					"\n" +
					" sh returns a map of\n" + 
					"  :exit => sub-process's exit code\n" + 
					"  :out  => sub-process's stdout (as byte[] or String)\n" + 
					"  :err  => sub-process's stderr (String via platform default encoding)");
		}
		
		public VncVal apply(final VncList args) {
			JavaInterop.getInterceptor().checkBlackListedVeniceFunction("sh", args);

			assertMinArity("sh", args, 1);
			
			final VncVector v = parseArgs(args);

			final VncList cmd = Coerce.toVncList(v.first());
			final VncMap opts = Coerce.toVncMap(v.second());

			try {
				final String[] cmdArr = toStringArray(cmd);
				final String[] envArr = toEnvStrings(opts.get(new VncKeyword(":env")));
				final File dir_ = toFile(opts.get(new VncKeyword(":dir")));
				
				final Process proc = Runtime.getRuntime().exec(cmdArr, envArr, dir_);
								
				final int exitCode = proc.waitFor();
				
				return new VncHashMap(
						new VncKeyword(":exit"),
						new VncLong(exitCode));
			}
			catch(Exception ex) {
				throw new VncException("Faile to exec shell", ex);
			}
		}
	};


	
	
	///////////////////////////////////////////////////////////////////////////
	// Util
	///////////////////////////////////////////////////////////////////////////

	private static File toFile(final VncVal dir) {
		if (dir == Nil) {
			return null;
		}
		else if (Types.isVncString(dir)) {
			return new File(((VncString)dir).getValue());
		}
		else if (Types.isVncJavaObject(dir)) {
			final Object delegate = ((VncJavaObject)dir).getDelegate();
			if (delegate instanceof File) {
				return (File)delegate;
			}
		}
	
		return null;
	}

	private static String[] toStringArray(final VncList list) {
		return list
				.getList()
				.stream()
				.map(it -> CoreFunctions.name.apply(new VncList(it)))
				.map(it -> ((VncString)it).getValue())
				.collect(Collectors.toList())
				.toArray(new String[] {});
	}

	private static String[] toEnvStrings(final VncVal envMap) {
		if (envMap == Nil) {
			return null;
		}
		else {
			return ((VncMap)envMap)
						.entries()
						.stream()
						.map(e -> 
							String.format(
									"%s=%s", 
									CoreFunctions.name.apply(new VncList(e.getKey())),
									e.getValue().toString()))
						.collect(Collectors.toList())
						.toArray(new String[] {});
		}
	}
	
	private static VncVector parseArgs(final VncList args) {
		final VncThreadLocal th = new VncThreadLocal();
		
		final VncMap defaultOptions = new VncHashMap(
										new VncKeyword(":out-enc"), new VncString("UTF-8"),
										new VncKeyword(":in-enc"), new VncString("UTF-8"),
										new VncKeyword(":dir"), th.get(":*sh-dir*"));

		final VncMap defaultEnv = (VncMap)th.get(":*sh-env*", new VncHashMap());

		
		final VncVector v = Coerce.toVncVector(
								CoreFunctions.split_with.apply(
									new VncList(CoreFunctions.string_Q, args)));

		final VncList cmd = Coerce.toVncList(v.first());

		final VncMap sh_opts = new VncHashMap((VncList)v.second());

		// merge options
		VncMap opts = (VncMap)CoreFunctions.merge.apply(
									new VncList(defaultOptions, sh_opts));
		
		// add merged :env map
		opts = opts.assoc(
					new VncKeyword(":env"),
					CoreFunctions.merge.apply(
							new VncList(defaultEnv, sh_opts.get(new VncKeyword(":env")))));
		
		return new VncVector(cmd, opts);
	}

	
	
	///////////////////////////////////////////////////////////////////////////
	// types_ns is namespace of type functions
	///////////////////////////////////////////////////////////////////////////

	public static Map<VncVal, VncVal> ns = 
			new VncHashMap.Builder()
					.put("sh",	sh)					
					.toMap();	
}

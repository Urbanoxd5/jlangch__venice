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
package com.github.jlangch.venice;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import com.github.jlangch.venice.impl.Env;
import com.github.jlangch.venice.impl.LoadPath;
import com.github.jlangch.venice.impl.Var;
import com.github.jlangch.venice.impl.VeniceInterpreter;
import com.github.jlangch.venice.impl.functions.AppFunctions;
import com.github.jlangch.venice.impl.javainterop.JavaInterop;
import com.github.jlangch.venice.impl.repl.REPL;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.util.Coerce;
import com.github.jlangch.venice.impl.util.CommandLineArgs;
import com.github.jlangch.venice.impl.util.FileUtil;
import com.github.jlangch.venice.javainterop.AcceptAllInterceptor;
import com.github.jlangch.venice.javainterop.IInterceptor;


public class Launcher {
	
	public static void main(final String[] args) {
		final CommandLineArgs cli = new CommandLineArgs(args);
		final IInterceptor interceptor = new AcceptAllInterceptor();
		JavaInterop.register(interceptor);
		
		final List<String> loadPaths = LoadPath.parseFromString(cli.switchValue("-loadpath"));

		try {
			if (cli.switchPresent("-file")) {
				final String file = suffixWithVeniceFileExt(cli.switchValue("-file"));
				final String script = new String(FileUtil.load(new File(file)));
				
				System.out.println(
						runScript(cli, loadPaths, interceptor, script, new File(file).getName()));
			}
			else if (cli.switchPresent("-script")) {
				final String script = cli.switchValue("-script");
				
				System.out.println(
						runScript(cli, loadPaths, interceptor, script, "script"));
			}
			else if (cli.switchPresent("-app")) {
				final File appFile = new File(suffixWithZipFileExt(cli.switchValue("-app")));
				
				final VncMap manifest = AppFunctions.getManifest(appFile);
				
				final String appName = Coerce.toVncString(manifest.get(new VncString("app-name"))).getValue();
				final String mainFile = Coerce.toVncString(manifest.get(new VncString("main-file"))).getValue();
						
				loadPaths.clear();
				loadPaths.add(appFile.getAbsolutePath());

				System.out.println(String.format("Launching Venice application '%s' ...", appName));

				final String appBootstrap = String.format("(do (load-file \"%s\") nil)", mainFile);

				runScript(cli, loadPaths, interceptor, appBootstrap, appName);
			}
			else if (cli.switchPresent("-repl")) {
				new REPL(interceptor, loadPaths).run(args);
			}
			else {
				new REPL(interceptor, loadPaths).run(args);
			}
			
			System.exit(0);
		}
		catch (VncException ex) {
			ex.printVeniceStackTrace();
			System.exit(1);
		}	
		catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}	
	}
	
	private static String runScript(
			final CommandLineArgs cli,
			final List<String> loadPaths,
			final IInterceptor interceptor,
			final String script,
			final String name
	) {
		final VeniceInterpreter venice = new VeniceInterpreter(interceptor, loadPaths);
		
		final Env env = createEnv(venice, cli);
	
		return venice.PRINT(venice.RE(script, name, env));
	}
	
	private static Env createEnv(final VeniceInterpreter venice, final CommandLineArgs cli) {
		return venice.createEnv(false)
					 .setGlobal(new Var(new VncSymbol("*ARGV*"), cli.argsAsList()))
					 .setStdoutPrintStream(new PrintStream(System.out, true));
	}

	private static String suffixWithVeniceFileExt(final String s) {
		return s == null ? null : (s.endsWith(".venice") ? s : s + ".venice");
	}

	private static String suffixWithZipFileExt(final String s) {
		return s == null ? null : (s.endsWith(".zip") ? s : s + ".zip");
	}

}

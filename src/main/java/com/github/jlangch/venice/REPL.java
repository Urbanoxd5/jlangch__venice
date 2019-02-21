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
package com.github.jlangch.venice;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.github.jlangch.venice.impl.DynamicVar;
import com.github.jlangch.venice.impl.Env;
import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.ReplConfig;
import com.github.jlangch.venice.impl.ValueException;
import com.github.jlangch.venice.impl.Var;
import com.github.jlangch.venice.impl.VeniceInterpreter;
import com.github.jlangch.venice.impl.types.VncJavaObject;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncList;
import com.github.jlangch.venice.impl.util.FileUtil;
import com.github.jlangch.venice.impl.util.ThreadLocalMap;
import com.github.jlangch.venice.util.CapturingPrintStream;
import com.github.jlangch.venice.util.CommandLineArgs;


public class REPL {
	
	public static void main(final String[] args) {
		final CommandLineArgs cli = new CommandLineArgs(args);
		if (cli.switchPresent("-file") || cli.switchPresent("-script")) {
			exec(cli);
		}
		else {
			System.out.println("REPL Venice: V" + Venice.getVersion());

			try {
				config = ReplConfig.load(cli);
				repl_jline(args);
			}
			catch (Exception ex) {
	            ex.printStackTrace();
			}	
		}
	}
	

	private static void exec(final CommandLineArgs cli) {
		final VncList argv = toList(cli.args());

		final VeniceInterpreter venice = new VeniceInterpreter();
		final Env env = venice.createEnv();
		env.setGlobal(new Var(new VncSymbol("*ARGV*"), argv));

		if (cli.switchPresent("-file")) {
			final String file = cli.switchValue("-file");
			final String script = new String(FileUtil.load(new File(file)));
			
			System.out.println(venice.PRINT(venice.RE(script, new File(file).getName(), env)));
		}
		else if (cli.switchPresent("-script")) {
			final String script = cli.switchValue("-script");
			
			System.out.println(venice.PRINT(venice.RE(script, "script", env)));
		}
	}

	private static void repl_jline(final String[] args) throws Exception {
		final VeniceInterpreter venice = new VeniceInterpreter();
		
		final CapturingPrintStream ps = CapturingPrintStream.create();
		
		final Env env = venice
							.createEnv()
							.setGlobal(new Var(new VncSymbol("*ARGV*"), toList(args)))
							.setGlobal(new DynamicVar(
											new VncSymbol("*out*"), 
											new VncJavaObject(ps)));
		
		final TerminalBuilder builder = TerminalBuilder.builder();

		final Terminal terminal = builder
									.encoding("UTF-8")
									.type("xterm-256color")
									.build();
		
        final DefaultParser parser = new DefaultParser();
        parser.setQuoteChars(new char[] {'"', '\''});
		
        final LineReader reader = LineReaderBuilder
	        						.builder()
	        						.appName("Venice")
					                .terminal(terminal)
					                //.completer(completer)
					                .parser(parser)
					                .variable(LineReader.SECONDARY_PROMPT_PATTERN, "%M%P > ")
					                .build();
        
		// REPL loop
		while (true) {
			String line;
			try {
				line = reader.readLine(PROMPT, null, (MaskingCallback) null, null);
				if (line == null) { 
					continue; 
				}
			} 
			catch (UserInterruptException ex) {
				write(terminal, "interrupt", " ! interrupted ! ");
				terminal.writer().println(" ");
				Thread.sleep(1000);
				break;
			} 
			catch (EofException | EndOfFileException ex) {
				break;
			} 
			catch (VncException ex) {
				write(terminal, "error", t -> ex.printVeniceStackTrace(t.writer()));
				continue;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				break;
			}
			
			try {
				ThreadLocalMap.clearCallStack();
				ps.reset();
				final VncVal result = venice.RE(line, "repl", env);
				final String out = ps.getOutput();
				if (out != null && !out.isEmpty()) {
					write(terminal, "stdout", out);					
				}
				write(terminal, "result", "=> " + venice.PRINT(result));
			} 
			catch (ContinueException ex) {
				continue;
			} 
			catch (ValueException ex) {
				write(terminal, "error", t -> ex.printVeniceStackTrace(t.writer()));
				write(terminal, "error", "Thrown value: " + Printer._pr_str(ex.getValue(), false));
				continue;
			} 
			catch (VncException ex) {
				write(terminal, "error", t -> ex.printVeniceStackTrace(t.writer()));
				continue;
			}
			catch (Exception ex) {
				write(terminal, "error", t -> ex.printStackTrace(t.writer()));
				continue;
			}
		}
	}
	
	private static VncList toList(final String[] args) {
		return new VncList(Arrays
							.asList(args)
							.stream()
							.map(s -> new VncString(s))
							.collect(Collectors.toList()));
	}

	private static void write(
			final Terminal terminal,
			final String colorID,
			final Consumer<Terminal> fn
	) {
		if (config.useColors()) {
			terminal.writer().print(config.getOrDefault("colors." + colorID, ""));
		}
		
		fn.accept(terminal);
		
		if (config.useColors()) {
			terminal.writer().print("\u001b[0m");
		}
		
		terminal.flush();
	}

	private static void write(
			final Terminal terminal,
			final String colorID,
			final String text
	) {
		write(terminal, colorID, t -> t.writer().println(text));
	}

	
	
	// http://www.lihaoyi.com/post/BuildyourownCommandLinewithANSIescapecodes.html#colors	
	private static ReplConfig config;
	
	private static final String PROMPT = "venice> ";
}

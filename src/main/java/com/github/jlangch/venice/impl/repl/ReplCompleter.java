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
package com.github.jlangch.venice.impl.repl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import com.github.jlangch.venice.impl.Env;
import com.github.jlangch.venice.impl.VeniceInterpreter;
import com.github.jlangch.venice.impl.util.StringUtil;

public class ReplCompleter implements Completer {
	
	public ReplCompleter(final VeniceInterpreter venice, final Env env) {
		this.venice = venice;
		this.env = env;
	}

    public void complete(
    		final LineReader reader, 
    		final ParsedLine line, 
    		final List<Candidate> candidates
    ) {
    	if (line.line().endsWith("(load-file ")) {
       		listFileNames().forEach(f -> candidates.add(new Candidate("\"" + f + "\"")));
    	}
    	else if (line.line().endsWith("(load-file \"")) {
    		listFileNames().forEach(f -> candidates.add(new Candidate(f)));
    	}
    	else if (line.line().endsWith("(load-module ")) {
    		venice.getAvailableModules()
    			  .forEach(m -> candidates.add(new Candidate(":" + m)));
     	}
    	else if (line.word().startsWith("(")) {
    		final String sym = StringUtil.trimToNull(line.word().substring(1));
    		env.getAllGlobalFunctionSymbols()
    		   .stream()
    		   .map(s -> s.getName())
    		   .filter(s -> (sym == null) || sym.isEmpty() || s.startsWith(sym))
    		   .sorted()
    		   .forEach(s -> candidates.add(new Candidate("(" + s, s, null, null, null, null, true)));
     	}
    }
    
    private List<String> listFileNames() {
    	return Arrays
	    		.stream(new File(".").listFiles())
	    		.filter(f -> f.isFile())
	    		.filter(f -> f.getName().endsWith("venice"))
	    		.map(f -> f.getName())
	    		.sorted()
	    		.collect(Collectors.toList());
     }
    
    
    private final VeniceInterpreter venice;
	private final Env env;
}

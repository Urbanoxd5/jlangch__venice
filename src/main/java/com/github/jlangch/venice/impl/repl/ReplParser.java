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
package com.github.jlangch.venice.impl.repl;

import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;
import org.jline.reader.impl.DefaultParser;

import com.github.jlangch.venice.EofException;
import com.github.jlangch.venice.impl.VeniceInterpreter;
import com.github.jlangch.venice.impl.util.StringUtil;


public class ReplParser extends DefaultParser {
	
	public ReplParser(final VeniceInterpreter venice) {
		this.venice = venice;
		
		setQuoteChars(new char[] { '"'});
	}

	@Override
	public ParsedLine parse(
			final String line, 
			final int cursor, 
			final ParseContext context
	) throws SyntaxError {
		try {
			if (context != ParseContext.COMPLETE) {
				venice.READ(line, "repl");
			}
			eof = false;
			return super.parse(line, cursor, context);
		}
		catch(EofException ex) {
			eof = true;
			// proceed with multi-line editing
			throw new EOFError(1, 1, ex.getMessage());
		}
	}

	public boolean isEOF() {
		return eof;
	}

	public void reset() {
		eof = false;
	}

	public static boolean isDroppedVeniceScriptFile(final String buffer) {
		final String filename = StringUtil.trimToEmpty(buffer);
		return filename.endsWith(".venice");
	}

	public static boolean isCommand(final String buffer) {
		final int len = buffer.length();

		if (len == 0) {
			return false;
		}
		else {
			int pos = 0;
			
			// skip spaces
			while(pos < len) {
				if (buffer.charAt(pos) != ' ') break;
				pos++;
			}
			
			return pos < len && buffer.charAt(pos) == '!';
		}
	}

	
	private final VeniceInterpreter venice;
	
	private boolean eof = false;
}

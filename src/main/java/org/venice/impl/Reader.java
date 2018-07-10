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
package org.venice.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.venice.ContinueException;
import org.venice.ParseError;
import org.venice.impl.types.Constants;
import org.venice.impl.types.VncBigDecimal;
import org.venice.impl.types.VncDouble;
import org.venice.impl.types.VncLong;
import org.venice.impl.types.VncString;
import org.venice.impl.types.VncSymbol;
import org.venice.impl.types.VncVal;
import org.venice.impl.types.collections.VncHashMap;
import org.venice.impl.types.collections.VncList;
import org.venice.impl.types.collections.VncVector;
import org.venice.impl.util.StringUtil;


public class Reader {
	
	public Reader(final ArrayList<String> tokens) {
		this.tokens = tokens;
		this.position = 0;
	}

	public String peek() {
		if (position >= tokens.size()) {
			return null;
		} 
		else {
			return tokens.get(position);
		}
	}
   
	public String next() {
		return tokens.get(position++);
	}
	
	public static ArrayList<String> tokenize(final String str) {
		final Matcher matcher = tokenize_pattern.matcher(str);

		final ArrayList<String> tokens = new ArrayList<String>();
		while (matcher.find()) {
			final String token = matcher.group(1);
			//final int tokenStartPos = matcher.start(1);
			if (token != null && !token.equals("") && !(token.charAt(0) == ';')) {
				tokens.add(token);
			}
		}
		return tokens;
	}

	public static VncVal read_atom(final Reader rdr) {
		final String token = rdr.next();
		final Matcher matcher = atom_pattern.matcher(token);
		
		if (!matcher.find()) {
			throw new ParseError("unrecognized token '" + token + "'");
		}
		
		if (matcher.group(1) != null) {
			return new VncLong(Long.parseLong(matcher.group(1)));
		} 
		else if (matcher.group(2) != null) {
			return new VncDouble(Double.parseDouble(matcher.group(2)));
		} 
		else if (matcher.group(3) != null) {
			String dec = matcher.group(3);
			dec = dec.substring(0, dec.length()-1);
			return new VncBigDecimal(new BigDecimal(dec));
		} 
		else if (matcher.group(4) != null) {
			return Constants.Nil;
		} 
		else if (matcher.group(5) != null) {
			return Constants.True;
		} 
		else if (matcher.group(6) != null) {
			return Constants.False;
		} 
		else if (matcher.group(7) != null) {
			return new VncString(StringUtil.unescape(matcher.group(7)));
		} 
		else if (matcher.group(8) != null) {
			return VncString.keyword(matcher.group(8));
		} 
		else if (matcher.group(9) != null) {
			return new VncSymbol(matcher.group(9));
		} 
		else {
			throw new ParseError("unrecognized '" + matcher.group(0) + "'");
		}
	}

	public static VncVal read_list(
			final Reader rdr, 
			final VncList lst, 
			final char start, 
			final char end
	) {
		String token = rdr.next();
		if (token.charAt(0) != start) {
			throw new ParseError("expected '" + start + "'");
		}

		while ((token = rdr.peek()) != null && token.charAt(0) != end) {
			lst.addAtEnd(read_form(rdr));
		}

		if (token == null) {
			throw new ParseError("expected '" + end + "', got EOF");
		}
		rdr.next();

		return lst;
	}

	public static VncVal read_hash_map(final Reader rdr) {
		final VncList lst = (VncList)read_list(rdr, new VncList(), '{', '}');
		return new VncHashMap(lst);
	}

	public static VncVal read_form(final Reader rdr) {
		final String token = rdr.peek();
		if (token == null) { 
			throw new ContinueException(); 
		}
		
		VncVal form;

		switch (token.charAt(0)) {
			case '\'': 
				rdr.next();
				return new VncList(new VncSymbol("quote"), read_form(rdr));
			
			case '`': 
				rdr.next();
				return new VncList(new VncSymbol("quasiquote"), read_form(rdr));
			
			case '~':
				if (token.equals("~")) {
					rdr.next();
					return new VncList(new VncSymbol("unquote"), read_form(rdr));
				} 
				else {
					rdr.next();
					return new VncList(new VncSymbol("splice-unquote"), read_form(rdr));
				}
			
			case '^': 
				rdr.next();
				final VncVal meta = read_form(rdr);
				return new VncList(new VncSymbol("with-meta"), read_form(rdr), meta);
			
			case '@': 
				rdr.next();
				return new VncList(new VncSymbol("deref"), read_form(rdr));
			
			case '(': 
				form = read_list(rdr, new VncList(), '(' , ')'); 
				break;
			
			case ')': 
				throw new ParseError("unexpected ')'");
			
			case '[': 
				form = read_list(rdr, new VncVector(), '[' , ']'); 
				break;
			
			case ']': 
				throw new ParseError("unexpected ']'");
				
			case '{': 
				form = read_hash_map(rdr); break;
				
			case '}': 
				throw new ParseError("unexpected '}'");
				
			default:  
				form = read_atom(rdr);
				break;
		}
		return form;
	}

	public static VncVal read_str(final String str) {
		return read_form(new Reader(tokenize(str)));
	}
	
	
	// group 1: integer = "(^-?[0-9]+$)";
	// group 2: decimal = "(^-?[0-9]+[.][0-9]*$)";
	// group 3: bigdecimal = "(^-?[0-9]+[.][0-9]*M$)";
	// group 4: nil = "(^nil$)";
	// group 5: true = "(^true$)";
	// group 6: false = "(^false$)";
	// group 7: string_escaped = "^\"(.*)\"$";
	// group 8: string = ":(.*)";
	// group 9: symbol = "(^[^\"]*$)";	
	private static final Pattern atom_pattern = Pattern.compile("(?s)(^-?[0-9]+$)|(^-?[0-9][0-9.]*$)|(^-?[0-9][0-9.]*M$)|(^nil$)|(^true$)|(^false$)|^\"(.*)\"$|:(.*)|(^[^\"]*$)");
	
	private static final Pattern tokenize_pattern = Pattern.compile("[\\s ,]*(~@|[\\[\\]{}()'`~@]|\"(?:[\\\\].|[^\\\\\"])*\"|;.*|[^\\s \\[\\]{}()'\"`~@,;]*)");

	private ArrayList<String> tokens;
	private int position;
}

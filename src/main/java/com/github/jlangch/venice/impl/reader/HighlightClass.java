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
package com.github.jlangch.venice.impl.reader;

public enum HighlightClass {
	// whitespaces
	COMMENT,
	WHITESPACES,
	
	// atoms
	STRING,
	NUMBER,
	CONSTANT,
	KEYWORD,
	SYMBOL,
	SYMBOL_SPECIAL_FORM,
	SYMBOL_FUNCTION_NAME,
	
	// quotes
	QUOTE,
	QUASI_QUOTE,
	UNQUOTE,
	UNQUOTE_SPLICING,

	META,
	AT,
	HASH,
	
	BRACE_BEGIN, // {
	BRACE_END,   // {
	
	BRACKET_BEGIN, // [
	BRACKET_END,   // ]
	
	PARENTHESIS_BEGIN, // (
	PARENTHESIS_END,   // )
	
	UNKNOWN;
}

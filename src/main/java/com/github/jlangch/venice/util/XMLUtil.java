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
package com.github.jlangch.venice.util;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;


public class XMLUtil {

	public static void parse(
			final InputSource is,
			final boolean namespaceAware,
			final XMLHandler handler
	) {
		try {
			newParser(namespaceAware).parse(is, handler);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void parse(
			final InputStream is,
			final boolean namespaceAware,
			final XMLHandler handler
	) {
		try {
			newParser(namespaceAware).parse(is, handler);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void parse(
			final File f,
			final boolean namespaceAware,
			final XMLHandler handler
	) {
		try {
			newParser(namespaceAware).parse(f, handler);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void parse(
			final String uri,
			final boolean namespaceAware,
			final XMLHandler handler
	) {
		try {
			newParser(namespaceAware).parse(uri, handler);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	
	private static SAXParser newParser(final boolean namespaceAware) throws Exception {
		final SAXParserFactory f = SAXParserFactory.newInstance();
		f.setNamespaceAware(namespaceAware);
		return f.newSAXParser();
	}
}
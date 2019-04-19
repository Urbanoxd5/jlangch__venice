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
package com.github.jlangch.venice.impl;

import static com.github.jlangch.venice.impl.types.Constants.Nil;
import static com.github.jlangch.venice.impl.types.Constants.True;

import java.util.HashMap;
import java.util.Map;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncMap;
import com.github.jlangch.venice.impl.types.util.Types;


public class MetaUtil {

	public static VncVal addDefMeta(final VncVal val, final VncMap meta) {
		VncVal valMeta = val.getMeta();
		
		if (valMeta == Constants.Nil) {
			valMeta = meta;
		}
		else if (Types.isVncMap(valMeta)) {
			valMeta = ((VncMap)valMeta).assoc(meta.toVncList());
		}
		
		return val.withMeta(valMeta);
	}
	
	public static VncVal toMeta(final Token token) {
		return VncHashMap.of(
					MODULE, new VncString(toModule(token.getFile())),
					FILE, new VncString(token.getFile()),
					LINE, new VncLong(token.getLine()),
					COLUMN, new VncLong(token.getColumn()));
	}

	public static VncVal addMetaVal(final VncVal meta, final VncString key, final VncVal val) {
		if (meta == Constants.Nil) {
			return new VncHashMap().assoc(key, val);	
		}
		else if (Types.isVncMap(meta)) {
			return ((VncMap)meta).assoc(key, val);	
		}
		else {
			// not a map
			return meta;
		}
	}

	public static VncVal mergeMeta(final VncVal meta1, VncVal meta2) {		
		if (meta1 == Nil) {
			return meta2;
		}
		else if (meta2 == Nil) {
			return meta1;
		}
		else if (Types.isVncMap(meta1) && Types.isVncMap(meta2)) {
			final Map<VncVal,VncVal> m = new HashMap<>(((VncMap)meta1).getMap());
			m.putAll(((VncMap)meta2).getMap());						
			return new VncHashMap(m);
		}
		else {
			throw new VncException(String.format(
					"Failed to merge meta data on incompatible old (%s) and new (%s) meta data types", 
					Types.getType(meta1),
					Types.getType(meta2)));
		}
	}

	public static boolean isPrivate(final VncVal meta) {
		if (meta == Nil) {
			return false;
		}
		else if (meta instanceof VncHashMap) {
			return ((VncHashMap)meta).get(PRIVATE) == True;
		}
		else {
			return false;
		}
	}

	public static String getModule(final VncVal meta) {
		if (meta == Nil) {
			return null;
		}
		else if (meta instanceof VncHashMap) {
			final VncVal file = ((VncHashMap)meta).get(MODULE);
			return file == Nil ? null : ((VncString)file).getValue();
		}
		else {
			return null;
		}
	}
	
	private static String toModule(final String file) {
		return file.endsWith(".venice") ? file.substring(0, file.length() - 7) : file;
	}
	
	
	// Var definition
	public static final VncKeyword ARGLIST = new VncKeyword(":arglists"); 
	public static final VncKeyword DOC = new VncKeyword(":doc"); 
	public static final VncKeyword EXAMPLES = new VncKeyword(":examples"); 
	
	// File location
	public static final VncKeyword FILE = new VncKeyword(":file"); 
	public static final VncKeyword LINE = new VncKeyword(":line"); 
	public static final VncKeyword COLUMN = new VncKeyword(":column"); 
	
	public static final VncKeyword MODULE = new VncKeyword(":module"); 
    public static final VncKeyword PRIVATE = new VncKeyword(":private");
}

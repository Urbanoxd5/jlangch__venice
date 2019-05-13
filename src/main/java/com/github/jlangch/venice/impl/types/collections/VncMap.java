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
package com.github.jlangch.venice.impl.types.collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.jlangch.venice.impl.functions.FunctionsUtil;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.IVncFunction;
import com.github.jlangch.venice.impl.types.VncVal;


public abstract class VncMap extends VncCollection implements IVncFunction {
	
	public VncMap(VncVal meta) {
		super(meta);
	}

	public VncVal apply(final VncList args) {
		FunctionsUtil.assertArity("map", args, 1);
		
		return args.first() == Constants.Nil ? Constants.Nil : get(args.first());
	}

	
	@Override
	public abstract VncMap empty();

	public abstract VncMap withValues(Map<VncVal,VncVal> replaceVals);
	
	public abstract VncMap withValues(Map<VncVal,VncVal> replaceVals, VncVal meta);

	@Override
	public abstract VncMap withMeta(VncVal meta);

	
	public abstract Map<VncVal,VncVal> getMap();
	
	public abstract VncVal get(VncVal key);

	public VncVal get(VncVal key, VncVal defaultValue) {
		final VncVal val = get(key);
		return val == Constants.Nil ? defaultValue : val;
	}

	public abstract VncVal containsKey(VncVal key);
	
	public abstract VncList keys();
	
	public abstract List<VncMapEntry> entries();

	public abstract VncMap putAll(VncMap map);

	public abstract VncMap assoc(VncVal... mvs);

	public abstract VncMap assoc(VncSequence mvs);

	public abstract VncMap dissoc(VncVal... keys);

	public abstract VncMap dissoc(VncSequence keys);

	@Override
	public Object convertToJavaObject() {
		final Map<Object,Object> map = new HashMap<>();
		for(VncMapEntry e : entries()) {
			map.put(
				e.getKey().convertToJavaObject(), 
				e.getValue().convertToJavaObject());
		}
		return map;
	}

 
	private static final long serialVersionUID = -1848883965231344442L;
}
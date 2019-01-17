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

import static com.github.jlangch.venice.impl.types.Constants.False;
import static com.github.jlangch.venice.impl.types.Constants.True;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.util.ErrorMessage;

public class VncOrderedMap extends VncMap {

	public VncOrderedMap() {
		this(null, null);
	}

	public VncOrderedMap(final VncVal meta) {
		this(null, meta);
	}

	public VncOrderedMap(final Map<VncVal,VncVal> vals) {
		this(vals, null);
	}

	public VncOrderedMap(final Map<VncVal,VncVal> vals, final VncVal meta) {
		super(meta == null ? Constants.Nil : meta);
		value = vals == null ? new LinkedHashMap<>() : new LinkedHashMap<>(vals);
	}
	
	
	public static VncOrderedMap ofAll(final VncList lst) {
		if (lst != null && (lst.size() %2 != 0)) {
			throw new VncException(String.format(
					"ordered-map: create requires an even number of list items. %s", 
					ErrorMessage.buildErrLocation(lst)));
		}

		return new VncOrderedMap().assoc(lst);
	}
	
	public static VncOrderedMap ofAll(final VncVector vec) {
		if (vec != null && (vec.size() %2 != 0)) {
			throw new VncException(String.format(
					"ordered-map: create requires an even number of vector items. %s", 
					ErrorMessage.buildErrLocation(vec)));
		}

		return new VncOrderedMap().assoc(vec);
	}

	public static VncOrderedMap ofAll(final VncVal... mvs) {
		if (mvs != null && (mvs.length %2 != 0)) {
			throw new VncException(String.format(
					"ordered-map: create requires an even number of items. %s", 
					ErrorMessage.buildErrLocation(mvs[0])));
		}
		
		return new VncOrderedMap().assoc(mvs);
	}

	
	@Override
	public VncOrderedMap empty() {
		return new VncOrderedMap(getMeta());
	}

	@Override
	public VncOrderedMap copy() {
		// shallow copy
		return new VncOrderedMap(value, getMeta());
	}

	@Override
	public VncOrderedMap withMeta(final VncVal meta) {
		// shallow copy
		return new VncOrderedMap(value, meta);
	}
	
	@Override
	public Map<VncVal,VncVal> getMap() {
		return Collections.unmodifiableMap(value);
	}
	
	@Override
	public VncVal get(final VncVal key) {
		final VncVal val = value.get(key);
		return val == null ? Constants.Nil : val;
	}

	@Override
	public VncVal containsKey(final VncVal key) {
		return value.containsKey(key) ? True : False;
	}

	@Override
	public VncList keys() {
		return new VncList(new ArrayList<>(value.keySet()));
	}

	@Override
	public List<VncMapEntry> entries() {
		return Collections.unmodifiableList(
					value
						.entrySet()
						.stream().map(e -> new VncMapEntry(e.getKey(), e.getValue()))
						.collect(Collectors.toList()));
	}

	@Override
	public VncMap putAll(final VncMap map) {
		value.putAll(map.getMap());
		return this;
	}
	
	@Override
	public VncOrderedMap assoc(final VncVal... mvs) {
		for (int i=0; i<mvs.length; i+=2) {
			value.put(mvs[i], mvs[i+1]);
		}
		return this;
	}

	@Override
	public VncOrderedMap assoc(final VncList mvs) {
		for (int i=0; i<mvs.getList().size(); i+=2) {
			value.put(mvs.nth(i), mvs.nth(i+1));
		}
		return this;
	}

	@Override
	public VncMap dissoc(final VncVal... keys) {
		for (VncVal key : keys) {
			value.remove(key);
		}
		return this;
	}

	@Override
	public VncOrderedMap dissoc(final VncList keys) {
		for (int i=0; i<keys.getList().size(); i++) {
			value.remove(keys.nth(i));
		}
		return this;
	}
	
	@Override
	public VncList toVncList() {
		return new VncList(
						value
							.entrySet()
							.stream()
							.map(e -> VncVector.of(e.getKey(), e.getValue()))
							.collect(Collectors.toList()),
						getMeta());
	}
	
	@Override
	public VncVector toVncVector() {
		return new VncVector(
						value
							.entrySet()
							.stream()
							.map(e -> VncVector.of(e.getKey(), e.getValue()))
							.collect(Collectors.toList()),
						getMeta());
	}
	
	@Override
	public int size() {
		return value.size();
	}
	
	@Override
	public boolean isEmpty() {
		return value.isEmpty();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		VncOrderedMap other = (VncOrderedMap) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override 
	public String toString() {
		return toString(true);
	}
	
	@Override
	public String toString(final boolean print_readably) {
		final List<VncVal> list = value
									.entrySet()
									.stream()
									.map(e -> VncList.of(e.getKey(), e.getValue()).getList())
									.flatMap(l -> l.stream())
									.collect(Collectors.toList());

		return "{" + Printer.join(list, " ", print_readably) + "}";
	}
	
	public static class Builder {
		public Builder() {
		}
		
		public Builder put(final String key, final VncVal val) {
			map.put(new VncSymbol(key), val);
			return this;
		}

		public Builder put(final VncVal key, final VncVal val) {
			map.put(key, val);
			return this;
		}

		public VncOrderedMap build() {
			return new VncOrderedMap(map);
		}
		
		public Map<VncVal,VncVal> toMap() {
			return map;
		}
		
		private final LinkedHashMap<VncVal,VncVal> map = new LinkedHashMap<>();
	}
	

    private static final long serialVersionUID = -1848883965231344442L;

	private final LinkedHashMap<VncVal,VncVal> value;	
}
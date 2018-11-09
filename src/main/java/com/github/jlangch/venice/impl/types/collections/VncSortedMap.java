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
package com.github.jlangch.venice.impl.types.collections;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;


public class VncSortedMap extends VncMap {

	public VncSortedMap(final Map<VncVal,VncVal> val) {
		value = (val instanceof TreeMap) 
					? (TreeMap<VncVal,VncVal>)val
					: new TreeMap<>(val);
	}
	
	public VncSortedMap(final VncList lst) {
		value = new TreeMap<>();
		assoc(lst);
	}
	
	public VncSortedMap(final VncVal... mvs) {
		value = new TreeMap<>();
		assoc(mvs);
	}

	@Override
	public VncSortedMap empty() {
		return new VncSortedMap();
	}
	
	@Override
	public Map<VncVal,VncVal> getMap() {
		return value;
	}
	
	@Override
	public VncVal get(final VncVal key) {
		final VncVal val = value.get(key);
		return val == null ? Constants.Nil : val;
	}

	@SuppressWarnings("unchecked")
	@Override
	public VncSortedMap copy() {
		final VncSortedMap v = new VncSortedMap((TreeMap<VncVal,VncVal>)value.clone());
		v.setMeta(getMeta());
		return v;
	}

	@Override
	public Set<Map.Entry<VncVal, VncVal>> entries() {
		return value.entrySet();
	}
	
	@Override
	public VncSortedMap assoc(final VncVal... mvs) {
		for (int i=0; i<mvs.length; i+=2) {
			value.put(mvs[i], mvs[i+1]);
		}
		return this;
	}

	@Override
	public VncSortedMap assoc(final VncList mvs) {
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
	public VncSortedMap dissoc(final VncList keys) {
		for (int i=0; i<keys.getList().size(); i++) {
			value.remove(keys.nth(i));
		}
		return this;
	}
	
	@Override
	public VncList toVncList() {
		return new VncList(value
							.entrySet()
							.stream()
							.map(e -> new VncVector(e.getKey(), e.getValue()))
							.collect(Collectors.toList()));
	}
	
	@Override
	public VncVector toVncVector() {
		return new VncVector(value
							.entrySet()
							.stream()
							.map(e -> new VncVector(e.getKey(), e.getValue()))
							.collect(Collectors.toList()));
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
		VncSortedMap other = (VncSortedMap) obj;
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
									.map(e -> new VncList(e.getKey(), e.getValue()).getList())
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

		public VncSortedMap build() {
			return new VncSortedMap(map);
		}
		
		public Map<VncVal,VncVal> toMap() {
			return map;
		}
		
		private final TreeMap<VncVal,VncVal> map = new TreeMap<>();
	}
	

    private static final long serialVersionUID = -1848883965231344442L;

	private final TreeMap<VncVal,VncVal> value;	
}
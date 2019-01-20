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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.Types;
import com.github.jlangch.venice.impl.types.VncSymbol;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.util.ErrorMessage;



public class VncOrderedMap extends VncMap {

	public VncOrderedMap() {
		this((io.vavr.collection.LinkedHashMap<VncVal,VncVal>)null, null);
	}

	public VncOrderedMap(final VncVal meta) {
		this((io.vavr.collection.LinkedHashMap<VncVal,VncVal>)null, meta);
	}

	public VncOrderedMap(final io.vavr.collection.Map<VncVal,VncVal> val) {
		this(val, null);
	}

	public VncOrderedMap(final Map<VncVal,VncVal> vals) {
		this(vals, null);
	}

	public VncOrderedMap(final Map<VncVal,VncVal> vals, final VncVal meta) {
		this(vals == null ? null : io.vavr.collection.LinkedHashMap.ofAll(vals), meta);
	}

	public VncOrderedMap(final io.vavr.collection.Map<VncVal,VncVal> val, final VncVal meta) {
		super(meta == null ? Constants.Nil : meta);
		if (val == null) {
			value = io.vavr.collection.LinkedHashMap.empty();
		}
		else if (val instanceof io.vavr.collection.TreeMap) {
			value = (io.vavr.collection.LinkedHashMap<VncVal,VncVal>)val;
		}
		else {
			value = io.vavr.collection.LinkedHashMap.ofEntries(val);
		}
	}
	
	
	public static VncOrderedMap ofAll(final VncSequence lst) {
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
	
	public static VncOrderedMap of(final VncVal... mvs) {
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
	public VncOrderedMap withValues(final Map<VncVal,VncVal> replaceVals) {
		return new VncOrderedMap(replaceVals, getMeta());
	}
	
	@Override
	public VncOrderedMap withValues(
			final Map<VncVal,VncVal> replaceVals, 
			final VncVal meta
	) {
		return new VncOrderedMap(replaceVals, meta);
	}


	@Override
	public VncOrderedMap withMeta(final VncVal meta) {
		return new VncOrderedMap(value, meta);
	}
	
	@Override
	public Map<VncVal,VncVal> getMap() {
		return Collections.unmodifiableMap(value.toJavaMap());
	}
	
	@Override
	public VncVal get(final VncVal key) {
		return value.get(key).getOrElse(Constants.Nil);
	}

	@Override
	public VncVal containsKey(final VncVal key) {
		return value.containsKey(key) ? True : False;
	}

	@Override
	public VncList keys() {
		return new VncList(new ArrayList<>(value.keySet().toJavaList()));
	}

	@Override
	public List<VncMapEntry> entries() {
		return Collections.unmodifiableList(
					value
						.map(e -> new VncMapEntry(e._1, e._2))
						.collect(Collectors.toList()));
	}

	@Override
	public VncOrderedMap putAll(final VncMap map) {
		return new VncOrderedMap(
						value.merge(io.vavr.collection.LinkedHashMap.ofAll(map.getMap())),
						getMeta());
	}
	
	@Override
	public VncOrderedMap assoc(final VncVal... mvs) {
		if (mvs.length %2 != 0) {
			throw new VncException(String.format(
					"ordered-map: assoc requires an even number of items. %s", 
					ErrorMessage.buildErrLocation(mvs[0])));
		}
		
		io.vavr.collection.LinkedHashMap<VncVal,VncVal> tmp = value;
		for (int i=0; i<mvs.length; i+=2) {
			tmp = tmp.put(mvs[i], mvs[i+1]);
		}
		return new VncOrderedMap(tmp, getMeta());
	}

	@Override
	public VncOrderedMap assoc(final VncSequence mvs) {
		if (mvs.size() %2 != 0) {
			throw new VncException(String.format(
					"ordered-map: assoc requires an even number of items. %s", 
					ErrorMessage.buildErrLocation(mvs)));
		}	

		io.vavr.collection.LinkedHashMap<VncVal,VncVal> tmp = value;
		for (int i=0; i<mvs.getList().size(); i+=2) {
			tmp = tmp.put(mvs.nth(i), mvs.nth(i+1));
		}
		return new VncOrderedMap(tmp, getMeta());
	}

	@Override
	public VncOrderedMap dissoc(final VncVal... keys) {
		return new VncOrderedMap(
					value.removeAll(Arrays.asList(keys)),
					getMeta());
	}

	@Override
	public VncOrderedMap dissoc(final VncSequence keys) {
		return new VncOrderedMap(
					value.removeAll(keys.getList()),
					getMeta());
	}
	
	@Override
	public VncList toVncList() {
		return new VncList(
						value.map(e -> VncVector.of(e._1, e._2))
							 .collect(Collectors.toList()),
						getMeta());
	}
	
	@Override
	public VncVector toVncVector() {
		return new VncVector(
						value.map(e -> VncVector.of(e._1, e._2))
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
	
	@Override public int typeRank() {
		return 209;
	}
	
	@Override
	public int compareTo(final VncVal o) {
		if (o == Constants.Nil) {
			return 1;
		}
		else if (Types.isVncOrderedMap(o)) {
			final Integer sizeThis = size();
			final Integer sizeOther = size();
			int c = sizeThis.compareTo(sizeOther);
			if (c != 0) {
				return c;
			}
			else {
				if (equals(o)) {
					return 0;
				}
			}
		}

		return super.compareTo(o);
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
									.map(e -> VncList.of(e._1, e._2).getList())
									.collect(Collectors.toList())
									.stream()
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
		
		private LinkedHashMap<VncVal,VncVal> map = new LinkedHashMap<>();
	}
	

    private static final long serialVersionUID = -1848883965231344442L;

	private final io.vavr.collection.LinkedHashMap<VncVal,VncVal> value;	
}
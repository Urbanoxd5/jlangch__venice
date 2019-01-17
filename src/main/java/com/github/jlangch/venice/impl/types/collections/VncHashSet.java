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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.Constants;
import com.github.jlangch.venice.impl.types.VncVal;


public class VncHashSet extends VncSet {

	public VncHashSet() {
		this(null, null);
	}

	public VncHashSet(final VncVal meta) {
		this(null, meta);
	}

	public VncHashSet(final io.vavr.collection.HashSet<VncVal> val) {
		this(val, null);
	}

	public VncHashSet(final io.vavr.collection.HashSet<VncVal> val, final VncVal meta) {
		super(meta == null ? Constants.Nil : meta);
		value = val == null ? io.vavr.collection.HashSet.of() : val;
	}

	
	public static VncHashSet ofAll(final Set<VncVal> val) {
		return new VncHashSet(io.vavr.collection.HashSet.ofAll(val));
	}
	
	public static VncHashSet ofAll(final VncSequence val) {
		return new VncHashSet(io.vavr.collection.HashSet.ofAll(val.getList()));
	}
	
	public static VncHashSet of(final VncVal... mvs) {
		return new VncHashSet(io.vavr.collection.HashSet.of(mvs));
	}
	
	
	@Override
	public VncHashSet empty() {
		return new VncHashSet(getMeta());
	}
	
	@Override
	public VncHashSet copy() {
		return new VncHashSet(value, getMeta());
	}

	@Override
	public VncHashSet withMeta(final VncVal meta) {
		return new VncHashSet(value, meta);
	}
	
	@Override
	public VncHashSet add(final VncVal val) {
		return new VncHashSet(value.add(val), getMeta());
	}
	
	@Override
	public VncHashSet addAll(final VncSet val) {
		return new VncHashSet(value.addAll(val.getSet()), getMeta());
	}
	
	@Override
	public VncHashSet addAll(final VncSequence val) {
		return new VncHashSet(value.addAll(val.getList()), getMeta());
	}

	@Override
	public VncHashSet remove(final VncVal val) {
		return new VncHashSet(value.remove(val), getMeta());
	}

	@Override
	public VncHashSet removeAll(final VncSet val) {
		return new VncHashSet(value.removeAll(val.getSet()), getMeta());
	}

	@Override
	public VncHashSet removeAll(final VncSequence val) {
		return new VncHashSet(value.removeAll(val.getList()), getMeta());
	}
	
	@Override
	public boolean contains(final VncVal val) {
		return value.contains(val);
	}

	@Override
	public Set<VncVal> getSet() { 
		return Collections.unmodifiableSet(value.toJavaSet()); 
	}

	@Override
	public List<VncVal> getList() { 
		return Collections.unmodifiableList(value.toJavaList()); 
	}
	
	@Override
	public VncList toVncList() {
		return new VncList(getList(), getMeta());
	}

	@Override
	public VncVector toVncVector() {
		return new VncVector(getList(), getMeta());
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
		VncHashSet other = (VncHashSet) obj;
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
		return "#{" + Printer.join(getList(), " ", print_readably) + "}";
	}

	
    private static final long serialVersionUID = -1848883965231344442L;

	private final io.vavr.collection.HashSet<VncVal> value;	
}
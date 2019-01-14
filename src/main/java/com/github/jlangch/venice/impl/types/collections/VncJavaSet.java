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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.javainterop.JavaInteropUtil;
import com.github.jlangch.venice.impl.types.IVncJavaObject;
import com.github.jlangch.venice.impl.types.Types;
import com.github.jlangch.venice.impl.types.VncVal;


public class VncJavaSet extends VncSet implements IVncJavaObject {

	public VncJavaSet() {
	}

	public VncJavaSet(final Set<Object> val) {
		val.forEach(v -> {
			if (v instanceof VncVal) {
				add((VncVal)v);
			}
			else {
				value.add(v);
			}
		});
	}
	
	
	@Override
	public Object getDelegate() {
		return value;
	}

	public VncJavaSet empty() {
		return copyMetaTo(new VncJavaSet());
	}
	
	public VncJavaSet add(final VncVal val) {
		value.add(JavaInteropUtil.convertToJavaObject(val));
		return this;
	}

	public VncJavaSet addAll(final VncSet val) {
		if (Types.isVncJavaSet(val)) {
			value.addAll(((VncJavaSet)val).value);
		}
		else {
			val.getList().forEach(v -> add(v));
		}
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public VncJavaSet addAll(final VncSequence val) {
		if (Types.isVncJavaList(val)) {
			value.addAll((List<Object>)((VncJavaList)val).getDelegate());
		}
		else {
			val.getList().forEach(v -> add(v));
		}
		return this;
	}

	public VncJavaSet remove(final VncVal val) {
		value.remove(JavaInteropUtil.convertToJavaObject(val));
		return this;
	}

	public VncJavaSet removeAll(final VncSet val) {
		if (Types.isVncJavaSet(val)) {
			value.removeAll(((VncJavaSet)val).value);
		}
		else {
			val.getList().forEach(v -> remove(v));
		}
		return this;
	}

	@SuppressWarnings("unchecked")
	public VncJavaSet removeAll(final VncSequence val) {
		if (Types.isVncJavaList(val)) {
			value.removeAll((List<Object>)((VncJavaList)val).getDelegate());
		}
		else {
			val.getList().forEach(v -> remove(v));
		}
		return this;
	}

	public boolean contains(final VncVal val) {
		return value.contains(JavaInteropUtil.convertToVncVal(val));
	}
	
	@SuppressWarnings("unchecked")
	public VncJavaSet copy() {
		return copyMetaTo(new VncJavaSet((Set<Object>)value.clone()));
	}


	public Set<VncVal> getSet() { 
		return Collections.unmodifiableSet(getVncValueSet()); 
	}

	public List<VncVal> getList() { 
		return Collections.unmodifiableList(getVncValueList()); 
	}

	public VncList toList() {
		return new VncList(getVncValueList());
	}

	public VncHashSet toVncSet() {
		return new VncHashSet(toVncList());
	}

	public VncList toVncList() {
		return new VncList(toList());
	}

	public VncVector toVncVector() {
		return new VncVector(toList());
	}

	public int size() {
		return value.size();
	}
	
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
		VncJavaSet other = (VncJavaSet) obj;
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
		return "#{" + Printer.join(getVncValueList(), " ", print_readably) + "}";
	}

	private List<VncVal> getVncValueList() {
		return value
				.stream()
				.map(v -> JavaInteropUtil.convertToVncVal(v))
				.collect(Collectors.toList());
	}

	private Set<VncVal> getVncValueSet() {
		return value
				.stream()
				.map(v -> JavaInteropUtil.convertToVncVal(v))
				.collect(Collectors.toSet());
	}

	
    private static final long serialVersionUID = -1848883965231344442L;

	private final HashSet<Object> value = new HashSet<>();	
}
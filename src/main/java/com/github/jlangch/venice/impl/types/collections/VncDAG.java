/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2021 Venice
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

import java.util.stream.Collectors;

import com.github.jlangch.venice.impl.Printer;
import com.github.jlangch.venice.impl.types.TypeRank;
import com.github.jlangch.venice.impl.types.VncKeyword;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.util.MetaUtil;
import com.github.jlangch.venice.impl.util.dag.DAG;


public class VncDAG extends VncCollection {
	
	public VncDAG(final VncVal meta) {
		super(meta);
		dag = new DAG<>();
	}

	private VncDAG(final DAG<VncVal> dag, final VncVal meta) {
		super(meta);
		this.dag = dag;
	}

	
	@Override
	public VncDAG withMeta(final VncVal meta) {
		return new VncDAG(dag, meta);
	}
	
	@Override
	public VncKeyword getType() {
		return new VncKeyword(
						TYPE, 
						MetaUtil.typeMeta(
							new VncKeyword(VncVal.TYPE)));
	}
	
	
	@Override
	public VncDAG emptyWithMeta() {
		return new VncDAG(getMeta());
	}

	public VncDAG addNode(final VncVal node) {
		dag.addNode(node);
		return this;
	}

	public VncDAG addEdge(final VncVal from, final VncVal to) {
		dag.addEdge(from, to);
		return this;
	}
	
	public VncVector topologicalSort() {
		return VncVector.ofColl(dag.topologicalSort());
	}
		
	@Override
	public VncList toVncList() {
		return VncList.ofColl(dag.getValues());
	}
	
	@Override
	public VncVector toVncVector() {
		return VncVector.ofColl(dag.getValues());
	}

	@Override
	public int size() {
		return dag.size();
	}
	
	@Override
	public boolean isEmpty() {
		return dag.isEmpty();
	}
	
	
	@Override 
	public TypeRank typeRank() {
		return TypeRank.DAG;
	}

	@Override
	public Object convertToJavaObject() {
		return toVncVector()
				.stream()
				.map(v -> v.convertToJavaObject())
				.collect(Collectors.toList());
	}

	@Override
	public int compareTo(final VncVal o) {
		return dag == ((VncDAG)o).dag ? 0 : -1; // limited compare!
	}

	@Override
	public int hashCode() {
		return dag.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		VncDAG other = (VncDAG) obj;
		return dag.equals(other.dag);
	}

	@Override 
	public String toString() {
		return "(" + Printer.join(toVncVector(), " ", true) + ")";
	}
	
	public String toString(final boolean print_readably) {
		return "(" + Printer.join(toVncVector(), " ", print_readably) + ")";
	}
	
	
	public static final String TYPE = ":core/dag";

    private static final long serialVersionUID = -1848883965231344442L;
	
    private final DAG<VncVal> dag;
}
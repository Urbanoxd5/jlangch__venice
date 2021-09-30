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
package com.github.jlangch.venice.impl.util.dag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;


public class DAG_topologicalSort_Test {

	@Test
	public void test_topologicalSort_1() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B"); 
		dag.update();
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("A B", String.join(" ", sorted));
	}

	@Test
	public void test_topologicalSort_2() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");     //     A
		dag.addEdge("B", "C");     //     |
		dag.update();              //     B
		                           //     |
		                           //     C
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("A B C", String.join(" ", sorted));
	}

	@Test
	public void test_topologicalSort_3() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //       A
		dag.addEdge("B", "D");      //      / \
		dag.addEdge("A", "C");      //     B   C
		dag.addEdge("A", "D");      //      \ /
		dag.update();               //       D
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("A C B D", String.join(" ", sorted));
	}

	@Test
	public void test_topologicalSort_4() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //     A   C
		dag.addEdge("B", "E");      //     |   |
		dag.addEdge("C", "D");      //     B   D
		dag.addEdge("D", "E");      //      \ /
		dag.update();               //       E
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("C D A B E", String.join(" ", sorted));
	}

	@Test
	public void test_topologicalSort_5() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //     A  C
		dag.addEdge("B", "E");      //     | /
		dag.addEdge("C", "B");      //     B   D
		dag.addEdge("D", "E");      //      \ /
		dag.update();               //       E
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("D C A B E", String.join(" ", sorted));
	}
	
	@Test
	public void test_topologicalSort_6a() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //	   A  E
		dag.addEdge("B", "C");      //	   |  |
		dag.addEdge("C", "D");      //     B  F
		dag.addEdge("E", "F");      //     | / \
		dag.addEdge("F", "C");      //     C   G
		dag.addEdge("F", "G");      //      \ /
		dag.addEdge("G", "D");      //       D
		dag.update();
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("E F G A B C D", String.join(" ", sorted));
	}
	
	@Test
	public void test_topologicalSort_6b() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("B", "A");      //       D
		dag.addEdge("C", "B");      //      / \
		dag.addEdge("D", "C");      //     C   G
		dag.addEdge("F", "E");      //     | \ /
		dag.addEdge("C", "F");      //     B  F
		dag.addEdge("G", "F");      //	   |  |
		dag.addEdge("D", "G");      //	   A  E
		dag.update();
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("D G C F E B A", String.join(" ", sorted));
	}
	
	@Test
	public void test_topologicalSort_7a() {
		final DAG<String> dag = new DAG<>();
		dag.addEdge("A", "B");      //	     A
		dag.addEdge("A", "C");      //	    / \ 
		dag.addEdge("B", "D");      //     B   C
		dag.addEdge("C", "D");      //      \ /
		dag.addEdge("D", "E");      //       D 
		dag.addEdge("D", "F");      //      / \
		dag.update();               //     E   F
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("A C B D F E", String.join(" ", sorted));
	}
	
	@Test
	public void test_topologicalSort_7b() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("B", "A");      //     E   F
		dag.addEdge("C", "A");      //      \ /
		dag.addEdge("D", "B");      //       D 
		dag.addEdge("D", "C");      //      / \
		dag.addEdge("E", "D");      //     B   C 
		dag.addEdge("F", "D");      //      \ /
		dag.update();               //       A
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("F E D C B A", String.join(" ", sorted));
	}

	
	@Test
	public void test_topologicalSort_8() {
		final DAG<String> dag = new DAG<>();
		dag.addEdge("A", "B");      //	     A       Z
		dag.addEdge("A", "C");      //	    / \ 
		dag.addEdge("B", "D");      //     B   C
		dag.addEdge("C", "D");      //      \ /
		dag.addEdge("D", "E");      //       D 
		dag.addEdge("D", "F");      //      / \
		dag.addNode("Z");           //     E   F
		dag.update();
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("Z A C B D F E", String.join(" ", sorted));
	}


	@Test
	public void test_topologicalSort_9() {
		final DAG<String> dag = new DAG<>();
		
		dag.addNode("A"); 
		dag.addNode("B"); 
		dag.update();
		
		final List<String> sorted = dag.topologicalSort();

		assertEquals("B A", String.join(" ", sorted));
	}

	@Test
	public void test_comparator_1() {
		final DAG<String> dag = new DAG<>();
		
		dag.addEdge("A", "B");      //	   A  E
		dag.addEdge("B", "C");      //	   |  |
		dag.addEdge("C", "D");      //     B  F
		dag.addEdge("E", "F");      //     | / \
		dag.addEdge("F", "C");      //     C   G
		dag.addEdge("F", "G");      //      \ /
		dag.addEdge("G", "D");      //       D
		dag.update();
		
		final List<String> sorted = Arrays.asList("D", "F", "A", "Z")
										  .stream()
										  .sorted(dag.comparator())
										  .collect(Collectors.toList());

		assertEquals("F A D Z", String.join(" ", sorted));
	}
}

/*   __    __         _
 *   \ \  / /__ _ __ (_) ___ ___ 
 *    \ \/ / _ \ '_ \| |/ __/ _ \
 *     \  /  __/ | | | | (_|  __/
 *      \/ \___|_| |_|_|\___\___|
 *
 *
 * Copyright 2017-2020 Venice
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
package com.github.jlangch.venice;

import java.util.concurrent.TimeUnit;

import io.vavr.collection.Vector;

import org.openjdk.jmh.annotations.*;


@Warmup(iterations=3, time=3, timeUnit=TimeUnit.SECONDS)
@Measurement(iterations=3, time=10, timeUnit=TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode (Mode.AverageTime)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
@State (Scope.Benchmark)
@Threads (1)
public class VavrVectorBenchmark {
	
	public VavrVectorBenchmark() {
	}
	
	
	@Benchmark
	public void prepend() {
		vector.prepend(0L);
	}
	
	@Benchmark
	public void append() {
		vector.append(0L);
	}

	@Benchmark
	public void first() {
		vector.get(0);
 	}
	
	@Benchmark
	public void last() {
		vector.last();
	}

	@Benchmark
	public void rest() {
		vector.tail();
	}
	
	@Benchmark
	public void butlast() {
		vector.slice(0, vector.length()-1);
	}
	
	@Benchmark
	public void drop_1() {
		vector.drop(1);
	}


	private Vector<Long> create(final int len) {
		Vector<Long> v = Vector.empty();
		
		for(int ii=0; ii<len; ii++) {
			v = v.append(0L);
		}
		
		return v;
	}
	
	
	private final Vector<Long> vector = create(1000);
}

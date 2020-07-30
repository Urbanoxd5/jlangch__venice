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
package com.github.jlangch.venice.bench;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import io.vavr.collection.Vector;


@Warmup(iterations=3, time=3, timeUnit=TimeUnit.SECONDS)
@Measurement(iterations=3, time=10, timeUnit=TimeUnit.SECONDS)
@Fork(1)
@BenchmarkMode (Mode.AverageTime)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
@State (Scope.Benchmark)
@Threads (1)
public class IteratorBenchmark {
	
	public IteratorBenchmark() {
	}
	
	@Benchmark
	public Object sum_vavr_tail() {
		Vector<Long> vec = vector;
		long sum = 0;
		while(!vec.isEmpty()) {
			sum += vec.head();
			vec = vec.drop(1);
		}
		return sum;
	}
	
	@Benchmark
	public Object sum_vavr_iterator() {
		final Iterator<Long> iter = vector.iterator();
		
		long sum = 0;
		while(iter.hasNext()) {
			sum += iter.next();
		}
		return sum;
	}

	@Benchmark
	public Object sum_iterator_java_for_loop() {
		
		long sum = 0;
		for(Long v : vector.asJava()) {
			sum += v;
		}
		return sum;
	}
	

	private final Vector<Long> vector = Vector.range(1L, 100L);
}

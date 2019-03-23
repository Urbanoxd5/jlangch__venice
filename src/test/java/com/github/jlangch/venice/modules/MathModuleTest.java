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
package com.github.jlangch.venice.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import com.github.jlangch.venice.Venice;


public class MathModuleTest {

	@Test
	public void test_create() {
		final Venice venice = new Venice();

		final String script =
				"(do                                       " +
				"   (load-module :math)                    " +
				"                                          " +
				"   (math/bigint \"12345678901234567890\") " + 
				") ";

		assertEquals("12345678901234567890", ((BigInteger)venice.eval(script)).toString());
	}
	
	@Test
	public void test_class() {
		final Venice venice = new Venice();

		final String script =
				"(do                                       " +
				"   (load-module :math)                    " +
				"                                          " +
				"   (class (math/bigint \"100\"))          " + 
				") ";

		assertEquals("java.math.BigInteger", venice.eval(script));
	}

	@Test
	public void test_add() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-add                            " + 
				"        (math/bigint \"100\")                  " + 
				"        (math/bigint \"200\"))                 " + 
				") ";

		assertEquals("300", ((BigInteger)venice.eval(script)).toString());
	}

	@Test
	public void test_sub() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-sub                            " + 
				"        (math/bigint \"100\")                  " + 
				"        (math/bigint \"200\"))                 " + 
				") ";

		assertEquals("-100", ((BigInteger)venice.eval(script)).toString());
	}

	@Test
	public void test_mul() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-mul                            " + 
				"        (math/bigint \"100\")                  " + 
				"        (math/bigint \"200\"))                 " + 
				") ";

		assertEquals("20000", ((BigInteger)venice.eval(script)).toString());
	}

	@Test
	public void test_div() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-div                            " + 
				"        (math/bigint \"100000\")               " + 
				"        (math/bigint \"200\"))                 " + 
				") ";

		assertEquals("500", ((BigInteger)venice.eval(script)).toString());
	}

	@Test
	public void test_abs() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-abs (math/bigint \"-100000\")) " + 
				") ";

		assertEquals("100000", ((BigInteger)venice.eval(script)).toString());
	}

	@Test
	public void test_neg() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-neg (math/bigint \"100000\"))  " + 
				") ";

		assertEquals("-100000", ((BigInteger)venice.eval(script)).toString());
	}

	@Test
	public void test_sig() {
		final Venice venice = new Venice();

		final String script =
				"(do                                            " +
				"   (load-module :math)                         " +
				"                                               " +
				"   (math/bigint-sig (math/bigint \"-100000\")) " + 
				") ";

		assertEquals(-1L, venice.eval(script));
	}
	
}

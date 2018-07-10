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
package org.venice.sandbox;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.venice.javainterop.WhiteList;


public class WhiteListTest {

	@Test
	public void classesTest() {
		final WhiteList wl = WhiteList.create(
								Arrays.asList(
									"java.lang.Math",
									"java.math.BigDecimal"
								));
		
		assertTrue(wl.isWhiteListed(java.lang.Math.class));
		assertTrue(wl.isWhiteListed(java.math.BigDecimal.class));
		assertFalse(wl.isWhiteListed(java.math.BigInteger.class));
	}

	@Test
	public void classesWildcardTest() {
		final WhiteList wl = WhiteList.create(
								Arrays.asList(
									"java.lang.Math",
									"java.lang.String:*",
									"java.math.*"
								));
		
		assertTrue(wl.isWhiteListed(java.lang.Math.class));
		assertTrue(wl.isWhiteListed(java.lang.String.class));
		assertTrue(wl.isWhiteListed(java.math.BigDecimal.class));
		assertTrue(wl.isWhiteListed(java.math.BigInteger.class));
	}

	@Test
	public void methodsTest() {
		final WhiteList wl = WhiteList.create(
								Arrays.asList(
									"java.lang.Math:min",
									"java.lang.Math:max"
								));
		
		assertTrue(wl.isWhiteListed(java.lang.Math.class));
		assertTrue(wl.isWhiteListed(java.lang.Math.class, "min"));
		assertTrue(wl.isWhiteListed(java.lang.Math.class, "max"));
		assertFalse(wl.isWhiteListed(java.lang.Math.class, "abs"));
	}

	@Test
	public void methodsWildcardTest() {
		final WhiteList wl = WhiteList.create(
								Arrays.asList(
									"java.lang.Math:*"
								));

		assertTrue(wl.isWhiteListed(java.lang.Math.class));
		assertTrue(wl.isWhiteListed(java.lang.Math.class, "min"));
		assertTrue(wl.isWhiteListed(java.lang.Math.class, "max"));
		assertTrue(wl.isWhiteListed(java.lang.Math.class, "abs"));
	}
}

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
package org.venice;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.venice.support.AuditEvent;
import org.venice.support.AuditEventType;


public class VeniceTest {

	@Test
	public void evalWithIntegerAndLong() {
		final Venice venice = new Venice();
		
		assertEquals(Long.valueOf(7), venice.eval("(+ 1 x)", Parameters.of("x", 6)));
		assertEquals(Long.valueOf(7), venice.eval("(+ 1 x)", Parameters.of("x", 6L)));
	}

	@Test
	public void evalEnv() {
		final AuditEvent event = new AuditEvent(
										"su",
										2000L,
										AuditEventType.ALERT,
										"superuser",
										"webapp.started",
										"text");

		final Venice venice = new Venice();
		
		final Map<String,Object> symbols = Parameters.of("event", event);

		assertEquals("webapp.started", venice.eval("(get event :eventName)", symbols));
		assertEquals("superuser", venice.eval("(get event :eventKey)", symbols));
		assertEquals("ALERT", venice.eval("(get event :eventType)", symbols));
		assertEquals(2000L, venice.eval("(get event :elapsedTimeMillis)", symbols));
	}

	@Test
	public void evalWithObject() {
		final AuditEvent event1 = new AuditEvent(
										"su",
										2000L,
										AuditEventType.ALERT,
										"superuser",
										"webapp.started",
										"text");
		
		final AuditEvent event2 = new AuditEvent(
										"jd",
										2000L,
										AuditEventType.INFO,
										"john.doe",
										"login",
										"text");

		final Venice venice = new Venice();
		

		String script = 
				"(or (match (get event :eventName) \"webapp[.](started|stopped)\") " +
				"    (== (get event :eventKey) \"superuser\") " +
				"    (== (get event :eventType) \"ALERT\") " +
				")";       

		assertEquals(Boolean.TRUE, (Boolean)venice.eval(script, Parameters.of("event", event1)));

		assertEquals(Boolean.FALSE, (Boolean)venice.eval(script, Parameters.of("event", event2)));
	}
}

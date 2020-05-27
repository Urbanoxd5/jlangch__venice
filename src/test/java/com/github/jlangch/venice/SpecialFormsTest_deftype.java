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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


public class SpecialFormsTest_deftype {
	
	@Test
	public void test_deftype() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :complex [real :long, imaginary :long])  \n" +
				"  (def x (.: :complex 100 200))                     \n" +
				"  (pr-str x))                                         ";

		assertEquals("#:user/complex{:real 100 :imaginary 200}", venice.eval(script));					
	}

	@Test
	public void test_deftype_type_builder() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :complex [real :long, imaginary :long])  \n" +
				"  (def x (complex. 100 200))                        \n" +
				"  (pr-str x))                                         ";

		assertEquals("#:user/complex{:real 100 :imaginary 200}", venice.eval(script));					
	}

	@Test
	public void test_deftype_type_checker() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :complex [real :long, imaginary :long])  \n" +
				"  (complex? (.: :complex 100 200)))                   ";

		assertTrue((Boolean)venice.eval(script));					
	}

	@Test
	public void test_deftype_access_fields() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :complex [real :long, imaginary :long])  \n" +
				"  (def x (.: :complex 100 200))                     \n" +
				"  (pr-str [(:real x) (:imaginary x)]))                ";

		assertEquals("[100 200]", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_nested() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                      \n" +
				"  (deftype :complex [real :long, imaginary :long])       \n" +
				"  (deftype :test [a :user/complex, b :long])             \n" +
				"  (def x (.: :test (.: :user/complex 100 200) 400))      \n" +
				"  (pr-str x))                                              ";

		assertEquals("#:user/test{:a #:user/complex{:real 100 :imaginary 200} :b 400}", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_nested_access_fields() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                           \n" +
				"  (deftype :complex [real :long, imaginary :long])            \n" +
				"  (deftype :test [a :user/complex, b :long])                  \n" +
				"  (def x (.: :test (.: :complex 100 200) 400))                \n" +
				"  (pr-str [[(-> x :a :real)  (-> x :a :imaginary)] (:b x)]))    ";
	
		assertEquals("[[100 200] 400]", venice.eval(script));					
	}

	
	@Test
	public void test_deftype_nested_complex() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                              \n" +
				"  (ns foo)                                                       \n" +
				"                                                                 \n" +
				"  (deftype-of :check-number :integer)                            \n" +
				"                                                                 \n" +
				"  (deftype-of :card-number :string)                              \n" +
				"                                                                 \n" +
				"  (deftype-or :card-type :mastercard :visa)                      \n" +
				"                                                                 \n" +
				"  (deftype :credit-card [type    :card-type                      \n" +
				"                         number  :card-number])                  \n" +
				"                                                                 \n" +
				"  (deftype :check [number :check-number])                        \n" +
				"                                                                 \n" +
				"  (deftype-of :payment-amount :decimal)                          \n" +
				"                                                                 \n" +
				"  (deftype-or :payment-currency :CHF :EUR)                       \n" +
				"                                                                 \n" +
				"  (deftype-or :payment-method :cash                              \n" +
				"                              :check                             \n" +
				"                              :credit-card)                      \n" +
				"                                                                 \n" +
				"  (deftype :payment [amount    :payment-amount                   \n" +
				"                     currency  :payment-currency                 \n" +
				"                     method    :payment-method ])                \n" +
				"                                                                 \n" +
				"  ; build a credit card payment                                  \n" +
				"  (def payment                                                   \n" +
				"        (.: :payment                                             \n" +
				"               (.: :payment-amount 2000.0M)                      \n" +
				"               (.: :payment-currency :CHF)                       \n" +
				"               (.: :payment-method                               \n" +
				"                      (.: :credit-card                           \n" +
				"                             (.: :card-type :mastercard)         \n" +
				"                             (.: :card-number \"123-4567\")))))  \n" +
				"                                                                 \n" +
				"  (pr-str [ (:amount payment)                                    \n" +
				"            (:currency payment)                                  \n" +
				"            (-> payment :method :number)                         \n" +
				"            (-> payment :method :type) ]))                         ";

		assertEquals("[2000.0M \"CHF\" \"123-4567\" \"mastercard\"]", venice.eval(script));					
	}

	@Test
	public void test_deftype_any() {
		final Venice venice = new Venice();

		final String script =
				"(do                                             \n" +
				"  (deftype :named [name :string, value :any])   \n" +
				"  (def x (.: :named \"a\" 200))                 \n" +
				"  (def y (.: :named \"b\" [1 2]))               \n" +
				"  (pr-str [x y]))                                 ";

		assertEquals(
				"[#:user/named{:name \"a\" :value 200} "
					+ "#:user/named{:name \"b\" :value [1 2]}]", 
				venice.eval(script));					
	}
	
	@Test
	public void test_deftype_with_list() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :named [name :string, values :list])     \n" +
				"  (def x (.: :named \"a\" '(1 2)))                  \n" +
				"  (pr-str x))                                         ";

		assertEquals("#:user/named{:name \"a\" :values (1 2)}", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_with_vector() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :named [name :string, values :vector])   \n" +
				"  (def x (.: :named \"a\" [1 2]))                   \n" +
				"  (pr-str x))                                         ";

		assertEquals("#:user/named{:name \"a\" :values [1 2]}", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_with_sequence() {
		final Venice venice = new Venice();

		final String script_list =
				"(do                                                     \n" +
				"  (deftype :named [name :string, values :sequence])     \n" +
				"  (def x (.: :named \"a\" '(1 2)))                      \n" +
				"  (pr-str x))                                             ";

		assertEquals("#:user/named{:name \"a\" :values (1 2)}", venice.eval(script_list));					

		final String script_vector =
				"(do                                                     \n" +
				"  (deftype :named [name :string, values :sequence])     \n" +
				"  (def x (.: :named \"a\" [1 2]))                       \n" +
				"  (pr-str x))                                             ";

		assertEquals("#:user/named{:name \"a\" :values [1 2]}", venice.eval(script_vector));					
	}
	
	@Test
	public void test_deftype_with_hashmap() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                 \n" +
				"  (deftype :named [name :string, values :hash-map]) \n" +
				"  (def x (.: :named \"a\" {:x 1 :y 2}))             \n" +
				"  (pr-str x))                                         ";

		assertEquals("#:user/named{:name \"a\" :values {:x 1 :y 2}}", venice.eval(script));					
	}

	@Test
	public void test_deftype_type() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                      \n" +
				"  (deftype :complex [real :long, imaginary :long])       \n" +
				"  (def x (.: :complex 100 200))                          \n" +
				"  (pr-str (type x)))                                       ";

		assertEquals(":user/complex", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_supertype() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                      \n" +
				"  (deftype :complex [real :long, imaginary :long])       \n" +
				"  (def x (.: :complex 100 200))                           \n" +
				"  (pr-str (supertype x)))                                  ";

		assertEquals(":core/custom-type", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_validation_OK() {
		final Venice venice = new Venice();

		final String script =
				"(do                                                      \n" +
				"  (deftype :complex                                      \n" +
				"           [real :long, imaginary :long]                 \n" +
				"           (fn [t] (assert (pos? (:real t))              \n" +
				"                   \"real must be positive\")))          \n" +
				"  (def x (.: :complex 100 200))                          \n" +
				"  (pr-str [(:real x) (:imaginary x)]))                     ";

		assertEquals("[100 200]", venice.eval(script));					
	}
	
	@Test
	public void test_deftype_validation_FAILED() {
		final String script =
				"(do                                                      \n" +
				"  (deftype :complex                                      \n" +
				"           [real :long, imaginary :long]                 \n" +
				"           (fn [t] (assert (pos? (:real t))              \n" +
				"                   \"real must be positive\")))          \n" +
				"  (def x (.: :complex -100 200))                         \n" +
				"  (pr-str [(:real x) (:imaginary x)]))                     ";

		assertThrows(AssertionException.class, () -> new Venice().eval(script));
	}
	
	@Test
	public void test_deftype_ns_hijack_1() {
		final String script =
				"(do                                                      \n" +
				"  (ns foo)                                               \n" +
				"  (deftype :user/complex [real :long, imaginary :long]))   ";

		assertThrows(VncException.class, () -> new Venice().eval(script));
	}
	
	@Test
	public void test_deftype_ns_hijack_2() {
		final String script =
				"(do                                                      \n" +
				"  (ns foo)                                               \n" +
				"  (deftype :core/complex [real :long, imaginary :long]))   ";

		assertThrows(VncException.class, () -> new Venice().eval(script));
	}
}

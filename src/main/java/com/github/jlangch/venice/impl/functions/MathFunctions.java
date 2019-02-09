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
package com.github.jlangch.venice.impl.functions;

import static com.github.jlangch.venice.impl.functions.FunctionsUtil.assertArity;
import static com.github.jlangch.venice.impl.types.Constants.False;
import static com.github.jlangch.venice.impl.types.Constants.True;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.jlangch.venice.ArityException;
import com.github.jlangch.venice.VncException;
import com.github.jlangch.venice.impl.types.Coerce;
import com.github.jlangch.venice.impl.types.Types;
import com.github.jlangch.venice.impl.types.VncBigDecimal;
import com.github.jlangch.venice.impl.types.VncDouble;
import com.github.jlangch.venice.impl.types.VncFunction;
import com.github.jlangch.venice.impl.types.VncLong;
import com.github.jlangch.venice.impl.types.VncString;
import com.github.jlangch.venice.impl.types.VncVal;
import com.github.jlangch.venice.impl.types.collections.VncHashMap;
import com.github.jlangch.venice.impl.types.collections.VncList;


public class MathFunctions {

	public static VncFunction add = 
		new VncFunction(
				"+", 
				VncFunction
					.meta()
					.arglists("(+)", "(+ x)", "(+ x y)", "(+ x y & more)")		
					.doc("Returns the sum of the numbers. (+) returns 0.")
					.examples("(+)", "(+ 1)", "(+ 1 2)", "(+ 1 2 3 4)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				switch(args.size()) {
					case 0: return new VncLong(0);
					case 1: return args.first();
					case 2: return Numeric.calc(MathOp.ADD, args.first(), args.second());
					default:
						VncVal val = args.first();
						for(VncVal v : args.rest().getList()) { val = Numeric.calc(MathOp.ADD, val, v); }
						return val;
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction subtract = 
		new VncFunction(
				"-", 
				VncFunction
					.meta()
					.arglists("(- x)", "(- x y)", "(- x y & more)")		
					.doc(
						"If one number is supplied, returns the negation, else subtracts " +
						"the numbers from x and returns the result.")
					.examples("(- 4)", "(- 8 3 -2 -1)", "(- 8 2.5)", "(- 8 1.5M)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				switch(args.size()) {
					case 0: 
						throw new ArityException(args, 0, "-");
					case 1: 
						final VncVal first = args.first();
						if (Types.isVncLong(first)) {
							return new VncLong(((VncLong)first).getValue() * -1L);
						}
						else if (Types.isVncDouble(first)) {
							return new VncDouble(((VncDouble)first).getValue() * -1D);
						}
						else if (Types.isVncBigDecimal(first)) {
							return new VncBigDecimal(((VncBigDecimal)first).getValue().negate());
						}
						else {
							return first;
						}	
					case 2: 
						return Numeric.calc(MathOp.SUB, args.first(), args.second());
					default:
						VncVal val = args.first();
						for(VncVal v : args.rest().getList()) { val = Numeric.calc(MathOp.SUB, val, v); }
						return val;
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction multiply = 
		new VncFunction(
				"*", 
				VncFunction
					.meta()
					.arglists("(*)", "(* x)", "(* x y)", "(* x y & more)")		
					.doc("Returns the product of numbers. (*) returns 1")
					.examples("(*)", "(* 4)", "(* 4 3)", "(* 4 3 2)", "(* 6.0 2)", "(* 6 1.5M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				switch(args.size()) {
					case 0: return new VncLong(1);
					case 1: return args.first();
					case 2: return Numeric.calc(MathOp.MUL, args.first(), args.second());
					default:
						VncVal val = args.first();
						for(VncVal v : args.rest().getList()) { val = Numeric.calc(MathOp.MUL, val, v); }
						return val;
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction divide = 
		new VncFunction(
				"/", 
				VncFunction
					.meta()
					.arglists("(/ x)", "(/ x y)", "(/ x y & more)")		
					.doc(
						"If no denominators are supplied, returns 1/numerator, " + 
						"else returns numerator divided by all of the denominators.")
					.examples("(/ 2.0)", "(/ 12 2 3)", "(/ 12 3)", "(/ 6.0 2)", "(/ 6 1.5M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				switch(args.size()) {
					case 0: 
						throw new ArityException(args, 0, "/");
					case 1: 
						final VncVal first = args.first();
						if (Types.isVncLong(first)) {
							return Numeric.calc(MathOp.DIV, new VncLong(1L), first);
						}
						else if (Types.isVncDouble(first)) {
							return Numeric.calc(MathOp.DIV, new VncDouble(1D), first);
						}
						else if (Types.isVncBigDecimal(first)) {
							return Numeric.calc(MathOp.DIV, new VncBigDecimal(BigDecimal.ONE), first);
						}
						else {
							return first;
						}	
					case 2: 
						return Numeric.calc(MathOp.DIV, args.first(), args.second());
					default:
						VncVal val = args.first();
						for(VncVal v : args.rest().getList()) { val = Numeric.calc(MathOp.DIV, val, v); }
						return val;
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction modulo = 
		new VncFunction(
				"mod", 
				VncFunction
					.meta()
					.arglists("(mod n d)")		
					.doc("Modulus of n and d.")
					.examples("(mod 10 4)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("mod", args, 2);
	
				if (!Types.isVncLong(args.first())) {
					throw new VncException(String.format(
							"Function 'mod' does not allow %s as numerator", 
							Types.getClassName(args.first())));
				}
				if (!Types.isVncLong(args.second())) {
					throw new VncException(String.format(
							"Function 'mod' does not allow %s as denominator", 
							Types.getClassName(args.second())));
				}
				
				return new VncLong(
							((VncLong)args.first()).getValue().longValue() 
							% 
							((VncLong)args.second()).getValue().longValue());
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction inc = 
		new VncFunction(
				"inc", 
				VncFunction
					.meta()
					.arglists("(inc x)")		
					.doc("Increments the number x")
					.examples("(inc 10)", "(inc 10.1)", "(inc 10.12M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("inc", args, 1);
	
				final VncVal arg = args.first();
				if (Types.isVncLong(arg)) {
					return new VncLong(((VncLong)arg).getValue() + 1L);
				}
				else if (Types.isVncDouble(arg)) {
					return new VncDouble(((VncDouble)arg).getValue() + 1D);
				}
				else if (Types.isVncBigDecimal(arg)) {
					return new VncBigDecimal(((VncBigDecimal)arg).getValue().add(new BigDecimal(1)));
				}
				else {
					throw new VncException(String.format(
							"Invalid argument type %s while calling function 'inc'",
							Types.getClassName(arg)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction dec = 
		new VncFunction(
				"dec", 
				VncFunction
					.meta()
					.arglists("(dec x)")		
					.doc("Decrements the number x")
					.examples("(dec 10)", "(dec 10.1)", "(dec 10.12M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("dec", args, 1);
	
				final VncVal arg = args.first();
				if (Types.isVncLong(arg)) {
					return new VncLong(((VncLong)arg).getValue() - 1L);
				}
				else if (Types.isVncDouble(arg)) {
					return new VncDouble(((VncDouble)arg).getValue() - 1D);
				}
				else if (Types.isVncBigDecimal(arg)) {
					return new VncBigDecimal(((VncBigDecimal)arg).getValue().subtract(new BigDecimal(1)));
				}
				else {
					throw new VncException(String.format(
							"Invalid argument type %s while calling function 'dec'",
							Types.getClassName(arg)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction max = 
		new VncFunction(
				"max", 
				VncFunction
					.meta()
					.arglists("(max x)", "(max x y)", "(max x y & more)")		
					.doc("Returns the greatest of the values")
					.examples(
						"(max 1)", "(max 1 2)", "(max 4 3 2 1)",
						"(max 1.0)", "(max 1.0 2.0)", "(max 4.0 3.0 2.0 1.0)",
						"(max 1.0M)", "(max 1.0M 2.0M)", "(max 4.0M 3.0M 2.0M 1.0M)",
						"(max 1.0M 2)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				if (args.isEmpty()) {
					throw new ArityException(args, 0, "max");
				}
	
				final VncVal op1 = args.first();
				
				VncVal max = op1;
				for(VncVal op : args.rest().getList()) {
					if (Types.isVncNumber(op)) {
						max = op.compareTo(max) > 0 ? op : max;
					}
					else {
						throw new VncException(String.format(
												"Function 'max' does not allow %s as operand", 
												Types.getClassName(max)));
					}
				}
				
				return max;			
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction min = 
		new VncFunction(
				"min", 
				VncFunction
					.meta()
					.arglists("(min x)", "(min x y)", "(min x y & more)")		
					.doc("Returns the smallest of the values")
					.examples(
						"(min 1)", "(min 1 2)", "(min 4 3 2 1)",
						"(min 1.0)", "(min 1.0 2.0)", "(min 4.0 3.0 2.0 1.0)",
						"(min 1.0M)", "(min 1.0M 2.0M)", "(min 4.0M 3.0M 2.0M 1.0M)",
						"(min 1.0M 2)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				if (args.isEmpty()) {
					throw new ArityException(args, 0, "min");
				}
				
				final VncVal op1 = args.first();
				
				VncVal min = op1;
				for(VncVal op : args.rest().getList()) {
					if (Types.isVncNumber(op)) {
						min = op.compareTo(min) < 0 ? op : min;
					}
					else {
						throw new VncException(String.format(
												"Function 'min' does not allow %s as operand", 
												Types.getClassName(min)));
					}
				}
				
				return min;			
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction abs = 
		new VncFunction(
				"abs", 
				VncFunction
					.meta()
					.arglists("(abs x)")		
					.doc("Returns the absolute value of the number")
					.examples("(abs 10)", "(abs -10)", "(abs -10.1)", "(abs -10.12M)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				assertArity("abs", args, 1);
				
				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					return new VncLong(Math.abs(((VncLong)arg).getValue().longValue()));
				}
				else if (Types.isVncDouble(arg)) {
					return new VncDouble(Math.abs(((VncDouble)arg).getValue().doubleValue()));
				}
				else if (Types.isVncBigDecimal(arg)) {
					return new VncBigDecimal(((VncBigDecimal)arg).getValue().abs());
				}
				else {
					throw new VncException(String.format(
							"Invalid argument type %s while calling function 'abs'",
							Types.getClassName(arg)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction negate = 
		new VncFunction(
				"negate", 
				VncFunction
					.meta()
					.arglists("(negate x)")		
					.doc("Negates x")
					.examples("(negate 10)", "(negate 1.23)", "(negate 1.23M)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				assertArity("negate", args, 1);
				
				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					return new VncLong(Math.negateExact(((VncLong)arg).getValue().longValue()));
				}
				else if (Types.isVncDouble(arg)) {
					return new VncDouble(((VncDouble)arg).getValue().doubleValue() * -1D);
				}
				else if (Types.isVncBigDecimal(arg)) {
					return new VncBigDecimal(Coerce.toVncBigDecimal(args.first()).getValue().negate());
				}
				else {
					throw new VncException(String.format(
							"Invalid argument type %s while calling function 'negate'",
							Types.getClassName(arg)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction sqrt = 
		new VncFunction(
				"sqrt", 
				VncFunction
					.meta()
					.arglists("(sqrt x)")		
					.doc("Square root of x")
					.examples("(sqrt 10)", "(sqrt 10.23)", "(sqrt 10.23M)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				assertArity("sqrt", args, 1);
				
				final VncVal arg = args.first();
				
				if (Types.isVncLong(arg)) {
					return new VncDouble(Math.sqrt(((VncLong)arg).getValue().doubleValue()));
				}
				else if (Types.isVncDouble(arg)) {
					return new VncDouble(Math.sqrt(((VncDouble)arg).getValue()));
				}
				else if (Types.isVncBigDecimal(arg)) {
					return new VncBigDecimal(
								new BigDecimal(
										Math.sqrt(
											Coerce.toVncBigDecimal(args.first()).getValue().doubleValue())));
				}
				else {
					throw new VncException(String.format(
							"Invalid argument type %s while calling function 'sqrt'",
							Types.getClassName(arg)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction rand_long = 
		new VncFunction(
				"rand-long", 
				VncFunction
					.meta()
					.arglists("(rand-long)", "(rand-long max)")		
					.doc(
						"Without argument returns a random long between 0 and MAX_LONG. " +
						"With argument max returns a random long between 0 and max exclusive.")
					.examples("(rand-long)", "(rand-long 100)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("rand-long", args, 0, 1);
				
				if (args.isEmpty()) {
					return new VncLong(Math.abs(random.nextLong()));
				}
				else {
					final long max = Coerce.toVncLong(args.first()).getValue();
					if (max < 2) {
						throw new VncException("Function 'rand-long' does not allow negative max values");
	
					}
					return new VncLong(Math.abs(random.nextLong()) % max);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction rand_double = 
		new VncFunction(
				"rand-double", 
				VncFunction
					.meta()
					.arglists("(rand-double)", "(rand-double max)")		
					.doc(
						"Without argument returns a double between 0.0 and 1.0. " +
						"With argument max returns a random double between 0.0 and max.")
					.examples("(rand-double)", "(rand-double 100.0)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("rand-double", args, 0, 1);
				
				if (args.isEmpty()) {
					return new VncDouble(random.nextDouble());
				}
				else {
					final double max = Coerce.toVncDouble(args.first()).getValue();
					if (max < 0.0) {
						throw new VncException(
								"Function 'rand-double' does not allow negative max values");
	
					}
					return new VncDouble(random.nextDouble() * max);
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction rand_gaussian = 
		new VncFunction(
				"rand-gaussian", 
				VncFunction
					.meta()
					.arglists("(rand-gaussian)", "(rand-gaussian mean stddev)")		
					.doc(
						"Without argument returns a Gaussion distributed double value with " +
						"mean 0.0 and standard deviation 1.0. " +
						"With argument mean and stddev returns a Gaussion distributed double " +
						"value with the given mean and standard deviation.")
					.examples("(rand-gaussian)", "(rand-gaussian 0.0 5.0)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("rand-gaussian", args, 0, 2);
				
				if (args.isEmpty()) {
					return new VncDouble(random.nextGaussian());
				}
				else {
					final double mean = Coerce.toVncDouble(args.first()).getValue();
					final double stddev = Coerce.toVncDouble(args.second()).getValue();
					return new VncDouble(mean + stddev * random.nextGaussian());
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction zero_Q = 
		new VncFunction(
				"zero?", 
				VncFunction
					.meta()
					.arglists("(zero? x)")		
					.doc("Returns true if x zero else false")
					.examples("(zero? 0)", "(zero? 2)", "(zero? 0.0)", "(zero? 0.0M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("zero?", args, 1);
				
				final VncVal op1 = args.first();
				if (Types.isVncLong(op1)) {
					return ((VncLong)op1).getValue() == 0 ? True : False;
				}
				else if (Types.isVncDouble(op1)) {
					return ((VncDouble)op1).getValue() == 0.0 ? True : False;
				}
				else if (Types.isVncBigDecimal(op1)) {
					return ((VncBigDecimal)op1).getValue().compareTo(BigDecimal.ZERO) == 0 ? True : False;
				}
				else {
					throw new VncException(String.format(
											"Function 'zero?' does not allow %s as operand 1", 
											Types.getClassName(op1)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction pos_Q = 
		new VncFunction(
				"pos?", 
				VncFunction
					.meta()
					.arglists("(pos? x)")		
					.doc("Returns true if x greater than zero else false")
					.examples("(pos? 3)", "(pos? -3)", "(pos? 3.2)", "(pos? 3.2M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("pos?", args, 1);
				
				final VncVal op1 = args.first();
				if (Types.isVncLong(op1)) {
					return ((VncLong)op1).getValue() > 0 ? True : False;
				}
				else if (Types.isVncDouble(op1)) {
					return ((VncDouble)op1).getValue() > 0 ? True : False;
				}
				else if (Types.isVncBigDecimal(op1)) {
					return ((VncBigDecimal)op1).getValue().compareTo(BigDecimal.ZERO) > 0 ? True : False;
				}
				else {
					throw new VncException(String.format(
											"Function 'pos?' does not allow %s as operand 1", 
											Types.getClassName(op1)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction neg_Q = 
		new VncFunction(
				"neg?", 
				VncFunction
					.meta()
					.arglists("(neg? x)")		
					.doc("Returns true if x smaller than zero else false")
					.examples("(neg? -3)", "(neg? 3)", "(neg? -3.2)", "(neg? -3.2M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("neg?", args, 1);
				
				final VncVal op1 = args.first();
				if (Types.isVncLong(op1)) {
					return ((VncLong)op1).getValue() < 0 ? True : False;
				}
				else if (Types.isVncDouble(op1)) {
					return ((VncDouble)op1).getValue() < 0 ? True : False;
				}
				else if (Types.isVncBigDecimal(op1)) {
					return ((VncBigDecimal)op1).getValue().compareTo(BigDecimal.ZERO) < 0 ? True : False;
				}
				else {
					throw new VncException(String.format(
											"Function 'neg?' does not allow %s as operand 1s", 
											Types.getClassName(op1)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction even_Q = 
		new VncFunction(
				"even?", 
				VncFunction
					.meta()
					.arglists("(even? n)")		
					.doc("Returns true if n is even, throws an exception if n is not an integer")
					.examples("(even? 4)", "(even? 3)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				assertArity("even?", args, 1);
				
				final VncVal op1 = args.first();
				if (Types.isVncLong(op1)) {
					return ((VncLong)op1).getValue() % 2L == 0L ? True : False;
				}
				else {
					throw new VncException(String.format(
											"Function 'even?' does not allow %s as operand.", 
											Types.getClassName(op1)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction odd_Q = 
		new VncFunction(
				"odd?", 
				VncFunction
					.meta()
					.arglists("(odd? n)")		
					.doc("Returns true if n is odd, throws an exception if n is not an integer")
					.examples("(odd? 3)", "(odd? 4)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("odd?", args, 1);
				
				final VncVal op1 = args.first();
				if (Types.isVncLong(op1)) {
					return ((VncLong)op1).getValue() % 2L == 1L ? True : False;
				}
				else {
					throw new VncException(String.format(
											"Function 'odd?' does not allow %s as operand", 
											Types.getClassName(op1)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	
	public static VncFunction dec_add = 
		new VncFunction(
				"dec/add", 
				VncFunction
					.meta()
					.arglists("(dec/add x y scale rounding-mode)")		
					.doc(
						"Adds two decimals and scales the result. rounding-mode is one of (:CEILING, :DOWN, " +
						":FLOOR, :HALF_DOWN, :HALF_EVEN, :HALF_UP, :UNNECESSARY, :UP)")
					.examples("(dec/add 2.44697M 1.79882M 3 :HALF_UP)")
					.build()
		) {	
			public VncVal apply(final VncList args) {
				assertArity("dec/add", args, 4);
	
				final VncBigDecimal op1 = Coerce.toVncBigDecimal(args.first());
				final VncBigDecimal op2 = Coerce.toVncBigDecimal(args.second());
				final VncLong scale = Coerce.toVncLong(args.nth(2));
				final RoundingMode roundingMode = VncBigDecimal.toRoundingMode(Coerce.toVncString(args.nth(3)));
					
				return new VncBigDecimal(op1.getValue()
								.add(op2.getValue())
								.setScale(scale.getValue().intValue(), roundingMode));
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction dec_sub = 
		new VncFunction(
				"dec/sub", 
				VncFunction
					.meta()
					.arglists("(dec/sub x y scale rounding-mode)")		
					.doc(
						"Subtract y from x and scales the result. rounding-mode is one of (:CEILING, :DOWN, " +
						":FLOOR, :HALF_DOWN, :HALF_EVEN, :HALF_UP, :UNNECESSARY, :UP)")
					.examples("(dec/sub 2.44697M 1.79882M 3 :HALF_UP)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("dec/sub", args, 4);
	
				final VncBigDecimal op1 = Coerce.toVncBigDecimal(args.first());
				final VncBigDecimal op2 = Coerce.toVncBigDecimal(args.second());
				final VncLong scale = Coerce.toVncLong(args.nth(2));
				final RoundingMode roundingMode = VncBigDecimal.toRoundingMode(Coerce.toVncString(args.nth(3)));
					
				return new VncBigDecimal(op1.getValue().subtract(op2.getValue()).setScale(scale.getValue().intValue(), roundingMode));
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction dec_mul = 
		new VncFunction(
				"dec/mul", 
				VncFunction
					.meta()
					.arglists("(dec/mul x y scale rounding-mode)")		
					.doc(
						"Multiplies two decimals and scales the result. rounding-mode is one of (:CEILING, :DOWN, " +
						":FLOOR, :HALF_DOWN, :HALF_EVEN, :HALF_UP, :UNNECESSARY, :UP)")
					.examples("(dec/mul 2.44697M 1.79882M 5 :HALF_UP)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("dec/mul", args, 4);
	
				final VncBigDecimal op1 = Coerce.toVncBigDecimal(args.first());
				final VncBigDecimal op2 = Coerce.toVncBigDecimal(args.second());
				final VncLong scale = Coerce.toVncLong(args.nth(2));
				final RoundingMode roundingMode = VncBigDecimal.toRoundingMode(Coerce.toVncString(args.nth(3)));
					
				return new VncBigDecimal(op1.getValue().multiply(op2.getValue()).setScale(scale.getValue().intValue(), roundingMode));
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	public static VncFunction dec_div = 
		new VncFunction(
				"dec/div", 
				VncFunction
					.meta()
					.arglists("(dec/div x y scale rounding-mode)")		
					.doc(
						"Divides x by y and scales the result. rounding-mode is one of (:CEILING, :DOWN, " +
						":FLOOR, :HALF_DOWN, :HALF_EVEN, :HALF_UP, :UNNECESSARY, :UP)")
					.examples("(dec/div 2.44697M 1.79882M 5 :HALF_UP)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("dec/div", args, 4);
	
				final VncBigDecimal op1 = Coerce.toVncBigDecimal(args.first());
				final VncBigDecimal op2 = Coerce.toVncBigDecimal(args.second());
				final VncLong scale = Coerce.toVncLong(args.nth(2));
				final RoundingMode roundingMode = VncBigDecimal.toRoundingMode(Coerce.toVncString(args.nth(3)));
					
				return new VncBigDecimal(op1.getValue().divide(op2.getValue(), scale.getValue().intValue(), roundingMode));
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};

	public static VncFunction dec_scale = 
		new VncFunction(
				"dec/scale", 
				VncFunction
					.meta()
					.arglists("(dec/scale x scale rounding-mode)")		
					.doc(
						"Scales a decimal. rounding-mode is one of (:CEILING, :DOWN, " +
						":FLOOR, :HALF_DOWN, :HALF_EVEN, :HALF_UP, :UNNECESSARY, :UP)")
					.examples(
						"(dec/scale 2.44697M 0 :HALF_UP)",
						"(dec/scale 2.44697M 1 :HALF_UP)",
						"(dec/scale 2.44697M 2 :HALF_UP)",
						"(dec/scale 2.44697M 3 :HALF_UP)",
						"(dec/scale 2.44697M 10 :HALF_UP)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("dec/scale", args, 3);
	
				final VncVal arg = args.first();
				final VncLong scale = Coerce.toVncLong(args.second());
				final RoundingMode roundingMode = VncBigDecimal.toRoundingMode((VncString)args.nth(2));
							
				if (Types.isVncBigDecimal(arg)) {
					final BigDecimal val = ((VncBigDecimal)arg).getValue();
					return new VncBigDecimal(val.setScale(scale.getValue().intValue(), roundingMode));
				}
				else {
					throw new VncException(String.format(
											"Function 'dec/scale' does not allow %s as operand 1s",
											Types.getClassName(arg)));
				}
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};


	public static VncFunction range = 
		new VncFunction(
				"range", 
				VncFunction
					.meta()
					.arglists("(range end)", "(range start end)", "(range start end step)")		
					.doc(
						"Returns a collection of numbers from start (inclusive) to end " + 
						"(exclusive), by step, where start defaults to 0 and step defaults to 1. " +
						"When start is equal to end, returns empty list.")
					.examples(
						"(range 10)",
						"(range 10 20)",
						"(range 10 20 3)",
						"(range 10 15 0.5)",
						"(range 1.1M 2.2M 0.1M)")
					.build()
		) {		
			public VncVal apply(final VncList args) {
				assertArity("range", args, 1, 2, 3);
	
				VncVal start = new VncLong(0);
				VncVal end = new VncLong(0);
				VncVal step = new VncLong(1);
	
				switch(args.size()) {
					case 1:
						end = args.first();
						break;
					case 2:
						start = args.first();
						end = args.second();
						break;
					case 3:
						start = args.first();
						end = args.second();
						step = args.nth(2);
						break;
				}
							
				if (!Types.isVncNumber(start)) {
					throw new VncException("range: start value must be a number");
				}
				if (!Types.isVncNumber(end)) {
					throw new VncException("range: end value must be a number");	
				}
				if (!Types.isVncNumber(step)) {
					throw new VncException("range: step value must be a number");	
				}
	
				final List<VncVal> values = new ArrayList<>();
	
				if (zero_Q.apply(VncList.of(step)) == True) {
					throw new VncException("range: a step value must not be 0");	
				}
				
				if (MathFunctions.pos_Q.apply(VncList.of(step)) == True) {
					if (CoreFunctions.lt.apply(VncList.of(end, start)) == True) {
						throw new VncException("range positive step: end must not be lower than start");	
					}
					
					VncVal val = start;
					while(CoreFunctions.lt.apply(VncList.of(val, end)) == True) {
						values.add(val);
						val = add.apply(VncList.of(val, step));
					}
				}
				else {
					if (CoreFunctions.gt.apply(VncList.of(end, start)) == True) {
						throw new VncException("range negative step: end must not be greater than start");	
					}
					
					VncVal val = start;
					while(CoreFunctions.gt.apply(VncList.of(val, end)) == True) {
						values.add(val);
						val = add.apply(VncList.of(val, step));
					}
				}
				
				return new VncList(values);
			}
	
		    private static final long serialVersionUID = -1848883965231344442L;
		};
	
	///////////////////////////////////////////////////////////////////////////
	// types_ns is namespace of type functions
	///////////////////////////////////////////////////////////////////////////

	public static Map<VncVal, VncVal> ns = 
			new VncHashMap.Builder()
					.put("+",					add)
					.put("-",					subtract)
					.put("*",					multiply)
					.put("/",					divide)
					.put("mod",					modulo)
					.put("inc",					inc)
					.put("dec",					dec)
					.put("abs",					abs)
					.put("min",					min)
					.put("max",					max)
					.put("negate",				negate)
					.put("sqrt",				sqrt)

					.put("dec/add",				dec_add)
					.put("dec/sub",				dec_sub)
					.put("dec/mul",				dec_mul)
					.put("dec/div",				dec_div)
					.put("dec/scale",			dec_scale)
					
					.put("zero?",				zero_Q)
					.put("pos?",				pos_Q)
					.put("neg?",				neg_Q)
					.put("even?",				even_Q)
					.put("odd?",				odd_Q)

					.put("rand-long",			rand_long)
					.put("rand-double",			rand_double)
					.put("rand-gaussian",		rand_gaussian)
					
					.put("range",				range)

					.toMap();	


	private static final Random random = new Random();
}

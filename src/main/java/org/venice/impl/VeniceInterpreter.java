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
package org.venice.impl;

import static org.venice.impl.types.Constants.False;
import static org.venice.impl.types.Constants.Nil;

import java.util.concurrent.atomic.AtomicReference;

import org.venice.Version;
import org.venice.VncException;
import org.venice.impl.javainterop.JavaImports;
import org.venice.impl.javainterop.JavaInteropFn;
import org.venice.impl.types.Coerce;
import org.venice.impl.types.Constants;
import org.venice.impl.types.Types;
import org.venice.impl.types.VncFunction;
import org.venice.impl.types.VncString;
import org.venice.impl.types.VncSymbol;
import org.venice.impl.types.VncVal;
import org.venice.impl.types.collections.VncList;
import org.venice.impl.types.collections.VncMap;
import org.venice.impl.util.ClassPathResource;


public class VeniceInterpreter {
	
	public VeniceInterpreter() {
		
	}
	
	
	// read
	public VncVal READ(final String script, final String filename) {
		return Reader.read_str(script, filename);
	}

	// eval
	public static boolean is_pair(final VncVal x) {
		return x instanceof VncList && !((VncList)x).isEmpty();
	}

	public static VncVal quasiquote(final VncVal ast) {
		if (!is_pair(ast)) {
			return new VncList(new VncSymbol("quote"), ast);
		} 
		else {
			final VncVal a0 = Coerce.toVncList(ast).nth(0);
			if ((a0 instanceof VncSymbol) && (Coerce.toVncSymbol(a0).getName().equals("unquote"))) {
				return ((VncList)ast).nth(1);
			} 
			else if (is_pair(a0)) {
				final VncVal a00 = Coerce.toVncList(a0).nth(0);
				if ((a00 instanceof VncSymbol) && (((VncSymbol)a00).getName().equals("splice-unquote"))) {
					return new VncList(new VncSymbol("concat"),
										Coerce.toVncList(a0).nth(1),
									   quasiquote(((VncList)ast).rest()));
				}
			}
			return new VncList(new VncSymbol("cons"),
							   quasiquote(a0),
							   quasiquote(((VncList)ast).rest()));
		}
	}

	/**
	 * Returns true if ast is a list that contains a symbol as the first element 
	 * and that symbol refers to a function in the env environment and that 
	 * function has the is_macro attribute set to true. 
	 * Otherwise, it returns false.
	 * 
	 * @param ast
	 * @param env
	 * @return true if the ast starts with a macro
	 */
	public boolean is_macro_call(final VncVal ast, final Env env) {
		if (Types.isVncList(ast) && !((VncList)ast).isEmpty()) {
			final VncVal a0 = Coerce.toVncList(ast).nth(0);
			if (Types.isVncSymbol(a0)) {
				final VncSymbol macroName = (VncSymbol)a0;
				if (env.find(macroName) != null) {
					final VncVal fn = env.get(macroName);
					if (Types.isVncFunction(fn) && ((VncFunction)fn).isMacro()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Recursively expands a macro. It calls is_macro_call with ast and env and 
	 * loops while that condition is true. Inside the loop, the first element 
	 * of the ast list (a symbol), is looked up in the environment to get 
	 * the macro function. This macro function is then called/applied with 
	 * the rest of the ast elements (2nd through the last) as arguments. 
	 * The return value of the macro call becomes the new value of ast. 
	 * When the loop completes because ast no longer represents a macro call, 
	 * the current value of ast is returned.
	 * 
	 * @param ast
	 * @param env
	 * @return
	 */
	public VncVal macroexpand(VncVal ast, final Env env) {
		while (is_macro_call(ast, env)) {
			final VncSymbol macroName = Coerce.toVncSymbol(Coerce.toVncList(ast).nth(0));
			final VncFunction macroFn = Coerce.toVncFunction(env.get(macroName));
			final VncList macroFnArgs = Coerce.toVncList(ast).rest();
			ast = macroFn.apply(macroFnArgs);
		}
		return ast;
	}

	public VncVal eval_ast(final VncVal ast, final Env env) {
		if (ast instanceof VncSymbol) {
			return env.get((VncSymbol)ast);
		} 
		else if (ast instanceof VncList) {
			final VncList old_lst = (VncList)ast;
			final VncList new_lst = old_lst.empty();
			new_lst.setMeta(old_lst.getMeta().copy());
			
			old_lst.forEach(mv -> new_lst.addAtEnd(EVAL(mv, env)));
			return new_lst;
		} 
		else if (ast instanceof VncMap) {
			final VncMap old_hm = (VncMap)ast;
			final VncMap new_hm = (VncMap)old_hm.empty();
			new_hm.setMeta(old_hm.getMeta().copy());

			((VncMap)ast).getMap().entrySet().forEach(entry ->
				new_hm.getMap().put(
						entry.getKey(), 
						EVAL((VncVal)entry.getValue(), env)));
			return new_hm;
		} 
		else {
			return ast;
		}
	}

	public VncVal EVAL(VncVal orig_ast, Env env) {
		RecursionPoint recursionPoint = null;
		
		while (true) {
			//System.out.println("EVAL: " + printer._pr_str(orig_ast, true));
			if (!orig_ast.isList()) {
				return eval_ast(orig_ast, env);
			}
	
			// apply list
			final VncVal expanded = macroexpand(orig_ast, env);
			if (!expanded.isList()) {
				return eval_ast(expanded, env);
			}
			
			final VncList ast = (VncList)expanded;
			if (ast.isEmpty()) { 
				return ast; 
			}
			
			final VncVal a0 = ast.nth(0);		
			final String a0sym = a0 instanceof VncSymbol ? ((VncSymbol)a0).getName() : "__<*fn*>__";
			
			switch (a0sym) {
				case "def": {
					final VncVal a1 = ast.nth(1);
					final VncVal a2 = ast.nth(2);
					final VncVal res = EVAL(a2, env);
					env.set((VncSymbol)a1, res);
					return res;
				}
				
				case "eval":
					orig_ast = Coerce.toVncList(eval_ast(ast.slice(1), env)).last();
					break;
					
				case "let":  { // (let [bindings*] exprs*)
					env = new Env(env);

					final VncList bindings = Coerce.toVncList(ast.nth(1));
					final VncList expressions = ast.slice(2);
				
					for(int i=0; i<bindings.size(); i+=2) {
						final VncVal sym = bindings.nth(i);
						final VncVal val = EVAL(bindings.nth(i+1), env);

						final Env _env = env;
						Destructuring
							.destructure(sym, val)
							.forEach(b -> _env.set(b.sym, b.val));
					}
						
					if (expressions.isEmpty()) {
						orig_ast = Constants.Nil;
					}
					else {
						eval_ast(expressions.slice(0, expressions.size()-1), env);
						orig_ast = expressions.last();
					}
					break;
				}
					
				case "loop": { // (loop [bindings*] exprs*)
					env = new Env(env);					

					final VncList bindings = Coerce.toVncList(ast.nth(1));
					final VncVal expressions = ast.nth(2);
					
					final VncList bindingNames = new VncList();
					for(int i=0; i<bindings.size(); i+=2) {
						final VncVal sym = bindings.nth(i);
						final VncVal val = EVAL(bindings.nth(i+1), env);

						final Env _env = env;
						Destructuring
							.destructure(sym, val)
							.forEach(b -> { 
								_env.set(b.sym, b.val); 
								bindingNames.addAtEnd(b.sym);
							 });
					}
					
					recursionPoint = new RecursionPoint(bindingNames, expressions, env);
					orig_ast = expressions;
					break;
				}

				case "recur":  // (recur exprs*)
					// [1] calculate new values
					final VncList recur_values = new VncList();
					for(int i=1; i<ast.size(); i++) {
						recur_values.addAtEnd(EVAL(ast.nth(i), env));
					}
					// [2]bind the values
					final VncList recur_bindingNames = recursionPoint.getLoopBindingNames();					
					final Env recur_env = recursionPoint.getLoopEnv();
					for(int i=0; i<recur_bindingNames.size(); i++) {
						final VncSymbol key = Coerce.toVncSymbol(recur_bindingNames.nth(i));
						recur_env.set(key, recur_values.nth(i));
					}
					orig_ast = recursionPoint.getLoopExpressions();
					env = recur_env;
					break;
					
				case "quote":
					return ast.nth(1);
					
				case "quasiquote":
					orig_ast = quasiquote(ast.nth(1));
					break;
	
				case "defmacro": {
					final VncVal macroName = ast.nth(1);
					final VncList macroParams = Coerce.toVncList(ast.nth(2));
					final VncVal macroFnAst = ast.nth(3);
	
					final String sMacroName = Types.isVncSymbol(ast.nth(1)) 
												? ((VncSymbol)ast.nth(1)).getName() 
												: ((VncString)ast.nth(1)).getValue();

					final Env _env = env;
					final VncFunction macroFn = 
							new VncFunction(sMacroName, macroFnAst, env, macroParams) {
								public VncVal apply(final VncList args) {
									final Env localEnv = new Env(_env);
									
									// destructuring macro params -> args
									Destructuring
										.destructure(macroParams, args)
										.forEach(b -> localEnv.set(b.sym, b.val));
									
									// run the macro
									final VncVal result = EVAL(macroFnAst, localEnv);
									return result;
								}
							};

					macroFn.setMacro();
					env.set((VncSymbol)macroName, macroFn);
					return macroFn;
				}

				case "macroexpand": {
					final VncVal a1 = ast.nth(1);
					return macroexpand(a1, env);
				}
					
				case "try":
					try {
						return EVAL(ast.nth(1), env);
					} 
					catch (Throwable t) {
						if (ast.size() > 2) {
							final VncList catchBlock = findFirstCatchBlock(ast.slice(2));
							if (catchBlock != null) {
								final VncVal result = eval_ast(catchBlock.slice(1), env);
								return Coerce.toVncList(result).first();
							}
							else {
								return Nil;
							}
						}
						
						throw t;
					}
					finally {
						if (ast.size() > 2) {
							final VncList finallyBlock = findFirstFinallyBlock(ast.slice(2));
							if (finallyBlock != null) {
								final VncVal result = eval_ast(finallyBlock.slice(1), env);
								return Coerce.toVncList(result).first();
							}
						}
					}
					
				case "do":
					if (ast.size() < 2) {
						orig_ast = Constants.Nil;
					}
					else {
						final VncList head_exprs = ast.slice(1, ast.size()-1);
						eval_ast(head_exprs, env);
						orig_ast = ast.last();
					}
					break;
					
				case "if": 
					final VncVal condArg = ast.nth(1);
					VncVal cond = EVAL(condArg, env);
					if (cond == Nil || cond == False) {
						// eval false slot form
						if (ast.size() > 3) {
							orig_ast = ast.nth(3);
						} 
						else {
							return Nil;
						}
					} 
					else {
						// eval true slot form
						orig_ast = ast.nth(2);
					}
					break;
					
				case "fn":
					final VncList fnParams = Coerce.toVncList(ast.nth(1));
					final VncVal fnAst = ast.nth(2);
					final Env cur_env = env;
					return new VncFunction(fnAst, env, fnParams) {
								public VncVal apply(final VncList args) {
									final Env localEnv = new Env(cur_env);
									
									Destructuring
										.destructure(fnParams, args)
										.forEach(b -> localEnv.set(b.sym, b.val));

									return EVAL(fnAst, localEnv);
								}
							};

				case "import":
					ast.slice(1).forEach(clazz -> javaImports.add(Coerce.toVncString(clazz).unkeyword().getValue()));
					return Nil;

				default:
					final VncList el = Coerce.toVncList(eval_ast(ast, env));
					if (!Types.isVncFunction(el.nth(0))) {
						throw new VncException(String.format("Not a function: '%s'", PRINT(el.nth(0))));
					}
					final VncFunction f = Coerce.toVncFunction(el.nth(0));
					final VncVal fnast = f.getAst();
					if (fnast != null) {
						orig_ast = fnast;
						env = f.genEnv(el.slice(1));
					} 
					else {
						return f.apply(el.rest());
					}
			}
		}
	}

	// print
	public String PRINT(final VncVal exp) {
		return Printer._pr_str(exp, true);
	}
	
	public VncVal RE(final String script, final String filename, final Env env) {
		final VncVal ast = READ(script, filename);
		return EVAL(ast, env);
	}

	public Env createEnv() {
		final Env env = new Env(null);

		// core functions defined in Java
		CoreFunctions.ns.keySet().forEach(
				key -> env.set(
						Types.isVncSymbol(key) ? (VncSymbol)key : ((VncString)key).toSymbol(), 
						CoreFunctions.ns.get(key)));
				
		// JavaInterop function
		env.set(new VncSymbol("."), JavaInteropFn.create(javaImports)); 
				
//		// eval function
//		env.set(
//			new VncSymbol("eval"), 
//			new VncFunction("eval") {
//				public VncVal apply(final VncList args) {
//					return EVAL(args.nth(0), env);
//				}
//			});

		// set version
		env.set(new VncSymbol("*VERSION*"), new VncString(Version.VERSION));

		// load core.vnc 
		final String core = loadCore();
		RE("(eval " + core + ")", "core.vnc", env);
		
		return env;
	}
	
	private VncList findFirstCatchBlock(final VncList blocks) {
		for(int ii=0; ii<blocks.size(); ii++) {
			final VncList block = (VncList)blocks.nth(ii);
			
			final VncSymbol sym = (VncSymbol)block.nth(0);
			if (sym.getName().equals("catch")) {
				return block;
			}
		}
		return null;
	}
	
	private VncList findFirstFinallyBlock(final VncList blocks) {
		for(int ii=0; ii<blocks.size(); ii++) {
			final VncList block = Coerce.toVncList(blocks.nth(ii));
			
			final VncSymbol sym = Coerce.toVncSymbol(block.nth(0));
			if (sym.getName().equals("finally")) {
				return block;
			}
		}
		return null;
	}
	
	private String loadCore() {
		try {
			if (veniceCore.get() == null) {
				veniceCore.set(new ClassPathResource("org/venice/core.vnc")
										.getResourceAsString("UTF-8"));
			}
			return veniceCore.get();
		}
		catch(Exception ex) {
			throw new RuntimeException("Failed to load 'core.vnc'", ex);
		}
	}
	
	
	private final JavaImports javaImports = new JavaImports();
	private final AtomicReference<String> veniceCore = new AtomicReference<>();
}

package org.pocketcampus.core.communication;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SignatureChecker {
	/**
	 * Policies for the declared exceptions comparisons.
	 */
	public enum ExceptionsPolicy {
		/**
		 * The checked method must declare exactly the same (no more, no less)
		 * exceptions (checked and unchecked) as the reference method.
		 * The order of declaration does not matter.
		 */
		DECLARED,
		
		/**
		 * The checked method must declare the exactly the same (no more, no less)
		 * checked exceptions as the reference method.
		 * The order of declaration does not matter.
		 */
		CHECKED,
		
		/**
		 * Thrown exceptions are not taken into account for the comparison.
		 */
		NONE
	}
	
	/**
	 * Policies for annotations comparisons.<br />
	 * Only annotations which have the <code>@target(RUNTIME)</code> annotation
	 * are compared (other parameters such as SOURCE or CLASS are not taken into
	 * account)
	 */
	public enum AnnotationsPolicy {
		/**
		 * The checked method must declare exactly the same (runtime) annotations
		 * as the reference method. The declaration order does not matter.
		 */
		ALL,
		
		/**
		 * The checked method must declare at least all the (runtime) annotations
		 * of the reference method. The declaration order does not matter.
		 */
		AT_LEAST,
		
		/**
		 * Annotations are not taken into account for the comparison.
		 */
		NONE
	}
	
	/**
	 * Policies for the return type comparisons.
	 */
	public enum ReturnTypePolicy {
		/**
		 * The checked method's return type must be the same (not a subclass) as the
		 * reference method's return type. For generic types, the parameterized type
		 * is not compared. For primitives, their wrapper object is used for comparison.
		 */
		SAME,
		
		/**
		 * The checked method's return type must be a subclass of (or the same class as)
		 * the reference method's return type. For primitives, their wrapper object is
		 * used for comparison (e.g. <code>int extends Object</code> is true, since
		 * <code>[int ~ Integer] extends Object</code>).
		 */
		SUBCLASS,
		
		/**
		 * The return type is not taken into account for the comparison.
		 */
		IGNORE
	}
	
	
	
	
	/**
	 * Maps each primitive's class (plus the pseudo-primitive 'void') to its Wrapper
	 * Object's class
	 */
	private final static Map<Class<?>, Class<?>> primitivesWrapperMap_ = new HashMap<Class<?>, Class<?>>();
	static {
		primitivesWrapperMap_.put(boolean.class, Boolean.class);
		primitivesWrapperMap_.put(byte.class, Byte.class);
		primitivesWrapperMap_.put(char.class, Character.class);
		primitivesWrapperMap_.put(short.class, Short.class);
		primitivesWrapperMap_.put(int.class, Integer.class);
		primitivesWrapperMap_.put(long.class, Long.class);
		primitivesWrapperMap_.put(float.class, Float.class);
		primitivesWrapperMap_.put(double.class, Double.class);
		primitivesWrapperMap_.put(void.class, Void.class);
	}
	
	
	
	/**
	 * Checks that checked's arguments are the same (in the same order) as refence's ones
	 * @param checked
	 * @param reference
	 * @return true if they are the same
	 */
	public static boolean checkArguments(Method checked, Method reference) {
		return Arrays.equals(checked.getParameterTypes(), reference.getParameterTypes());
	}
	
	/**
	 * Checks that checked's return type is the same as reference's one
	 * @param checked
	 * @param reference
	 * @param policy
	 * @return true if they are the same
	 */
	public static boolean checkReturnType(Method checked, Method reference,
			ReturnTypePolicy policy) {
		boolean valid = false;
		Class<?> crt = toWrapper(checked.getReturnType());
		Class<?> rrt = toWrapper(reference.getReturnType());
		
		switch(policy) {
		case SAME:
			valid = rrt.equals(crt);
			break;
		case SUBCLASS:
			valid = rrt.isAssignableFrom(crt);
			break;
		case IGNORE:
			valid = true;
			break;
		default:
			throw new NotImplementedException();
		}
		
		return valid;
	}
	
	public static boolean checkThrownExceptions(Method checked, Method reference,
			ExceptionsPolicy policy) {
		boolean valid = true;
		
		if (policy != ExceptionsPolicy.NONE) {
			HashSet<Throwable> c = toThrowables(checked.getExceptionTypes());
			HashSet<Throwable> r = toThrowables(reference.getExceptionTypes());
			
			switch (policy) {
			case CHECKED :
				// Unchecked exceptions are removed
				c = retainCheckedExceptions(c);
				r = retainCheckedExceptions(r);
				break;
			case DECLARED :
				// All collected exceptions will be compared
				break;
			default :
				throw new NotImplementedException();
			}
			
			
			if (c.containsAll(r)) {
				c.removeAll(r);
				valid &= (c.size() == 0);
			}
			else {
				valid = false;
			}
		}
		
		return valid;
	}
	
	public static boolean checkAnnotations(Method checked, Method reference,
			AnnotationsPolicy policy) {
		boolean valid = true;
		
		if (policy != AnnotationsPolicy.NONE) {
			HashSet<Annotation> c = toAnnotations(checked.getAnnotations());
			HashSet<Annotation> r = toAnnotations(reference.getAnnotations());
			
			switch (policy) {
			case AT_LEAST :
				if (!c.containsAll(r)) {
					valid = false;
				}
				break;
			case ALL :
				if (c.containsAll(r)) {
					c.removeAll(r);
					valid &= (c.size() == 0);
				}
				else {
					valid = false;
				}
				break;
			default :
				throw new NotImplementedException();
			}
		}
		
		return valid;
	}
	
	/**
	 * Checks the method signature of the checked method against the reference method.<br />
	 * 
	 * Returns true if arguments types and order are exactly the same, and all of the
	 * following policies returns true :
	 * <ul>
	 * 	<li>exceptions (DECLARED / CHECKED / NONE)</li>
	 * 	<li>annotations (ALL / AT_LEAST / NONE)</li>
	 * 	<li>returnType (COMPARE / IGNORE)
	 * </ul>
	 * 
	 * @param checked
	 * @param reference
	 * @param exceptions
	 * @param annotations
	 * @param returnType
	 * @return
	 */
	public static boolean sameSignature(Method checked, Method reference,
			ExceptionsPolicy exceptions, AnnotationsPolicy annotations, ReturnTypePolicy returnType) {
		
		boolean valid = true;
		
		valid &= checkArguments(checked, reference);
		valid &= checkReturnType(checked, reference, returnType);
		valid &= checkThrownExceptions(checked, reference, exceptions);
		valid &= checkAnnotations(checked, reference, annotations);
		
		return valid;
	}
	
	
	/**
	 * Returns a new set containing all checked exceptions of the given set.
	 * @param ths
	 * @return
	 */
	private static HashSet<Throwable> retainCheckedExceptions(HashSet<Throwable> ths) {
		HashSet<Throwable> out = new HashSet<Throwable>();
		
		for (Throwable t : ths) {
			if (isCheckedException(t)) {
				out.add(t);
			}
		}
		
		return out;
	}
	
	/**
	 * Return a set containing all Throwables of the given array
	 * @param cls
	 * @return
	 */
	private static HashSet<Throwable> toThrowables(Class<?>[] cls) {
		HashSet<Throwable> out = new HashSet<Throwable>();
		
		for (Class<?> cl : cls) {
			if (Throwable.class.isInstance(cl)) {
				Throwable t = Throwable.class.cast(cl);
				out.add(t);
			}
		}
		
		return out;
	}
	
	/**
	 * Convert an array of annotations into a set of annotations
	 * @param as
	 * @return
	 */
	private static HashSet<Annotation> toAnnotations(Annotation[] as) {
		HashSet<Annotation> out = new HashSet<Annotation>();
		out.addAll(Arrays.asList(as));
		return out;
	}
	
	/**
	 * If the given class is a primitive, return its wrapper. Else, return the given class
	 * @param clazz
	 * @return
	 */
	private static Class<?> toWrapper(Class<?> clazz) {
		if (!clazz.isPrimitive())
			return clazz;
		return primitivesWrapperMap_.get(clazz);
	}
	
	
	/*
	 * Copyright 2002-2007 the original author or authors.
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *      http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 * 
	 * From : http://www.java2s.com/Code/Java/Reflection/IsCheckedException.htm
	 */
	
	/**
	   * Return whether the given throwable is a checked exception:
	   * that is, neither a RuntimeException nor an Error.
	   * @param ex the throwable to check
	   * @return whether the throwable is a checked exception
	   * @see java.lang.Exception
	   * @see java.lang.RuntimeException
	   * @see java.lang.Error
	   */
	  private static boolean isCheckedException(Throwable ex) {
	    return !(ex instanceof RuntimeException || ex instanceof Error);
	  }
}

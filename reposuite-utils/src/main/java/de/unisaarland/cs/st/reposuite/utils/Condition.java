package de.unisaarland.cs.st.reposuite.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class Condition {
	
	private static final class NoneNullPredicate implements Predicate {
		
		String string = violation;
		
		@Override
		public boolean evaluate(final Object object) {
			if (object == null) {
				return true;
			}
			if (object instanceof Map<?, ?>) {
				noneNull((Map<?, ?>) object, this.string);
			} else if (object instanceof Collection<?>) {
				noneNull((Collection<?>) object, this.string);
			} else if (object.getClass().isArray()) {
				if (object.getClass().getComponentType().isAssignableFrom(Object.class)) {
					noneNull((Object[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Integer.class)) {
					noneNull((int[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Byte.class)) {
					noneNull((byte[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Short.class)) {
					noneNull((Short[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Long.class)) {
					noneNull((long[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Float.class)) {
					noneNull((float[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Double.class)) {
					noneNull((Double[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Boolean.class)) {
					noneNull((boolean[]) object, this.string);
				} else if (object.getClass().getComponentType().isAssignableFrom(Character.class)) {
					noneNull((Character[]) object, this.string);
				} else {
					noneNull((Object[]) object, this.string);
				}
			}
			return false;
		}
		
		/**
		 * @param message
		 */
		public NoneNullPredicate setMessage(final String message) {
			this.string = message;
			return this;
		}
		
		/**
		 * @param formatString
		 * @param arguments
		 * @return
		 */
		public NoneNullPredicate setMessage(final String formatString, final Object... arguments) {
			this.string = formatter.format(formatString, arguments).toString();
			return this;
		}
		
	}
	
	private static final String            violation         = "(no requirement specification given).";
	private static final Formatter         formatter         = new Formatter();
	private static final NoneNullPredicate noneNullPredicate = new Condition.NoneNullPredicate();
	
	/**
	 * @param condition
	 */
	@Deprecated
	public static final void check(final boolean condition) {
		assert condition : getCallerString()
		        + formatter.format("Condition evaluated to false. Violated assertion: %s", violation);
	}
	
	/**
	 * @param condition
	 * @param message
	 */
	public static final void check(final boolean condition, final String message) {
		assert condition : getCallerString()
		        + formatter.format("Condition evaluated to false. Violated assertion: %s", message);
	}
	
	/**
	 * @param condition
	 * @param formatString
	 * @param arguments
	 */
	public static final void check(final boolean condition, final String formatString, final Object... arguments) {
		assert condition : getCallerString()
		        + formatter.format("Condition evaluated to false. Violated assertion: %s",
		                formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final boolean[] array, final boolean element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final boolean[] array, final boolean element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final boolean[] array, final boolean element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final byte[] array, final byte element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final byte[] array, final byte element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final byte[] array, final byte element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final char[] array, final char element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final char[] array, final char element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final char[] array, final char element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 * @param object
	 */
	@Deprecated
	public static final void contains(final Collection<?> collection, final Object object) {
		assert collection.contains(object) : getCallerString()
		        + formatter
		                .format("Collection does not contain object `%s`. Violated assertion: %s", object, violation);
	}
	
	/**
	 * @param collection
	 * @param object
	 * @param message
	 */
	public static final void contains(final Collection<?> collection, final Object object, final String message) {
		assert collection.contains(object) : getCallerString()
		        + formatter.format("Collection does not contain object `%s`. Violated assertion: %s", object, message);
	}
	
	/**
	 * @param collection
	 * @param object
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final Collection<?> collection, final Object object, final String formatString,
	        final Object... arguments) {
		assert collection.contains(object) : getCallerString()
		        + formatter.format("Collection does not contain object `%s`. Violated assertion: %s", object,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final double[] array, final double element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final double[] array, final double element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final double[] array, final double element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final float[] array, final float element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final float[] array, final float element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final float[] array, final float element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final int[] array, final int element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final int[] array, final int element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final int[] array, final int element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final long[] array, final long element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final long[] array, final long element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final long[] array, final long element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final Object[] array, final Object element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final Object[] array, final Object element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final Object[] array, final Object element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param element
	 */
	@Deprecated
	public static final void contains(final short[] array, final short element) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, violation);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param message
	 */
	public static final void contains(final short[] array, final short element, final String message) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element, message);
	}
	
	/**
	 * @param array
	 * @param element
	 * @param formatString
	 * @param arguments
	 */
	public static final void contains(final short[] array, final short element, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.contains(array, element) : getCallerString()
		        + formatter.format("Array does not contain element `%s`. Violated assertion: %s", element,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 */
	@Deprecated
	public static final void containsAll(final Collection<?> collection, final Collection<?> innerCollection) {
		assert collection.containsAll(innerCollection) : getCallerString()
		        + formatter.format("Collection does not contain all objects in `%s`. Violated assertion: %s",
		                innerCollection, violation);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param message
	 */
	public static final void containsAll(final Collection<?> collection, final Collection<?> innerCollection,
	        final String message) {
		assert collection.containsAll(innerCollection) : getCallerString()
		        + formatter.format("Collection does not contain all objects in `%s`. Violated assertion: %s",
		                innerCollection, message);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param formatString
	 * @param arguments
	 */
	public static final void containsAll(final Collection<?> collection, final Collection<?> innerCollection,
	        final String formatString, final Object... arguments) {
		assert collection.containsAll(innerCollection) : getCallerString()
		        + formatter.format("Collection does not contain all objects in `%s`. Violated assertion: %s",
		                innerCollection, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 */
	@Deprecated
	public static final void containsAny(final Collection<?> collection, final Collection<?> innerCollection) {
		assert CollectionUtils.containsAny(collection, innerCollection) : getCallerString()
		        + formatter.format("Collection does not contain any of the objects in `%s`. Violated assertion: %s",
		                innerCollection, violation);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param message
	 */
	public static final void containsAny(final Collection<?> collection, final Collection<?> innerCollection,
	        final String message) {
		assert CollectionUtils.containsAny(collection, innerCollection) : getCallerString()
		        + formatter.format("Collection does not contain any of the objects in `%s`. Violated assertion: %s",
		                innerCollection, message);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param formatString
	 * @param arguments
	 */
	public static final void containsAny(final Collection<?> collection, final Collection<?> innerCollection,
	        final String formatString, final Object... arguments) {
		assert CollectionUtils.containsAny(collection, innerCollection) : getCallerString()
		        + formatter.format("Collection does not contain any of the objects in `%s`. Violated assertion: %s",
		                innerCollection, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final boolean[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final boolean[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final boolean[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final byte[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final byte[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final byte[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final char[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final char[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final char[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 */
	@Deprecated
	public static final void empty(final Collection<?> collection) {
		assert !collection.isEmpty() : getCallerString()
		        + formatter.format("Collection is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param collection
	 * @param message
	 */
	public static final void empty(final Collection<?> collection, final String message) {
		assert !collection.isEmpty() : getCallerString()
		        + formatter.format("Collection is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param collection
	 * @param formatString
	 * @param arguments
	 */
	public static final void empty(final Collection<?> collection, final String formatString, final Object... arguments) {
		assert !collection.isEmpty() : getCallerString()
		        + formatter.format("Collection is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final double[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final double[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final double[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final float[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final float[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final float[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final int[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final int[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final int[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final long[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final long[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final long[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final Object[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final Object[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final Object[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void empty(final short[] array) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final short[] array, final String message) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void empty(final short[] array, final String formatString, final Object... arguments) {
		assert ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is not empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param first
	 * @param second
	 */
	@Deprecated
	public static final void equals(final Object first, final Object second) {
		assert ((first == null) && (second == null)) || first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        first, second, violation);
	}
	
	/**
	 * @param first
	 * @param second
	 * @param message
	 */
	public static final void equals(final Object first, final Object second, final String message) {
		assert ((first == null) && (second == null)) || first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        first, second, message);
	}
	
	/**
	 * @param first
	 * @param second
	 * @param formatString
	 * @param arguments
	 */
	public static final void equals(final Object first, final Object second, final String formatString,
	        final Object... arguments) {
		assert ((first == null) && (second == null)) || first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        first, second, formatter.format(formatString, arguments));
	}
	
	/**
	 * @return
	 */
	private static final String getCallerString() {
		Throwable throwable = new Throwable();
		Condition.notNull(throwable);
		
		throwable.fillInStackTrace();
		
		Integer lineNumber = throwable.getStackTrace()[2].getLineNumber();
		String methodName = throwable.getStackTrace()[2].getMethodName();
		String className = throwable.getStackTrace()[2].getClassName();
		
		return "[" + className + "::" + methodName + "#" + lineNumber + "] Assertion violated: ";
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 */
	@Deprecated
	public static final <T> void greater(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) > 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, violation);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param message
	 */
	public static final <T> void greater(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) > 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, message);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param formatString
	 * @param arguments
	 */
	public static final <T> void greater(final Comparable<T> original, final T compareTo, final String formatString,
	        final Object... arguments) {
		assert (original.compareTo(compareTo) > 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 */
	@Deprecated
	public static final <T> void greaterOrEqual(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) >= 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than or equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, violation);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param message
	 */
	public static final <T> void greaterOrEqual(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) >= 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than ot equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, message);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param formatString
	 * @param arguments
	 */
	public static final <T> void greaterOrEqual(final Comparable<T> original, final T compareTo,
	        final String formatString, final Object... arguments) {
		assert (original.compareTo(compareTo) >= 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than or equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param object
	 */
	@Deprecated
	public static final void isNull(final Object object) {
		assert object == null : getCallerString()
		        + formatter.format("Argument MUST be (null). Violated assertion: %s", violation);
	}
	
	/**
	 * @param object
	 * @param message
	 */
	public static final void isNull(final Object object, final String message) {
		assert object == null : getCallerString()
		        + formatter.format("Argument MUST be (null). Violated assertion: %s", message);
	}
	
	/**
	 * @param object
	 * @param formatString
	 * @param arguments
	 */
	public static final void isNull(final Object object, final String formatString, final Object... arguments) {
		assert object == null : getCallerString()
		        + formatter.format("Argument MUST be (null). Violated assertion: %s",
		                formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 */
	@Deprecated
	public static final <T> void less(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) < 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, violation);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param message
	 */
	public static final <T> void less(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) < 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, message);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param formatString
	 * @param arguments
	 */
	public static final <T> void less(final Comparable<T> original, final T compareTo, final String formatString,
	        final Object... arguments) {
		assert (original.compareTo(compareTo) < 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 */
	@Deprecated
	public static final <T> void lessOrEqual(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) <= 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than or equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, violation);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param message
	 */
	public static final <T> void lessOrEqual(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) <= 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than ot equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, message);
	}
	
	/**
	 * @param <T>
	 * @param original
	 * @param compareTo
	 * @param formatString
	 * @param arguments
	 */
	public static final <T> void lessOrEqual(final Comparable<T> original, final T compareTo,
	        final String formatString, final Object... arguments) {
		assert (original.compareTo(compareTo) <= 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than or equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final boolean[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final boolean[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final boolean[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final byte[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final byte[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final byte[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final char[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final char[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final char[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final Collection<?> collection, final int length) {
		assert collection.size() <= length : getCallerString()
		        + formatter.format("Collection exceeds max size of %s (actual size: %s). Violated assertion: %s",
		                length, collection.size(), violation);
	}
	
	/**
	 * @param collection
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final Collection<?> collection, final int length, final String message) {
		assert collection.size() <= length : getCallerString()
		        + formatter.format("Collection exceeds max size of %s (actual size: %s). Violated assertion: %s",
		                length, collection.size(), message);
	}
	
	/**
	 * @param collection
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final Collection<?> collection, final int length, final String formatString,
	        final Object... arguments) {
		assert collection.size() <= length : getCallerString()
		        + formatter.format("Collection exceeds max size of %s (actual size: %s). Violated assertion: %s",
		                length, collection.size(), formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final double[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final double[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final double[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final float[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final float[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final float[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final int[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final int[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final int[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final long[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final long[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final long[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param map
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final Map<?, ?> map, final int length) {
		assert map.size() <= length : getCallerString()
		        + formatter.format("Map exceeds max size of %s (actual size: %s). Violated assertion: %s", length,
		                map.size(), violation);
	}
	
	/**
	 * @param map
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final Map<?, ?> map, final int length, final String message) {
		assert map.size() <= length : getCallerString()
		        + formatter.format("Map exceeds max size of %s (actual size: %s). Violated assertion: %s", length,
		                map.size(), message);
	}
	
	/**
	 * @param map
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final Map<?, ?> map, final int length, final String formatString,
	        final Object... arguments) {
		assert map.size() <= length : getCallerString()
		        + formatter.format("Map exceeds max size of %s (actual size: %s). Violated assertion: %s", length,
		                map.size(), formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final Object[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final Object[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final Object[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void maxSize(final short[] array, final int length) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void maxSize(final short[] array, final int length, final String message) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds max length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void maxSize(final short[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length <= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final boolean[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final boolean[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final boolean[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final byte[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final byte[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final byte[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final char[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final char[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final char[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final Collection<?> collection, final int length) {
		assert collection.size() >= length : getCallerString()
		        + formatter.format("Collection exceeds min size of %s (actual size: %s). Violated assertion: %s",
		                length, collection.size(), violation);
	}
	
	/**
	 * @param collection
	 * @param length
	 * @param message
	 */
	public static final void minSize(final Collection<?> collection, final int length, final String message) {
		assert collection.size() >= length : getCallerString()
		        + formatter.format("Collection exceeds min size of %s (actual size: %s). Violated assertion: %s",
		                length, collection.size(), message);
	}
	
	/**
	 * @param collection
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final Collection<?> collection, final int length, final String formatString,
	        final Object... arguments) {
		assert collection.size() >= length : getCallerString()
		        + formatter.format("Collection exceeds min size of %s (actual size: %s). Violated assertion: %s",
		                length, collection.size(), formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final double[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final double[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final double[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final float[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final float[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final float[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final int[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final int[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final int[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final long[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final long[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final long[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param map
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final Map<?, ?> map, final int length) {
		assert map.size() >= length : getCallerString()
		        + formatter.format("Map exceeds min size of %s (actual size: %s). Violated assertion: %s", length,
		                map.size(), violation);
	}
	
	/**
	 * @param map
	 * @param length
	 * @param message
	 */
	public static final void minSize(final Map<?, ?> map, final int length, final String message) {
		assert map.size() >= length : getCallerString()
		        + formatter.format("Map exceeds min size of %s (actual size: %s). Violated assertion: %s", length,
		                map.size(), message);
	}
	
	/**
	 * @param map
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final Map<?, ?> map, final int length, final String formatString,
	        final Object... arguments) {
		assert map.size() >= length : getCallerString()
		        + formatter.format("Map exceeds min size of %s (actual size: %s). Violated assertion: %s", length,
		                map.size(), formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final Object[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final Object[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final Object[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 * @param length
	 */
	@Deprecated
	public static final void minSize(final short[] array, final int length) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, violation);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param message
	 */
	public static final void minSize(final short[] array, final int length, final String message) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length, message);
	}
	
	/**
	 * @param array
	 * @param length
	 * @param formatString
	 * @param arguments
	 */
	public static final void minSize(final short[] array, final int length, final String formatString,
	        final Object... arguments) {
		assert array.length >= length : getCallerString()
		        + formatter.format("Array exceeds min length of %s. Violated assertion: %s", length,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final boolean[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type boolean[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final boolean[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type boolean[]. Violated assertion: %s",
		                message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final boolean[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type boolean[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final byte[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type byte[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final byte[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type byte[]. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final byte[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type byte[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final char[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type char[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final char[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type char[]. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final char[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type char[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 */
	@Deprecated
	public static final void noneNull(final Collection<?> collection) {
		assert (collection != null) && (CollectionUtils.countMatches(collection, noneNullPredicate) == 0) : getCallerString()
		        + formatter.format("Recursive search found null element. Violated assertion: %s", violation);
	}
	
	/**
	 * @param collection
	 * @param message
	 */
	public static final void noneNull(final Collection<?> collection, final String message) {
		assert (collection != null)
		        && (CollectionUtils.countMatches(collection, noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format("Recursive search found null element. Violated assertion: %s", message);
	}
	
	/**
	 * @param collection
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final Collection<?> collection, final String formatString,
	        final Object... arguments) {
		assert (collection != null)
		        && (CollectionUtils.countMatches(collection, noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format("Recursive search found null element. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final double[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type double[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final double[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type double[]. Violated assertion: %s",
		                message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final double[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type double[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final float[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type float[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final float[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter
		                .format("Recursive search found null element in array of type float[]. Violated assertion: %s",
		                        message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final float[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type float[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final int[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter
		                .format("Recursive search found null element in array of type int[]. Violated assertion: %s",
		                        violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final int[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type int[]. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final int[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type int[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final long[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type long[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final long[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type long[]. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final long[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type long[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param map
	 */
	@Deprecated
	public static final void noneNull(final Map<?, ?> map) {
		assert (map != null) && (CollectionUtils.countMatches(map.values(), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format("Recursive search found null element. Violated assertion: %s", violation);
	}
	
	/**
	 * @param map
	 * @param message
	 */
	public static final void noneNull(final Map<?, ?> map, final String message) {
		assert (map != null)
		        && (CollectionUtils.countMatches(map.values(), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format("Recursive search found null element. Violated assertion: %s", message);
	}
	
	/**
	 * @param map
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final Map<?, ?> map, final String formatString, final Object... arguments) {
		assert (map != null)
		        && (CollectionUtils.countMatches(map.values(), noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format("Recursive search found null element. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final Object[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type Object[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final Object[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type Object[]. Violated assertion: %s",
		                message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final Object[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type Object[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void noneNull(final short[] array) {
		assert (array != null) && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type short[]. Violated assertion: %s",
		                violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void noneNull(final short[] array, final String message) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array), noneNullPredicate.setMessage(message)) == 0) : getCallerString()
		        + formatter
		                .format("Recursive search found null element in array of type short[]. Violated assertion: %s",
		                        message);
	}
	
	/**
	 * @param array
	 * @param formatString
	 * @param arguments
	 */
	public static final void noneNull(final short[] array, final String formatString, final Object... arguments) {
		assert (array != null)
		        && (CollectionUtils.countMatches(Arrays.asList(array),
		                noneNullPredicate.setMessage(formatString, arguments)) == 0) : getCallerString()
		        + formatter.format(
		                "Recursive search found null element in array of type short[]. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 * @param object
	 */
	@Deprecated
	public static final void notContains(final Collection<?> collection, final Object object) {
		assert !collection.contains(object) : getCallerString()
		        + formatter.format("Collection contains object `%s`. Violated assertion: %s", object, violation);
	}
	
	/**
	 * @param collection
	 * @param object
	 * @param message
	 */
	public static final void notContains(final Collection<?> collection, final Object object, final String message) {
		assert !collection.contains(object) : getCallerString()
		        + formatter.format("Collection contains object `%s`. Violated assertion: %s", object, violation);
	}
	
	/**
	 * @param collection
	 * @param object
	 * @param formatString
	 * @param arguments
	 */
	public static final void notContains(final Collection<?> collection, final Object object,
	        final String formatString, final Object... arguments) {
		assert !collection.contains(object) : getCallerString()
		        + formatter.format("Collection contains object `%s`. Violated assertion: %s", object,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 */
	@Deprecated
	public static final void notContainsAll(final Collection<?> collection, final Collection<?> innerCollection) {
		assert !CollectionUtils.containsAny(collection, innerCollection) : getCallerString()
		        + formatter.format("Collection contains all objects in `%s`. Violated assertion: %s", innerCollection,
		                violation);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param message
	 */
	public static final void notContainsAll(final Collection<?> collection, final Collection<?> innerCollection,
	        final String message) {
		assert !CollectionUtils.containsAny(collection, innerCollection) : getCallerString()
		        + formatter.format("Collection contains all objects in `%s`. Violated assertion: %s", innerCollection,
		                message);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param formatString
	 * @param arguments
	 */
	public static final void notContainsAll(final Collection<?> collection, final Collection<?> innerCollection,
	        final String formatString, final Object... arguments) {
		assert !CollectionUtils.containsAny(collection, innerCollection) : getCallerString()
		        + formatter.format("Collection contains all objects in `%s`. Violated assertion: %s", innerCollection,
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 */
	@Deprecated
	public static final void notContainsAny(final Collection<?> collection, final Collection<?> innerCollection) {
		assert !collection.containsAll(innerCollection) : getCallerString()
		        + formatter.format("Collection contains any of the objects in `%s`. Violated assertion: %s",
		                innerCollection, violation);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param message
	 */
	public static final void notContainsAny(final Collection<?> collection, final Collection<?> innerCollection,
	        final String message) {
		assert !collection.containsAll(innerCollection) : getCallerString()
		        + formatter.format("Collection contains any of the objects in `%s`. Violated assertion: %s",
		                innerCollection, message);
	}
	
	/**
	 * @param collection
	 * @param innerCollection
	 * @param formatString
	 * @param arguments
	 */
	public static final void notContainsAny(final Collection<?> collection, final Collection<?> innerCollection,
	        final String formatString, final Object... arguments) {
		assert !collection.containsAll(innerCollection) : getCallerString()
		        + formatter.format("Collection contains any of the objects in `%s`. Violated assertion: %s",
		                innerCollection, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final boolean[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final boolean[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final boolean[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final byte[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final byte[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final byte[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final char[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final char[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final char[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param collection
	 */
	@Deprecated
	public static final void notEmpty(final Collection<?> collection) {
		assert !collection.isEmpty() : getCallerString()
		        + formatter.format("Collection is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param collection
	 * @param message
	 */
	public static final void notEmpty(final Collection<?> collection, final String message) {
		assert !collection.isEmpty() : getCallerString()
		        + formatter.format("Collection is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param collection
	 * @param formatString
	 * @param arguments
	 */
	public static final void notEmpty(final Collection<?> collection, final String formatString,
	        final Object... arguments) {
		assert !collection.isEmpty() : getCallerString()
		        + formatter.format("Collection is empty. Violated assertion: %s",
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final double[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final double[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final double[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final float[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final float[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final float[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final int[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final int[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final int[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final long[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final long[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final long[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final Object[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final Object[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final Object[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param array
	 */
	@Deprecated
	public static final void notEmpty(final short[] array) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", violation);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final short[] array, final String message) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", message);
	}
	
	/**
	 * @param array
	 * @param message
	 */
	public static final void notEmpty(final short[] array, final String formatString, final Object... arguments) {
		assert !ArrayUtils.isEmpty(array) : getCallerString()
		        + formatter.format("Array is empty. Violated assertion: %s", formatter.format(formatString, arguments));
	}
	
	/**
	 * @param first
	 * @param second
	 */
	@Deprecated
	public static final void notEquals(final Object first, final Object second) {
		assert !first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be NOT equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        first, second, violation);
	}
	
	/**
	 * @param first
	 * @param second
	 * @param message
	 */
	public static final void notEquals(final Object first, final Object second, final String message) {
		assert !first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be NOT equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        first, second, message);
	}
	
	/**
	 * @param first
	 * @param second
	 * @param formatString
	 * @param arguments
	 */
	public static final void notEquals(final Object first, final Object second, final String formatString,
	        final Object... arguments) {
		assert !first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be NOT equal to the second argument, but got first `%s` vs second `%s`. Violated assertion: %s",
		                        first, second, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param object
	 */
	@Deprecated
	public static final void notNull(final Object object) {
		assert object != null : getCallerString()
		        + formatter.format("Argument should not be (null). Violated assertion: %s", violation);
	}
	
	/**
	 * @param object
	 * @param message
	 */
	public static final void notNull(final Object object, final String message) {
		assert object != null : getCallerString()
		        + formatter.format("Argument should not be (null). Violated assertion: %s", message);
		
	}
	
	/**
	 * @param object
	 * @param formatString
	 * @param arguments
	 */
	public static final void notNull(final Object object, final String formatString, final Object... arguments) {
		assert object != null : getCallerString()
		        + formatter.format("Argument should not be (null). Violated assertion: %s",
		                formatter.format(formatString, arguments).toString());
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final boolean[] firstArray, final boolean[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final boolean[] firstArray, final boolean[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final boolean[] firstArray, final boolean[] secondArray,
	        final String formatString, final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final byte[] firstArray, final byte[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final byte[] firstArray, final byte[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final byte[] firstArray, final byte[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final char[] firstArray, final char[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final char[] firstArray, final char[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final char[] firstArray, final char[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstCollection
	 * @param secondCollection
	 */
	@Deprecated
	public static final void sameSize(final Collection<?> firstCollection, final Collection<?> secondCollection) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Collections do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection), violation);
	}
	
	/**
	 * @param firstCollection
	 * @param secondCollection
	 * @param message
	 */
	public static final void sameSize(final Collection<?> firstCollection, final Collection<?> secondCollection,
	        final String message) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Collections do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection), message);
	}
	
	/**
	 * @param firstCollection
	 * @param secondCollection
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final Collection<?> firstCollection, final Collection<?> secondCollection,
	        final String formatString, final Object... arguments) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Collections do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection),
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final double[] firstArray, final double[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final double[] firstArray, final double[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final double[] firstArray, final double[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstEnumeration
	 * @param secondEnumeration
	 */
	@Deprecated
	public static final void sameSize(final Enumeration<?> firstCollection, final Enumeration<?> secondCollection) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Enumerations do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection), violation);
	}
	
	/**
	 * @param firstEnumeration
	 * @param secondEnumeration
	 * @param message
	 */
	public static final void sameSize(final Enumeration<?> firstCollection, final Enumeration<?> secondCollection,
	        final String message) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Enumerations do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection), message);
	}
	
	/**
	 * @param firstEnumeration
	 * @param secondEnumeration
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final Enumeration<?> firstCollection, final Enumeration<?> secondCollection,
	        final String formatString, final Object... arguments) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Enumerations do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection),
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final float[] firstArray, final float[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final float[] firstArray, final float[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final float[] firstArray, final float[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final int[] firstArray, final int[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final int[] firstArray, final int[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final int[] firstArray, final int[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstIterator
	 * @param secondIterator
	 */
	@Deprecated
	public static final void sameSize(final Iterator<?> firstCollection, final Iterator<?> secondCollection) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Iterators do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection), violation);
	}
	
	/**
	 * @param firstIterator
	 * @param secondIterator
	 * @param message
	 */
	public static final void sameSize(final Iterator<?> firstCollection, final Iterator<?> secondCollection,
	        final String message) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Iterators do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection), message);
	}
	
	/**
	 * @param firstIterator
	 * @param secondIterator
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final Iterator<?> firstCollection, final Iterator<?> secondCollection,
	        final String formatString, final Object... arguments) {
		assert CollectionUtils.size(firstCollection) == CollectionUtils.size(secondCollection) : getCallerString()
		        + formatter.format("Iterators do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstCollection), CollectionUtils.size(secondCollection),
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final long[] firstArray, final long[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final long[] firstArray, final long[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final long[] firstArray, final long[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstMap
	 * @param secondMap
	 */
	@Deprecated
	public static final void sameSize(final Map<?, ?> firstMap, final Map<?, ?> secondMap) {
		assert CollectionUtils.size(firstMap) == CollectionUtils.size(secondMap) : getCallerString()
		        + formatter.format("Maps do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstMap), CollectionUtils.size(secondMap), violation);
	}
	
	/**
	 * @param firstMap
	 * @param secondMap
	 * @param message
	 */
	public static final void sameSize(final Map<?, ?> firstMap, final Map<?, ?> secondMap, final String message) {
		assert CollectionUtils.size(firstMap) == CollectionUtils.size(secondMap) : getCallerString()
		        + formatter.format("Maps do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstMap), CollectionUtils.size(secondMap), message);
	}
	
	/**
	 * @param firstMap
	 * @param secondMap
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final Map<?, ?> firstMap, final Map<?, ?> secondMap, final String formatString,
	        final Object... arguments) {
		assert CollectionUtils.size(firstMap) == CollectionUtils.size(secondMap) : getCallerString()
		        + formatter.format("Maps do not have same size (`%s` vs. `%s`). Violated assertion: %s",
		                CollectionUtils.size(firstMap), CollectionUtils.size(secondMap),
		                formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final Object[] firstArray, final Object[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final Object[] firstArray, final Object[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final Object[] firstArray, final Object[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 */
	@Deprecated
	public static final void sameSize(final short[] firstArray, final short[] secondArray) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, violation);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param message
	 */
	public static final void sameSize(final short[] firstArray, final short[] secondArray, final String message) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, message);
	}
	
	/**
	 * @param firstArray
	 * @param secondArray
	 * @param formatString
	 * @param arguments
	 */
	public static final void sameSize(final short[] firstArray, final short[] secondArray, final String formatString,
	        final Object... arguments) {
		assert ArrayUtils.isSameLength(firstArray, secondArray) : getCallerString()
		        + formatter.format("Arrays differ in length (`%s` vs. `%s`). Violated assertion: %s",
		                firstArray.length, secondArray.length, formatter.format(formatString, arguments));
	}
}

package de.unisaarland.cs.st.reposuite.utils;

import java.util.Formatter;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class Condition {
	
	private static final String    violation = "(no requirement specification given).";
	private static final Formatter formatter = new Formatter();
	
	/**
	 * @param condition
	 */
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
	 * @param first
	 * @param second
	 */
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
	 * @param first
	 * @param second
	 */
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
	
}

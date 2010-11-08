package de.unisaarland.cs.st.reposuite.utils;

import java.util.Formatter;

public class Condition {
	
	private static String    violation = "no error message specified.";
	private static Formatter formatter = new Formatter();
	
	public static void check(final boolean condition) {
		assert condition : getCallerString()
		        + formatter.format("Condition evaluated to false. Error message: %s", violation);
	}
	
	public static void check(final boolean condition, final String message) {
		assert condition : getCallerString()
		        + formatter.format("Condition evaluated to false. Error message: %s", message);
	}
	
	public static void check(final boolean condition, final String formatString, final Object... arguments) {
		assert condition : getCallerString()
		        + formatter.format("Condition evaluated to false. Error message: %s",
		                formatter.format(formatString, arguments).toString());
	}
	
	public static <T> void lessOrEqual(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) <= 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than or equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, violation);
	}
	
	public static <T> void lessOrEqual(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) <= 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than ot equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, message);
	}
	
	public static <T> void lessOrEqual(final Comparable<T> original, final T compareTo, final String formatString,
	        final Object... arguments) {
		assert (original.compareTo(compareTo) <= 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than or equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	public static void notEquals(final Object first, final Object second) {
		assert !first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be NOT equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        first, second, violation);
	}
	
	public static void notEquals(final Object first, final Object second, final String message) {
		assert !first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be NOT equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        first, second, message);
	}
	
	public static void notEquals(final Object first, final Object second, final String formatString,
	        final Object... arguments) {
		assert !first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be NOT equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        first, second, formatter.format(formatString, arguments));
	}
	
	public static void notNull(final Object object) {
		assert object != null : getCallerString()
		        + formatter.format("Argument should not be (null). Error message: %s", violation);
	}
	
	public static void notNull(final Object object, final String message) {
		assert object != null : getCallerString()
		        + formatter.format("Argument should not be (null). Error message: %s", message);
		
	}
	
	public static void notNull(final Object object, final String formatString, final Object... arguments) {
		assert object != null : getCallerString()
		        + formatter.format("Argument should not be (null). Error message: %s",
		                formatter.format(formatString, arguments).toString());
	}
	
	public static void isNull(final Object object) {
		assert object == null : getCallerString()
		        + formatter.format("Argument MUST be (null). Error message: %s", violation);
	}
	
	public static void isNull(final Object object, final String message) {
		assert object == null : getCallerString()
		        + formatter.format("Argument MUST be (null). Error message: %s", message);
	}
	
	public static void isNull(final Object object, final String formatString, final Object... arguments) {
		assert object == null : getCallerString()
		        + formatter.format("Argument MUST be (null). Error message: %s",
		                formatter.format(formatString, arguments).toString());
	}
	
	public static void equals(final Object first, final Object second) {
		assert first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        first, second, violation);
	}
	
	public static void equals(final Object first, final Object second, final String message) {
		assert first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        first, second, message);
	}
	
	public static void equals(final Object first, final Object second, final String formatString,
	        final Object... arguments) {
		assert first.equals(second) : getCallerString()
		        + formatter
		                .format("First argument should be equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        first, second, formatter.format(formatString, arguments));
	}
	
	private static String getCallerString() {
		Throwable throwable = new Throwable();
		Condition.notNull(throwable);
		
		throwable.fillInStackTrace();
		
		Integer lineNumber = throwable.getStackTrace()[2].getLineNumber();
		String methodName = throwable.getStackTrace()[2].getMethodName();
		String className = throwable.getStackTrace()[2].getClassName();
		
		return "[" + className + "::" + methodName + "#" + lineNumber + "] Assertion violated: ";
	}
	
	public static <T> void greater(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) > 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, violation);
	}
	
	public static <T> void greater(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) > 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, message);
	}
	
	public static <T> void greater(final Comparable<T> original, final T compareTo, final String formatString,
	        final Object... arguments) {
		assert (original.compareTo(compareTo) > 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	public static <T> void greaterOrEqual(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) >= 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than or equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, violation);
	}
	
	public static <T> void greaterOrEqual(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) >= 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than ot equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, message);
	}
	
	public static <T> void greaterOrEqual(final Comparable<T> original, final T compareTo, final String formatString,
	        final Object... arguments) {
		assert (original.compareTo(compareTo) >= 0) : getCallerString()
		        + formatter
		                .format("First argument should be greater than or equal to the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
	public static <T> void less(final Comparable<T> original, final T compareTo) {
		assert (original.compareTo(compareTo) < 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, violation);
	}
	
	public static <T> void less(final Comparable<T> original, final T compareTo, final String message) {
		assert (original.compareTo(compareTo) < 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, message);
	}
	
	public static <T> void less(final Comparable<T> original, final T compareTo, final String formatString,
	        final Object... arguments) {
		assert (original.compareTo(compareTo) < 0) : getCallerString()
		        + formatter
		                .format("First argument should be less than the second argument, but got first `%s` vs second `%s`. Error message: %s",
		                        original, compareTo, formatter.format(formatString, arguments).toString());
	}
	
}

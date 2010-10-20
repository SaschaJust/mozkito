package de.unisaarland.cs.st.reposuite.utils;

import org.slf4j.LoggerFactory;

/**
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Logger {
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with debug log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void debug(String message) {
		Tuple<org.slf4j.Logger, String> ret = tags();
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		ret.getFirst().debug("[" + ret.getSecond() + "] " + message);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with error log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void error(String message) {
		Tuple<org.slf4j.Logger, String> ret = tags();
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		ret.getFirst().error("[" + ret.getSecond() + "] " + message);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with info log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void info(String message) {
		Tuple<org.slf4j.Logger, String> ret = tags();
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		ret.getFirst().info("[" + ret.getSecond() + "] " + message);
	}
	
	/**
	 * 
	 * @return a tuple containing the corresponding logger to the calling
	 *         instance and the exact calling location (class, method, line
	 *         number). Both entries are guaranteed to not be null
	 */
	private static Tuple<org.slf4j.Logger, String> tags() {
		Throwable throwable = new Throwable();
		assert (throwable != null);
		
		throwable.fillInStackTrace();
		assert (throwable.getStackTrace().length > 2);
		
		Integer lineNumber = throwable.getStackTrace()[2].getLineNumber();
		String methodName = throwable.getStackTrace()[2].getMethodName();
		String className = throwable.getStackTrace()[2].getClassName();
		
		org.slf4j.Logger logger = LoggerFactory.getLogger(className);
		
		assert (lineNumber != null);
		assert (lineNumber > 0);
		assert (methodName != null);
		assert (className != null);
		assert (logger != null);
		
		return new Tuple<org.slf4j.Logger, String>(logger, className + "::" + methodName + "#" + lineNumber);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with trace log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void trace(String message) {
		Tuple<org.slf4j.Logger, String> ret = tags();
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		ret.getFirst().trace("[" + ret.getSecond() + "] " + message);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with warn log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void warn(String message) {
		Tuple<org.slf4j.Logger, String> ret = tags();
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		ret.getFirst().warn("[" + ret.getSecond() + "] " + message);
	}
}

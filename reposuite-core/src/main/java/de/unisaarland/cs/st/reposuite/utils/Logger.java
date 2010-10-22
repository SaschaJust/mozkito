package de.unisaarland.cs.st.reposuite.utils;

import org.slf4j.LoggerFactory;

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * Logger class to instrument SLF4J
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
		debug(message, null, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with debug log level
	 * 
	 * @category external loggers
	 * 
	 * @param message
	 *            the string to be logged
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	public static void debug(String message, int offset) {
		debug(message, null, null, offset);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * argument using the format string with debug log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj
	 *            the object that shall be logged
	 */
	public static void debug(String fmt, Object obj) {
		debug(fmt, new Object[] { obj }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * arguments using the format string with debug log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj1
	 *            an object that shall be logged
	 * @param obj2
	 *            an object that shall be logged
	 */
	public static void debug(String fmt, Object obj1, Object obj2) {
		debug(fmt, new Object[] { obj1, obj2 }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with debug log level
	 * 
	 * @param message
	 *            the string to be logged or format string if arguments are not
	 *            null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding
	 *            format string
	 * @param t
	 *            exception to be logged along the error message supplied
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	private static void debug(String message, Object[] arguments, Throwable t, int offset) {
		assert (((arguments != null) && (arguments.length <= 2) && (arguments.length > 0)) || (arguments == null));
		assert (((arguments != null) && (t == null)) || ((t != null) && (arguments == null)) || ((arguments == null) && (t == null)));
		assert (offset > 2);
		assert (RepoSuiteSettings.logDebug());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		
		if (arguments != null) {
			if (arguments.length == 2) {
				ret.getFirst().debug("[" + ret.getSecond() + "] " + message, arguments[0], arguments[1]);
			} else if (arguments.length == 1) {
				ret.getFirst().debug("[" + ret.getSecond() + "] " + message, arguments[0]);
			}
			return;
		} else if (t != null) {
			ret.getFirst().debug("[" + ret.getSecond() + "] " + message, t);
		} else {
			ret.getFirst().debug("[" + ret.getSecond() + "] " + message);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message and the exception string with debug log level
	 * 
	 * @param message
	 *            the format string to be used
	 * @param t
	 *            the exception that shall be logged
	 */
	public static void debug(String message, Throwable t) {
		debug(message, null, t, 3);
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
		error(message, null, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with error log level
	 * 
	 * @category external loggers
	 * 
	 * @param message
	 *            the string to be logged
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	public static void error(String message, int offset) {
		error(message, null, null, offset);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * argument using the format string with error log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj
	 *            the object that shall be logged
	 */
	public static void error(String fmt, Object obj) {
		error(fmt, new Object[] { obj }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * arguments using the format string with error log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj1
	 *            an object that shall be logged
	 * @param obj2
	 *            an object that shall be logged
	 */
	public static void error(String fmt, Object obj1, Object obj2) {
		error(fmt, new Object[] { obj1, obj2 }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with error log level
	 * 
	 * @param message
	 *            the string to be logged or format string if arguments are not
	 *            null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding
	 *            format string
	 * @param t
	 *            exception to be logged along the error message supplied
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	private static void error(String message, Object[] arguments, Throwable t, int offset) {
		assert (((arguments != null) && (arguments.length <= 2) && (arguments.length > 0)) || (arguments == null));
		assert (((arguments != null) && (t == null)) || ((t != null) && (arguments == null)) || ((arguments == null) && (t == null)));
		assert (offset > 2);
		assert (RepoSuiteSettings.logError());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		
		if (arguments != null) {
			if (arguments.length == 2) {
				ret.getFirst().error("[" + ret.getSecond() + "] " + message, arguments[0], arguments[1]);
			} else if (arguments.length == 1) {
				ret.getFirst().error("[" + ret.getSecond() + "] " + message, arguments[0]);
			}
			return;
		} else if (t != null) {
			ret.getFirst().error("[" + ret.getSecond() + "] " + message, t);
		} else {
			ret.getFirst().error("[" + ret.getSecond() + "] " + message);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message and the exception string with error log level
	 * 
	 * @param message
	 *            the format string to be used
	 * @param t
	 *            the exception that shall be logged
	 */
	public static void error(String message, Throwable t) {
		error(message, null, t, 3);
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
		info(message, null, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with info log level
	 * 
	 * @category external loggers
	 * 
	 * @param message
	 *            the string to be logged
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	public static void info(String message, int offset) {
		info(message, null, null, offset);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * argument using the format string with info log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj
	 *            the object that shall be logged
	 */
	public static void info(String fmt, Object obj) {
		info(fmt, new Object[] { obj }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * arguments using the format string with info log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj1
	 *            an object that shall be logged
	 * @param obj2
	 *            an object that shall be logged
	 */
	public static void info(String fmt, Object obj1, Object obj2) {
		info(fmt, new Object[] { obj1, obj2 }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with info log level
	 * 
	 * @param message
	 *            the string to be logged or format string if arguments are not
	 *            null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding
	 *            format string
	 * @param t
	 *            exception to be logged along the error message supplied
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	private static void info(String message, Object[] arguments, Throwable t, int offset) {
		assert (((arguments != null) && (arguments.length <= 2) && (arguments.length > 0)) || (arguments == null));
		assert (((arguments != null) && (t == null)) || ((t != null) && (arguments == null)) || ((arguments == null) && (t == null)));
		assert (offset > 2);
		assert (RepoSuiteSettings.logInfo());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		
		if (arguments != null) {
			if (arguments.length == 2) {
				ret.getFirst().info("[" + ret.getSecond() + "] " + message, arguments[0], arguments[1]);
			} else if (arguments.length == 1) {
				ret.getFirst().info("[" + ret.getSecond() + "] " + message, arguments[0]);
			}
			return;
		} else if (t != null) {
			ret.getFirst().info("[" + ret.getSecond() + "] " + message, t);
		} else {
			ret.getFirst().info("[" + ret.getSecond() + "] " + message);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message and the exception string with info log level
	 * 
	 * @param message
	 *            the format string to be used
	 * @param t
	 *            the exception that shall be logged
	 */
	public static void info(String message, Throwable t) {
		info(message, null, t, 3);
	}
	
	/**
	 * 
	 * @return a tuple containing the corresponding logger to the calling
	 *         instance and the exact calling location (class, method, line
	 *         number). Both entries are guaranteed to not be null
	 */
	private static Tuple<org.slf4j.Logger, String> tags(int offset) {
		assert (offset > 1);
		Throwable throwable = new Throwable();
		assert (throwable != null);
		
		throwable.fillInStackTrace();
		assert (throwable.getStackTrace().length > offset);
		
		Integer lineNumber = throwable.getStackTrace()[offset].getLineNumber();
		String methodName = throwable.getStackTrace()[offset].getMethodName();
		String className = throwable.getStackTrace()[offset].getClassName();
		
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
		trace(message, null, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with trace log level
	 * 
	 * @category external loggers
	 * 
	 * @param message
	 *            the string to be logged
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	public static void trace(String message, int offset) {
		trace(message, null, null, offset);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * argument using the format string with trace log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj
	 *            the object that shall be logged
	 */
	public static void trace(String fmt, Object obj) {
		trace(fmt, new Object[] { obj }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * arguments using the format string with trace log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj1
	 *            an object that shall be logged
	 * @param obj2
	 *            an object that shall be logged
	 */
	public static void trace(String fmt, Object obj1, Object obj2) {
		trace(fmt, new Object[] { obj1, obj2 }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with trace log level
	 * 
	 * @param message
	 *            the string to be logged or format string if arguments are not
	 *            null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding
	 *            format string
	 * @param t
	 *            exception to be logged along the error message supplied
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	private static void trace(String message, Object[] arguments, Throwable t, int offset) {
		assert (((arguments != null) && (arguments.length <= 2) && (arguments.length > 0)) || (arguments == null));
		assert (((arguments != null) && (t == null)) || ((t != null) && (arguments == null)) || ((arguments == null) && (t == null)));
		assert (offset > 2);
		assert (RepoSuiteSettings.logTrace());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		
		if (arguments != null) {
			if (arguments.length == 2) {
				ret.getFirst().trace("[" + ret.getSecond() + "] " + message, arguments[0], arguments[1]);
			} else if (arguments.length == 1) {
				ret.getFirst().trace("[" + ret.getSecond() + "] " + message, arguments[0]);
			}
			return;
		} else if (t != null) {
			ret.getFirst().trace("[" + ret.getSecond() + "] " + message, t);
		} else {
			ret.getFirst().trace("[" + ret.getSecond() + "] " + message);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message and the exception string with trace log level
	 * 
	 * @param message
	 *            the format string to be used
	 * @param t
	 *            the exception that shall be logged
	 */
	public static void trace(String message, Throwable t) {
		trace(message, null, t, 3);
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
		warn(message, null, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with warn log level
	 * 
	 * @category external loggers
	 * 
	 * @param message
	 *            the string to be logged
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	public static void warn(String message, int offset) {
		warn(message, null, null, offset);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * argument using the format string with warn log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj
	 *            the object that shall be logged
	 */
	public static void warn(String fmt, Object obj) {
		warn(fmt, new Object[] { obj }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * arguments using the format string with warn log level
	 * 
	 * @param fmt
	 *            the format string to be used
	 * @param obj1
	 *            an object that shall be logged
	 * @param obj2
	 *            an object that shall be logged
	 */
	public static void warn(String fmt, Object obj1, Object obj2) {
		warn(fmt, new Object[] { obj1, obj2 }, null, 3);
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with warn log level
	 * 
	 * @param message
	 *            the string to be logged or format string if arguments are not
	 *            null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding
	 *            format string
	 * @param t
	 *            exception to be logged along the error message supplied
	 * @param offset
	 *            determines the offset in the stacktrace
	 */
	private static void warn(String message, Object[] arguments, Throwable t, int offset) {
		assert (((arguments != null) && (arguments.length <= 2) && (arguments.length > 0)) || (arguments == null));
		assert (((arguments != null) && (t == null)) || ((t != null) && (arguments == null)) || ((arguments == null) && (t == null)));
		assert (offset > 2);
		assert (RepoSuiteSettings.logWarn());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		assert (ret.getFirst() != null);
		assert (ret.getSecond() != null);
		
		if (arguments != null) {
			if (arguments.length == 2) {
				ret.getFirst().warn("[" + ret.getSecond() + "] " + message, arguments[0], arguments[1]);
			} else if (arguments.length == 1) {
				ret.getFirst().warn("[" + ret.getSecond() + "] " + message, arguments[0]);
			}
			return;
		} else if (t != null) {
			ret.getFirst().warn("[" + ret.getSecond() + "] " + message, t);
		} else {
			ret.getFirst().warn("[" + ret.getSecond() + "] " + message);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message and the exception string with warn log level
	 * 
	 * @param message
	 *            the format string to be used
	 * @param t
	 *            the exception that shall be logged
	 */
	public static void warn(String message, Throwable t) {
		warn(message, null, t, 3);
	}
}

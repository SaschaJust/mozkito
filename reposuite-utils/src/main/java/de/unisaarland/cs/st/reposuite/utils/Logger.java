package de.unisaarland.cs.st.reposuite.utils;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

/**
 * Logger class to instrument SLF4J
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Logger {
	
	private static LogLevel logLevel = LogLevel.WARN;
	private static boolean  debug    = false;
	
	static {
		setLogLevel(LogLevel.valueOf(System.getProperty("log.level", "WARN").toUpperCase()));
		// FIXME what if we do not use log4j?
		org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.toLevel(getLogLevel().toString()));
		if ((System.getProperty("debug") != null) || (logLevel.compareTo(LogLevel.DEBUG) >= 0)) {
			Logger.debug = true;
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with debug log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void debug(final String message) {
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
	public static void debug(final String message,
	                         final int offset) {
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
	public static void debug(final String fmt,
	                         final Object obj) {
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
	public static void debug(final String fmt,
	                         final Object obj1,
	                         final Object obj2) {
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
	private static void debug(final String message,
	                          final Object[] arguments,
	                          final Throwable t,
	                          final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		        || (arguments == null));
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
		        || ((arguments == null) && (t == null)));
		Condition.greater(offset, 2);
		Condition.check(logDebug());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		Condition.notNull(ret.getFirst());
		Condition.notNull(ret.getSecond());
		
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
	public static void debug(final String message,
	                         final Throwable t) {
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
	public static void error(final String message) {
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
	public static void error(final String message,
	                         final int offset) {
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
	public static void error(final String fmt,
	                         final Object obj) {
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
	public static void error(final String fmt,
	                         final Object obj1,
	                         final Object obj2) {
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
	private static void error(final String message,
	                          final Object[] arguments,
	                          final Throwable t,
	                          final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		        || (arguments == null));
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
		        || ((arguments == null) && (t == null)));
		Condition.greater(offset, 2);
		Condition.check(logError());
		
		if (debug) {
			Tuple<org.slf4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst());
			Condition.notNull(ret.getSecond());
			
			if (arguments != null) {
				if (arguments.length >= 2) {
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
		} else {
			org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);
			
			if (arguments != null) {
				if (arguments.length >= 2) {
					
					logger.error(message, arguments[0], arguments[1]);
				} else if (arguments.length == 1) {
					logger.error(message, arguments[0]);
					
				}
				return;
			} else if (t != null) {
				logger.error(message, t);
			} else {
				logger.error(message);
			}
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
	public static void error(final String message,
	                         final Throwable t) {
		error(message, null, t, 3);
	}
	
	/**
	 * @return
	 */
	public static Enum<LogLevel> getLogLevel() {
		return logLevel;
	}
	
	/**
	 * @param logLevel
	 */
	public static void increaseLogLevel(final LogLevel logLevel) {
		if (Logger.logLevel.compareTo(logLevel) < 0) {
			setLogLevel(logLevel);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the
	 * classname::methodname#linenumber tag and uses this information to log the
	 * message with info log level
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void info(final String message) {
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
	public static void info(final String message,
	                        final int offset) {
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
	public static void info(final String fmt,
	                        final Object obj) {
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
	public static void info(final String fmt,
	                        final Object obj1,
	                        final Object obj2) {
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
	private static void info(final String message,
	                         final Object[] arguments,
	                         final Throwable t,
	                         final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		        || (arguments == null));
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
		        || ((arguments == null) && (t == null)));
		Condition.greater(offset, 2);
		Condition.check(logInfo());
		
		if (debug) {
			Tuple<org.slf4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst());
			Condition.notNull(ret.getSecond());
			
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
		} else {
			org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);
			
			if (arguments != null) {
				if (arguments.length >= 2) {
					
					logger.info(message, arguments[0], arguments[1]);
				} else if (arguments.length == 1) {
					logger.info(message, arguments[0]);
					
				}
				return;
			} else if (t != null) {
				logger.info(message, t);
			} else {
				logger.info(message);
			}
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
	public static void info(final String message,
	                        final Throwable t) {
		info(message, null, t, 3);
	}
	
	public static boolean logDebug() {
		return logLevel.compareTo(LogLevel.DEBUG) >= 0;
	}
	
	public static boolean logError() {
		return logLevel.compareTo(LogLevel.ERROR) >= 0;
	}
	
	public static boolean logInfo() {
		return logLevel.compareTo(LogLevel.INFO) >= 0;
	}
	
	public static boolean logTrace() {
		return logLevel.compareTo(LogLevel.TRACE) >= 0;
	}
	
	public static boolean logWarn() {
		return logLevel.compareTo(LogLevel.WARN) >= 0;
	}
	
	/**
	 * Reads the logger specific configurations from the specified file.
	 * 
	 * @param fileName
	 *            full path to the configuration file
	 */
	public static void readConfiguration(final String fileName) {
		PropertyConfigurator.configure(fileName);
	}
	
	public static void setLogLevel(final LogLevel logLevel) {
		Logger.logLevel = logLevel;
		if (logLevel.compareTo(LogLevel.DEBUG) >= 0) {
			Logger.debug("Setting log level to " + logLevel.name());
		}
		if ((System.getProperty("debug") != null) || (logLevel.compareTo(LogLevel.DEBUG) >= 0)) {
			Logger.debug = true;
		}
	}
	
	/**
	 * 
	 * @return a tuple containing the corresponding logger to the calling
	 *         instance and the exact calling location (class, method, line
	 *         number). Both entries are guaranteed to not be null
	 */
	private static Tuple<org.slf4j.Logger, String> tags(final int offset) {
		Condition.greater(offset, 1);
		Throwable throwable = new Throwable();
		Condition.notNull(throwable);
		
		throwable.fillInStackTrace();
		Condition.greater(throwable.getStackTrace().length, offset);
		
		Integer lineNumber = throwable.getStackTrace()[offset].getLineNumber();
		String methodName = throwable.getStackTrace()[offset].getMethodName();
		String className = throwable.getStackTrace()[offset].getClassName();
		
		org.slf4j.Logger logger = LoggerFactory.getLogger(className);
		
		Condition.notNull(lineNumber);
		Condition.greater(lineNumber, 0);
		Condition.notNull(methodName);
		Condition.notNull(className);
		Condition.notNull(logger);
		
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
	public static void trace(final String message) {
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
	public static void trace(final String message,
	                         final int offset) {
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
	public static void trace(final String fmt,
	                         final Object obj) {
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
	public static void trace(final String fmt,
	                         final Object obj1,
	                         final Object obj2) {
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
	private static void trace(final String message,
	                          final Object[] arguments,
	                          final Throwable t,
	                          final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		        || (arguments == null));
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
		        || ((arguments == null) && (t == null)));
		Condition.greater(offset, 2);
		Condition.check(logTrace());
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		Condition.notNull(ret.getFirst());
		Condition.notNull(ret.getSecond());
		
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
	public static void trace(final String message,
	                         final Throwable t) {
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
	public static void warn(final String message) {
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
	public static void warn(final String message,
	                        final int offset) {
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
	public static void warn(final String fmt,
	                        final Object obj) {
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
	public static void warn(final String fmt,
	                        final Object obj1,
	                        final Object obj2) {
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
	private static void warn(final String message,
	                         final Object[] arguments,
	                         final Throwable t,
	                         final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		        || (arguments == null));
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
		        || ((arguments == null) && (t == null)));
		Condition.greater(offset, 2);
		Condition.check(logWarn());
		
		if (debug) {
			Tuple<org.slf4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst());
			Condition.notNull(ret.getSecond());
			
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
		} else {
			org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);
			
			if (arguments != null) {
				if (arguments.length >= 2) {
					
					logger.warn(message, arguments[0], arguments[1]);
				} else if (arguments.length == 1) {
					logger.warn(message, arguments[0]);
					
				}
				return;
			} else if (t != null) {
				logger.warn(message, t);
			} else {
				logger.warn(message);
			}
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
	public static void warn(final String message,
	                        final Throwable t) {
		warn(message, null, t, 3);
	}
	
	/**
	 * @return the simple class name of the instance
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

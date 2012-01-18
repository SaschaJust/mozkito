package net.ownhero.dev.kisa;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import net.ownhero.dev.kanuni.annotations.compare.GreaterInt;
import net.ownhero.dev.kanuni.conditions.CompareCondition;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.varia.LevelRangeFilter;
import org.slf4j.LoggerFactory;

/**
 * Logger class to instrument SLF4J
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Logger {
	
	public static class LoggerOutputStream extends ByteArrayOutputStream {
		
		private final CountDownLatch latch = new CountDownLatch(1);
		
		/**
		 * 
		 */
		public LoggerOutputStream() {
			super();
		}
		
		/**
		 * @param size
		 */
		public LoggerOutputStream(final int size) {
			super(size);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.ByteArrayOutputStream#close()
		 */
		@Override
		public void close() throws IOException {
			super.close();
			latch.countDown();
		}
		
		/**
		 * @return
		 */
		public CountDownLatch latch() {
			return latch;
		}
	}
	private static class Tuple<K, M> {
		
		private final K first;
		private final M second;
		
		public Tuple(final K f, final M s) {
			this.first = f;
			this.second = s;
		}
		
		/**
		 * @return the first
		 */
		public K getFirst() {
			return this.first;
		}
		
		/**
		 * @return the second
		 */
		public M getSecond() {
			return this.second;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Tuple [first=" + this.first + ", second=" + this.second + "]";
		}
	}
	
	
	private static Set<String> registeredAppenders = new HashSet<String>();
	
	private static LogLevel logLevel = LogLevel.WARN;
	
	private static boolean  debug    = false;
	
	private static LogLevel              maxLevel       = null;
	private static Layout                layout         = new EnhancedPatternLayout("%d (%8r) [%t] %-5p %m%n");
	
	static {
		// CONSOLE APPENDER
		WriterAppender consoleAppender = new WriterAppender(layout, System.err);
		consoleAppender.setLayout(layout);
		LevelRangeFilter consoleLevelRangeFilter = new org.apache.log4j.varia.LevelRangeFilter();
		// set levels and minLevel
		LogLevel consoleLevel = LogLevel.valueOf(System.getProperty("log.console.level", "INFO").toUpperCase());
		consoleLevelRangeFilter.setLevelMin(Level.toLevel(consoleLevel.toString()));
		if ((maxLevel == null) || (consoleLevel.compareTo(maxLevel) > 0)) {
			maxLevel = consoleLevel;
		}
		consoleAppender.addFilter(consoleLevelRangeFilter);
		consoleAppender.activateOptions();
		org.apache.log4j.Logger.getRootLogger().addAppender(consoleAppender);
		readConfiguration();
		
	}
	
	/**
	 * @return
	 */
	public static OutputStream debug() {
		final LoggerOutputStream debugStream = new LoggerOutputStream();
		final OutputStream stream = new BufferedOutputStream(debugStream);
		
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				try {
					debugStream.latch.await();
				} catch (InterruptedException e) {
					if (Logger.logError()) {
						Logger.error(e.getMessage(), e);
					}
				}
				debug(debugStream.toString(), null, null, 3);
			};
		};
		
		thread.start();
		
		return stream;
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
	public static void debug(final String message, final int offset) {
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
	public static void debug(final String fmt, final Object obj) {
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
	public static void debug(final String fmt, final Object obj1, final Object obj2) {
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
	private static void debug(final String message, final Object[] arguments, final Throwable t,
			@GreaterInt(ref = 2) final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
				|| (arguments == null),
				"Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
				|| ((arguments == null) && (t == null)), "Arguments and exception may not be set at the same time.");
		Condition.check(logDebug(), "Calling the debug method requires debug to be enabled.");
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
		Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
		
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
	public static void debug(final String message, final Throwable t) {
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
	public static void error(final String message, final int offset) {
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
	public static void error(final String fmt, final Object obj) {
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
	public static void error(final String fmt, final Object obj1, final Object obj2) {
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
	private static void error(final String message, final Object[] arguments, final Throwable t,
			@GreaterInt(ref = 2) final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
				|| (arguments == null),
				"Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
				|| ((arguments == null) && (t == null)), "Arguments and exception may not be set at the same time.");
		Condition.check(logError(), "Calling the debug method requires debug to be enabled.");
		
		if (debug) {
			Tuple<org.slf4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
			Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
			
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
	public static void error(final String message, final Throwable t) {
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
	public static void info(final String message, final int offset) {
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
	public static void info(final String fmt, final Object obj) {
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
	public static void info(final String fmt, final Object obj1, final Object obj2) {
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
	private static void info(final String message, final Object[] arguments, final Throwable t,
			@GreaterInt(ref = 2) final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
				|| (arguments == null),
				"Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
				|| ((arguments == null) && (t == null)), "Arguments and exception may not be set at the same time.");
		Condition.check(logInfo(), "Calling the debug method requires debug to be enabled.");
		
		if (debug) {
			Tuple<org.slf4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
			Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
			
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
	public static void info(final String message, final Throwable t) {
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
	
	public static void readConfiguration() {
		// FIXME what if we do not use log4j?
		
		//		for (Appender appender : appenders) {
		//			appender.close();
		//			org.apache.log4j.Logger.getRootLogger().removeAppender(appender);
		//		}
		//		appenders.clear();
		//
		//
		//		for (String clazz : classAppenders.keySet()) {
		//			classAppenders.get(clazz).close();
		//			LogManager.getLogger(clazz).removeAppender(classAppenders.get(clazz));
		//		}
		//		//		LogManager.resetConfiguration();
		//		classAppenders.clear();
		
		
		// FILE APPENDER
		if (!registeredAppenders.contains("log.file")) {
			String logFileName = System.getProperty("log.file", ".log");
			RollingFileAppender fileAppender = new RollingFileAppender();
			fileAppender.setLayout(layout);
			LevelRangeFilter fileLevelRangeFilter = new org.apache.log4j.varia.LevelRangeFilter();
			// set levels and minLevel
			LogLevel fileLevel = LogLevel.valueOf(System.getProperty("log.file.level", "INFO").toUpperCase());
			fileLevelRangeFilter.setLevelMin(Level.toLevel(fileLevel.toString()));
			if ((maxLevel == null) || (fileLevel.compareTo(maxLevel) > 0)) {
				maxLevel = fileLevel;
			}
			
			fileAppender.setFile(logFileName);
			fileAppender.addFilter(fileLevelRangeFilter);
			fileAppender.setMaxFileSize("10GB");
			fileAppender.activateOptions();
			org.apache.log4j.Logger.getRootLogger().addAppender(fileAppender);
			registeredAppenders.add("log.file");
		}
		
		for (Entry<Object, Object> prop : System.getProperties().entrySet()) {
			if (prop.getKey().toString().startsWith("log.class.")) {
				if (!registeredAppenders.contains(prop.getKey().toString())) {
					String className = prop.getKey().toString().substring(10);
					String[] values = prop.getValue().toString().split(",");
					Condition.check(values.length < 3, "log.class. arguments can have two options at most.");
					Condition.check(values.length > 0, "log.class. arguments must have at least a log level specified.");
					org.apache.log4j.Logger classLogger = LogManager.getLogger(className);
					LogLevel classLogLevel = LogLevel.valueOf(values[0].toUpperCase());
					classLogger.setLevel(org.apache.log4j.Level.toLevel(classLogLevel.toString()));
					if (values.length > 1) {
						RollingFileAppender classFileAppender = new RollingFileAppender();
						classFileAppender.setFile(values[1]);
						classFileAppender.setLayout(layout);
						classFileAppender.setMaxFileSize("1GB");
						classFileAppender.activateOptions();
						classLogger.addAppender(classFileAppender);
					}
					// set maxLevel
					if ((maxLevel == null) || (classLogLevel.compareTo(maxLevel) > 0)) {
						maxLevel = classLogLevel;
					}
					registeredAppenders.add(prop.getKey().toString());
				}
			}
		}
		
		setLogLevel(maxLevel);
		
		// org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.toLevel(getLogLevel().toString()));
		if ((System.getProperty("debug") != null) || (logLevel.compareTo(LogLevel.DEBUG) >= 0)) {
			Logger.debug = true;
		}
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
	
	/**
	 * Reads the logger specific configurations from the specified file.
	 * 
	 * @param url
	 *            url to the configuration file
	 */
	public static void readConfiguration(final URL url) {
		PropertyConfigurator.configure(url);
	}
	
	public static void setLogLevel(final LogLevel logLevel) {
		if (Logger.logLevel.compareTo(LogLevel.DEBUG) >= 0) {
			Logger.debug("Setting log level to " + logLevel.name());
		}
		
		Logger.logLevel = logLevel;
		
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
	private static Tuple<org.slf4j.Logger, String> tags(@GreaterInt(ref = 1) final int offset) {
		Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		
		CompareCondition
		.greater(
				throwable.getStackTrace().length,
				offset,
				"The length of the created stacktrace must never be less than the specified offset (which determines the original location).");
		
		Integer lineNumber = throwable.getStackTrace()[offset].getLineNumber();
		String methodName = throwable.getStackTrace()[offset].getMethodName();
		String className = throwable.getStackTrace()[offset].getClassName();
		
		org.slf4j.Logger logger = LoggerFactory.getLogger(className);
		
		Condition.notNull(lineNumber, "Linenumber determined from stacktrace must never be null.");
		CompareCondition.greater(lineNumber, 0, "Determined line number has to be always greater than 0.");
		Condition.notNull(methodName, "Methodname determined from stacktrace must never be null.");
		Condition.notNull(className, "Classname determined from stacktrace must never be null.");
		Condition.notNull(logger, "Requested logger must never be null.");
		
		return new Tuple<org.slf4j.Logger, String>(logger, className + "::" + methodName + "#" + lineNumber);
	}
	
	protected static void testDebug() {
		if (Logger.logDebug()) {
			Logger.debug("This is a test debug message");
		}
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
	public static void trace(final String message, final int offset) {
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
	public static void trace(final String fmt, final Object obj) {
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
	public static void trace(final String fmt, final Object obj1, final Object obj2) {
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
	private static void trace(final String message, final Object[] arguments, final Throwable t,
			@GreaterInt(ref = 2) final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
				|| (arguments == null),
				"Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
				|| ((arguments == null) && (t == null)), "Arguments and exception may not be set at the same time.");
		Condition.check(logTrace(), "Calling the debug method requires debug to be enabled.");
		
		Tuple<org.slf4j.Logger, String> ret = tags(offset);
		
		Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
		Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
		
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
	public static void trace(final String message, final Throwable t) {
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
	public static void warn(final String message, final int offset) {
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
	public static void warn(final String fmt, final Object obj) {
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
	public static void warn(final String fmt, final Object obj1, final Object obj2) {
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
	private static void warn(final String message, final Object[] arguments, final Throwable t,
			@GreaterInt(ref = 2) final int offset) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
				|| (arguments == null),
				"Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (t == null)) || ((t != null) && (arguments == null))
				|| ((arguments == null) && (t == null)), "Arguments and exception may not be set at the same time.");
		Condition.check(logWarn(), "Calling the debug method requires debug to be enabled.");
		
		if (debug) {
			Tuple<org.slf4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
			Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
			
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
	public static void warn(final String message, final Throwable t) {
		warn(message, null, t, 3);
	}
	
	/**
	 * @return the simple class name of the instance
	 */
	public String getHandle() {
		return this.getClass().getSimpleName();
	}
}

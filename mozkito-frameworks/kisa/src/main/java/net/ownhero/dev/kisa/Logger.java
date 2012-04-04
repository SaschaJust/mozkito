/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/
package net.ownhero.dev.kisa;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

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

/**
 * Logger class to instrument SLF4J.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Logger {
	
	/**
	 * The Class DebugOutputStream.
	 */
	private static class DebugOutputStream extends LogOutputStream {
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			if (Logger.logDebug()) {
				Logger.debug(this.stream.toString());
				this.stream = new ByteArrayOutputStream();
				super.flush();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int b) throws IOException {
			if (Logger.logDebug()) {
				this.stream.write(b);
				if (((char) b == '\n') || (b == 0)) {
					super.flush();
				} else {
					this.logger.debug(this.stream.toString());
				}
			}
		}
	}
	
	/**
	 * The Class ErrorOutputStream.
	 */
	private static class ErrorOutputStream extends LogOutputStream {
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			if (Logger.logError()) {
				Logger.error(this.stream.toString());
				this.stream = new ByteArrayOutputStream();
				super.flush();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int b) throws IOException {
			if (Logger.logError()) {
				this.stream.write(b);
				if (((char) b == '\n') || (b == 0)) {
					super.flush();
				} else {
					this.logger.error(this.stream.toString());
				}
			}
		}
	}
	
	/**
	 * The Class InfoOutputStream.
	 */
	private static class InfoOutputStream extends LogOutputStream {
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			if (Logger.logInfo()) {
				Logger.info(this.stream.toString());
				this.stream = new ByteArrayOutputStream();
				super.flush();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int b) throws IOException {
			if (Logger.logInfo()) {
				this.stream.write(b);
				if (((char) b == '\n') || (b == 0)) {
					super.flush();
				} else {
					this.logger.info(this.stream.toString());
				}
			}
		}
	}
	
	/**
	 * The Class LoggerOutputStream.
	 */
	private static abstract class LogOutputStream extends OutputStream {
		
		/** The stream. */
		protected ByteArrayOutputStream         stream = new ByteArrayOutputStream();
		
		/** The logger. */
		protected final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#close()
		 */
		@Override
		public void close() throws IOException {
			flush();
			super.close();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[])
		 */
		@Override
		public void write(final byte[] b) throws IOException {
			super.write(b, 0, b.length);
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(final byte[] b,
		                  final int off,
		                  final int len) throws IOException {
			super.write(b, off, len);
		}
		
	}
	
	/**
	 * The Class LogStream.
	 */
	public static class LogStream extends PrintStream {
		
		/**
		 * Creates the.
		 * 
		 * @param level
		 *            the level
		 * @return the output stream
		 */
		private static OutputStream create(final LogLevel level) {
			switch (level) {
				case DEBUG:
					return new DebugOutputStream();
				case ERROR:
					return new ErrorOutputStream();
				case INFO:
					return new InfoOutputStream();
				case WARN:
					return new WarnOutputStream();
				case TRACE:
					return new TraceOutputStream();
				default:
					return new OffOutputStream();
			}
		}
		
		/**
		 * Instantiates a new log stream.
		 * 
		 * @param level
		 *            the level
		 */
		public LogStream(final LogLevel level) {
			super(create(level));
		}
	}
	
	/**
	 * The Class OffOutputStream.
	 */
	private static class OffOutputStream extends LogOutputStream {
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			// ignore
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int b) throws IOException {
			// ignore
			
		}
	}
	
	/**
	 * The Enum TerminalColor.
	 */
	public static enum TerminalColor {
		
		/** The BLACK. */
		BLACK ("\u001b[30m"),
		
		/** The BACKGROUND_BLACK. */
		BGBLACK ("\u001b[40m"),
		
		/** The RED. */
		RED ("\u001b[31m"),
		
		/** The BACKGROUND_RED. */
		BGRED ("\u001b[41m"),
		
		/** The GREEN. */
		GREEN ("\u001b[32m"),
		
		/** The BACKGROUND_GREEN. */
		BGGREEN ("\u001b[42m"),
		
		/** The YELLOW. */
		YELLOW ("\u001b[33m"),
		
		/** The BACKGROUND_YELLOW. */
		BGYELLOW ("\u001b[43m"),
		
		/** The BLUE. */
		BLUE ("\u001b[34m"),
		
		/** The BACKGROUND_BLUE. */
		BGBLUE ("\u001b[44m"),
		
		/** The MAGENTA. */
		MAGENTA ("\u001b[35m"),
		
		/** The BACKGROUND_MAGENTA. */
		BGMAGENTA ("\u001b[45m"),
		
		/** The CYAN. */
		CYAN ("\u001b[36m"),
		
		/** The BACKGROUND_CYAN. */
		BGCYAN ("\u001b[46m"),
		
		/** The WHITE. */
		WHITE ("\u001b[37m"),
		
		/** The BACKGROUND_WHITE. */
		BGWHITE ("\u001b[47m"),
		
		/** The BOLD. */
		BOLD ("\u001b[1m"),
		
		/** The UNDERLINE. */
		UNDERLINE ("\u001b[4m"),
		
		/** The BLINK. */
		BLINK ("\u001b[5m"),
		
		/** The INVERT. */
		INVERT ("\u001b[7m"),
		
		/** The NONE. */
		NONE ("\u001b[m"),
		
		/** The BACKGROUND_NONE. */
		BGNONE ("\u001b[48m");
		
		/**
		 * Checks if is supported.
		 * 
		 * @return true, if is supported
		 */
		public static boolean isSupported() {
			if (System.getProperty("disableTermColors") == null) {
				if (System.console() != null) { // avoid colors when piping output
					final String termVariable = System.getenv("TERM");
					if (termVariable != null) {
						final Pattern pattern = Pattern.compile(".*color.*", Pattern.CASE_INSENSITIVE);
						return pattern.matcher(termVariable).matches() || termVariable.equalsIgnoreCase("screen");
					}
				}
			}
			
			return false;
		}
		
		/** The tag. */
		private final String tag;
		
		/**
		 * Instantiates a new terminal color.
		 * 
		 * @param tag
		 *            the tag
		 */
		TerminalColor(final String tag) {
			this.tag = tag;
		}
		
		/**
		 * Gets the tag.
		 * 
		 * @return the tag
		 */
		public String getTag() {
			return isSupported()
			                    ? this.tag
			                    : "";
		}
	}
	
	/**
	 * The Class TraceOutputStream.
	 */
	private static class TraceOutputStream extends LogOutputStream {
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			if (Logger.logTrace()) {
				Logger.trace(this.stream.toString());
				this.stream = new ByteArrayOutputStream();
				super.flush();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int b) throws IOException {
			if (Logger.logTrace()) {
				this.stream.write(b);
				if (((char) b == '\n') || (b == 0)) {
					super.flush();
				} else {
					this.logger.trace(this.stream.toString());
				}
			}
		}
	}
	
	/**
	 * The Class Tuple.
	 * 
	 * @param <K>
	 *            the key type
	 * @param <M>
	 *            the generic type
	 */
	private static class Tuple<K, M> {
		
		/** The first. */
		private final K first;
		
		/** The second. */
		private final M second;
		
		/**
		 * Instantiates a new tuple.
		 * 
		 * @param f
		 *            the f
		 * @param s
		 *            the s
		 */
		public Tuple(final K f, final M s) {
			this.first = f;
			this.second = s;
		}
		
		/**
		 * Gets the first.
		 * 
		 * @return the first
		 */
		public K getFirst() {
			return this.first;
		}
		
		/**
		 * Gets the second.
		 * 
		 * @return the second
		 */
		public M getSecond() {
			return this.second;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Tuple [first=" + this.first + ", second=" + this.second + "]";
		}
	}
	
	/**
	 * The Class WarnOutputStream.
	 */
	private static class WarnOutputStream extends LogOutputStream {
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		@Override
		public void flush() throws IOException {
			if (Logger.logWarn()) {
				Logger.error(this.stream.toString());
				this.stream = new ByteArrayOutputStream();
				super.flush();
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(final int b) throws IOException {
			if (Logger.logWarn()) {
				this.stream.write(b);
				if (((char) b == '\n') || (b == 0)) {
					super.flush();
				} else {
					this.logger.error(this.stream.toString());
				}
			}
		}
	}
	
	/** The registered appenders. */
	private static Set<String>          registeredAppenders = new HashSet<String>();
	
	/** The log level. */
	private static LogLevel             logLevel            = LogLevel.WARN;
	
	/** The debug enabled. */
	private static boolean              debugEnabled        = false;
	
	/** The max level. */
	private static LogLevel             maxLevel            = null;
	
	/** The default layout. */
	private static Layout               defaultLayout       = new EnhancedPatternLayout("%d (%8r) [%t] %-5p %m%n"); //$NON-NLS-1$
	                                                                                                                
	/** The error layout. */
	private static Layout               errorLayout         = new EnhancedPatternLayout("%d (%8r) [%t] " //$NON-NLS-1$
	                                                                + TerminalColor.RED.getTag() + "%-5p" //$NON-NLS-1$
	                                                                + TerminalColor.NONE.getTag() + " %m%n");      //$NON-NLS-1$
	                                                                                                                
	/** The warning layout. */
	private static Layout               warningLayout       = new EnhancedPatternLayout("%d (%8r) [%t] " //$NON-NLS-1$
	                                                                + TerminalColor.YELLOW.getTag() + "%-5p" //$NON-NLS-1$
	                                                                + TerminalColor.NONE.getTag() + " %m%n");      //$NON-NLS-1$
	                                                                                                                
	/** The console appenders. */
	private static List<WriterAppender> consoleAppenders    = new LinkedList<WriterAppender>();
	
	static {
		readConfiguration();
	}
	
	/** The Constant debug. */
	public static final PrintStream     debug               = new LogStream(LogLevel.DEBUG);
	
	/** The Constant info. */
	public static final PrintStream     info                = new LogStream(LogLevel.INFO);
	
	/** The Constant warn. */
	public static final PrintStream     warn                = new LogStream(LogLevel.WARN);
	
	/** The Constant trace. */
	public static final PrintStream     trace               = new LogStream(LogLevel.TRACE);
	
	/** The Constant error. */
	public static final PrintStream     error               = new LogStream(LogLevel.ERROR);
	
	/** The Constant formatString. */
	private static final String         formatString        = "[%s] %s";
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with debug log level.
	 * 
	 * @param offset
	 *            determines the offset in the stacktrace
	 * @param throwable
	 *            exception to be logged along the error message supplied
	 * @param message
	 *            the string to be logged or format string if arguments are not null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding format string
	 */
	private static void debug(@GreaterInt (ref = 2) final int offset,
	                          final Throwable throwable,
	                          final String message,
	                          final Object... arguments) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		                        || (arguments == null),
		                "Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (throwable == null)) || ((throwable != null) && (arguments == null))
		                        || ((arguments == null) && (throwable == null)),
		                "Arguments and exception may not be set at the same time.");
		Condition.check(logDebug(), "Calling the debug method requires debug to be enabled.");
		
		final Tuple<org.apache.log4j.Logger, String> ret = tags(offset);
		
		Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
		Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
		
		String formattedMessage;
		
		if (arguments != null) {
			formattedMessage = String.format(message, arguments);
		} else {
			formattedMessage = message;
		}
		
		if (throwable != null) {
			ret.getFirst().debug(String.format(formatString, ret.getSecond(), formattedMessage), throwable);
		} else {
			ret.getFirst().debug(String.format(formatString, ret.getSecond(), formattedMessage));
		}
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with debug log level.
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void debug(final String message) {
		debug(3, null, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the argument using the format string with debug log level.
	 * 
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void debug(final String formatString,
	                         final Object... arguments) {
		debug(3, null, formatString, arguments);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with debug log level.
	 * 
	 * @param throwable
	 *            the exception that shall be logged
	 */
	public static void debug(final Throwable throwable) {
		debug(3, throwable, null);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with debug log level.
	 * 
	 * @param throwable
	 *            the exception that shall be logged
	 * @param message
	 *            the format string to be used
	 */
	public static void debug(final Throwable throwable,
	                         final String message) {
		debug(3, throwable, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with debug log level.
	 * 
	 * @param throwable
	 *            the exception that shall be logged
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void debug(final Throwable throwable,
	                         final String formatString,
	                         final Object... arguments) {
		debug(3, throwable, formatString, arguments);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with error log level.
	 * 
	 * @param offset
	 *            determines the offset in the stacktrace
	 * @param throwable
	 *            exception to be logged along the error message supplied
	 * @param message
	 *            the string to be logged or format string if arguments are not null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding format string
	 */
	private static void error(@GreaterInt (ref = 2) final int offset,
	                          final Throwable throwable,
	                          final String message,
	                          final Object... arguments) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		                        || (arguments == null),
		                "Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (throwable == null)) || ((throwable != null) && (arguments == null))
		                        || ((arguments == null) && (throwable == null)),
		                "Arguments and exception may not be set at the same time.");
		Condition.check(logError(), "Calling the debug method requires debug to be enabled.");
		
		if (debugEnabled) {
			final Tuple<org.apache.log4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
			Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
			
			String formattedMessage;
			
			if (arguments != null) {
				formattedMessage = String.format(message, arguments);
			} else {
				formattedMessage = message;
			}
			
			if (throwable != null) {
				ret.getFirst().error(String.format(formatString, ret.getSecond(), formattedMessage), throwable);
			} else {
				ret.getFirst().error(String.format(formatString, ret.getSecond(), formattedMessage));
			}
		} else {
			final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);
			
			String formattedMessage;
			
			if (arguments != null) {
				formattedMessage = String.format(message, arguments);
			} else {
				formattedMessage = message;
			}
			
			if (throwable != null) {
				logger.error(formattedMessage, throwable);
			} else {
				logger.error(formattedMessage);
			}
		}
		
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with error log level.
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void error(final String message) {
		error(3, null, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the argument using the format string with error log level.
	 * 
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void error(final String formatString,
	                         final Object... arguments) {
		error(3, null, formatString, arguments);
	}
	
	/**
	 * Error.
	 * 
	 * @param throwable
	 *            the throwable
	 */
	public static void error(final Throwable throwable) {
		error(3, throwable, null);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with error log level.
	 * 
	 * @param throwable
	 *            , String message the exception that shall be logged
	 * @param message
	 *            the format string to be used
	 */
	public static void error(final Throwable throwable,
	                         final String message) {
		error(3, throwable, message);
	}
	
	/**
	 * Error.
	 * 
	 * @param throwable
	 *            the throwable
	 * @param message
	 *            the message
	 * @param arguments
	 *            the arguments
	 */
	public static void error(final Throwable throwable,
	                         final String message,
	                         final Object... arguments) {
		error(3, throwable, message, arguments);
	}
	
	/**
	 * Gets the log level.
	 * 
	 * @return the log level
	 */
	public static Enum<LogLevel> getLogLevel() {
		return logLevel;
	}
	
	/**
	 * Increase log level.
	 * 
	 * @param logLevel
	 *            the log level
	 */
	public static void increaseLogLevel(final LogLevel logLevel) {
		if (Logger.logLevel.compareTo(logLevel) < 0) {
			setLogLevel(logLevel);
		}
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with info log level.
	 * 
	 * @param offset
	 *            determines the offset in the stacktrace
	 * @param throwable
	 *            exception to be logged along the error message supplied
	 * @param message
	 *            the string to be logged or format string if arguments are not null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding format string
	 */
	private static void info(@GreaterInt (ref = 2) final int offset,
	                         final Throwable throwable,
	                         final String message,
	                         final Object... arguments) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		                        || (arguments == null),
		                "Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (throwable == null)) || ((throwable != null) && (arguments == null))
		                        || ((arguments == null) && (throwable == null)),
		                "Arguments and exception may not be set at the same time.");
		Condition.check(logInfo(), "Calling the debug method requires debug to be enabled.");
		
		if (debugEnabled) {
			final Tuple<org.apache.log4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
			Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
			
			String formattedMessage;
			
			if (arguments != null) {
				formattedMessage = String.format(message, arguments);
			} else {
				formattedMessage = message;
			}
			
			if (throwable != null) {
				ret.getFirst().info(String.format(formatString, ret.getSecond(), formattedMessage), throwable);
			} else {
				ret.getFirst().info(String.format(formatString, ret.getSecond(), formattedMessage));
			}
		} else {
			final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);
			
			String formattedMessage;
			
			if (arguments != null) {
				formattedMessage = String.format(message, arguments);
			} else {
				formattedMessage = message;
			}
			
			if (throwable != null) {
				logger.info(formattedMessage, throwable);
			} else {
				logger.info(formattedMessage);
			}
		}
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with info log level.
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void info(final String message) {
		info(3, null, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the argument using the format string with info log level.
	 * 
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void info(final String formatString,
	                        final Object... arguments) {
		info(3, null, formatString, arguments);
	}
	
	/**
	 * Info.
	 * 
	 * @param throwable
	 *            the throwable
	 */
	public static void info(final Throwable throwable) {
		info(3, throwable, null);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with info log level.
	 * 
	 * @param throwable
	 *            the throwable
	 * @param message
	 *            the format string to be used
	 */
	public static void info(final Throwable throwable,
	                        final String message) {
		info(3, throwable, message);
	}
	
	/**
	 * Info.
	 * 
	 * @param throwable
	 *            the throwable
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void info(final Throwable throwable,
	                        final String formatString,
	                        final Object... arguments) {
		info(3, throwable, formatString, arguments);
	}
	
	/**
	 * Log debug.
	 * 
	 * @return true, if successful
	 */
	public static boolean logDebug() {
		return logLevel.compareTo(LogLevel.DEBUG) >= 0;
	}
	
	/**
	 * Log error.
	 * 
	 * @return true, if successful
	 */
	public static boolean logError() {
		return logLevel.compareTo(LogLevel.ERROR) >= 0;
	}
	
	/**
	 * Log info.
	 * 
	 * @return true, if successful
	 */
	public static boolean logInfo() {
		return logLevel.compareTo(LogLevel.INFO) >= 0;
	}
	
	/**
	 * Log trace.
	 * 
	 * @return true, if successful
	 */
	public static boolean logTrace() {
		return logLevel.compareTo(LogLevel.TRACE) >= 0;
	}
	
	/**
	 * Log warn.
	 * 
	 * @return true, if successful
	 */
	public static boolean logWarn() {
		return logLevel.compareTo(LogLevel.WARN) >= 0;
	}
	
	/**
	 * Read configuration.
	 */
	public static void readConfiguration() {
		updateConsoleLevel();
		updateFileLevel();
		updateClassLevels();
		
		setLogLevel(maxLevel);
		
		if ((System.getProperty("debug") != null) || (logLevel.compareTo(LogLevel.DEBUG) >= 0)) {
			Logger.debugEnabled = true;
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
	
	/**
	 * Sets the console level.
	 * 
	 * @param level
	 *            the new console level
	 */
	public static synchronized void setConsoleLevel(final LogLevel level) {
		final Properties properties = System.getProperties();
		properties.put("log.console.level", level.name());
		System.setProperties(properties);
		updateConsoleLevel();
	}
	
	/**
	 * Sets the file level.
	 * 
	 * @param level
	 *            the new file level
	 */
	public static void setFileLevel(final LogLevel level) {
		final Properties properties = System.getProperties();
		properties.put("log.file.level", level.name());
		System.setProperties(properties);
		updateFileLevel();
	}
	
	/**
	 * Sets the log level.
	 * 
	 * @param logLevel
	 *            the new log level
	 */
	public static void setLogLevel(final LogLevel logLevel) {
		if (Logger.logLevel.compareTo(LogLevel.DEBUG) >= 0) {
			Logger.debug("Setting log level to " + logLevel.name());
		}
		
		Logger.logLevel = logLevel;
		
		if ((System.getProperty("debug") != null) || (logLevel.compareTo(LogLevel.DEBUG) >= 0)) {
			Logger.debugEnabled = true;
		}
	}
	
	/**
	 * Tags.
	 * 
	 * @param offset
	 *            the offset
	 * @return a tuple containing the corresponding logger to the calling instance and the exact calling location
	 *         (class, method, line number). Both entries are guaranteed to not be null
	 */
	private static Tuple<org.apache.log4j.Logger, String> tags(@GreaterInt (ref = 1) final int offset) {
		final Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		
		CompareCondition.greater(throwable.getStackTrace().length,
		                         offset,
		                         "The length of the created stacktrace must never be less than the specified offset (which determines the original location).");
		
		final Integer lineNumber = throwable.getStackTrace()[offset].getLineNumber();
		final String methodName = throwable.getStackTrace()[offset].getMethodName();
		final String className = throwable.getStackTrace()[offset].getClassName();
		
		final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(className);
		
		Condition.notNull(lineNumber, "Linenumber determined from stacktrace must never be null.");
		CompareCondition.greater(lineNumber, 0, "Determined line number has to be always greater than 0.");
		Condition.notNull(methodName, "Methodname determined from stacktrace must never be null.");
		Condition.notNull(className, "Classname determined from stacktrace must never be null.");
		Condition.notNull(logger, "Requested logger must never be null.");
		
		return new Tuple<org.apache.log4j.Logger, String>(logger, className + "::" + methodName + "#" + lineNumber);
	}
	
	static void testDebug() {
		if (Logger.logDebug()) {
			Logger.debug("This is a test debug message");
		}
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with trace log level.
	 * 
	 * @param offset
	 *            determines the offset in the stacktrace
	 * @param throwable
	 *            exception to be logged along the error message supplied
	 * @param message
	 *            the string to be logged or format string if arguments are not null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding format string
	 */
	private static void trace(@GreaterInt (ref = 2) final int offset,
	                          final Throwable throwable,
	                          final String message,
	                          final Object... arguments) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		                        || (arguments == null),
		                "Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (throwable == null)) || ((throwable != null) && (arguments == null))
		                        || ((arguments == null) && (throwable == null)),
		                "Arguments and exception may not be set at the same time.");
		Condition.check(logDebug(), "Calling the trace method requires trace to be enabled.");
		
		final Tuple<org.apache.log4j.Logger, String> ret = tags(offset);
		
		Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
		Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
		
		String formattedMessage;
		
		if (arguments != null) {
			formattedMessage = String.format(message, arguments);
		} else {
			formattedMessage = message;
		}
		
		if (throwable != null) {
			ret.getFirst().trace(String.format(formatString, ret.getSecond(), formattedMessage), throwable);
		} else {
			ret.getFirst().trace(String.format(formatString, ret.getSecond(), formattedMessage));
		}
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with trace log level.
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void trace(final String message) {
		trace(3, null, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the argument using the format string with trace log level.
	 * 
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void trace(final String formatString,
	                         final Object... arguments) {
		trace(3, null, formatString, arguments);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with trace log level.
	 * 
	 * @param throwable
	 *            the exception that shall be logged
	 */
	public static void trace(final Throwable throwable) {
		trace(3, throwable, null);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with trace log level.
	 * 
	 * @param throwable
	 *            the exception that shall be logged
	 * @param message
	 *            the format string to be used
	 */
	public static void trace(final Throwable throwable,
	                         final String message) {
		trace(3, throwable, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with trace log level.
	 * 
	 * @param throwable
	 *            the exception that shall be logged
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void trace(final Throwable throwable,
	                         final String formatString,
	                         final Object... arguments) {
		trace(3, throwable, formatString, arguments);
	}
	
	/**
	 * Update class levels.
	 */
	public static void updateClassLevels() {
		for (final Entry<Object, Object> prop : System.getProperties().entrySet()) {
			if (prop.getKey().toString().startsWith("log.class.")) {
				if (!registeredAppenders.contains(prop.getKey().toString())) {
					final String className = prop.getKey().toString().substring(10);
					final String[] values = prop.getValue().toString().split(",");
					CompareCondition.less(values.length, 3, "log.class. arguments can have two options at most.");
					CompareCondition.greater(values.length, 0,
					                         "log.class. arguments must have at least a log level specified.");
					final org.apache.log4j.Logger classLogger = LogManager.getLogger(className);
					final LogLevel classLogLevel = LogLevel.valueOf(values[0].toUpperCase());
					
					final Level log4jLevel = org.apache.log4j.Level.toLevel(classLogLevel.toString());
					if (org.apache.log4j.Logger.getRootLogger().getLevel().isGreaterOrEqual(log4jLevel)) {
						org.apache.log4j.Logger.getRootLogger().setLevel(log4jLevel);
					}
					
					classLogger.setLevel(log4jLevel);
					if (values.length > 1) {
						final RollingFileAppender classFileAppender = new RollingFileAppender();
						classFileAppender.setFile(values[1]);
						classFileAppender.setLayout(defaultLayout);
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
	}
	
	/**
	 * Update console level.
	 */
	public static void updateConsoleLevel() {
		// CONSOLE APPENDER
		
		for (final WriterAppender appender : consoleAppenders) {
			org.apache.log4j.Logger.getRootLogger().removeAppender(appender);
		}
		
		// set levels and minLevel
		final LogLevel consoleLevel = LogLevel.valueOf(System.getProperty("log.console.level", "INFO").toUpperCase());
		
		final WriterAppender consoleDefaultAppender = new WriterAppender(defaultLayout, System.err);
		final LevelRangeFilter normalFilter = new LevelRangeFilter();
		
		final Level log4jLevel = Level.toLevel(consoleLevel.toString());
		if (org.apache.log4j.Logger.getRootLogger().getLevel().isGreaterOrEqual(log4jLevel)) {
			org.apache.log4j.Logger.getRootLogger().setLevel(log4jLevel);
		}
		
		if (TerminalColor.isSupported()) {
			
			if (consoleLevel.compareTo(LogLevel.INFO) < 0) {
				// e.g. consoleLevel = ERROR
				// then don't do default logging at all
				
			} else {
				// e.g. consoleLevel = DEBUG
				normalFilter.setLevelMin(Level.toLevel(consoleLevel.toString()));
				normalFilter.setLevelMax(Level.INFO);
				consoleDefaultAppender.addFilter(normalFilter);
				consoleDefaultAppender.setLayout(defaultLayout);
				consoleDefaultAppender.activateOptions();
				org.apache.log4j.Logger.getRootLogger().addAppender(consoleDefaultAppender);
				consoleAppenders.add(consoleDefaultAppender);
			}
			
			// if we got log level ERROR, add stylized ERROR log appender
			if (consoleLevel.compareTo(LogLevel.ERROR) >= 0) {
				final WriterAppender consoleErrorAppender = new WriterAppender(errorLayout, System.err);
				final LevelRangeFilter errorFilter = new LevelRangeFilter();
				errorFilter.setLevelMin(Level.ERROR);
				errorFilter.setLevelMax(Level.FATAL);
				consoleErrorAppender.addFilter(errorFilter);
				consoleErrorAppender.setLayout(errorLayout);
				consoleErrorAppender.activateOptions();
				org.apache.log4j.Logger.getRootLogger().addAppender(consoleErrorAppender);
				consoleAppenders.add(consoleErrorAppender);
			}
			
			if (consoleLevel.compareTo(LogLevel.WARN) >= 0) {
				final WriterAppender consoleWarningAppender = new WriterAppender(warningLayout, System.err);
				final LevelRangeFilter warningFilter = new LevelRangeFilter();
				warningFilter.setLevelMin(Level.WARN);
				warningFilter.setLevelMax(Level.WARN);
				consoleWarningAppender.addFilter(warningFilter);
				consoleWarningAppender.setLayout(warningLayout);
				consoleWarningAppender.activateOptions();
				org.apache.log4j.Logger.getRootLogger().addAppender(consoleWarningAppender);
				consoleAppenders.add(consoleWarningAppender);
			}
		} else {
			normalFilter.setLevelMin(Level.toLevel(consoleLevel.toString()));
			consoleDefaultAppender.addFilter(normalFilter);
			consoleDefaultAppender.activateOptions();
			org.apache.log4j.Logger.getRootLogger().addAppender(consoleDefaultAppender);
			consoleAppenders.add(consoleDefaultAppender);
		}
		
		// consoleLevelRangeFilter.setLevelMin(Level.toLevel(consoleLevel.toString()));
		if ((maxLevel == null) || (consoleLevel.compareTo(maxLevel) > 0)) {
			maxLevel = consoleLevel;
		}
	}
	
	/**
	 * Update file level.
	 */
	public static void updateFileLevel() {
		// FILE APPENDER
		if (!registeredAppenders.contains("log.file")) {
			final String logFileName = System.getProperty("log.file", ".log");
			final RollingFileAppender fileAppender = new RollingFileAppender();
			fileAppender.setLayout(defaultLayout);
			final LevelRangeFilter fileLevelRangeFilter = new org.apache.log4j.varia.LevelRangeFilter();
			// set levels and minLevel
			final LogLevel fileLevel = LogLevel.valueOf(System.getProperty("log.file.level", "INFO").toUpperCase());
			
			final Level log4jLevel = Level.toLevel(fileLevel.toString());
			if (org.apache.log4j.Logger.getRootLogger().getLevel().isGreaterOrEqual(log4jLevel)) {
				org.apache.log4j.Logger.getRootLogger().setLevel(log4jLevel);
			}
			
			fileLevelRangeFilter.setLevelMin(log4jLevel);
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
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with warn log level.
	 * 
	 * @param offset
	 *            determines the offset in the stacktrace
	 * @param throwable
	 *            exception to be logged along the warn message supplied
	 * @param message
	 *            the string to be logged or format string if arguments are not null
	 * @param arguments
	 *            array of 1 or 2 objects to be logged with the corresponding format string
	 */
	private static void warn(@GreaterInt (ref = 2) final int offset,
	                         final Throwable throwable,
	                         final String message,
	                         final Object... arguments) {
		Condition.check(((arguments != null) && (arguments.length <= 2) && (arguments.length > 0))
		                        || (arguments == null),
		                "Either no arguments may be given at all or the number of arguments has to be between 1 and 2.");
		Condition.check(((arguments != null) && (throwable == null)) || ((throwable != null) && (arguments == null))
		                        || ((arguments == null) && (throwable == null)),
		                "Arguments and exception may not be set at the same time.");
		Condition.check(logError(), "Calling the debug method requires debug to be enabled.");
		
		if (debugEnabled) {
			final Tuple<org.apache.log4j.Logger, String> ret = tags(offset);
			
			Condition.notNull(ret.getFirst(), "Requested logger must never be null.");
			Condition.notNull(ret.getSecond(), "Determined logging source must never be null.");
			
			String formattedMessage;
			
			if (arguments != null) {
				formattedMessage = String.format(message, arguments);
			} else {
				formattedMessage = message;
			}
			
			if (throwable != null) {
				ret.getFirst().warn(String.format(formatString, ret.getSecond(), formattedMessage), throwable);
			} else {
				ret.getFirst().warn(String.format(formatString, ret.getSecond(), formattedMessage));
			}
		} else {
			final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Logger.class);
			
			String formattedMessage;
			
			if (arguments != null) {
				formattedMessage = String.format(message, arguments);
			} else {
				formattedMessage = message;
			}
			
			if (throwable != null) {
				logger.warn(formattedMessage, throwable);
			} else {
				logger.warn(formattedMessage);
			}
		}
		
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message with warn log level.
	 * 
	 * @param message
	 *            the string to be logged
	 */
	public static void warn(final String message) {
		warn(3, null, message);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the argument using the format string with warn log level.
	 * 
	 * @param formatString
	 *            the format string
	 * @param arguments
	 *            the arguments
	 */
	public static void warn(final String formatString,
	                        final Object... arguments) {
		warn(3, null, formatString, arguments);
	}
	
	/**
	 * Error.
	 * 
	 * @param throwable
	 *            the throwable
	 */
	public static void warn(final Throwable throwable) {
		warn(3, throwable, null);
	}
	
	/**
	 * requests the logger for the calling instance and the classname::methodname#linenumber tag and uses this
	 * information to log the message and the exception string with warn log level.
	 * 
	 * @param throwable
	 *            , String message the exception that shall be logged
	 * @param message
	 *            the format string to be used
	 */
	public static void warn(final Throwable throwable,
	                        final String message) {
		warn(3, throwable, message);
	}
	
	/**
	 * Error.
	 * 
	 * @param throwable
	 *            the throwable
	 * @param message
	 *            the message
	 * @param arguments
	 *            the arguments
	 */
	public static void warn(final Throwable throwable,
	                        final String message,
	                        final Object... arguments) {
		warn(3, throwable, message, arguments);
	}
}

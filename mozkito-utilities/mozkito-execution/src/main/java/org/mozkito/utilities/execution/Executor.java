/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.utilities.execution;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;

import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.exceptions.ExternalExecutableException;

/**
 * The Class Executor.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
class Executor extends Thread implements Executable {
	
	/**
	 * The Class ExceptionHandler.
	 */
	private static final class ExceptionHandler implements UncaughtExceptionHandler {
		
		/** The executor. */
		private final Executor executor;
		
		/** The stats. */
		private final Stats    stats;
		
		/**
		 * Instantiates a new exception handler.
		 * 
		 * @param stats
		 *            the stats
		 * @param executor
		 *            the executor
		 */
		public ExceptionHandler(final Stats stats, final Executor executor) {
			this.stats = stats;
			this.executor = executor;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
		 */
		@Override
		public void uncaughtException(final Thread t,
		                              final Throwable e) {
			PRECONDITIONS: {
				// none
			}
			
			try {
				this.stats.exception(e);
				this.executor.killOnError();
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
	}
	
	/**
	 * The Enum ExecutorState.
	 */
	private static enum ExecutorState {
		
		/** The created. */
		CREATED,
		/** The processed. */
		PROCESSED,
		/** The running. */
		RUNNING,
		/** The terminated. */
		TERMINATED;
	}
	
	/**
	 * The Class StateMonitor.
	 */
	private static class StateMonitor {
		
		/** The executor state. */
		private ExecutorState executorState = ExecutorState.CREATED;
		
		/**
		 * Gets the state.
		 * 
		 * @return the state
		 */
		public synchronized final ExecutorState getState() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.executorState;
			} finally {
				POSTCONDITIONS: {
					Condition.notNull(this.executorState, "Field '%s' in '%s'.", "state", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
		
		/**
		 * Sets the state.
		 * 
		 * @param executorState
		 *            the state to set
		 */
		public synchronized final void setState(final ExecutorState executorState) {
			PRECONDITIONS: {
				Condition.notNull(executorState, "Argument '%s' in '%s'.", "state", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			try {
				this.executorState = executorState;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
	}
	
	/**
	 * The Class Stats.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	public class Stats {
		
		/** The timestamp the thread was created. */
		private final DateTime   created;
		
		/** Points to an exception if an error occurred. */
		private Throwable        error        = null;
		
		/** The time the thread terminated. */
		private DateTime         finished     = null;
		
		/** The internal id of the thread. */
		private final int        id;
		
		/** The name of the thread. */
		private final String     name;
		
		/** The number of bytes that have been read. */
		private long             readBytes    = 0l;
		
		/** The time the thread has been started. */
		private DateTime         started      = null;
		
		/** The type of the thread. @see ThreadType */
		private final ThreadType type;
		
		/** The number of bytes that have been written. */
		private long             writtenBytes = 0l;
		
		/**
		 * Instantiates a new stats object.
		 * 
		 * @param name
		 *            the name of the thread
		 * @param type
		 *            the type of the thread
		 */
		public Stats(final String name, final ThreadType type) {
			PRECONDITIONS: {
				if (name == null) {
					throw new NullPointerException("The name of the thread must not be null.");
				}
				
				if (type == null) {
					throw new NullPointerException("The type of the thread must not be null.");
				}
			}
			
			this.name = name;
			this.created = new DateTime();
			this.id = hashCode();
			this.type = type;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Stats other = (Stats) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.created == null) {
				if (other.created != null) {
					return false;
				}
			} else if (!this.created.equals(other.created)) {
				return false;
			}
			if (this.name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!this.name.equals(other.name)) {
				return false;
			}
			return true;
		}
		
		/**
		 * Exception.
		 * 
		 * @param e
		 *            the e
		 */
		public void exception(final Throwable e) {
			PRECONDITIONS: {
				if (e == null) {
					throw new NullPointerException("The error set for the thread must not be null.");
				}
			}
			
			try {
				this.error = e;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Sets the finished timestamp of the thread. This method is write-once. This means that once this method was
		 * called, the timestamp will never be updated again.
		 * 
		 * @return true, if the timestamp was set. false, if the timestamp was already set.
		 */
		public synchronized boolean finished() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				if (this.finished == null) {
					this.finished = new DateTime();
					return true;
				} else {
					return false;
				}
			} finally {
				POSTCONDITIONS: {
					assert this.finished != null;
				}
			}
		}
		
		/**
		 * Returns a copy of the timestamp denoting the creation of the thread.
		 * 
		 * @return the creation timestamp
		 */
		public final DateTime getCreated() {
			PRECONDITIONS: {
				assert this.created != null;
			}
			
			try {
				return new DateTime(this.created);
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the error.
		 * 
		 * @return the error
		 */
		public final Throwable getError() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.error;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Returns a copy of the timestamp denoting the time the thread terminated. Returns null if the thread is still
		 * alive.
		 * 
		 * @return the termination timestamp
		 */
		public final DateTime getFinished() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return new DateTime(this.finished);
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the id.
		 * 
		 * @return the id
		 */
		public final int getId() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.id;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the name.
		 * 
		 * @return the name
		 */
		public final String getName() {
			PRECONDITIONS: {
				assert this.name != null;
			}
			
			try {
				return this.name;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the outer type.
		 * 
		 * @return the outer type
		 */
		private Executor getOuterType() {
			return Executor.this;
		}
		
		/**
		 * Gets the number of bytes read by the thread.
		 * 
		 * @return the readBytes
		 */
		public final long getReadBytes() {
			PRECONDITIONS: {
				assert this.readBytes >= 0;
			}
			
			try {
				return this.readBytes;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Returns a copy of the start time of the thread. Returns null if the thread hasn't been started yet.
		 * 
		 * @return the started
		 */
		public final DateTime getStarted() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return new DateTime(this.started);
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the type of the thread.
		 * 
		 * @return the type
		 */
		public final ThreadType getType() {
			PRECONDITIONS: {
				assert this.type != null;
			}
			
			try {
				return this.type;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Returns the number of written bytes.
		 * 
		 * @return the writtenBytes
		 */
		public final long getWrittenBytes() {
			PRECONDITIONS: {
				assert this.writtenBytes >= 0;
			}
			
			try {
				return this.writtenBytes;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + getOuterType().hashCode();
			result = (prime * result) + ((this.created == null)
			                                                   ? 0
			                                                   : this.created.hashCode());
			result = (prime * result) + ((this.name == null)
			                                                ? 0
			                                                : this.name.hashCode());
			return result;
		}
		
		/**
		 * Sets the number of read bytes.
		 * 
		 * @param numberOfBytes
		 *            the number of bytes
		 */
		public void read(final long numberOfBytes) {
			PRECONDITIONS: {
				if (numberOfBytes < 0) {
					throw new IllegalArgumentException("You cannot read a negative number of bytes.");
				}
			}
			
			try {
				this.readBytes = numberOfBytes;
			} finally {
				POSTCONDITIONS: {
					assert this.readBytes == numberOfBytes;
				}
			}
		}
		
		/**
		 * Sets the start timestamp of the thread to now. This is a write-once method, i.e. calling this method multiple
		 * times won't update the initial timestamp.
		 * 
		 * @return true, if the timestamp was set. false, if the timestamp already has been set.
		 */
		public synchronized boolean started() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				if (this.started == null) {
					this.started = new DateTime();
					return true;
				} else {
					return false;
				}
			} finally {
				POSTCONDITIONS: {
					assert this.started != null;
				}
			}
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			final StringBuilder builder = new StringBuilder();
			
			builder.append("Stats [created=");
			builder.append(this.created);
			builder.append(", name=");
			builder.append(this.name);
			builder.append(", id=");
			builder.append(this.id);
			builder.append(", writtenBytes=");
			builder.append(this.writtenBytes);
			builder.append(", error=");
			builder.append(this.error);
			builder.append(", started=");
			builder.append(this.started);
			builder.append(", readBytes=");
			builder.append(this.readBytes);
			builder.append(", finished=");
			builder.append(this.finished);
			builder.append(", type=");
			builder.append(this.type);
			builder.append("]");
			
			return builder.toString();
		}
		
		/**
		 * Sets the number of bytes written by the thread.
		 * 
		 * @param numberOfBytes
		 *            the number of bytes
		 */
		public void written(final long numberOfBytes) {
			PRECONDITIONS: {
				if (numberOfBytes < 0) {
					throw new IllegalArgumentException("You cannot write a negative number of bytes.");
				}
			}
			
			try {
				this.writtenBytes = numberOfBytes;
			} finally {
				POSTCONDITIONS: {
					assert this.writtenBytes == numberOfBytes;
				}
			}
		}
		
	}
	
	/**
	 * The Enum ThreadType.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	public enum ThreadType {
		
		/** The connected input. */
		CONNECTED_INPUT,
		/** The error handler. */
		ERROR_HANDLER,
		/** The input handler. */
		INPUT_HANDLER,
		/** The output handler. */
		OUTPUT_HANDLER,
		/** The piped outputhandler. */
		PIPED_OUTPUTHANDLER;
	}
	
	/** The Constant EOF. */
	protected static final int        EOF          = -1;
	
	/** The arguments. */
	private String[]                  arguments;
	
	/** The charset. */
	private Charset                   charset;
	
	/** The command. */
	private String                    command;
	
	/** The connected input. */
	private Object                    connectedInput;
	
	/** The environment. */
	private Map<String, String>       environment;
	
	/** The standard err handler. */
	private Thread                    errorHandler;
	
	/** The exit value. */
	private Integer                   exitValue    = null;
	
	/** The input handler. */
	private Thread                    inputHandler;
	
	/** The log stream. */
	private PrintStream               logStream;
	
	/** The standard out handler. */
	private Thread                    outputHandler;
	
	/** The pipe to. */
	private final Set<Executable>     pipeTo       = new HashSet<>();
	
	/** The process. */
	private Process                   process      = null;
	
	/** The redirect standard error. */
	private boolean                   redirectStandardError;
	
	/** The standard err. */
	private final CircularByteBuffer  standardErr  = new CircularByteBuffer();
	
	/** The standard in. */
	private final CircularByteBuffer  standardIn   = new CircularByteBuffer();
	
	/** The standard out. */
	private final CircularByteBuffer  standardOut  = new CircularByteBuffer();
	
	/** The started. */
	private boolean                   started      = false;
	
	/** The state monitor. */
	private final StateMonitor        stateMonitor = new StateMonitor();
	
	/** The statistics. */
	private final Map<Integer, Stats> statistics   = new HashMap<>();
	
	/** The terminated. */
	private boolean                   terminated   = false;
	
	/** The working directory. */
	private File                      workingDirectory;                        ;
	
	/**
	 * Instantiates a new executor.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @param environment
	 *            the environment
	 * @param charset
	 *            the charset
	 */
	Executor(final String command, final String[] arguments, final File dir, final Map<String, String> environment,
	        final Charset charset) {
		PRECONDITIONS: {
			if (command == null) {
				throw new NullPointerException("You have to specify a command.");
			}
		}
		
		try {
			// body
			setName("Executor-" + getId() + " (`" + command + "`)");
			
			this.command = command;
			this.arguments = arguments == null
			                                  ? new String[0]
			                                  : arguments;
			this.workingDirectory = dir == null
			                                   ? new File(System.getProperty("user.dir"))
			                                   : dir;
			this.environment = environment == null
			                                      ? new HashMap<String, String>()
			                                      : environment;
			this.charset = charset == null
			                              ? Charset.defaultCharset()
			                              : charset;
		} finally {
			POSTCONDITIONS: {
				try {
					FileUtils.checkExecutable(command);
				} catch (final ExternalExecutableException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#connectStandardIn(java.io.InputStream)
	 */
	@Override
	public synchronized boolean connectStandardIn(final InputStream inputStream) {
		PRECONDITIONS: {
			if (inputStream == null) {
				throw new NullPointerException("InputStream to connect to must not be null.");
			}
			
			assert this.statistics != null;
		}
		
		try {
			if (this.connectedInput == null) {
				this.connectedInput = inputStream;
				
				final String name = "ConnectedInput-" + inputStream.hashCode();
				final Stats stats = new Stats(name, ThreadType.CONNECTED_INPUT);
				this.statistics.put(stats.getId(), stats);
				final Thread thread = new Thread(name) {
					
					@Override
					public void run() {
						stats.started();
						final OutputStream standardIn = getStandardIn();
						final byte[] buffer = new byte[2048];
						long writeCount = 0;
						long readCount = 0;
						try {
							int n = 0;
							while (EOF != (n = inputStream.read(buffer))) {
								readCount += n;
								standardIn.write(buffer, 0, n);
								writeCount += n;
							}
							
						} catch (final IOException e) {
							if (Executor.this.logStream != null) {
								e.printStackTrace(Executor.this.logStream);
							}
							stats.exception(e);
							throw new RuntimeException(e);
						} finally {
							stats.read(readCount);
							stats.written(writeCount);
							stats.finished();
							try {
								standardIn.flush();
								standardIn.close();
							} catch (final IOException ignore) {
								// ignore
							}
							
						}
					};
				};
				
				thread.start();
				return true;
			} else {
				return false;
			}
		} finally {
			POSTCONDITIONS: {
				assert this.connectedInput != null;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#exitValue()
	 */
	@Override
	public Integer exitValue() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return new Integer(this.exitValue);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets a copy of the arguments.
	 * 
	 * @return the arguments
	 */
	public String[] getArguments() {
		PRECONDITIONS: {
			assert this.arguments != null;
		}
		
		try {
			return (String[]) ArrayUtils.clone(this.arguments);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the charset.
	 * 
	 * @return the charset
	 */
	protected Charset getCharset() {
		PRECONDITIONS: {
			assert this.charset != null;
		}
		
		try {
			return this.charset;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Returns the command.
	 * 
	 * @return the command
	 */
	public String getCommand() {
		PRECONDITIONS: {
			assert this.command != null;
		}
		
		try {
			return this.command;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets a copy of the environment.
	 * 
	 * @return the environment
	 */
	public Map<String, String> getEnvironment() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return new HashMap<>(this.environment);
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.environment, "Field '%s' in '%s'.", "environment", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#getNextStdOutLine()
	 */
	@Override
	public String getNextStdOutLine() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getNextStdOutLine' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the pipe to.
	 * 
	 * @return the pipeTo
	 */
	protected Executable[] getPipeTo() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.pipeTo.toArray(new Executable[0]);
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.pipeTo, "Field '%s' in '%s'.", "pipeTo", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#getStandardErr()
	 */
	@Override
	public InputStream getStandardErr() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.standardErr.getInputStream();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#getStandardIn()
	 */
	@Override
	public OutputStream getStandardIn() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (this.inputHandler == null) {
				startStandardInHandler();
				
				SANITY: {
					assert this.inputHandler != null;
				}
			}
			
			return this.standardIn.getOutputStream();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#getStandardOut()
	 */
	@Override
	public InputStream getStandardOut() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (this.outputHandler == null) {
				if (!this.pipeTo.isEmpty()) {
					startPipedOutHandler();
				} else {
					startStandardOutHandler();
				}
				
				SANITY: {
					assert this.outputHandler != null;
				}
			}
			
			return this.standardOut.getInputStream();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#getStdOutLines()
	 */
	@Override
	public List<String> getStdOutLines() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getStdOutLines' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the working directory.
	 * 
	 * @return the workingDirectory
	 */
	protected File getWorkingDirectory() {
		PRECONDITIONS: {
			assert this.workingDirectory != null;
		}
		
		try {
			return this.workingDirectory;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Kill on error.
	 */
	private void killOnError() {
		// stub
		System.err.println("KILL IT WITH FIRE!");
		if (this.process != null) {
			this.process.destroy();
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#pipeFrom(org.mozkito.utilities.execution.Executable)
	 */
	@Override
	public boolean pipeFrom(final Executable executable) {
		PRECONDITIONS: {
			if (executable == null) {
				throw new NullPointerException("Piping from a null object is not allowed.");
			}
		}
		
		try {
			return executable.pipeTo(this);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#pipeTo(org.mozkito.utilities.execution.Executable...)
	 */
	@Override
	public boolean pipeTo(final Executable... executable) {
		PRECONDITIONS: {
			if (executable == null) {
				throw new NullPointerException("Piping to a null object is not allowed.");
			}
		}
		
		try {
			return this.pipeTo.addAll(Arrays.asList(executable));
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#printStats(java.io.PrintStream)
	 */
	@Override
	public void printStats(final PrintStream err) {
		PRECONDITIONS: {
			assert this.statistics != null;
		}
		
		try {
			for (final Entry<Integer, Stats> entry : this.statistics.entrySet()) {
				err.println(entry.getValue());
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#redirectStandardError(boolean)
	 */
	@Override
	public void redirectStandardError(final boolean redirect) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.redirectStandardError = redirect;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		PRECONDITIONS: {
			assert getCommand() != null;
			assert getArguments() != null;
			assert getWorkingDirectory() != null;
			assert getEnvironment() != null;
			assert this.pipeTo != null;
		}
		
		try {
			// merge command and arguments to one list
			final List<String> lineElements = new LinkedList<String>();
			String localCommand = getCommand();
			lineElements.add(localCommand);
			lineElements.addAll(Arrays.asList(getArguments()));
			
			try {
				localCommand = FileUtils.checkExecutable(localCommand);
			} catch (final ExternalExecutableException e) {
				throw new RuntimeException(e);
			}
			
			// create new ProcessBuilder
			final ProcessBuilder processBuilder = new ProcessBuilder(lineElements);
			
			/*
			 * Set the working directory to `dir`. If `dir` is null, the subsequent processes will use the current
			 * working directory of the executing java process.
			 */
			processBuilder.directory(getWorkingDirectory());
			
			/*
			 * If a environment map has been supplied, manipulate the subprocesses environment with the corresponding
			 * mappings.
			 */
			for (final String environmentVariable : getEnvironment().keySet()) {
				processBuilder.environment().put(environmentVariable, getEnvironment().get(environmentVariable));
			}
			
			if (this.redirectStandardError) {
				processBuilder.redirectErrorStream(true);
			}
			
			try {
				this.process = processBuilder.start();
				
				synchronized (this.stateMonitor) {
					this.stateMonitor.setState(ExecutorState.RUNNING);
					this.stateMonitor.notify();
				}
				
				if (!this.redirectStandardError) {
					startStandardErrHandler();
					SANITY: {
						assert this.errorHandler != null;
					}
				}
				
				// startStandardInHandler();
				//
				// SANITY: {
				// assert this.inputHandler != null;
				// }
				
				System.err.println("Waiting for process.");
				this.process.waitFor();
				System.err.println("Process finished.");
				
				this.stateMonitor.setState(ExecutorState.PROCESSED);
				
				this.terminated = true;
				this.exitValue = this.process.exitValue();
				
				try {
					this.standardIn.getOutputStream().close();
					
					if (this.inputHandler != null) {
						System.err.println("Stopping InputHandler.");
						this.inputHandler.join();
						System.err.println("Stopped InputHandler.");
					}
					
					this.standardIn.getInputStream().close();
					this.process.getOutputStream().close();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				
				try {
					if (this.outputHandler != null) {
						System.err.println("Stopping OutputHandler.");
						this.outputHandler.join();
						System.err.println("Stopped OutputHandler.");
					}
					
					try {
						this.standardOut.getOutputStream().close();
					} catch (final IOException e) {
						// ignore
					}
					
					// close the output. everything is written to the pipe
					if (!this.pipeTo.isEmpty()) {
						this.standardOut.getInputStream().close();
					}
					this.process.getInputStream().close();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				
				if (this.errorHandler != null) {
					try {
						this.standardErr.getOutputStream().close();
						
						System.err.println("Stopping ErrorHandler.");
						this.errorHandler.join();
						System.err.println("Stopped ErrorHandler.");
						
						if (!this.pipeTo.isEmpty()) {
							this.standardErr.getInputStream().close();
						}
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				this.process.getErrorStream().close();
				
			} catch (final IOException | InterruptedException e) {
				throw new RuntimeException(e);
			}
		} finally {
			this.terminated = true;
			
			POSTCONDITIONS: {
				// none
			}
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.utilities.execution.Executable#setLogger(java.io.PrintStream)
	 */
	@Override
	public boolean setLogger(final PrintStream logStream) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.logStream = logStream;
			return true;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Starts the executor thread. If the output is piped to other {@link Executable}s, start() is also called on those
	 * objects.
	 * 
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start() {
		PRECONDITIONS: {
			assert this.pipeTo != null;
		}
		
		try {
			if (!this.pipeTo.isEmpty()) {
				for (final Executable executable : this.pipeTo) {
					executable.start();
				}
			}
			if (!this.started) {
				this.started = true;
				super.start();
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Start piped out handler.
	 */
	private void startPipedOutHandler() {
		PRECONDITIONS: {
			assert this.statistics != null;
			assert this.outputHandler == null;
			assert this.process != null;
			assert this.pipeTo != null;
		}
		
		try {
			if (this.logStream != null) {
				this.logStream.println("Starting PipedOutputHander.");
			}
			
			final String name = getName() + "-PipedOutputHandler";
			final Stats stats = new Stats(name, ThreadType.PIPED_OUTPUTHANDLER);
			this.statistics.put(stats.getId(), stats);
			this.outputHandler = new Thread(name) {
				
				@Override
				public void run() {
					stats.started();
					int n = 0;
					long readCount = 0;
					long writeCount = 0;
					final byte[] buffer = new byte[2048];
					
					try {
						while ((n = Executor.this.process.getInputStream().read(buffer)) != EOF) {
							readCount += n;
							for (final Executable pipe : Executor.this.pipeTo) {
								pipe.getStandardIn().write(buffer, 0, n);
							}
							writeCount += n;
						}
					} catch (final IOException e) {
						stats.exception(e);
						throw new RuntimeException(e);
					} finally {
						stats.written(writeCount);
						stats.read(readCount);
						stats.finished();
					}
					
					SANITY: {
						assert Executor.this.pipeTo != null;
					}
					
					for (final Executable pipe : Executor.this.pipeTo) {
						try {
							pipe.getStandardIn().flush();
							pipe.getStandardIn().close();
						} catch (final IOException e) {
							if (Executor.this.logStream != null) {
								e.printStackTrace(Executor.this.logStream);
							}
						}
					}
				}
			};
			
			this.outputHandler.setUncaughtExceptionHandler(new ExceptionHandler(stats, this));
			
			this.outputHandler.start();
		} finally {
			POSTCONDITIONS: {
				assert this.outputHandler != null;
			}
		}
	}
	
	/**
	 * Start standard err handler.
	 */
	private void startStandardErrHandler() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			if (!this.redirectStandardError) {
				final String name = getName() + "-ErrorHandler";
				final Stats stats = new Stats(name, ThreadType.ERROR_HANDLER);
				this.statistics.put(stats.getId(), stats);
				this.errorHandler = new Thread(name) {
					
					@Override
					public void run() {
						stats.started();
						long writeCount = 0;
						long readCount = 0;
						int n = 0;
						final byte[] buffer = new byte[2048];
						
						try {
							while ((n = Executor.this.process.getErrorStream().read(buffer)) != EOF) {
								readCount += n;
								Executor.this.standardErr.getOutputStream().write(buffer, 0, n);
								writeCount += n;
							}
						} catch (final IOException e) {
							stats.exception(e);
							throw new RuntimeException(e);
						} finally {
							stats.written(writeCount);
							stats.read(readCount);
							stats.finished();
						}
					}
				};
				this.errorHandler.setUncaughtExceptionHandler(new ExceptionHandler(stats, this));
				this.errorHandler.start();
			}
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Start standard in handler.
	 */
	private void startStandardInHandler() {
		PRECONDITIONS: {
			assert this.statistics != null;
			assert this.inputHandler == null;
			assert this.standardIn != null;
		}
		
		try {
			final String name = getName() + "-InputHandler";
			final Stats stats = new Stats(name, ThreadType.INPUT_HANDLER);
			this.statistics.put(stats.getId(), stats);
			this.inputHandler = new Thread(name) {
				
				@Override
				public void run() {
					PRECONDITIONS: {
						// none
					}
					
					try {
						stats.started();
						int n = 0;
						long readCount = 0;
						long writeCount = 0;
						final byte[] buffer = new byte[2048];
						
						try {
							synchronized (Executor.this.stateMonitor) {
								while (!ExecutorState.RUNNING.equals(Executor.this.stateMonitor.getState())) {
									try {
										Executor.this.stateMonitor.wait();
									} catch (final InterruptedException e) {
										// TODO Auto-generated catch block
										
									}
								}
							}
							while ((n = Executor.this.standardIn.getInputStream().read(buffer)) != EOF) {
								readCount += n;
								
								if (Executor.this.process == null) {
									throw new IOException("Process hasn't been started yet.");
								}
								
								Executor.this.process.getOutputStream().write(buffer, 0, n);
								writeCount += n;
							}
						} catch (final IOException e) {
							if (!Executor.this.terminated) {
								stats.exception(e);
								throw new RuntimeException(e);
							}
						} finally {
							stats.read(readCount);
							stats.written(writeCount);
							stats.finished();
							try {
								if (Executor.this.process != null) {
									Executor.this.process.getOutputStream().flush();
									Executor.this.process.getOutputStream().close();
								}
							} catch (final IOException ignore) {
								// ignore
							}
						}
					} finally {
						POSTCONDITIONS: {
							// none
						}
					}
				}
			};
			
			this.inputHandler.setUncaughtExceptionHandler(new ExceptionHandler(stats, this));
			
			this.inputHandler.start();
		} finally {
			POSTCONDITIONS: {
				assert this.inputHandler != null;
			}
		}
	}
	
	/**
	 * Start standard out handler.
	 */
	private void startStandardOutHandler() {
		PRECONDITIONS: {
			assert this.statistics != null;
			assert this.outputHandler == null;
			assert this.standardOut != null;
			assert this.process != null;
		}
		
		try {
			final String name = getName() + "-OutputHandler";
			final Stats stats = new Stats(name, ThreadType.OUTPUT_HANDLER);
			this.statistics.put(stats.getId(), stats);
			this.outputHandler = new Thread(name) {
				
				@Override
				public void run() {
					stats.started();
					int n = 0;
					long readCount = 0;
					long writeCount = 0;
					final byte[] buffer = new byte[2048];
					
					try {
						while ((n = Executor.this.process.getInputStream().read(buffer)) != EOF) {
							readCount += n;
							Executor.this.standardOut.getOutputStream().write(buffer, 0, n);
							writeCount += n;
						}
					} catch (final IOException e) {
						stats.exception(e);
						throw new RuntimeException(e);
					} finally {
						stats.written(writeCount);
						stats.read(readCount);
						stats.finished();
					}
				}
			};
			this.outputHandler.setUncaughtExceptionHandler(new ExceptionHandler(stats, this));
			this.outputHandler.start();
		} finally {
			POSTCONDITIONS: {
				assert this.outputHandler != null;
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		
		builder.append("Executor [command=");
		builder.append(this.command);
		builder.append(", arguments=");
		builder.append(Arrays.toString(this.arguments));
		builder.append(", workingDirectory=");
		builder.append(this.workingDirectory);
		builder.append("]");
		
		return builder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws InterruptedException
	 * @see org.mozkito.utilities.execution.Executable#waitFor()
	 */
	@Override
	public int waitFor() throws InterruptedException {
		PRECONDITIONS: {
			if (!this.started) {
				throw new IllegalThreadStateException("Process hasn't bee started.");
			}
		}
		
		try {
			if ((this.exitValue == null) || !this.terminated) {
				System.err.println("Waiting for thread to die.");
				join();
			}
			return this.exitValue;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

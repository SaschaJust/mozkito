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

package net.ownhero.dev.ioda;

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

import net.ownhero.dev.ioda.exceptions.ExternalExecutableException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.lang.ArrayUtils;
import org.joda.time.DateTime;

/**
 * The Class Executor.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Executor extends Thread implements Executable {
	
	/**
	 * The Class Stats.
	 * 
	 * @author Sascha Just <sascha.just@mozkito.org>
	 */
	public class Stats {
		
		/** The timestamp the thread was created. */
		private final DateTime   created;
		
		/** The name of the thread. */
		private final String     name;
		
		/** The internal id of the thread. */
		private final int        id;
		
		/** The number of bytes that have been written. */
		private long             writtenBytes = 0l;
		
		/** Points to an exception if an error occurred. */
		private Throwable        error        = null;
		
		/** The time the thread has been started. */
		private DateTime         started      = null;
		
		/** The number of bytes that have been read. */
		private long             readBytes    = 0l;
		
		/** The time the thread terminated. */
		private DateTime         finished     = null;
		
		/** The type of the thread. @see ThreadType */
		private final ThreadType type;
		
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
		/** The input handler. */
		INPUT_HANDLER,
		/** The output handler. */
		OUTPUT_HANDLER,
		/** The error handler. */
		ERROR_HANDLER,
		/** The piped outputhandler. */
		PIPED_OUTPUTHANDLER;
	}
	
	/** The Constant EOF. */
	protected static final int EOF = -1;
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command) {
		return create(command, new String[0]);
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments) {
		return create(command, arguments, null);
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments,
	                                final File dir) {
		return create(command, arguments, dir, null);
	}
	
	/**
	 * Creates the.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @param environment
	 *            the environment
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments,
	                                final File dir,
	                                final Map<String, String> environment) {
		return create(command, arguments, dir, environment, Charset.defaultCharset());
	}
	
	/**
	 * Creates the.
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
	 * @return the executable
	 */
	public static Executable create(@NotNull final String command,
	                                @NotNull final String[] arguments,
	                                final File dir,
	                                final Map<String, String> environment,
	                                @NotNull final Charset charset) {
		return new Executor(command, arguments, dir, environment, charset);
	}
	
	/** The terminated. */
	private boolean                   terminated  = false;
	
	/** The command. */
	private String                    command;
	
	/** The arguments. */
	private String[]                  arguments;
	
	/** The working directory. */
	private File                      workingDirectory;
	
	/** The environment. */
	private Map<String, String>       environment;
	
	/** The charset. */
	private Charset                   charset;
	
	/** The pipe to. */
	private final Set<Executable>     pipeTo      = new HashSet<>();
	
	/** The standard err. */
	private final CircularByteBuffer  standardErr = new CircularByteBuffer();
	
	/** The standard out. */
	private final CircularByteBuffer  standardOut = new CircularByteBuffer();
	
	/** The standard in. */
	private final CircularByteBuffer  standardIn  = new CircularByteBuffer();
	
	/** The process. */
	private Process                   process     = null;
	
	/** The standard err handler. */
	private Thread                    errorHandler;
	
	/** The standard out handler. */
	private Thread                    outputHandler;
	
	/** The redirect standard error. */
	private boolean                   redirectStandardError;
	
	/** The input handler. */
	private Thread                    inputHandler;
	
	/** The log stream. */
	private PrintStream               logStream;
	
	/** The statistics. */
	private final Map<Integer, Stats> statistics  = new HashMap<>();
	
	private Integer                   exitValue   = null;
	
	private boolean                   started     = false;
	
	private Object                    connectedInput;
	
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
	private Executor(final String command, final String[] arguments, final File dir,
	        final Map<String, String> environment, final Charset charset) {
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
	 * @see net.ownhero.dev.ioda.Executable#connectStandardIn(java.io.InputStream)
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
	 * @see net.ownhero.dev.ioda.Executable#exitValue()
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
	 * @see net.ownhero.dev.ioda.Executable#getStandardErr()
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
	 * @see net.ownhero.dev.ioda.Executable#getStandardIn()
	 */
	@Override
	public OutputStream getStandardIn() {
		PRECONDITIONS: {
			// none
		}
		
		try {
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
	 * @see net.ownhero.dev.ioda.Executable#getStandardOut()
	 */
	@Override
	public InputStream getStandardOut() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.standardOut.getInputStream();
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
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.ioda.Executable#pipeFrom(net.ownhero.dev.ioda.Executable)
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
	 * @see net.ownhero.dev.ioda.Executable#pipeTo(net.ownhero.dev.ioda.Executable...)
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
	 * @see net.ownhero.dev.ioda.Executable#printStats(java.io.PrintStream)
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
	 * @see net.ownhero.dev.ioda.Executable#redirectStandardError(boolean)
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
				
				if (!this.redirectStandardError) {
					startStandardErrHandler();
					SANITY: {
						assert this.errorHandler != null;
					}
				}
				
				if (!this.pipeTo.isEmpty()) {
					startPipedOutHandler();
				} else {
					startStandardOutHandler();
				}
				
				SANITY: {
					assert this.outputHandler != null;
				}
				
				startStandardInHandler();
				
				SANITY: {
					assert this.inputHandler != null;
				}
				
				this.process.waitFor();
				this.terminated = true;
				this.exitValue = this.process.exitValue();
				
				try {
					this.standardIn.getOutputStream().close();
					this.inputHandler.join(1000);
					this.standardIn.getInputStream().close();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				
				try {
					this.standardOut.getOutputStream().close();
					this.outputHandler.join();
					
					// close the output. everything is written to the pipe
					if (!this.pipeTo.isEmpty()) {
						this.standardOut.getInputStream().close();
					}
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
				
				if (this.errorHandler != null) {
					try {
						this.standardErr.getOutputStream().close();
						this.errorHandler.join();
						
						if (!this.pipeTo.isEmpty()) {
							this.standardErr.getInputStream().close();
						}
					} catch (final InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			} catch (final IOException | InterruptedException e) {
				throw new RuntimeException(e);
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
	 * @see net.ownhero.dev.ioda.Executable#setLogger(java.io.PrintStream)
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
			assert this.process != null;
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
							while ((n = Executor.this.standardIn.getInputStream().read(buffer)) != EOF) {
								readCount += n;
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
								Executor.this.process.getOutputStream().flush();
								Executor.this.process.getOutputStream().close();
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
	 * @see net.ownhero.dev.ioda.Executable#waitFor()
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

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.exceptions.ExternalExecutableException;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Command line process interaction wrapper class.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CommandExecutor extends Thread {
	
	/**
	 * The Enum Task.
	 * 
	 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
	 */
	enum Task {
		
		/** The reader. */
		READER,
		/** The writer. */
		WRITER;
	}
	
	/**
	 * Execute.
	 * 
	 * @param command
	 *            the command
	 * @param arguments
	 *            the arguments
	 * @param dir
	 *            the dir
	 * @param input
	 *            the input
	 * @param environment
	 *            the environment
	 * @return the tuple
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Tuple<Integer, List<String>> execute(@NotNull final String command,
	                                                   @NotNull final String[] arguments,
	                                                   final File dir,
	                                                   final InputStream input,
	                                                   @NotNull final Map<String, String> environment) throws IOException {
		return execute(command, arguments, dir, input, environment, Charset.defaultCharset());
	}
	
	/**
	 * Executes a command with the given arguments in the specified directory. Input to the program is piped from
	 * `input` if present.
	 * 
	 * @param command
	 *            the executable
	 * @param arguments
	 *            optional arguments to the program
	 * @param dir
	 *            optional working directory.
	 * @param input
	 *            optional {@link InputStream} that is piped to
	 * @param environment
	 *            additional changes to the process environment. This is null in most scenarios.
	 * @param charset
	 *            the charset
	 * @return a tuple with the program's exit code and a list of lines representing the output of the program
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Tuple<Integer, List<String>> execute(@NotNull final String command,
	                                                   @NotNull final String[] arguments,
	                                                   final File dir,
	                                                   final InputStream input,
	                                                   final Map<String, String> environment,
	                                                   @NotNull final Charset charset) throws IOException {
		// merge command and arguments to one list
		final List<String> lineElements = new LinkedList<String>();
		String localCommand = command;
		lineElements.add(localCommand);
		lineElements.addAll(Arrays.asList(arguments));
		
		final Map<String, String> environment2 = environment != null
		                                                            ? environment
		                                                            : new HashMap<String, String>();
		
		try {
			localCommand = FileUtils.checkExecutable(localCommand);
		} catch (final ExternalExecutableException e) {
			throw new IOException(e);
		}
		
		// create new ProcessBuilder
		final ProcessBuilder processBuilder = new ProcessBuilder(lineElements);
		
		/*
		 * Set the working directory to `dir`. If `dir` is null, the subsequent processes will use the current working
		 * directory of the executing java process.
		 */
		processBuilder.directory(dir);
		
		/*
		 * If a environment map has been supplied, manipulate the subprocesses environment with the corresponding
		 * mappings.
		 */
		for (final String environmentVariable : environment2.keySet()) {
			processBuilder.environment().put(environmentVariable, environment2.get(environmentVariable));
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Executing: [command:" + localCommand + "][arguments:"
			        + StringEscapeUtils.escapeJava(Arrays.toString(arguments)) + "][workingdir:"
			        + (dir != null
			                      ? dir.getAbsolutePath()
			                      : "(null)") + "][input:" + (input != null
			                                                               ? "present"
			                                                               : "omitted") + "][environment:"
			        + StringEscapeUtils.escapeJava(JavaUtils.mapToString(processBuilder.environment())) + "]");
		}
		
		// Merge stdout and stderr to one stream
		processBuilder.redirectErrorStream(true);
		
		Process process;
		
		try {
			// start the process
			process = processBuilder.start();
			
			// get the streams
			final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			BufferedReader pipe = null;
			
			CommandExecutor readTask = null;
			CommandExecutor writeTask = null;
			
			if (input != null) {
				pipe = new BufferedReader(new InputStreamReader(input));
				// create writing thread
				writeTask = new CommandExecutor(Task.WRITER, reader, writer, pipe);
			}
			
			// create reading thread
			readTask = new CommandExecutor(Task.READER, reader, writer, pipe);
			
			// run threads
			readTask.start();
			if (writeTask != null) {
				writeTask.start();
			}
			
			if (writeTask != null) {
				writeTask.join();
			}
			
			final int returnValue = process.waitFor();
			
			// wait for threads to finish
			readTask.join();
			
			writer.close();
			reader.close();
			if (pipe != null) {
				pipe.close();
			}
			
			// wait for the process (this should return instantly if no error
			// occurred
			
			if (readTask.error || ((writeTask != null) && writeTask.error)) {
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("Executed: [command:");
				stringBuilder.append(localCommand);
				stringBuilder.append("][arguments:");
				stringBuilder.append(StringEscapeUtils.escapeJava(Arrays.toString(arguments)));
				stringBuilder.append("][workingdir:");
				stringBuilder.append((dir != null
				                                 ? dir.getAbsolutePath()
				                                 : "(null)"));
				stringBuilder.append("][input:");
				stringBuilder.append((input != null
				                                   ? "present"
				                                   : "omitted"));
				stringBuilder.append("][environment:");
				stringBuilder.append(StringEscapeUtils.escapeJava(JavaUtils.mapToString(processBuilder.environment())));
				stringBuilder.append("] failed with exitCode: ");
				stringBuilder.append(returnValue);
				final String logMessage = stringBuilder.toString();
				if (Logger.logDebug()) {
					Logger.debug(logMessage);
					readTask.logLinesOnError();
					if (writeTask != null) {
						writeTask.logLinesOnError();
					}
				}
				throw new IOException(logMessage);
			}
			
			return new Tuple<Integer, List<String>>(returnValue, readTask.getReadLines());
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			return new Tuple<Integer, List<String>>(-1, null);
		}
	}
	
	/**
	 * wrapper for the simple class name.
	 * 
	 * @return the class name
	 */
	public static String getClassName() {
		return CommandExecutor.class.getSimpleName();
	}
	
	/** The error. */
	private boolean              error      = false;
	
	/*
	 * input
	 */
	/** The pipe. */
	private final BufferedReader pipe;
	
	/*
	 * stdin
	 */
	/** The reader. */
	private final BufferedReader reader;
	
	/** The read lines. */
	private final List<String>   readLines  = new LinkedList<String>();
	
	/** The task. */
	private final Task           task;
	
	/*
	 * stdout
	 */
	/** The writer. */
	private final BufferedWriter writer;
	
	/** The wrote lines. */
	private final List<String>   wroteLines = new LinkedList<String>();
	
	/**
	 * Thread handler to communicate with external processes.
	 * 
	 * @param task
	 *            determines task, i.e. communication with STDIN/STDOU/...
	 * @param reader
	 *            the reader
	 * @param writer
	 *            the writer
	 * @param pipe
	 *            the pipe
	 */
	private CommandExecutor(final Task task, final BufferedReader reader, final BufferedWriter writer,
	        final BufferedReader pipe) {
		if (Logger.logDebug()) {
			Logger.debug("Spawning " + getClassName() + "[" + task.toString() + "] ");
		}
		this.task = task;
		this.reader = reader;
		this.writer = writer;
		this.pipe = pipe;
		setName(getClassName() + "-" + task.toString());
	}
	
	/**
	 * Gets the read lines.
	 * 
	 * @return the read lines
	 */
	private List<String> getReadLines() {
		return this.readLines;
	}
	
	/**
	 * Gets the wrote lines.
	 * 
	 * @return the wrote lines
	 */
	private List<String> getWroteLines() {
		return this.wroteLines;
	}
	
	/**
	 * in case of an error, this method can be called to log the already processed lines.
	 */
	private void logLinesOnError() {
		if (Logger.logError()) {
			Logger.error(getClassName() + "[" + this.task.toString() + "] lines processed:");
			for (final String outputLine : getReadLines()) {
				Logger.error("read<< " + outputLine);
			}
			for (final String outputLine : getWroteLines()) {
				Logger.error("wrote<< " + outputLine);
			}
		}
	}
	
	/**
	 * handles the STDOUT communication.
	 */
	private void reading() {
		String line;
		try {
			while ((line = this.reader.readLine()) != null) {
				this.readLines.add(line);
			}
		} catch (final IOException e) {
			this.error = true;
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	/**
	 * Run.
	 */
	@Override
	public void run() {
		switch (this.task) {
			case READER:
				reading();
				break;
			case WRITER:
				writing();
				break;
			default:
				if (Logger.logError()) {
					Logger.error("Unsupported task " + this.task + " for " + CommandExecutor.getClassName() + ".");
				}
		}
	}
	
	/**
	 * handles the STDIN communication.
	 */
	private void writing() {
		String line;
		
		try {
			while ((line = this.pipe.readLine()) != null) {
				this.wroteLines.add(line);
				this.writer.write(line);
				this.writer.write(FileUtils.lineSeparator);
			}
			this.writer.flush();
			this.writer.close();
		} catch (final IOException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			this.error = true;
		}
	}
}

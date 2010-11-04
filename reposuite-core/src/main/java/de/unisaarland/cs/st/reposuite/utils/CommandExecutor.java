package de.unisaarland.cs.st.reposuite.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import de.unisaarland.cs.st.reposuite.exceptions.ExternalExecutableException;

/**
 * Command line process interaction wrapper class
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CommandExecutor extends Thread {
	
	/**
	 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
	 * 
	 */
	enum Task {
		READER, WRITER;
	}
	
	/**
	 * @param commandLine
	 * @param dir
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public static Tuple<Integer, List<String>> execute(final String commandLine, final File dir) {
		return execute(commandLine, dir, null);
	}
	
	/**
	 * @param commandLine
	 * @param dir
	 * @param line
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public static Tuple<Integer, List<String>> execute(final String commandLine, final File dir, final String line) {
		assert (commandLine != null);
		
		String[] split = commandLine.split(" ");
		String[] arguments = new String[split.length - 1];
		
		for (int i = 1; i < split.length; ++i) {
			arguments[i - 1] = split[i];
		}
		
		ByteArrayInputStream input = line != null ? new ByteArrayInputStream(line.getBytes()) : null;
		return execute(split[0], arguments, dir, input, null);
	}
	
	/**
	 * Executes a command with the given arguments in the specified directory.
	 * Input to the program is piped from `input` if present.
	 * 
	 * @param command
	 *            the executable
	 * @param arguments
	 *            optional arguments to the program
	 * @param dir
	 *            optional working directory. If unspecified the temporary
	 *            directory from {@link FileUtils.tmpDir} is used.
	 * @param input
	 *            optional {@link InputStream} that is piped to
	 * @param environment
	 *            additional changes to the process environment. This is null in
	 *            most scenarios.
	 * @return a tuple with the program's exit code and a list of lines
	 *         representing the output of the program
	 */
	public static Tuple<Integer, List<String>> execute(String command, final String[] arguments, final File dir,
	        final InputStream input, final Map<String, String> environment) {
		assert (command != null);
		assert ((arguments == null) || (arguments.length > 0));
		
		// merge command and arguments to one list
		List<String> lineElements = new LinkedList<String>();
		lineElements.add(command);
		if (arguments != null) {
			lineElements.addAll(Arrays.asList(arguments));
		}
		
		try {
			command = FileUtils.checkExecutable(command);
		} catch (ExternalExecutableException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return new Tuple<Integer, List<String>>(-1, null);
		}
		
		// create new ProcessBuilder
		ProcessBuilder processBuilder = new ProcessBuilder(lineElements);
		
		/*
		 * Set the working directory to `dir`. If `dir` is null, the subsequent
		 * processes will use the current working directory of the executing
		 * java process.
		 */
		processBuilder.directory(dir);
		
		/*
		 * If a environment map has been supplied, manipulate the subprocesses
		 * environment with the corresponding mappings.
		 */
		if ((environment != null) && (environment.keySet().size() > 0)) {
			Map<String, String> actualEnvironment = processBuilder.environment();
			for (String environmentVariable : environment.keySet()) {
				actualEnvironment.put(environmentVariable, environment.get(environmentVariable));
			}
		}
		
		if (Logger.logDebug()) {
			Logger.debug("Executing: [command:"
			        + command
			        + "][arguments:"
			        + StringEscapeUtils.escapeJava(Arrays.toString(arguments))
			        + "][workingdir:"
			        + (dir != null ? dir.getAbsolutePath() : "(null)")
			        + "][input:"
			        + (input != null ? "present" : "omitted")
			        + "][environment:"
			        + StringEscapeUtils.escapeJava(JavaUtils.mapToString(environment != null ? environment : System
			                .getenv())) + "]");
		}
		
		// Merge stdout and stderr to one stream
		processBuilder.redirectErrorStream(true);
		
		Process process;
		
		try {
			// start the process
			process = processBuilder.start();
			
			// get the streams
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			BufferedReader pipe = null;
			
			CommandExecutor readTask = null;
			CommandExecutor writeTask = null;
			
			if (input != null) {
				pipe = new BufferedReader(new InputStreamReader(input));
				// create writing thread
				writeTask = new CommandExecutor(Task.WRITER, reader, writer, pipe);
				writeTask.setName(CommandExecutor.getHandle() + writeTask.getTask().toString());
			}
			
			// create reading thread
			readTask = new CommandExecutor(Task.READER, reader, writer, pipe);
			readTask.setName(CommandExecutor.getHandle() + readTask.getTask().toString());
			
			// run threads
			readTask.start();
			if (writeTask != null) {
				writeTask.start();
			}
			
			if (writeTask != null) {
				writeTask.join();
			}
			
			int returnValue = process.waitFor();
			
			// wait for threads to finish
			readTask.join(10);
			
			writer.close();
			reader.close();
			if (pipe != null) {
				pipe.close();
			}
			
			// wait for the process (this should return instantly if no error
			// occurred
			
			if ((returnValue != 0) || readTask.error || ((writeTask != null) && writeTask.error)) {
				if (Logger.logError()) {
					StringBuilder stringBuilder = new StringBuilder();
					stringBuilder.append("Executed: [command:");
					stringBuilder.append(command);
					stringBuilder.append("][arguments:");
					stringBuilder.append(StringEscapeUtils.escapeJava(Arrays.toString(arguments)));
					stringBuilder.append("][workingdir:");
					stringBuilder.append((dir != null ? dir.getAbsolutePath() : "(null)"));
					stringBuilder.append("][input:");
					stringBuilder.append((input != null ? "present" : "omitted"));
					stringBuilder.append("][environment:");
					stringBuilder.append(StringEscapeUtils.escapeJava(JavaUtils
					        .mapToString(environment != null ? environment : System.getenv())));
					stringBuilder.append("] failed with exitCode: ");
					stringBuilder.append(returnValue);
					Logger.error(stringBuilder.toString());
					readTask.logLinesOnError();
					if (writeTask != null) {
						writeTask.logLinesOnError();
					}
				}
			}
			return new Tuple<Integer, List<String>>(returnValue, readTask.getReadLines());
		} catch (Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			return new Tuple<Integer, List<String>>(-1, null);
		}
	}
	
	/**
	 * wrapper for the simple class name
	 * 
	 * @return
	 */
	public static String getHandle() {
		return CommandExecutor.class.getSimpleName();
	}
	
	private boolean              error      = false;
	
	/*
	 * input
	 */
	private final BufferedReader pipe;
	
	/*
	 * stdin
	 */
	private final BufferedReader reader;
	
	private final List<String>   readLines  = new LinkedList<String>();
	
	private final Task           task;
	
	/*
	 * stdout
	 */
	private final BufferedWriter writer;
	
	private final List<String>   wroteLines = new LinkedList<String>();
	
	/**
	 * Thread handler to communicate with external processes.
	 * 
	 * @param task
	 *            determines task, i.e. communication with STDIN/STDOU/...
	 * @param reader
	 * @param writer
	 * @param pipe
	 */
	private CommandExecutor(final Task task, final BufferedReader reader, final BufferedWriter writer,
	        final BufferedReader pipe) {
		if (Logger.logDebug()) {
			Logger.debug("Spawning " + getHandle() + "[" + task.toString() + "] ");
		}
		this.task = task;
		this.reader = reader;
		this.writer = writer;
		this.pipe = pipe;
	}
	
	/**
	 * @return the read lines
	 */
	private List<String> getReadLines() {
		return readLines;
	}
	
	/**
	 * @return the task
	 */
	private Task getTask() {
		return task;
	}
	
	/**
	 * @return the wrote lines
	 */
	private List<String> getWroteLines() {
		return wroteLines;
	}
	
	/**
	 * in case of an error, this method can be called to log the already
	 * processed lines
	 */
	private void logLinesOnError() {
		if (Logger.logError()) {
			Logger.error(getHandle() + "[" + task.toString() + "] lines processed:");
			for (String outputLine : getReadLines()) {
				Logger.error("read<< " + outputLine);
			}
			for (String outputLine : getWroteLines()) {
				Logger.error("wrote<< " + outputLine);
			}
		}
	}
	
	/**
	 * handles the STDOUT communication
	 */
	private void reading() {
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				readLines.add(line);
			}
		} catch (IOException e) {
			error = true;
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		switch (task) {
			case READER:
				reading();
				break;
			case WRITER:
				writing();
				break;
			default:
				if (Logger.logError()) {
					Logger.error("Unsupported task " + task + " for " + CommandExecutor.getHandle() + ".");
				}
		}
	}
	
	/**
	 * handles the STDIN communication
	 */
	private void writing() {
		String line;
		
		try {
			while ((line = pipe.readLine()) != null) {
				wroteLines.add(line);
				writer.write(line);
				writer.write(FileUtils.lineSeparator);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			error = true;
		}
	}
}

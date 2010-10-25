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

import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;

/**
 * Command line process interaction wrapper class
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class CommandExecutor extends Thread {
	
	enum Task {
		READER, WRITER;
	}
	
	@Deprecated
	public static Tuple<Integer, List<String>> execute(String commandLine, File dir) {
		return execute(commandLine, dir, null);
	}
	
	@Deprecated
	public static Tuple<Integer, List<String>> execute(String commandLine, File dir, String line) {
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
	 *            additional changes to the process environment
	 * @return a tuple with the program's exit code and a list of lines
	 *         representing the output of the program
	 */
	public static Tuple<Integer, List<String>> execute(String command, String[] arguments, File dir, InputStream input,
	        Map<String, String> environment) {
		// merge command and arguments to one list
		List<String> lineElements = new LinkedList<String>();
		lineElements.add(command);
		lineElements.addAll(Arrays.asList(arguments));
		
		if (RepoSuiteSettings.logDebug()) {
			Logger.debug("Executing: [command:" + command + "][" + Arrays.toString(arguments) + "][dir:"
			        + (dir != null ? dir.getAbsolutePath() : "(null)") + "][input:"
			        + (input != null ? "present" : "omitted") + "][environment:"
			        + JavaUtils.mapToString(environment != null ? environment : System.getenv()) + "]");
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
		
		// Merge stdout and stderr to one stream
		processBuilder.redirectErrorStream();
		
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
			
			// wait for threads to finish
			readTask.join(3000);
			if (writeTask != null) {
				writeTask.join(3000);
			}
			
			writer.close();
			reader.close();
			if (pipe != null) {
				pipe.close();
			}
			
			if (readTask.error || ((writeTask != null) && writeTask.error)) {
				readTask.logLinesOnError();
				if (writeTask != null) {
					writeTask.logLinesOnError();
				}
			}
			
			// wait for the process (this should return instantly if no error occurred
			int waitFor = process.waitFor();
			
			return new Tuple<Integer, List<String>>(waitFor, readTask.getReadLines());
		} catch (Exception e) {
			if (RepoSuiteSettings.logError()) {
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
	private CommandExecutor(Task task, BufferedReader reader, BufferedWriter writer, BufferedReader pipe) {
		if (RepoSuiteSettings.logDebug()) {
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
		return this.readLines;
	}
	
	/**
	 * @return the task
	 */
	private Task getTask() {
		return this.task;
	}
	
	/**
	 * @return the wrote lines
	 */
	private List<String> getWroteLines() {
		return this.wroteLines;
	}
	
	/**
	 * in case of an error, this method can be called to log the already
	 * processed lines
	 */
	private void logLinesOnError() {
		if (RepoSuiteSettings.logDebug()) {
			Logger.debug(getHandle() + "[" + this.task.toString() + "] lines processed:");
			for (String outputLine : this.getReadLines()) {
				Logger.error("read<< " + outputLine);
			}
			for (String outputLine : this.getWroteLines()) {
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
			while ((line = this.reader.readLine()) != null) {
				this.readLines.add(line);
			}
		} catch (IOException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			this.error = true;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
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
				if (RepoSuiteSettings.logError()) {
					Logger.error("Unsupported task " + this.task + " for " + CommandExecutor.getHandle() + ".");
				}
		}
	}
	
	/**
	 * handles the STDIN communication
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
		} catch (IOException e) {
			if (RepoSuiteSettings.logError()) {
				Logger.error(e.getMessage(), e);
			}
			this.error = true;
		}
	}
}

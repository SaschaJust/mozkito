/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.mozkito.testing.MozkitoSuite.TestResult;
import org.mozkito.testing.MozkitoSuite.TestRun;


/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public final class MozkitoTestBuilder extends Thread {
	
	private final TestRun     testRun;
	
	private TestResult        testResult;
	
	private final RunNotifier runNotifier;
	
	private final String      classPath;
	private final String      javaBin;
	private final String      testClassName;
	private final String      testMethodName;
	
	private final String      exampleCommandLine;
	
	private final String      testTag;
	
	/**
	 * @param testRun
	 */
	public MozkitoTestBuilder(final TestRun testRun, final RunNotifier runNotifier) {
		this.testRun = testRun;
		this.runNotifier = runNotifier;
		this.testTag = "[" + testRun.getDescription().getMethodName() + "] ";
		
		this.javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		this.classPath = System.getProperty("java.class.path") + File.pathSeparator + "src" + File.separator + "main"
		        + File.separator + "java ";
		
		this.testClassName = testRun.getDescription().getTestClass().getCanonicalName();
		this.testMethodName = testRun.getDescription().getMethodName();
		this.exampleCommandLine = this.javaBin + " -cp " + this.classPath + " " + MozkitoTest.class.getCanonicalName()
		        + " " + this.testClassName + " " + this.testMethodName + " <STDOUT_FILE> <STDERR_FILE>";
	}
	
	/**
	 * @param run
	 * @return
	 */
	@NoneNull
	private TestResult exec() {
		String testStdOut = null;
		String testStdErr = null;
		
		final File stdOutFile = FileUtils.createRandomFile(FileShutdownAction.KEEP);
		String stdOutPath = null;
		try {
			FileUtils.ensureFilePermissions(stdOutFile, FileUtils.WRITABLE_FILE);
			stdOutPath = stdOutFile.getCanonicalPath();
		} catch (final FilePermissionException e) {
			// TODO: handle exception
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final File stdErrFile = FileUtils.createRandomFile(FileShutdownAction.KEEP);
		String stdErrPath = null;
		try {
			FileUtils.ensureFilePermissions(stdErrFile, FileUtils.WRITABLE_FILE);
			stdErrPath = stdErrFile.getCanonicalPath();
		} catch (final FilePermissionException e) {
			// TODO: handle exception
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final List<String> commandList = new LinkedList<>();
		commandList.add(this.javaBin);
		for (final Object property : System.getProperties().keySet()) {
			commandList.add(String.format("-D%s=%s", property, System.getProperty((String) property)));
		}
		commandList.add("-cp");
		commandList.add(this.classPath);
		commandList.add(MozkitoTest.class.getCanonicalName());
		commandList.add(this.testClassName);
		commandList.add(this.testMethodName);
		commandList.add(stdOutPath);
		commandList.add(stdErrPath);
		final ProcessBuilder builder = new ProcessBuilder(commandList);
		
		if (System.getProperty("test.debug") != null) {
			System.err.println("Launching test: " + this.testClassName + "#" + this.testMethodName);
		}
		
		Process process;
		int exitValue = -1;
		String theError = null;
		String theLog = null;
		try {
			process = builder.start();
			process.waitFor();
			if (System.getProperty("test.debug") != null) {
				System.err.println("Test finished.");
			}
			exitValue = process.exitValue();
			final StringWriter testErrorWriter = new StringWriter();
			IOUtils.copy(process.getErrorStream(), testErrorWriter);
			theError = testErrorWriter.toString();
			
			final StringWriter testOutWriter = new StringWriter();
			IOUtils.copy(process.getInputStream(), testOutWriter);
			theLog = testOutWriter.toString();
		} catch (final IOException e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			pw.println(e.getMessage());
			pw.println(FileUtils.lineSeparator);
			e.printStackTrace(pw);
			pw.println(FileUtils.lineSeparator);
			theError = sw.toString();
		} catch (final InterruptedException e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			pw.println(e.getMessage());
			pw.println(FileUtils.lineSeparator);
			e.printStackTrace(pw);
			pw.println(FileUtils.lineSeparator);
			theError = sw.toString();
		}
		
		try (final BufferedReader reader = new BufferedReader(new FileReader(stdErrFile));) {
			String line;
			final StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(this.testTag).append(line).append(FileUtils.lineSeparator);
			}
			testStdErr = sb.toString();
			stdErrFile.delete();
		} catch (final IOException e) {
			// TODO: handle exception
		}
		
		try (final BufferedReader reader = new BufferedReader(new FileReader(stdOutFile));) {
			String line;
			final StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(this.testTag).append(line).append(FileUtils.lineSeparator);
			}
			testStdOut = sb.toString();
			stdOutFile.delete();
		} catch (final IOException e) {
			// TODO: handle exception
		}
		
		return new MozkitoSuite.TestResult(exitValue, theLog, theError, testStdOut, testStdErr);
	}
	
	/**
	 * @return
	 */
	public TestResult getTestResult() {
		return this.testResult;
	}
	
	/**
	 * @return
	 */
	public TestRun getTestRun() {
		return this.testRun;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Failure failure = null;
		this.runNotifier.fireTestStarted(this.testRun.getDescription());
		try {
			final String testTag = "[" + this.testRun.getDescription().getMethodName() + "] "; //$NON-NLS-1$ //$NON-NLS-2$
			System.err.println(testTag + "Running test (" + this.exampleCommandLine + ")."); //$NON-NLS-2$
			final DateTime start = new DateTime();
			this.testResult = exec();
			final DateTime end = new DateTime();
			System.err.println(testTag + "Test " + (this.testResult.getReturnValue() != 0
			                                                                             ? "failed"
			                                                                             : "succeeded") + " after "
			        + new Period(start, end).toString(PeriodFormat.getDefault()) + ".");
			if (this.testResult.getReturnValue() != 0) {
				System.err.println(testTag + "========== STRACKTRACE ==========");
				System.err.print(this.testResult.getTestError());
				System.err.println(testTag + "========== Moskito-LOG ==========");
				System.err.print(this.testResult.getTestLog());
				System.err.println(testTag + "========== TEST-STDOUT ==========");
				System.err.print(this.testResult.getTestStdOut());
				System.err.println(testTag + "========== TEST-STDERR ==========");
				System.err.print(this.testResult.getTestStdErr());
				throw new AssertionError(this.testResult.getTestError());
			}
		} catch (final AssertionError e) {
			failure = new Failure(this.testRun.getDescription(), e);
			this.testRun.setFailure(failure);
			
			this.runNotifier.fireTestFailure(failure);
		}
		this.runNotifier.fireTestFinished(this.testRun.getDescription());
	}
}

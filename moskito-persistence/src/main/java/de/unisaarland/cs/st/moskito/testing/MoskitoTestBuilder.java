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
package de.unisaarland.cs.st.moskito.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.ownhero.dev.andama.utils.AndamaUtils;
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

import de.unisaarland.cs.st.moskito.testing.MoskitoSuite.TestResult;
import de.unisaarland.cs.st.moskito.testing.MoskitoSuite.TestRun;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public final class MoskitoTestBuilder extends Thread {
	
	private final TestRun     testRun;
	
	private TestResult        testResult;
	
	private final RunNotifier runNotifier;
	
	/**
	 * @param testRun
	 */
	public MoskitoTestBuilder(final TestRun testRun, final RunNotifier runNotifier) {
		this.testRun = testRun;
		this.runNotifier = runNotifier;
	}
	
	/**
	 * @param run
	 * @return
	 */
	@NoneNull
	private TestResult exec(final TestRun run) {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String classpath = System.getProperty("java.class.path");
		String testStdOut = null;
		String testStdErr = null;
		final String testTag = "[" + run.getDescription().getMethodName() + "] ";
		
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
		
		final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath + ":src/main/java",
		                                                  MoskitoTest.class.getCanonicalName(), run.getDescription()
		                                                                                           .getTestClass()
		                                                                                           .getCanonicalName(),
		                                                  run.getDescription().getMethodName(), stdOutPath, stdErrPath);
		
		if (System.getProperty("test.debug") != null) {
			System.err.println("Launching test: " + run.getDescription().getTestClass().getCanonicalName() + "#"
			        + run.getDescription().getMethodName());
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
			pw.println(AndamaUtils.lineSeparator);
			e.printStackTrace(pw);
			pw.println(AndamaUtils.lineSeparator);
			theError = sw.toString();
		} catch (final InterruptedException e) {
			final StringWriter sw = new StringWriter();
			final PrintWriter pw = new PrintWriter(sw);
			pw.println(e.getMessage());
			pw.println(AndamaUtils.lineSeparator);
			e.printStackTrace(pw);
			pw.println(AndamaUtils.lineSeparator);
			theError = sw.toString();
		}
		
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(stdErrFile));
			String line;
			final StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(testTag).append(line).append(AndamaUtils.lineSeparator);
			}
			testStdErr = sb.toString();
			stdErrFile.delete();
		} catch (final IOException e) {
			// TODO: handle exception
		}
		
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(stdOutFile));
			String line;
			final StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(testTag).append(line).append(AndamaUtils.lineSeparator);
			}
			testStdOut = sb.toString();
			stdOutFile.delete();
		} catch (final IOException e) {
			// TODO: handle exception
		}
		
		return new MoskitoSuite.TestResult(exitValue, theLog, theError, testStdOut, testStdErr);
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
			final String testTag = "[" + this.testRun.getDescription().getMethodName() + "] ";
			System.err.println(testTag + "Running test.");
			final DateTime start = new DateTime();
			this.testResult = exec(this.testRun);
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

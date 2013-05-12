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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

/**
 * The Class ExecutorTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ExecutorTest {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main(final String[] args) throws IOException {
		final Executable exec = Execution.create("git", new String[] { "diff" },
		                                         new File("/Users/just/Development/mozkito"));
		exec.start();
		
		final BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getStandardOut()));
		final String output = reader.readLine();
		reader.close();
		
		try {
			exec.waitFor();
		} catch (final InterruptedException e) {
			// ignore
			e.printStackTrace();
		}
		
		final boolean modified = (exec.exitValue() == 0) || !output.isEmpty();
		
		System.err.println((modified
		                            ? ""
		                            : "not ") + "modified");
		
		exec.printStats(System.err);
	}
	
	/**
	 * Main2.
	 * 
	 * @param args
	 *            the args
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void main2(final String[] args) throws IOException {
		final Pattern pattern = Pattern.compile("^\\p{XDigit}+$");
		System.getProperty("line.separator");
		Executable exec = Execution.create("git", new String[] { "log", "HEAD^..HEAD", "--pretty=format:%H" },
		                                   new File("/Users/just/Development/mozkito"));
		exec.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(exec.getStandardOut()));
		String output = reader.readLine();
		try {
			exec.waitFor();
		} catch (final InterruptedException e) {
			// ignore
		}
		if (output.isEmpty()) {
			System.err.println("Getting git head revision failed.");
			return;
		} else {
			if ((exec.exitValue() != 0) || output.isEmpty() || output.startsWith("fatal:")) {
				System.err.println("Getting git head revision failed: " + output);
			} else {
				final Matcher matcher = pattern.matcher(output);
				if (matcher.matches()) {
					System.err.println("Head: " + output);
				} else {
					System.err.println("Getting git head revision failed. Output is not a hash: " + output);
				}
			}
		}
		
		exec = Execution.create("git", new String[] { "diff" }, new File("/Users/just/Development/mozkito"));
		exec.start();
		try {
			exec.waitFor();
		} catch (final InterruptedException e) {
			// ignore
		}
		
		reader = new BufferedReader(new InputStreamReader(exec.getStandardOut()));
		output = reader.readLine();
		final boolean modified = (exec.exitValue() == 0) || !output.isEmpty();
		
		System.err.println((modified
		                            ? ""
		                            : "not ") + "modified");
	}
	
	/**
	 * Test advanced piping.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	@Ignore
	public final void testAdvancedPiping() throws InterruptedException {
		final ByteArrayInputStream inputStream = new ByteArrayInputStream("testAdvancedPiping".getBytes());
		final Executable executable = Execution.create("cat");
		final Executable executable2 = Execution.create("sed", new String[] { "-e", "s:\\([A-Z]\\): \\1:g" });
		final Executable executable3 = Execution.create("tr", new String[] { "[:lower:]", "[:upper:]" });
		final Executable executable4 = Execution.create("awk", new String[] { "{ print $3 \" \" $2 \" \" $1; }" });
		executable.connectStandardIn(inputStream);
		executable.pipeTo(executable2);
		executable3.pipeFrom(executable2);
		executable2.pipeTo(executable4);
		
		executable.start();
		
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(executable3.getStandardOut()))) {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			executable3.printStats(System.err);
		}
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(executable4.getStandardOut()))) {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			executable4.waitFor();
			executable4.printStats(System.err);
		}
		
		assertEquals(new Integer(0), executable.exitValue());
		assertEquals(new Integer(0), executable2.exitValue());
		assertEquals(new Integer(0), executable3.exitValue());
		assertEquals(new Integer(0), executable4.exitValue());
	}
	
	/**
	 * Test input.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	@Ignore
	public final void testInput() throws InterruptedException {
		final Executable executable = Execution.create("cat");
		final ByteArrayInputStream inputStream = new ByteArrayInputStream("testInput".getBytes());
		executable.connectStandardIn(inputStream);
		executable.start();
		
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(executable.getStandardOut()))) {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			executable.waitFor();
			executable.printStats(System.err);
		}
		
		assertEquals(new Integer(0), executable.exitValue());
	}
	
	/**
	 * Test.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	@Ignore
	public final void testSimple() throws InterruptedException {
		final Executable executable = Execution.create("echo", new String[] { "testSimple" });
		executable.start();
		executable.waitFor();
		assertEquals(new Integer(0), executable.exitValue());
		
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(executable.getStandardOut()))) {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			executable.waitFor();
			executable.printStats(System.err);
		}
		
		assertEquals(new Integer(0), executable.exitValue());
	}
	
	/**
	 * Test.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	@Ignore
	public final void testSimplePipe() throws InterruptedException {
		final Executable executable = Execution.create("echo", new String[] { "testSimplePipe" });
		final Executable executable2 = Execution.create("cat");
		
		executable.setLogger(System.err);
		executable2.setLogger(System.err);
		
		executable.pipeTo(executable2);
		
		executable.start();
		
		String line;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(executable2.getStandardOut()))) {
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch (final IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} finally {
			executable2.waitFor();
			executable.printStats(System.err);
			executable2.printStats(System.err);
		}
		
		assertEquals(new Integer(0), executable.exitValue());
		assertEquals(new Integer(0), executable2.exitValue());
		
	}
	
}

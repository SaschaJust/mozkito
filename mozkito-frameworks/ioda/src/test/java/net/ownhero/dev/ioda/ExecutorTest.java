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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class ExecutorTest {
	
	/**
	 * Process builder cat.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws InterruptedException
	 */
	@Test
	public final void processBuilderCat() throws IOException, InterruptedException {
		final ProcessBuilder builder = new ProcessBuilder("/Users/just/test");
		final Process process = builder.start();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		writer.write("processBuilderCat");
		writer.newLine();
		writer.flush();
		writer.close();
		process.waitFor();
		System.out.println(new BufferedReader(new InputStreamReader(process.getInputStream())).readLine());
	}
	
	/**
	 * Test advanced piping.
	 * 
	 * @throws InterruptedException
	 *             the interrupted exception
	 */
	@Test
	public final void testAdvancedPiping() throws InterruptedException {
		final ByteArrayInputStream inputStream = new ByteArrayInputStream("testAdvancedPiping".getBytes());
		final Executable executable = Executor.create("cat");
		final Executable executable2 = Executor.create("sed", new String[] { "-e", "s:\\([A-Z]\\): \\1:g" });
		final Executable executable3 = Executor.create("tr", new String[] { "[:lower:]", "[:upper:]" });
		final Executable executable4 = Executor.create("awk", new String[] { "{ print $3 \" \" $2 \" \" $1; }" });
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
	 */
	@Test
	public final void testInput() throws InterruptedException {
		final Executable executable = Executor.create("cat");
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
	 */
	@Test
	public final void testSimple() throws InterruptedException {
		final Executable executable = Executor.create("echo", new String[] { "testSimple" });
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
	 */
	@Test
	public final void testSimplePipe() throws InterruptedException {
		final Executable executable = Executor.create("echo", new String[] { "testSimplePipe" });
		final Executable executable2 = Executor.create("cat");
		
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

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

package net.ownhero.dev.kisa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LoggerTest {
	
	private String      fileName;
	private File        classLogFile;
	private File        logFile;
	private Properties  orgProperties;
	private File        stdoutFile;
	private PrintStream orgStdOut;
	private PrintStream errStream;
	
	@Before
	public void setUp() throws Exception {
		this.orgProperties = (Properties) System.getProperties().clone();
		// TODO this will fail on concurrent test runs. use FileUtils temporary
		// file provider?
		this.fileName = System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") + "testFileLog.log";
		this.logFile = new File(this.fileName);
		// TODO same here
		this.stdoutFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
		        + "testLog.stdout");
		
		this.orgStdOut = System.err;
		this.errStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(this.stdoutFile)));
		System.setErr(this.errStream);
		
		// TODO and here
		this.classLogFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator")
		        + "testClassLog.log");
	}
	
	@After
	public void tearDown() throws Exception {
		this.logFile.delete();
		this.stdoutFile.delete();
		this.classLogFile.delete();
		System.setProperties(this.orgProperties);
		System.setErr(this.orgStdOut);
	}
	
	@SuppressWarnings ("null")
	@Test
	public void testAll() {
		System.setProperty("log.console.level", "DEBUG");
		System.setProperty("log.file", this.fileName);
		System.setProperty("log.file.level", "WARN");
		System.setProperty("log.class.net.ownhero.dev.kisa.Logger", "DEBUG," + this.classLogFile.getAbsolutePath());
		
		assertEquals(LogLevel.DEBUG, Logger.getLogLevel());
		
		if (Logger.logTrace()) {
			Logger.trace("trace");
		}
		if (Logger.logDebug()) {
			Logger.debug("debug");
		}
		if (Logger.logInfo()) {
			Logger.info("info");
		}
		if (Logger.logWarn()) {
			Logger.warn("warn");
		}
		if (Logger.logError()) {
			Logger.error("error");
		}
		Logger.testDebug();
		
		assertTrue(this.logFile.exists());
		BufferedReader reader = null;
		
		// CHECK LOG FILE CONTENT
		try {
			reader = new BufferedReader(new FileReader(this.logFile));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		String line = null;
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" warn"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" error"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(line != null);
		
		this.errStream.close();
		
		// CHECK STDOUT CONTENT
		
		try {
			reader = new BufferedReader(new FileReader(this.stdoutFile));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		line = null;
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" debug"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" info"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" warn"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" error"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" This is a test debug message"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(line != null);
		
		// CHECK CLASS LOG CONTENT
		try {
			reader = new BufferedReader(new FileReader(this.classLogFile));
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		line = null;
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" This is a test debug message"));
		try {
			line = reader.readLine();
		} catch (final IOException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(line != null);
	}
	
	@Test
	public void testLevelOrder() {
		assertEquals(1, LogLevel.TRACE.compareTo(LogLevel.DEBUG));
		assertEquals(1, LogLevel.DEBUG.compareTo(LogLevel.VERBOSE));
		assertEquals(1, LogLevel.VERBOSE.compareTo(LogLevel.INFO));
		assertEquals(1, LogLevel.INFO.compareTo(LogLevel.WARN));
		assertEquals(1, LogLevel.WARN.compareTo(LogLevel.ERROR));
		assertEquals(1, LogLevel.ERROR.compareTo(LogLevel.OFF));
	}
}

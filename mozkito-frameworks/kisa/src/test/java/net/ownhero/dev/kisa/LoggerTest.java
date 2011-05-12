package net.ownhero.dev.kisa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	
	@Before
	public void setUp() throws Exception {
		orgProperties = (Properties) System.getProperties().clone();
		fileName = System.getProperty("java.io.tmpdir") + "testFileLog.log";
		logFile = new File(fileName);
		stdoutFile = new File(System.getProperty("java.io.tmpdir") + "testLog.stdout");
		orgStdOut = System.out;
		System.setOut(new PrintStream(new FileOutputStream(stdoutFile)));
		classLogFile = new File(System.getProperty("java.io.tmpdir") + "testClassLog.log");
	}
	
	@After
	public void tearDown() throws Exception {
		logFile.delete();
		stdoutFile.delete();
		classLogFile.delete();
		System.setProperties(orgProperties);
		System.setOut(orgStdOut);
	}
	
	@Test
	public void testAll() {
		System.setProperty("log.console.level", "DEBUG");
		System.setProperty("log.file", fileName);
		System.setProperty("log.file.level", "WARN");
		System.setProperty("log.class.net.ownhero.dev.kisa.Logger", "DEBUG," + classLogFile.getAbsolutePath());
		
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
		
		assertTrue(logFile.exists());
		BufferedReader reader = null;
		
		// CHECK LOG FILE CONTENT
		try {
			reader = new BufferedReader(new FileReader(logFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" warn"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" error"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(line != null);
		
		// CHECK STDOUT CONTENT
		
		try {
			reader = new BufferedReader(new FileReader(stdoutFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" debug"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" info"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" warn"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" error"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" This is a test debug message"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(line != null);
		
		// CHECK CLASS LOG CONTENT
		try {
			reader = new BufferedReader(new FileReader(classLogFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
		line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertTrue(line != null);
		assertTrue(line.endsWith(" This is a test debug message"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		assertFalse(line != null);
	}
	
	@Test
	public void testLevelOrder() {
		assertEquals(1, LogLevel.TRACE.compareTo(LogLevel.DEBUG));
		assertEquals(1, LogLevel.DEBUG.compareTo(LogLevel.INFO));
		assertEquals(1, LogLevel.INFO.compareTo(LogLevel.WARN));
		assertEquals(1, LogLevel.WARN.compareTo(LogLevel.ERROR));
		assertEquals(1, LogLevel.ERROR.compareTo(LogLevel.OFF));
	}
}

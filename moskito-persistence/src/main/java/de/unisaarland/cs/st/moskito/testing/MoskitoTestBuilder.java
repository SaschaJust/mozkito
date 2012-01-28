/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import net.ownhero.dev.andama.utils.AndamaUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.apache.commons.io.IOUtils;

import de.unisaarland.cs.st.moskito.testing.MoskitoSuite.TestResult;
import de.unisaarland.cs.st.moskito.testing.MoskitoSuite.TestRun;

/**
 * @author just
 * 
 */
public final class MoskitoTestBuilder {
	
	@NoneNull
	public static TestResult exec(final TestRun run) {
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
	
	@Deprecated
	public static Class<?> prepareTest(final MoskitoSuite.TestRun testRun,
	                                   final List<Method> bootMethods,
	                                   final List<Method> setupMethods,
	                                   final List<Method> tearDownMethods,
	                                   final List<Method> shutdownMethods) throws CannotCompileException,
	                                                                      NotFoundException {
		final ClassPool pool = ClassPool.getDefault();
		pool.appendSystemPath();
		
		final String fqName = testRun.getDescription().getTestClass().getCanonicalName() + "_test."
		        + testRun.getMethod().getName();
		final CtClass cc = pool.makeClass(fqName);
		
		// create method body
		final StringBuilder body = new StringBuilder();
		
		body.append('{').append(AndamaUtils.lineSeparator);
		body.append("Class c = null;").append(AndamaUtils.lineSeparator);
		body.append("java.lang.reflect.Method m = null;").append(AndamaUtils.lineSeparator);
		body.append(testRun.getDescription().getTestClass().getCanonicalName()).append(" o = null;")
		    .append(AndamaUtils.lineSeparator);
		
		body.append("try {").append(AndamaUtils.lineSeparator);
		body.append("c = Class.forName(\"").append(testRun.getDescription().getTestClass().getCanonicalName())
		    .append("\");").append(AndamaUtils.lineSeparator);
		body.append("m = c.getMethod(\"").append(testRun.getDescription().getMethodName()).append("\", new Class[0]);")
		    .append(AndamaUtils.lineSeparator);
		body.append("try {").append(AndamaUtils.lineSeparator);
		body.append(de.unisaarland.cs.st.moskito.testing.MoskitoTest.class.getCanonicalName())
		    .append(".setUpBeforeClass(m.getAnnotations());").append(AndamaUtils.lineSeparator);
		body.append("} catch (Throwable t) {").append(AndamaUtils.lineSeparator);
		body.append("t.printStackTrace();").append(AndamaUtils.lineSeparator);
		body.append("throw t;").append(AndamaUtils.lineSeparator);
		body.append("}").append(AndamaUtils.lineSeparator);
		for (final Method bootMethod : bootMethods) {
			body.append(testRun.getDescription().getTestClass().getCanonicalName() + "." + bootMethod.getName() + "();")
			    .append(AndamaUtils.lineSeparator);
		}
		
		body.append("o = (").append(testRun.getDescription().getTestClass().getCanonicalName())
		    .append(") c.newInstance();").append(AndamaUtils.lineSeparator);
		
		for (final Method setupMethod : setupMethods) {
			body.append("o." + setupMethod.getName() + "();").append(AndamaUtils.lineSeparator);
		}
		
		body.append("m.invoke(o, new Object[0]);").append(AndamaUtils.lineSeparator);
		body.append("} catch (Throwable t) {").append(AndamaUtils.lineSeparator);
		body.append("if (t.getCause() != null) {").append(AndamaUtils.lineSeparator);
		body.append("t.getCause().printStackTrace();").append(AndamaUtils.lineSeparator);
		body.append("throw t.getCause();").append(AndamaUtils.lineSeparator);
		body.append("} else { ").append(AndamaUtils.lineSeparator);
		body.append("t.printStackTrace();").append(AndamaUtils.lineSeparator);
		body.append("throw t;").append(AndamaUtils.lineSeparator);
		body.append("}").append(AndamaUtils.lineSeparator);
		body.append("} finally {").append(AndamaUtils.lineSeparator);
		body.append("if (o != null) {").append(AndamaUtils.lineSeparator);
		for (final Method tearDownMethod : tearDownMethods) {
			body.append("o." + tearDownMethod.getName() + "();").append(AndamaUtils.lineSeparator);
		}
		body.append("}").append(AndamaUtils.lineSeparator);
		body.append("if (c != null) {").append(AndamaUtils.lineSeparator);
		for (final Method shutdownMethod : shutdownMethods) {
			body.append(testRun.getDescription().getTestClass().getCanonicalName() + "." + shutdownMethod.getName()
			                    + "();").append(AndamaUtils.lineSeparator);
		}
		body.append("}").append(AndamaUtils.lineSeparator);
		
		body.append(de.unisaarland.cs.st.moskito.testing.MoskitoTest.class.getCanonicalName())
		    .append(".tearDownAfterClass(m.getAnnotations());").append(AndamaUtils.lineSeparator);
		
		body.append("}").append(AndamaUtils.lineSeparator);
		body.append("}").append(AndamaUtils.lineSeparator);
		if (System.getProperty("test.debug") != null) {
			System.err.println(body);
		}
		final CtMethod cm = CtNewMethod.make(Modifier.STATIC | Modifier.PUBLIC, CtClass.voidType, "main",
		                                     new CtClass[] { pool.get("java.lang.String[]") },
		                                     new CtClass[] { pool.get("java.lang.Throwable") }, body.toString(), cc);
		if (System.getProperty("test.debug") != null) {
			System.err.println("created method");
		}
		cc.addMethod(cm);
		try {
			if (System.getProperty("test.debug") != null) {
				System.err.println("compiling file");
			}
			cc.writeFile("src/main/java");
			final String relativePath = "src/main/java" + File.separator + fqName.replaceAll("\\.", File.separator)
			        + ".class";
			// System.err.println("Scheduling for deletion: " + relativePath);
			FileUtils.addToFileManager(new File(relativePath), FileUtils.FileShutdownAction.DELETE);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cc.toClass();
	}
	
	private MoskitoTestBuilder() {
	}
}

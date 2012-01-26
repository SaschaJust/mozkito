/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import java.io.File;
import java.io.IOException;
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
import net.ownhero.dev.ioda.Tuple;

import org.apache.commons.io.IOUtils;

/**
 * @author just
 * 
 */
public final class MoskitoTestBuilder {
	
	public static Tuple<Integer, String> exec(final Class<?> klass) throws IOException, InterruptedException {
		final String javaHome = System.getProperty("java.home");
		final String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		final String classpath = System.getProperty("java.class.path");
		final String className = klass.getCanonicalName();
		
		final ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath + ":src/main/java", className);
		
		final Process process = builder.start();
		process.waitFor();
		final StringWriter writer = new StringWriter();
		IOUtils.copy(process.getErrorStream(), writer);
		final String theString = writer.toString();
		return new Tuple<Integer, String>(process.exitValue(), theString);
	}
	
	public static void main(final String[] args) throws Throwable {
		try {
			final Class<?> c = Class.forName("de.unisaarland.cs.st.moskito.testing.MoskitoDerivationTest");
			final java.lang.reflect.Method m = c.getMethod("testFail", new Class<?>[0]);
			final Object o = c.newInstance();
			m.invoke(o, new Object[0]);
		} catch (final Throwable t) {
			t.getCause().printStackTrace();
			throw t.getCause();
		}
	}
	
	public static Class<?> prepareTest(final MoskitoSuite.MoskitoTestRun testRun,
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
		body.append(de.unisaarland.cs.st.moskito.testing.MoskitoTest.class.getCanonicalName())
		    .append(".setUpBeforeClass(m.getAnnotations());").append(AndamaUtils.lineSeparator);
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
		body.append("t.getCause().printStackTrace();").append(AndamaUtils.lineSeparator);
		body.append("throw t.getCause();").append(AndamaUtils.lineSeparator);
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
		
		cc.addMethod(cm);
		try {
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

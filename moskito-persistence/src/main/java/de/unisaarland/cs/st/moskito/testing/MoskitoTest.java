/**
 * 
 */
package de.unisaarland.cs.st.moskito.testing;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.exceptions.FilePermissionException;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import de.unisaarland.cs.st.moskito.exceptions.TestSettingsError;
import de.unisaarland.cs.st.moskito.exceptions.TestSetupException;
import de.unisaarland.cs.st.moskito.exceptions.TestTearDownException;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.testing.annotation.MoskitoTestingAnnotation;
import de.unisaarland.cs.st.moskito.testing.annotation.processors.MoskitoSettingsProcessor;

/**
 * @author just
 * 
 */
@RunWith (MoskitoSuite.class)
public abstract class MoskitoTest {
	
	private static PersistenceUtil         persistenceUtil    = null;
	
	private static List<Method>            beforeMethods      = new LinkedList<Method>();
	private static List<Method>            beforeClassMethods = new LinkedList<Method>();
	private static List<Method>            afterMethods       = new LinkedList<Method>();
	private static List<Method>            afterClassMethods  = new LinkedList<Method>();
	
	private static PrintStream             stdOut;
	private static PrintStream             stdErr;
	private static File                    stdOutFile;
	private static File                    stdErrFile;
	private static PrintStream             outStream;
	private static PrintStream             errStream;
	private static StringWriter            testWriter         = new StringWriter();
	private static PrintWriter             testPrinter        = new PrintWriter(testWriter);
	
	private static Annotation[]            classAnnotations   = new Annotation[0];
	private static Annotation[]            methodAnnotations  = new Annotation[0];
	
	private static Map<String, Annotation> annotationMap      = new HashMap<String, Annotation>();
	
	private static String                  testTag            = "";
	
	private static Throwable               failureCause       = null;
	
	/**
	 * @return the persistenceUtil for this test
	 */
	public static PersistenceUtil getPersistenceUtil() throws TestSettingsError {
		if (persistenceUtil == null) {
			testLog("Calling getPersistenceUtil() without having database arguments set.");
			throw new TestSettingsError("Calling getPersistenceUtil() without having database arguments set.");
		}
		
		return persistenceUtil;
	}
	
	/**
	 * @param aClass
	 * @return
	 */
	private static void lookupEnvMethods(final Class<?> aClass) {
		
		final Method[] classMethods = aClass.getDeclaredMethods();
		for (final Method classMethod : classMethods) {
			final int length = classMethod.getParameterTypes().length;
			final int modifiers = classMethod.getModifiers();
			
			if ((length != 0) || !Modifier.isPublic(modifiers) || Modifier.isInterface(modifiers)
			        || Modifier.isAbstract(modifiers)) {
				continue;
			}
			
			if (Modifier.isStatic(modifiers)) {
				if (classMethod.getAnnotation(BeforeClass.class) != null) {
					MoskitoTest.beforeClassMethods.add(classMethod);
				} else if (classMethod.getAnnotation(AfterClass.class) != null) {
					MoskitoTest.afterClassMethods.add(classMethod);
				}
			} else {
				if (classMethod.getAnnotation(Before.class) != null) {
					MoskitoTest.beforeMethods.add(classMethod);
				} else if (classMethod.getAnnotation(After.class) != null) {
					MoskitoTest.afterMethods.add(classMethod);
				}
			}
		}
	}
	
	public static void main(final String[] args) throws Throwable {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
		
		if (args.length < 4) {
			System.err.println(MoskitoTest.class.getSimpleName() + "#main called with wrong arguments.");
			System.exit(11);
		}
		
		final String fqcName = args[0];
		final String testName = args[1];
		final String outLogPath = args[2];
		final String errLogPath = args[3];
		testTag = "[" + testName + "] ";
		
		stdOut = System.out;
		stdErr = System.err;
		
		stdOutFile = new File(outLogPath);
		try {
			FileUtils.ensureFilePermissions(stdOutFile, FileUtils.WRITABLE_FILE);
			outStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(stdOutFile)));
			System.setOut(outStream);
		} catch (final FilePermissionException e1) {
			testLog("Can't redirect stdout.", e1);
		} catch (final FileNotFoundException e) {
			testLog("Can't find redirection file for STDOUT.", e);
		}
		
		stdErrFile = new File(errLogPath);
		try {
			FileUtils.ensureFilePermissions(stdErrFile, FileUtils.WRITABLE_FILE);
			errStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(stdErrFile)));
			System.setErr(errStream);
		} catch (final FilePermissionException e1) {
			testLog("Can't redirect stderr.", e1);
		} catch (final FileNotFoundException e) {
			testLog("Can't find redirection file for STDERR.", e);
		}
		
		KanuniAgent.initialize();
		
		Class<?> c = null;
		Method m = null;
		Object o = null;
		
		try {
			testLog("Loading test class: " + fqcName);
			try {
				c = Class.forName(fqcName);
			} catch (ClassNotFoundException e) {
				testLog("Could not load class under test: " + fqcName, e);
				throw e;
			}
			
			testLog("Getting test: " + testName);
			try {
				m = c.getMethod(testName, new Class[0]);
			} catch (SecurityException e) {
				testLog("Getting test method '" + testName + "' failed.", e);
				throw e;
			} catch (NoSuchMethodException e) {
				testLog("Getting test method '" + testName + "' failed.", e);
				throw e;
			}
			
			try {
				testLog("Setting up test environment.");
				classAnnotations = c.getAnnotations();
				methodAnnotations = m.getAnnotations();
				annotationMap = new HashMap<String, Annotation>();
				
				for (final Annotation annotation : classAnnotations) {
					annotationMap.put(annotation.annotationType().getCanonicalName(), annotation);
				}
				
				for (final Annotation annotation : methodAnnotations) {
					annotationMap.put(annotation.annotationType().getCanonicalName(), annotation);
				}
				MoskitoTest.setUpBeforeClass(annotationMap);
			} catch (final TestSetupException e) {
				throw e;
			}
			
			testLog("Looking up setup/tearDown methods.");
			lookupEnvMethods(c);
			
			if (!beforeClassMethods.isEmpty()) {
				testLog("@BeforeClass: ");
				for (final Method method : beforeClassMethods) {
					testLog(" * " + method.getName());
					try {
						method.invoke(null, new Object[0]);
					} catch (final TestSettingsError e) {
						testLog("@BeforeClass failed in: " + method.getName(), e);
						throw e;
					} catch (final Throwable t) {
						testLog("@BeforeClass failed in: " + method.getName(), t);
						throw t;
					}
				}
			}
			
			testLog("Creating test instance: " + c.getSimpleName());
			try {
				o = c.newInstance();
			} catch (final InstantiationException e) {
				testLog("Creating test instance failed", e);
				throw e;
			} catch (IllegalAccessException e) {
				testLog("Creating test instance failed", e);
				throw e;
			}
			
			if (!beforeMethods.isEmpty()) {
				testLog("@Before: ");
				for (final Method method : beforeMethods) {
					testLog(" * " + method.getName());
					try {
						method.invoke(o, new Object[0]);
					} catch (final Throwable t) {
						testLog("@Before failed in: " + method.getName(), t);
						throw t;
					}
				}
			}
			
			testLog("Running test instance: " + m.getName());
			try {
				m.invoke(o, new Object[0]);
			} catch (final Throwable t) {
				testLog("Test failed.");
				if (t.getCause() != null) {
					throw t.getCause();
				} else {
					throw t;
				}
			}
			
			if (o != null) {
				if (!afterMethods.isEmpty()) {
					testLog("@After: ");
					for (final Method method : afterMethods) {
						testLog(" * " + method.getName());
						try {
							method.invoke(o, new Object[0]);
						} catch (final Throwable t) {
							testLog("@After failed in: " + method.getName(), t);
							throw t;
						}
					}
				}
			}
			
			if (c != null) {
				if (!afterClassMethods.isEmpty()) {
					testLog("@AfterClass: ");
					for (final Method method : afterClassMethods) {
						testLog(" * " + method.getName());
						try {
							method.invoke(null, new Object[0]);
						} catch (final Throwable t) {
							testLog("@AfterClass failed in: " + method.getName(), t);
							throw t;
						}
					}
				}
			}
		} catch (Throwable t) {
			failureCause = t;
		} finally {
			
			try {
				testLog("Shutting down test environment.");
				MoskitoTest.tearDownAfterClass(annotationMap);
			} catch (final TestTearDownException e) {
				testLog("Shutdown failed.", e);
				failureCause = e;
			}
			
			System.setErr(stdErr);
			System.setOut(stdOut);
			outStream.flush();
			errStream.close();
			outStream.close();
			System.out.print(testWriter.toString());
			System.out.flush();
			
			if (failureCause != null) {
				failureCause.printStackTrace();
				System.exit(1);
				
			}
		}
	}
	
	/**
	 * @param util
	 */
	public static void setPersistenceUtil(final PersistenceUtil util) {
		MoskitoTest.persistenceUtil = util;
	}
	
	/**
	 * @param annotationMap
	 * @throws TestSetupException
	 */
	public static void setUpBeforeClass(final Map<String, Annotation> annotationMap) throws TestSetupException {
		for (final Annotation annotation : annotationMap.values()) {
			final MoskitoTestingAnnotation metaAnnotation = annotation.annotationType()
			                                                          .getAnnotation(MoskitoTestingAnnotation.class);
			if (metaAnnotation != null) {
				final Class<? extends MoskitoSettingsProcessor> processorClass = metaAnnotation.value();
				MoskitoSettingsProcessor processor;
				try {
					processor = processorClass.newInstance();
					processor.setup(annotation);
				} catch (final InstantiationException e) {
					testLog("Can't spawn processor: " + processorClass.getCanonicalName(), e);
					throw new TestSetupException("Can't spawn processor: " + processorClass.getCanonicalName(), e);
				} catch (final IllegalAccessException e) {
					testLog(e.getClass().getCanonicalName() + " when processing annotation: " + annotation.toString(),
					        e);
					throw new TestSetupException(e.getClass().getCanonicalName() + " when processing annotation: "
					        + annotation.toString(), e);
				} catch (final Throwable e) {
					testLog(e.getClass().getCanonicalName() + " when processing annotation: " + annotation.toString(),
					        e);
					throw new TestSetupException(e.getClass().getCanonicalName() + " when processing annotation: "
					        + annotation.toString(), e);
				}
			}
		}
	}
	
	/**
	 * @param annotationMap
	 * @throws Exception
	 */
	public static void tearDownAfterClass(final Map<String, Annotation> annotationMap) throws TestTearDownException {
		for (final Annotation annotation : annotationMap.values()) {
			final MoskitoTestingAnnotation metaAnnotation = annotation.annotationType()
			                                                          .getAnnotation(MoskitoTestingAnnotation.class);
			if (metaAnnotation != null) {
				final Class<? extends MoskitoSettingsProcessor> processorClass = metaAnnotation.value();
				MoskitoSettingsProcessor processor;
				try {
					processor = processorClass.newInstance();
					processor.tearDown(annotation);
				} catch (final InstantiationException e) {
					testLog("Can't spawn processor: " + processorClass.getCanonicalName(), e);
					throw new TestTearDownException("Can't spawn processor: " + processorClass.getCanonicalName(), e);
				} catch (final IllegalAccessException e) {
					testLog(e.getClass().getCanonicalName() + " when processing annotation: " + annotation.toString(),
					        e);
					throw new TestTearDownException(e.getClass().getCanonicalName() + " when processing annotation: "
					        + annotation.toString(), e);
				} catch (final Throwable e) {
					testLog(e.getClass().getCanonicalName() + " when processing annotation: " + annotation.toString(),
					        e);
					throw new TestTearDownException(e.getClass().getCanonicalName() + " when processing annotation: "
					        + annotation.toString(), e);
				}
			}
		}
	}
	
	/**
	 * @param message
	 */
	private static void testLog(final String message) {
		testPrinter.print(testTag);
		testPrinter.println(message);
	}
	
	/**
	 * @param message
	 * @param t
	 */
	private static void testLog(final String message,
	                            final Throwable t) {
		testPrinter.print(testTag);
		testPrinter.println(message);
		t.printStackTrace(testPrinter);
	}
	
}

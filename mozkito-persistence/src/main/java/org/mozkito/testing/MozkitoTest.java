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
import org.mozkito.exceptions.TestSettingsError;
import org.mozkito.exceptions.TestSetupException;
import org.mozkito.exceptions.TestTearDownException;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.annotation.MozkitoTestAnnotation;
import org.mozkito.testing.annotation.processors.MozkitoSettingsProcessor;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@RunWith (MozkitoSuite.class)
public abstract class MozkitoTest {
	
	static {
		KanuniAgent.initialize();
	}
	
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
	
	private static List<Throwable>         failureCauses      = new LinkedList<Throwable>();
	
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
					MozkitoTest.beforeClassMethods.add(classMethod);
				} else if (classMethod.getAnnotation(AfterClass.class) != null) {
					MozkitoTest.afterClassMethods.add(classMethod);
				}
			} else {
				if (classMethod.getAnnotation(Before.class) != null) {
					MozkitoTest.beforeMethods.add(classMethod);
				} else if (classMethod.getAnnotation(After.class) != null) {
					MozkitoTest.afterMethods.add(classMethod);
				}
			}
		}
	}
	
	public static void main(final String[] args) throws Throwable {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
		
		if (args.length < 4) {
			System.err.println(MozkitoTest.class.getSimpleName() + "#main called with wrong arguments.");
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
		
		Class<?> c = null;
		Method m = null;
		Object o = null;
		
		try {
			testLog("Loading test class: " + fqcName);
			try {
				c = Class.forName(fqcName);
			} catch (final ClassNotFoundException e) {
				testLog("Could not load class under test: " + fqcName, e);
				throw e;
			}
			
			testLog("Getting test: " + testName);
			try {
				m = c.getMethod(testName, new Class[0]);
			} catch (final SecurityException e) {
				testLog("Getting test method '" + testName + "' failed.", e);
				throw e;
			} catch (final NoSuchMethodException e) {
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
				MozkitoTest.setUpBeforeClass(c, annotationMap);
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
			} catch (final IllegalAccessException e) {
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
				}
				throw t;
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
			
		} catch (final Throwable t) {
			failureCauses.add(t);
		} finally {
			
			try {
				testLog("Shutting down test environment.");
				MozkitoTest.tearDownAfterClass(c, annotationMap);
			} catch (final TestTearDownException e) {
				testLog("Shutdown failed.", e);
				failureCauses.add(e);
			}
			
			System.setErr(stdErr);
			System.setOut(stdOut);
			outStream.flush();
			errStream.close();
			outStream.close();
			System.out.print(testWriter.toString());
			System.out.flush();
			
			if (!failureCauses.isEmpty()) {
				int i = 1;
				if (failureCauses.size() > 1) {
					System.err.println("There are multiple failure causes. These failures might depend on the first (#1) failure cause. ");
				}
				for (final Throwable failureCause : failureCauses) {
					System.err.println("Error number #" + i++);
					failureCause.printStackTrace();
				}
				System.exit(1);
				
			}
		}
	}
	
	/**
	 * @param util
	 */
	public static void setPersistenceUtil(final PersistenceUtil util) {
		MozkitoTest.persistenceUtil = util;
	}
	
	/**
	 * @param annotationMap
	 * @throws TestSetupException
	 */
	public static void setUpBeforeClass(final Class<?> aClass,
	                                    final Map<String, Annotation> annotationMap) throws TestSetupException {
		for (final Annotation annotation : annotationMap.values()) {
			final MozkitoTestAnnotation metaAnnotation = annotation.annotationType()
			                                                       .getAnnotation(MozkitoTestAnnotation.class);
			if (metaAnnotation != null) {
				final Class<? extends MozkitoSettingsProcessor> processorClass = metaAnnotation.value();
				MozkitoSettingsProcessor processor;
				try {
					processor = processorClass.newInstance();
					processor.setup(aClass, annotation);
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
	public static void tearDownAfterClass(final Class<?> aClass,
	                                      final Map<String, Annotation> annotationMap) throws TestTearDownException {
		for (final Annotation annotation : annotationMap.values()) {
			final MozkitoTestAnnotation metaAnnotation = annotation.annotationType()
			                                                       .getAnnotation(MozkitoTestAnnotation.class);
			if (metaAnnotation != null) {
				final Class<? extends MozkitoSettingsProcessor> processorClass = metaAnnotation.value();
				MozkitoSettingsProcessor processor;
				try {
					processor = processorClass.newInstance();
					processor.tearDown(aClass, annotation);
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
			
			getPersistenceUtil().shutdown();
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

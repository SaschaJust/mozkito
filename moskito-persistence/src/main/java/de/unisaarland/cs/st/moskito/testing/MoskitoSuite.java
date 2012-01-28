package de.unisaarland.cs.st.moskito.testing;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import de.unisaarland.cs.st.moskito.testing.annotation.MoskitoTestingAnnotation;

public class MoskitoSuite extends BlockJUnit4ClassRunner {
	
	static class TestResult {
		
		private final int    returnValue;
		private final String testLog;
		private final String testError;
		private final String testStdOut;
		
		private final String testStdErr;
		
		public TestResult(final int returnValue, final String testLog, final String testError, final String testStdOut,
		        final String testStdErr) {
			this.returnValue = returnValue;
			this.testLog = testLog;
			this.testError = testError;
			this.testStdOut = testStdOut;
			this.testStdErr = testStdErr;
		}
		
		public int getReturnValue() {
			return this.returnValue;
		}
		
		public String getTestError() {
			return this.testError;
		}
		
		public String getTestLog() {
			return this.testLog;
		}
		
		public String getTestStdErr() {
			return this.testStdErr;
		}
		
		public String getTestStdOut() {
			return this.testStdOut;
		}
	}
	
	static class TestRun {
		
		private final Method           method;
		private final Description      description;
		private Failure                failure;
		private final List<Annotation> settings;
		
		public TestRun(final Method method, final Description description, final List<Annotation> settings) {
			this.method = method;
			this.description = description;
			this.settings = settings;
		}
		
		public Description getDescription() {
			return this.description;
		}
		
		public Failure getFailure() {
			return this.failure;
		}
		
		public Method getMethod() {
			return this.method;
		}
		
		public List<Annotation> getSettings() {
			return this.settings;
		}
		
		public void setFailure(final Failure failure) {
			this.failure = failure;
		}
		
	}
	
	private final List<TestRun> fTestMethods    = new LinkedList<TestRun>();
	private final List<TestRun> fIgnoreMethods  = new LinkedList<TestRun>();
	private final List<Method>  setupMethods    = new LinkedList<Method>();
	private final List<Method>  tearDownMethods = new LinkedList<Method>();
	
	private final TestClass     fTestClass;
	
	private final Description   description;
	private final List<Method>  bootMethods     = new LinkedList<Method>();
	private final List<Method>  shutdownMethods = new LinkedList<Method>();
	
	/**
	 * @param aClass
	 * @throws InitializationError
	 */
	public MoskitoSuite(@NotNull final Class<?> aClass) throws InitializationError {
		super(aClass);
		this.fTestClass = new TestClass(aClass);
		this.description = Description.createSuiteDescription(this.fTestClass.getJavaClass());
		
		final Method[] classMethods = aClass.getDeclaredMethods();
		for (final Method classMethod : classMethods) {
			final Class<?> retClass = classMethod.getReturnType();
			final int length = classMethod.getParameterTypes().length;
			final int modifiers = classMethod.getModifiers();
			
			if ((retClass == null) || (length != 0) || !Modifier.isPublic(modifiers) || Modifier.isInterface(modifiers)
			        || Modifier.isAbstract(modifiers)) {
				continue;
			}
			
			classMethod.getName();
			
			if (Modifier.isStatic(modifiers)) {
				if (classMethod.getAnnotation(BeforeClass.class) != null) {
					this.bootMethods.add(classMethod);
				} else if (classMethod.getAnnotation(AfterClass.class) != null) {
					this.shutdownMethods.add(classMethod);
				} else {
					continue;
				}
			} else {
				if (classMethod.getAnnotation(Before.class) != null) {
					this.setupMethods.add(classMethod);
					
				} else if (classMethod.getAnnotation(After.class) != null) {
					this.tearDownMethods.add(classMethod);
				} else if (classMethod.getAnnotation(Ignore.class) == null) {
					if (classMethod.getAnnotation(Test.class) != null) {
						final List<Annotation> annotationList = new LinkedList<Annotation>();
						for (final Annotation annotation : classMethod.getAnnotations()) {
							if (annotation.annotationType().getAnnotation(MoskitoTestingAnnotation.class) != null) {
								annotationList.add(annotation);
							}
						}
						final TestRun testRun = new TestRun(
						                                    classMethod,
						                                    Description.createTestDescription(this.fTestClass.getJavaClass(),
						                                                                      classMethod.getName()),
						                                    annotationList);
						getDescription().addChild(testRun.getDescription());
						this.fTestMethods.add(testRun);
					}
				} else {
					if (classMethod.getAnnotation(Test.class) != null) {
						final TestRun testRun = new TestRun(
						                                    classMethod,
						                                    Description.createTestDescription(this.fTestClass.getJavaClass(),
						                                                                      classMethod.getName()),
						                                    new LinkedList<Annotation>());
						getDescription().addChild(testRun.getDescription());
						this.fIgnoreMethods.add(testRun);
					}
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.junit.runners.ParentRunner#getDescription()
	 */
	@Override
	public Description getDescription() {
		return this.description;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * org.junit.runners.ParentRunner#run(org.junit.runner.notification.RunNotifier
	 * )
	 */
	@Override
	public void run(final RunNotifier runNotifier) {
		final Result result = new Result();
		
		runNotifier.addFirstListener(result.createListener());
		
		try {
			this.fTestClass.getOnlyConstructor().newInstance();
		} catch (final Throwable t) {
			// throw new InitializationError(t);
		}
		
		runNotifier.fireTestRunStarted(getDescription());
		System.err.println("Running test suite: " + getDescription().toString());
		for (int i = 0; i < this.fTestMethods.size(); i++) {
			Failure failure = null;
			final TestRun testRun = this.fTestMethods.get(i);
			runNotifier.fireTestStarted(testRun.getDescription());
			try {
				final String testTag = "[" + testRun.getDescription().getMethodName() + "] ";
				System.err.println(testTag + "Running test.");
				final DateTime start = new DateTime();
				final TestResult testResult = MoskitoTestBuilder.exec(testRun);
				final DateTime end = new DateTime();
				System.err.println(testTag + "Test " + (testResult.getReturnValue() != 0
				                                                                        ? "failed"
				                                                                        : "succeeded") + " after "
				        + new Period(start, end).toString(PeriodFormat.getDefault()) + ".");
				if (testResult.getReturnValue() != 0) {
					System.err.println(testTag + "========== STRACKTRACE ==========");
					System.err.print(testResult.getTestError());
					System.err.println(testTag + "========== Moskito-LOG ==========");
					System.err.print(testResult.getTestLog());
					System.err.println(testTag + "========== TEST-STDOUT ==========");
					System.err.print(testResult.getTestStdOut());
					System.err.println(testTag + "========== TEST-STDERR ==========");
					System.err.print(testResult.getTestStdErr());
					throw new AssertionError(testResult.getTestError());
				}
			} catch (final AssertionError e) {
				failure = new Failure(testRun.getDescription(), e);
				testRun.setFailure(failure);
				
				runNotifier.fireTestFailure(failure);
				continue;
			}
			runNotifier.fireTestFinished(testRun.getDescription());
		}
		
		for (int i = 0; i < this.fIgnoreMethods.size(); i++) {
			final TestRun testIgnore = this.fIgnoreMethods.get(i);
			runNotifier.fireTestIgnored(testIgnore.getDescription());
		}
		
		runNotifier.fireTestRunFinished(result);
	}
}

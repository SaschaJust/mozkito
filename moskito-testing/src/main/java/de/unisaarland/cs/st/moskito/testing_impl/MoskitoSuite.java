package de.unisaarland.cs.st.moskito.testing_impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.Tuple;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

public class MoskitoSuite extends BlockJUnit4ClassRunner {
	
	class MoskitoTestRun {
		
		Method      method;
		Description description;
		
		Failure     failure;
		
		public MoskitoTestRun(final Method method, final Description description) {
			this.method = method;
			this.description = description;
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
		
		public void setFailure(final Failure failure) {
			this.failure = failure;
		}
	}
	
	public static void main(final String[] args) {
		try {
			final Tuple<Integer, String> tuple = MoskitoTest.exec(MoskitoTest.class);
			System.err.println("Return code: " + tuple.getFirst());
			System.err.println("Stack trace: " + tuple.getSecond());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private final List<MoskitoTestRun> fTestMethods   = new LinkedList<MoskitoTestRun>();
	private final List<MoskitoTestRun> fIgnoreMethods = new LinkedList<MoskitoTestRun>();
	private final TestClass            fTestClass;
	
	private Description                description;
	
	public MoskitoSuite(final Class<?> aClass) throws InitializationError {
		super(aClass);
		this.fTestClass = new TestClass(aClass);
		
		final Method[] classMethods = aClass.getDeclaredMethods();
		for (final Method classMethod : classMethods) {
			final Class<?> retClass = classMethod.getReturnType();
			final int length = classMethod.getParameterTypes().length;
			final int modifiers = classMethod.getModifiers();
			if ((retClass == null) || (length != 0) || Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)
			        || Modifier.isInterface(modifiers) || Modifier.isAbstract(modifiers)) {
				continue;
			}
			final String methodName = classMethod.getName();
			if (classMethod.getAnnotation(Ignore.class) == null) {
				if (methodName.toUpperCase().startsWith("TEST") || (classMethod.getAnnotation(Test.class) != null)) {
					final MoskitoTestRun testRun = new MoskitoTestRun(
					                                                  classMethod,
					                                                  Description.createTestDescription(this.fTestClass.getJavaClass(),
					                                                                                    classMethod.getName()));
					getDescription().addChild(testRun.getDescription());
					this.fTestMethods.add(testRun);
				}
			} else if (classMethod.getAnnotation(Ignore.class) != null) {
				final MoskitoTestRun testRun = new MoskitoTestRun(
				                                                  classMethod,
				                                                  Description.createTestDescription(this.fTestClass.getJavaClass(),
				                                                                                    classMethod.getName()));
				getDescription().addChild(testRun.getDescription());
				this.fIgnoreMethods.add(testRun);
			}
		}
	}
	
	@Override
	public Description getDescription() {
		if (this.description == null) {
			this.description = Description.createSuiteDescription(this.fTestClass.getJavaClass());
		}
		// final Description spec =
		// Description.createSuiteDescription(this.fTestClass.getName(),
		// this.fTestClass.getJavaClass().getAnnotations());
		return this.description;
	}
	
	@Override
	public void run(final RunNotifier runNotifier) {
		final Result result = new Result();
		final RunListener listener = new MoskitoListener();
		
		runNotifier.addListener(listener);
		runNotifier.addFirstListener(result.createListener());
		
		try {
			this.fTestClass.getOnlyConstructor().newInstance();
		} catch (final Throwable t) {
			// throw new InitializationError(t);
		}
		
		runNotifier.fireTestRunStarted(getDescription());
		
		for (int i = 0; i < this.fTestMethods.size(); i++) {
			Failure failure = null;
			final MoskitoTestRun testRun = this.fTestMethods.get(i);
			runNotifier.fireTestStarted(testRun.getDescription());
			try {
				final Class<?> preparedTest = MoskitoTest.prepareTest(testRun);
				final Tuple<Integer, String> tuple = MoskitoTest.exec(preparedTest);
				if (tuple.getFirst() != 0) {
					System.err.println("Return code: " + tuple.getFirst());
					System.err.println("Stack trace: " + tuple.getSecond());
					throw new AssertionError(tuple.getSecond());
				}
			} catch (final Throwable t) {
				failure = new Failure(testRun.getDescription(), t);
				testRun.setFailure(failure);
				
				runNotifier.fireTestFailure(failure);
				continue;
			}
			runNotifier.fireTestFinished(testRun.getDescription());
		}
		
		for (int i = 0; i < this.fIgnoreMethods.size(); i++) {
			final MoskitoTestRun testIgnore = this.fIgnoreMethods.get(i);
			runNotifier.fireTestIgnored(testIgnore.getDescription());
		}
		
		runNotifier.fireTestRunFinished(result);
	}
}

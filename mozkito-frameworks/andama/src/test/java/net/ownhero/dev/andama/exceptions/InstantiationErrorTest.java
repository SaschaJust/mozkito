package net.ownhero.dev.andama.exceptions;

import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import org.junit.Test;

public class InstantiationErrorTest {
	
	@Test
	public void testConstructorError() {
		try {
			Bla.class.newInstance();
		} catch (InstantiationException e) {
			System.err.println(new InstantiationError(e, TestContructorClass.class, null).analyzeFailureCause());
			return;
		} catch (IllegalAccessException e) {
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		fail();
	}
	
	@Test
	public void testNoDefaultConstructor() {
		try {
			TestContructorClass.class.newInstance();
		} catch (final InstantiationException e) {
			System.err.println(new InstantiationError(e, TestContructorClass.class, null).analyzeFailureCause());
			return;
		} catch (final IllegalAccessException e) {
		}
		fail();
	}
	
	@Test
	public void testWrongConstructor() {
		Constructor<TestContructorClass> constructor = null;
		try {
			constructor = TestContructorClass.class.getConstructor(Integer.class);
			constructor.newInstance(new LinkedList<String>(), 5);
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
			System.err.println(new InstantiationError(e, TestContructorClass.class, constructor).analyzeFailureCause());
			return;
		} catch (InvocationTargetException e) {
		} catch (InstantiationException e) {
			System.err.println(new InstantiationError(e, TestContructorClass.class, constructor).analyzeFailureCause());
			return;
		} catch (IllegalAccessException e) {
		}
		
		fail();
	}
}

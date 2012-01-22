package net.ownhero.dev.andama.exceptions;

import org.junit.Test;

public class NoSuchConstructorErrorTest {
	
	@Test
	public void test() {
		try {
			TestContructorClass.class.getConstructor(Double.class);
		} catch (final SecurityException e) {
		} catch (final NoSuchMethodException e) {
			System.err.println(new NoSuchConstructorError(e, TestContructorClass.class, Double.class).analyzeFailureCause());
		}
	}
	
}

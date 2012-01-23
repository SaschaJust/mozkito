package net.ownhero.dev.andama.exceptions;

import org.junit.Test;

public class InstantiationErrorTest {
	
	@Test
	public void test() {
		try {
			TestContructorClass.class.newInstance();
		} catch (final InstantiationException e) {
			System.err.println(new InstantiationError(e, TestContructorClass.class, null).analyzeFailureCause());
		} catch (final IllegalAccessException e) {
		}
	}
	
}

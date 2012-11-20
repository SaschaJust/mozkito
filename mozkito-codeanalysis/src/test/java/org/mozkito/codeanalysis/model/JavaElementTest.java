package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JavaElementTest {
	
	@Test
	public void testExtractMethodName() {
		final String methodName = JavaElement.extractMethodName("org.mozkito.codeanalysis.model.JavaElement.foo(String s, Object o, HashSet<String[]> set)");
		assertEquals("foo", methodName);
	}
	
}

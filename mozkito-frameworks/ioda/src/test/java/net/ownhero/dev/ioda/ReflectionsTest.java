/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package net.ownhero.dev.ioda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.ownhero.dev.ioda.Reflections.InstantianException;

import org.junit.Test;

/**
 * The Class ReflectionsTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ReflectionsTest {
	
	/**
	 * The Class AbstractClass.
	 */
	private static abstract class AbstractClass {
		// stub
	}
	
	/**
	 * The Interface InterfaceClass.
	 */
	private static interface InterfaceClass {
		// stub
	}
	
	/**
	 * The Class ValidClass.
	 */
	public static final class ValidClass {
		
		/** The first. */
		private final int    first;
		
		/** The second. */
		private final String second;
		
		private Double       third = 3d;
		
		/**
		 * Instantiates a new valid class.
		 * 
		 * @param fail
		 *            the fail
		 */
		@SuppressWarnings ("unused")
		private ValidClass(final boolean fail) {
			this(1, "second");
		}
		
		/**
		 * @param first
		 * @param second
		 */
		public ValidClass(final int first, final String second) {
			super();
			this.first = first;
			this.second = second;
		}
		
		/**
		 * Instantiates a new valid class.
		 * 
		 * @param first
		 *            the first
		 * @param second
		 *            the second
		 * @param third
		 *            the third
		 */
		@Deprecated
		public ValidClass(final int first, final String second, final Double third) {
			this(first, second);
			if (third == null) {
				throw new NullPointerException("Parameter third must not be null.");
			}
			this.third = third;
		}
		
		/**
		 * Gets the first.
		 * 
		 * @return the first
		 */
		public final int getFirst() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.first;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * Gets the second.
		 * 
		 * @return the second
		 */
		public final String getSecond() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.second;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
		
		/**
		 * @return the third
		 */
		public final Double getThird() {
			PRECONDITIONS: {
				// none
			}
			
			try {
				return this.third;
			} finally {
				POSTCONDITIONS: {
					// none
				}
			}
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testAbstractClass() {
		try {
			Reflections.saveInstantiate(AbstractClass.class, new Class<?>[0], new Object[0]);
			fail("Abstract classes can not be instantiated.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("abstract"));
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testActualArgumentsNullInvalid() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { int.class, String.class, Double.class },
			                            new Object[] { 5, "bling", null });
			fail("null is a valid parameter unless specified otherwise");
		} catch (final InstantianException expected) {
			// expected
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testActualArgumentsNullValid() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { int.class, String.class }, new Object[] { 5,
			        null });
		} catch (final InstantianException e) {
			e.printStackTrace();
			fail("null is a valid parameter unless specified otherwise, but we got: " + e.getMessage());
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testArgumentNotMatchingParameterTypeSpecified0() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { int.class, String.class }, new Object[] {
			        "5", "bleh" });
			fail("Actual parameter types do not match the ones specified in the parameter types array.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("type"));
			assertTrue(e.getMessage().contains("index '0'"));
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testArgumentNotMatchingParameterTypeSpecified1() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { int.class, String.class }, new Object[] { 5,
			        .2d });
			fail("Actual parameter types do not match the ones specified in the parameter types array.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("type"));
			assertTrue(e.getMessage().contains("index '1'"));
		}
	}
	
	/**
	 * Test arguments null.
	 * 
	 */
	@Test
	public final void testArgumentsNull() {
		try {
			Reflections.saveInstantiate(Object.class, new Class<?>[0], null);
			fail("Null argument arrays should cause an exception.");
		} catch (final InstantianException e) {
			assertEquals(NullPointerException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testArray() {
		try {
			Reflections.saveInstantiate(new int[0].getClass(), new Class<?>[0], new Object[0]);
			fail("Arrays can not be instantiated.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("array"));
		}
	}
	
	/**
	 * Test class null.
	 */
	@Test
	public final void testClassNull() {
		try {
			Reflections.saveInstantiate(Object.class, new Class<?>[0], null);
			fail("Cannot instantiate null.");
		} catch (final InstantianException e) {
			assertEquals(NullPointerException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testDifferentArrayLengths() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { int.class, String.class },
			                            new Object[] { 5 });
			fail("Different array lengths for parameter types and arguments are invalid.");
		} catch (final InstantianException e) {
			assertEquals(IndexOutOfBoundsException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("length"));
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testInterfaceClass() {
		try {
			Reflections.saveInstantiate(InterfaceClass.class, new Class<?>[0], new Object[0]);
			fail("Interface classes can not be instantiated.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("interface"));
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testNoSuchConstructor() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { float.class }, new Object[] { 5f });
			fail("There is no matching constructor and instantiation should fail accordingly.");
		} catch (final InstantianException e) {
			e.printStackTrace();
			assertTrue(e.getMessage().contains("alternatives"));
		}
	}
	
	/**
	 * Test parameter types null.
	 */
	@Test
	public final void testParameterTypesNull() {
		try {
			Reflections.saveInstantiate(Object.class, new Class<?>[0], null);
			fail("Null parameter types arrays should cause an exception.");
		} catch (final InstantianException e) {
			assertEquals(NullPointerException.class, e.getCause().getClass());
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testPrimitive() {
		try {
			Reflections.saveInstantiate(int.class, new Class<?>[0], new Object[0]);
			fail("Primitive classes can not be instantiated.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("primitive"));
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testPrivateConstructor() {
		try {
			Reflections.saveInstantiate(ValidClass.class, new Class<?>[] { boolean.class }, new Object[] { true });
		} catch (final InstantianException e) {
			e.printStackTrace();
			fail("Instantiating using private constructors should work since we handle accessibility, but got: "
			        + e.getMessage());
		}
	}
	
	/**
	 * Test abstract class.
	 */
	@Test
	public void testVoidClass() {
		try {
			Reflections.saveInstantiate(void.class, new Class<?>[0], new Object[0]);
			fail("Void can not be instantiated.");
		} catch (final InstantianException e) {
			assertEquals(IllegalArgumentException.class, e.getCause().getClass());
			assertTrue(e.getMessage().contains("void"));
		}
	}
	
}

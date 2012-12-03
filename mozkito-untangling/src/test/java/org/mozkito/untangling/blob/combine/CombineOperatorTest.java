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
package org.mozkito.untangling.blob.combine;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.Test;

import org.mozkito.untangling.utils.CollectionUtils;

/**
 * The Class ArtificialBlobGeneratorTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CombineOperatorTest {
	
	/**
	 * The Class TestObject.
	 * 
	 * @author Kim Herzig <herzig@mozkito.org>
	 */
	private class TestObject {
		
		/** The id. */
		private final String id;
		
		/** The path. */
		private final String path;
		
		/**
		 * Instantiates a new test object.
		 * 
		 * @param id
		 *            the id
		 * @param path
		 *            the path
		 */
		public TestObject(final String id, final String path) {
			this.id = id;
			this.path = path;
		}
		
		/**
		 * Gets the id.
		 * 
		 * @return the id
		 */
		@SuppressWarnings ("unused")
		public String getId() {
			return this.id;
		}
		
		/**
		 * Gets the path.
		 * 
		 * @return the path
		 */
		public String getPath() {
			return this.path;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return this.id;
		}
	}
	
	/**
	 * The Class TestObjectCombineOperator.
	 * 
	 * @author Kim Herzig <herzig@mozkito.org>
	 */
	private class TestObjectCombineOperator implements CombineOperator<TestObject> {
		
		/** The pd. */
		private final int pd;
		
		/**
		 * Instantiates a new test object combine operator.
		 * 
		 * @param pd
		 *            the pd
		 */
		public TestObjectCombineOperator(final int pd) {
			this.pd = pd;
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.mozkito.untangling.blob.CombineOperator#canBeCombined(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean canBeCombined(final TestObject t1,
		                             final TestObject t2) {
			return PackageDistanceCombineOperator.canCombinePaths(t1.getPath(), t2.getPath(), this.pd);
		}
		
	}
	
	/**
	 * Test get all combinations1.
	 */
	@Test
	public void testGetAllCombinations1() {
		
		final Set<TestObject> elements = new HashSet<TestObject>();
		elements.add(new TestObject("1", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "c"
		        + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("2", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "c"
		        + FileUtils.fileSeparator + "e"));
		elements.add(new TestObject("3", "a" + FileUtils.fileSeparator + "f" + FileUtils.fileSeparator + "g"
		        + FileUtils.fileSeparator + "h"));
		elements.add(new TestObject("4", "a" + FileUtils.fileSeparator + "f" + FileUtils.fileSeparator + "c"
		        + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("5", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "g"
		        + FileUtils.fileSeparator + "i"));
		elements.add(new TestObject("6", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "j"
		        + FileUtils.fileSeparator + "k"));
		final Set<Set<TestObject>> allCombinations = CollectionUtils.getAllCombinations(elements,
		                                                                                new TestObjectCombineOperator(2),
		                                                                                3);
		assertEquals(17, allCombinations.size());
		
	}
	
	/**
	 * Test get all combinations2.
	 */
	@Test
	public void testGetAllCombinations2() {
		
		final Set<TestObject> elements = new HashSet<TestObject>();
		elements.add(new TestObject("1", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "c"
		        + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("2", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "c"
		        + FileUtils.fileSeparator + "e"));
		elements.add(new TestObject("3", "a" + FileUtils.fileSeparator + "f" + FileUtils.fileSeparator + "g"
		        + FileUtils.fileSeparator + "h"));
		elements.add(new TestObject("4", "a" + FileUtils.fileSeparator + "f" + FileUtils.fileSeparator + "c"
		        + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("5", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "g"
		        + FileUtils.fileSeparator + "i"));
		elements.add(new TestObject("6", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "j"
		        + FileUtils.fileSeparator + "k"));
		
		final Set<Set<TestObject>> allCombinations = CollectionUtils.getAllCombinations(elements,
		                                                                                new TestObjectCombineOperator(2),
		                                                                                2);
		assertEquals(13, allCombinations.size());
		
	}
	
	/**
	 * Test get all combinations3.
	 */
	@Test
	public void testGetAllCombinations3() {
		
		final Set<TestObject> elements = new HashSet<TestObject>();
		elements.add(new TestObject("1", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "c"));
		elements.add(new TestObject("2", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("3", "a" + FileUtils.fileSeparator + "e" + FileUtils.fileSeparator + "c"));
		elements.add(new TestObject("4", "a" + FileUtils.fileSeparator + "e" + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("5", "a" + FileUtils.fileSeparator + "e" + FileUtils.fileSeparator + "f"));
		
		final Set<Set<TestObject>> allCombinations = CollectionUtils.getAllCombinations(elements,
		                                                                                new TestObjectCombineOperator(1),
		                                                                                10);
		assertEquals(10, allCombinations.size());
		
	}
	
}

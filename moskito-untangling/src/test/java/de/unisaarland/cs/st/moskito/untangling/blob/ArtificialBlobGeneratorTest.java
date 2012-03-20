/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.blob;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.untangling.blob.ArtificialBlobGenerator;
import de.unisaarland.cs.st.moskito.untangling.blob.BlobTransactionCombineOperator;
import de.unisaarland.cs.st.moskito.untangling.blob.CombineOperator;

public class ArtificialBlobGeneratorTest {
	
	private class TestObject {
		
		private final String id;
		private final String path;
		
		public TestObject(final String id, final String path) {
			this.id = id;
			this.path = path;
		}
		
		@SuppressWarnings ("unused")
		public String getId() {
			return id;
		}
		
		public String getPath() {
			return path;
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	private class TestObjectCombineOperator implements CombineOperator<TestObject> {
		
		private final int pd;
		
		public TestObjectCombineOperator(final int pd) {
			this.pd = pd;
		}
		
		@Override
		public boolean canBeCombined(final TestObject t1,
		                             final TestObject t2) {
			return BlobTransactionCombineOperator.canCombinePaths(t1.getPath(), t2.getPath(), pd);
		}
		
	}
	
	@Test
	public void testGetAllCombinations1() {
		
		Set<TestObject> elements = new HashSet<TestObject>();
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
		
		Set<Set<TestObject>> allCombinations = ArtificialBlobGenerator.getAllCombinations(elements,
		                                                                                  new TestObjectCombineOperator(
		                                                                                                                2),
		                                                                                  3);
		assertEquals(17, allCombinations.size());
		
	}
	
	@Test
	public void testGetAllCombinations2() {
		
		Set<TestObject> elements = new HashSet<TestObject>();
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
		
		Set<Set<TestObject>> allCombinations = ArtificialBlobGenerator.getAllCombinations(elements,
		                                                                                  new TestObjectCombineOperator(
		                                                                                                                2),
		                                                                                  2);
		assertEquals(13, allCombinations.size());
		
	}
	
	@Test
	public void testGetAllCombinations3() {
		
		Set<TestObject> elements = new HashSet<TestObject>();
		elements.add(new TestObject("1", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "c"));
		elements.add(new TestObject("2", "a" + FileUtils.fileSeparator + "b" + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("3", "a" + FileUtils.fileSeparator + "e" + FileUtils.fileSeparator + "c"));
		elements.add(new TestObject("4", "a" + FileUtils.fileSeparator + "e" + FileUtils.fileSeparator + "d"));
		elements.add(new TestObject("5", "a" + FileUtils.fileSeparator + "e" + FileUtils.fileSeparator + "f"));
		Set<Set<TestObject>> allCombinations = ArtificialBlobGenerator.getAllCombinations(elements,
		                                                                                  new TestObjectCombineOperator(
		                                                                                                                1),
		                                                                                  10);
		assertEquals(10, allCombinations.size());
		
	}
	
}

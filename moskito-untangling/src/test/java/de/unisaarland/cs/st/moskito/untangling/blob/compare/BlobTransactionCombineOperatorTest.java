/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.untangling.blob.compare;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.untangling.blob.compare.PackageDistanceCombineOperator;

/**
 * The Class BlobTransactionCombineOperatorTest.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class BlobTransactionCombineOperatorTest {
	
	/**
	 * Test can combine1.
	 */
	@Test
	public void testCanCombine1() {
		String pathA = "a/b/c/d/";
		String pathB = "a/b/";
		int packageDistance = 2;
		assertTrue(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine2.
	 */
	@Test
	public void testCanCombine2() {
		String pathA = "a/b/c/d/";
		String pathB = "a/b/";
		int packageDistance = 1;
		assertFalse(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine3.
	 */
	@Test
	public void testCanCombine3() {
		String pathB = "a/b/c/d/";
		String pathA = "a/b/";
		int packageDistance = 2;
		assertTrue(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine4.
	 */
	@Test
	public void testCanCombine4() {
		String pathB = "a/b/c/d/";
		String pathA = "a/b/";
		int packageDistance = 1;
		assertFalse(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine5.
	 */
	@Test
	public void testCanCombine5() {
		String pathB = "a/x/c/d/";
		String pathA = "a/y/c/d/";
		int packageDistance = 2;
		assertFalse(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
}

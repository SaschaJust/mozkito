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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * The Class BlobTransactionCombineOperatorTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class BlobTransactionCombineOperatorTest {
	
	/**
	 * Test can combine1.
	 */
	@Test
	public void testCanCombine1() {
		final String pathA = "a/b/c/d/";
		final String pathB = "a/b/";
		final int packageDistance = 2;
		assertTrue(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine2.
	 */
	@Test
	public void testCanCombine2() {
		final String pathA = "a/b/c/d/";
		final String pathB = "a/b/";
		final int packageDistance = 1;
		assertFalse(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine3.
	 */
	@Test
	public void testCanCombine3() {
		final String pathB = "a/b/c/d/";
		final String pathA = "a/b/";
		final int packageDistance = 2;
		assertTrue(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine4.
	 */
	@Test
	public void testCanCombine4() {
		final String pathB = "a/b/c/d/";
		final String pathA = "a/b/";
		final int packageDistance = 1;
		assertFalse(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	/**
	 * Test can combine5.
	 */
	@Test
	public void testCanCombine5() {
		final String pathB = "a/x/c/d/";
		final String pathA = "a/y/c/d/";
		final int packageDistance = 2;
		assertFalse(PackageDistanceCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
}

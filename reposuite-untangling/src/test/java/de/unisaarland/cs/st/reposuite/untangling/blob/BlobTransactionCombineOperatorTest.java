package de.unisaarland.cs.st.reposuite.untangling.blob;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;



public class BlobTransactionCombineOperatorTest {
	
	@Test
	public void  testCanCombine1(){
		String pathA = "a/b/c/d/";
		String pathB = "a/b/";
		int packageDistance = 2;
		assertTrue(BlobTransactionCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	@Test
	public void testCanCombine2() {
		String pathA = "a/b/c/d/";
		String pathB = "a/b/";
		int packageDistance = 1;
		assertFalse(BlobTransactionCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	@Test
	public void testCanCombine3() {
		String pathB = "a/b/c/d/";
		String pathA = "a/b/";
		int packageDistance = 2;
		assertTrue(BlobTransactionCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	@Test
	public void testCanCombine4() {
		String pathB = "a/b/c/d/";
		String pathA = "a/b/";
		int packageDistance = 1;
		assertFalse(BlobTransactionCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
	
	@Test
	public void testCanCombine5() {
		String pathB = "a/x/c/d/";
		String pathA = "a/y/c/d/";
		int packageDistance = 2;
		assertFalse(BlobTransactionCombineOperator.canCombinePaths(pathA, pathB, packageDistance));
	}
}

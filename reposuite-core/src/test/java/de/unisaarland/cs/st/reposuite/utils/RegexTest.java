/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RegexTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#analyzePattern(java.lang.String)}
	 * .
	 */
	@Test
	public void testAnalyzePattern() {
		fail("Not yet implemented"); // TODO
	}
	
	@Test
	public void testCheckRegex() {
		assertTrue(Regex.checkRegex("(a)"));
		assertTrue(Regex.checkRegex("(a)(b)"));
		assertTrue(Regex.checkRegex("(a(b))"));
		assertTrue(Regex.checkRegex("[(](a)"));
		assertTrue(Regex.checkRegex("[(][(][(](a)"));
		
		assertFalse(Regex.checkRegex("(a"));
		assertFalse(Regex.checkRegex("(a)b)"));
		assertFalse(Regex.checkRegex("(a(b"));
		assertFalse(Regex.checkRegex("()"));
		assertFalse(Regex.checkRegex("(?!)"));
		assertFalse(Regex.checkRegex("(?<!)"));
		assertFalse(Regex.checkRegex("(?=)"));
		assertFalse(Regex.checkRegex("(?<=)"));
		assertFalse(Regex.checkRegex("({test})"));
		assertFalse(Regex.checkRegex("(?!{test})"));
		assertFalse(Regex.checkRegex("(?<!{test})"));
		assertFalse(Regex.checkRegex("(?={test})"));
		assertFalse(Regex.checkRegex("(?<={test})"));
		
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#find(java.lang.String)}
	 * .
	 */
	@Test
	public void testFind() {
		Regex regex = new Regex("bleh(b+lub)bla(h+)");
		String text = "blehbbbblubblahh";
		List<RegexGroup> find = regex.find(text);
		
		assertEquals(2, find.size());
		
		assertEquals(1, find.get(0).getIndex());
		assertEquals("bbbblub", find.get(0).getMatch());
		assertEquals("", find.get(0).getName());
		assertEquals(regex.getPattern(), find.get(0).getPattern());
		assertEquals(text, find.get(0).getText());
		
		assertEquals(2, find.get(1).getIndex());
		assertEquals("hh", find.get(1).getMatch());
		assertEquals("", find.get(1).getName());
		assertEquals(regex.getPattern(), find.get(1).getPattern());
		assertEquals(text, find.get(1).getText());
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#findAll(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindAll() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#find(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindAllPossibleMatches() {
		Regex regex = new Regex("\\w+");
		String text = " abc,de ";
		List<String> find = regex.findAllPossibleMatches(text);
		String[] expected = new String[] { "abc", "ab", "a", "bc", "b", "c", "de", "d", "e" };
		
		assertEquals(expected.length, find.size());
		for (int i = 0; i < expected.length; ++i) {
			assertEquals(expected[i], find.get(i));
		}
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#findLongestMatchingPattern(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindLongestMatchingPattern() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#getGroupCount()}.
	 */
	@Test
	public void testGetGroupCount() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#getPattern()}.
	 */
	@Test
	public void testGetPattern() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#getText()}.
	 */
	@Test
	public void testGetText() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#matches(java.lang.String)}
	 * .
	 */
	@Test
	public void testMatches() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#find(java.lang.String)}
	 * .
	 */
	@Test
	public void testNamedGroups() {
		Regex regex = new Regex("({year}[0-9]{4})-({month}\\d{2})-({day}\\d{2})");
		String text = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		List<RegexGroup> find = regex.find(text);
		
		assertEquals(3, find.size());
		
		assertEquals(1, find.get(0).getIndex());
		assertEquals("2007", find.get(0).getMatch());
		assertEquals("year", find.get(0).getName());
		assertEquals(regex.getPattern(), find.get(0).getPattern());
		assertEquals(text, find.get(0).getText());
		
		assertEquals(2, find.get(1).getIndex());
		assertEquals("11", find.get(1).getMatch());
		assertEquals("month", find.get(1).getName());
		assertEquals(regex.getPattern(), find.get(1).getPattern());
		assertEquals(text, find.get(1).getText());
		
		assertEquals(3, find.get(2).getIndex());
		assertEquals("20", find.get(2).getMatch());
		assertEquals("day", find.get(2).getName());
		assertEquals(regex.getPattern(), find.get(2).getPattern());
		assertEquals(text, find.get(2).getText());
	}
	
	@Test
	public void testNegativeLookAhead() {
		Regex regex = new Regex(".*b(?!a).*");
		assertFalse(regex.matches("ba"));
		assertTrue(regex.matches("b"));
		assertTrue(regex.matches("bta"));
	}
	
	@Test
	public void testNegativeLookBehind() {
		Regex regex = new Regex(".*(?<!a)b.*");
		assertFalse(regex.matches("ab"));
		assertTrue(regex.matches("b"));
		assertTrue(regex.matches("atb"));
	}
	
	@Test
	public void testPositiveLookAhead() {
		Regex regex = new Regex(".*b(?=a).*");
		assertTrue(regex.matches("ba"));
		assertFalse(regex.matches("b"));
		assertFalse(regex.matches("bta"));
		
		regex = new Regex("b(?=({test}a))");
		List<RegexGroup> find = regex.find("ba");
		assertEquals(1, find.size());
		assertEquals("a", find.get(0).getMatch());
	}
	
	@Test
	public void testPositiveLookBehind() {
		Regex regex = new Regex(".*(?<=a)b.*");
		assertTrue(regex.matches("ab"));
		assertFalse(regex.matches("b"));
		assertFalse(regex.matches("atb"));
		
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#Regex(java.lang.String)}
	 * .
	 */
	@Test
	public void testRegexString() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#Regex(java.lang.String, int)}
	 * .
	 */
	@Test
	public void testRegexStringInt() {
		fail("Not yet implemented"); // TODO
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Regex#setPattern(java.lang.String)}
	 * .
	 */
	@Test
	public void testSetPattern() {
		fail("Not yet implemented"); // TODO
	}
	
}

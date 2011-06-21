/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

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
	 * {@link net.ownhero.dev.ioda.Regex#checkRegex(String)}
	 */
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
		
		assertFalse(Regex.checkRegex("akrfnr(a+)sdf(?!bleh(blub)blah)bleh"));
		assertFalse(Regex.checkRegex("({test}"));
		assertFalse(Regex.checkRegex("({test})\\"));
	}
	
	@Test
	public void testEmailRegex() {
		String anonEmail = "elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5";
		String email = "elharo@test-domain.de";
		
		Regex regex = new Regex("({email}" + Regex.emailPattern + ")");
		List<RegexGroup> find = regex.find(anonEmail);
		assertTrue(find.size() > 1);
		assertEquals("elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5", find.get(1).getMatch());
		find = regex.find(email);
		assertTrue(find.size() > 1);
		assertEquals("elharo@test-domain.de", find.get(1).getMatch());
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#find(java.lang.String)}
	 * .
	 */
	@Test
	public void testFind() {
		Regex regex = new Regex("bleh(b+lub)bla(h+)");
		String text = "blehbbbblubblahh";
		List<RegexGroup> find = regex.find(text);
		
		assertEquals(3, find.size());
		
		assertEquals(1, find.get(1).getIndex());
		assertEquals("bbbblub", find.get(1).getMatch());
		assertEquals("", find.get(1).getName());
		assertEquals(regex.getPattern(), find.get(1).getPattern());
		assertEquals(text, find.get(1).getText());
		
		assertEquals(2, find.get(2).getIndex());
		assertEquals("hh", find.get(2).getMatch());
		assertEquals("", find.get(2).getName());
		assertEquals(regex.getPattern(), find.get(2).getPattern());
		assertEquals(text, find.get(2).getText());
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#findAll(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindAll() {
		String text = "";
		
		for (int i = 0; i < 10; ++i) {
			text += "sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml";
			text += FileUtils.lineSeparator;
		}
		
		String pattern = "({test}[^\\s]+)";
		
		Regex regex = new Regex(pattern);
		
		List<List<RegexGroup>> findAll = regex.findAll(text);
		assertTrue(findAll != null);
		assertEquals(90, findAll.size());
		
		// System.err.println(JavaUtils.collectionToString(findAll));
		System.err.println(findAll.get(8).get(0).getMatch());
		for (int i = 0; i < 90; ++i) {
			assertEquals(1, findAll.get(i).size());
			System.err.println(i + " " + i % 9);
			assertEquals("test", findAll.get(i).get(0).getName());
			if (i % 9 == 3) {
				assertEquals("Oct", findAll.get(i).get(0).getMatch());
			}
			if (i % 9 == 6) {
				assertEquals("2010", findAll.get(i).get(0).getMatch());
			}
		}
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#find(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindAllPossibleMatches() {
		Regex regex = new Regex("\\w+");
		String text = " abc,de ";
		List<List<RegexGroup>> find = regex.findAllPossibleMatches(text);
		String[] expected = new String[] { "abc", "ab", "a", "bc", "b", "c", "de", "d", "e" };
		
		assertEquals(expected.length, find.size());
		for (int i = 0; i < expected.length; ++i) {
			assertEquals(expected[i], find.get(i).get(0).getMatch());
		}
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#findLongestMatchingPattern(java.lang.String)}
	 * .
	 */
	@Test
	public void testFindLongestMatchingPattern() {
		String pattern = "^({author}[^\\s]+)\\s+({hash}[^\\s]+)\\s([^+-]+[+-]\\d{4})\\s+[a-zA-Z]+:\\s.*(tinkabell+)\\w(?!bleh)";
		String text = "sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">";
		String match = Regex.findLongestMatchingPattern(pattern, text);
		
		assertTrue(new Regex("({author}[^\\s]+)\\s+({hash}[^\\s]+)\\s([^+-]+[+-]\\d{4})\\s+[a-zA-Z]+.*").matchesFull(text));
		assertEquals("^({author}[^\\s]+)\\s+({hash}[^\\s]+)\\s([^+-]+[+-]\\d{4})\\s+[a-zA-Z]+", match);
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#getGroupCount()}.
	 */
	@Test
	public void testGetGroupCount() {
		Regex regex;
		
		regex = new Regex("({test}this)out(2groups)");
		assertEquals(new Integer("2"), regex.getGroupCount());
		
		regex = new Regex("({test}this)out(?!2groups)");
		assertEquals(new Integer("1"), regex.getGroupCount());
		
		regex = new Regex("(?<=this)out(2groups)");
		assertEquals(new Integer("1"), regex.getGroupCount());
		
		regex = new Regex("(this)out(2groups)");
		assertEquals(new Integer("2"), regex.getGroupCount());
		
		regex = new Regex("({test}this)out(groups){2}");
		assertEquals(new Integer("2"), regex.getGroupCount());
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#matchesFull(java.lang.String)}
	 * .
	 */
	@Test
	public void testMatches() {
		String test = "abbatabc";
		Regex regex = new Regex(".*b+a.*");
		
		assertTrue(regex.matchesFull(test));
		assertEquals(new Integer(0), regex.getGroupCount());
		assertTrue(regex.matched());
		
		List<String> lines = new ArrayList<String>();
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: <?xml version=\"1.0\"?>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<parent>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 		<groupId>de.unisaarland.cs.st</groupId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 		<artifactId>reposuite</artifactId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 		<version>0.1-SNAPSHOT</version>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	</parent>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<modelVersion>4.0.0</modelVersion>");
		lines.add("   kim d5156a110af8 Wed Oct 20 17:25:58 2010 +0200 reposuite-fixindchanges/pom.xml: 	<groupId>de.unisaarland.cs.st</groupId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<artifactId>reposuite-fixindchanges</artifactId>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<packaging>jar</packaging>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: 	<name>reposuite-fixindchanges</name>");
		lines.add("sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: </project>");
		String pattern = "^\\s*([^ ]+)\\s+([^ ]+)\\s+([^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+([^:]+):\\s(.*)$";
		
		regex = new Regex(pattern);
		for (String line : lines) {
			assertTrue(regex.matchesFull(line));
			assertEquals(new Integer(5), regex.getGroupCount());
		}
	}
	
	/**
	 * Test method for
	 * {@link net.ownhero.dev.ioda.Regex#find(java.lang.String)}
	 * .
	 */
	@Test
	public void testNamedGroups() {
		Regex regex = new Regex("({year}[0-9]{4})-({month}\\d{2})-({day}\\d{2})");
		String text = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		List<RegexGroup> find = regex.find(text);
		
		assertEquals(4, find.size());
		
		assertEquals(1, find.get(1).getIndex());
		assertEquals("2007", find.get(1).getMatch());
		assertEquals("year", find.get(1).getName());
		assertEquals(regex.getPattern(), find.get(1).getPattern());
		assertEquals(text, find.get(1).getText());
		
		assertEquals(2, find.get(2).getIndex());
		assertEquals("11", find.get(2).getMatch());
		assertEquals("month", find.get(2).getName());
		assertEquals(regex.getPattern(), find.get(2).getPattern());
		assertEquals(text, find.get(2).getText());
		
		assertEquals(3, find.get(3).getIndex());
		assertEquals("20", find.get(3).getMatch());
		assertEquals("day", find.get(3).getName());
		assertEquals(regex.getPattern(), find.get(3).getPattern());
		assertEquals(text, find.get(3).getText());
	}
	
	@Test
	public void testNegativeLookAhead() {
		Regex regex = new Regex(".*b(?!a).*");
		assertFalse(regex.matchesFull("ba"));
		assertTrue(regex.matchesFull("b"));
		assertTrue(regex.matchesFull("bta"));
	}
	
	@Test
	public void testNegativeLookBehind() {
		Regex regex = new Regex(".*(?<!a)b.*");
		assertFalse(regex.matchesFull("ab"));
		assertTrue(regex.matchesFull("b"));
		assertTrue(regex.matchesFull("atb"));
	}
	
	@Test
	public void testPositiveLookAhead() {
		Regex regex = new Regex(".*b(?=a).*");
		assertTrue(regex.matchesFull("ba"));
		assertFalse(regex.matchesFull("b"));
		assertFalse(regex.matchesFull("bta"));
		
		regex = new Regex("b(?=({test}a))");
		List<RegexGroup> find = regex.find("ba");
		assertEquals(2, find.size());
		assertEquals("a", find.get(1).getMatch());
	}
	
	@Test
	public void testPositiveLookBehind() {
		Regex regex = new Regex(".*(?<=a)b.*");
		assertTrue(regex.matchesFull("ab"));
		assertFalse(regex.matchesFull("b"));
		assertFalse(regex.matchesFull("atb"));
		
	}
}

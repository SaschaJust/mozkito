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
/**
 * 
 */
package net.ownhero.dev.regex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RegexTest {
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#checkRegex(String)}
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
		final String anonEmail = "elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5";
		final String email = "elharo@test-domain.de";
		
		final Regex regex = new Regex("({email}" + Regex.emailPattern + ")");
		Match find = regex.find(anonEmail);
		assertTrue(find.getGroupCount() > 1);
		assertEquals("elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5", find.getGroup(1).getMatch());
		find = regex.find(email);
		assertTrue(find.getGroupCount() > 1);
		assertEquals("elharo@test-domain.de", find.getGroup(1).getMatch());
	}
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#find(java.lang.String)} .
	 */
	@Test
	public void testFind() {
		final Regex regex = new Regex("bleh(b+lub)bla(h+)");
		final String text = "blehbbbblubblahh";
		final Match find = regex.find(text);
		
		assertEquals(2, find.getGroupCount());
		
		assertEquals(1, find.getGroup(1).getIndex());
		assertEquals("bbbblub", find.getGroup(1).getMatch());
		assertEquals("", find.getGroup(1).getName());
		assertEquals(regex.getPattern(), find.getGroup(1).getPattern());
		assertEquals(text, find.getGroup(1).getText());
		
		assertEquals(2, find.getGroup(2).getIndex());
		assertEquals("hh", find.getGroup(2).getMatch());
		assertEquals("", find.getGroup(2).getName());
		assertEquals(regex.getPattern(), find.getGroup(2).getPattern());
		assertEquals(text, find.getGroup(2).getText());
	}
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#findAll(java.lang.String)} .
	 */
	@SuppressWarnings ("null")
	@Test
	public void testFindAll() {
		String text = "";
		
		for (int i = 0; i < 10; ++i) {
			text += "sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml";
			text += System.getProperty("line.separator");
		}
		
		final String pattern = "({test}[^\\s]+)";
		
		final Regex regex = new Regex(pattern);
		
		final MultiMatch multiMatch = regex.findAll(text);
		assertTrue(multiMatch != null);
		assertEquals(90, multiMatch.size());
		
		// System.err.println(JavaUtils.collectionToString(findAll));
		System.err.println(multiMatch.getMatch(8).getFullMatch().getMatch());
		for (int i = 1; i < 90; ++i) {
			assertEquals(1, multiMatch.getMatch(i).getGroupCount());
			System.err.println(i + " " + (i % 9));
			assertEquals("test", multiMatch.getMatch(i).getGroup(1).getName());
			if ((i % 9) == 3) {
				assertEquals("Oct", multiMatch.getMatch(i).getGroup(1).getMatch());
			}
			if ((i % 9) == 6) {
				assertEquals("2010", multiMatch.getMatch(i).getGroup(1).getMatch());
			}
		}
	}
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#find(java.lang.String)} .
	 */
	@Test
	public void testFindAllPossibleMatches() {
		final Regex regex = new Regex("\\w+");
		final String text = " abc,de ";
		final MultiMatch find = regex.findAllPossibleMatches(text);
		final String[] expected = new String[] { "abc", "ab", "a", "bc", "b", "c", "de", "d", "e" };
		
		assertEquals(expected.length, find.size());
		for (int i = 0; i < expected.length; ++i) {
			assertEquals(expected[i], find.getMatch(i).getFullMatch().getMatch());
		}
	}
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#findLongestMatchingPattern(java.lang.String)} .
	 */
	@Test
	public void testFindLongestMatchingPattern() {
		final String pattern = "^({author}[^\\s]+)\\s+({hash}[^\\s]+)\\s([^+-]+[+-]\\d{4})\\s+[a-zA-Z]+:\\s.*(tinkabell+)\\w(?!bleh)";
		final String text = "sascha e63a20871c7f Tue Oct 19 15:24:30 2010 +0200 reposuite-fixindchanges/pom.xml: <project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">";
		final String match = Regex.findLongestMatchingPattern(pattern, text);
		
		assertTrue(new Regex("({author}[^\\s]+)\\s+({hash}[^\\s]+)\\s([^+-]+[+-]\\d{4})\\s+[a-zA-Z]+.*").matchesFull(text));
		assertEquals("^({author}[^\\s]+)\\s+({hash}[^\\s]+)\\s([^+-]+[+-]\\d{4})\\s+[a-zA-Z]+", match);
	}
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#getGroupCount()}.
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
	 * Test method for {@link net.ownhero.dev.ioda.Regex#matchesFull(java.lang.String)} .
	 */
	@Test
	public void testMatches() {
		final String test = "abbatabc";
		Regex regex = new Regex(".*b+a.*");
		
		assertTrue(regex.matchesFull(test));
		assertEquals(new Integer(0), regex.getGroupCount());
		assertTrue(regex.matched());
		
		final List<String> lines = new ArrayList<String>();
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
		final String pattern = "^\\s*([^ ]+)\\s+([^ ]+)\\s+([^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+[^ ]+\\s+\\+[0-9]{4})\\s+([^:]+):\\s(.*)$";
		
		regex = new Regex(pattern);
		for (final String line : lines) {
			assertTrue(regex.matchesFull(line));
			assertEquals(new Integer(5), regex.getGroupCount());
		}
	}
	
	/**
	 * Test method for {@link net.ownhero.dev.ioda.Regex#find(java.lang.String)} .
	 */
	@Test
	public void testNamedGroups() {
		final Regex regex = new Regex("({year}[0-9]{4})-({month}\\d{2})-({day}\\d{2})");
		final String text = "^f554664a346629dc2b839f7292d06bad2db4aec hello.py (Mike Donaghy 2007-11-20 15:28:39 -0500 1) #!/usr/bin/env python";
		final Match find = regex.find(text);
		
		assertEquals(3, find.getGroupCount());
		assertEquals(3, find.getNamedGroupCount());
		
		assertEquals(1, find.getGroup(1).getIndex());
		assertEquals("2007", find.getGroup(1).getMatch());
		assertEquals("year", find.getGroup(1).getName());
		assertEquals(regex.getPattern(), find.getGroup(1).getPattern());
		assertEquals(text, find.getGroup(1).getText());
		
		assertEquals(2, find.getGroup(2).getIndex());
		assertEquals("11", find.getGroup(2).getMatch());
		assertEquals("month", find.getGroup(2).getName());
		assertEquals(regex.getPattern(), find.getGroup(2).getPattern());
		assertEquals(text, find.getGroup(2).getText());
		
		assertEquals(3, find.getGroup(3).getIndex());
		assertEquals("20", find.getGroup(3).getMatch());
		assertEquals("day", find.getGroup(3).getName());
		assertEquals(regex.getPattern(), find.getGroup(3).getPattern());
		assertEquals(text, find.getGroup(3).getText());
	}
	
	@Test
	public void testNegativeLookAhead() {
		final Regex regex = new Regex(".*b(?!a).*");
		assertFalse(regex.matchesFull("ba"));
		assertTrue(regex.matchesFull("b"));
		assertTrue(regex.matchesFull("bta"));
	}
	
	@Test
	public void testNegativeLookBehind() {
		final Regex regex = new Regex(".*(?<!a)b.*");
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
		final Match match = regex.find("ba");
		assertEquals(1, match.getGroupCount());
		assertEquals("a", match.getGroup(1).getMatch());
	}
	
	@Test
	public void testPositiveLookBehind() {
		final Regex regex = new Regex(".*(?<=a)b.*");
		assertTrue(regex.matchesFull("ab"));
		assertFalse(regex.matchesFull("b"));
		assertFalse(regex.matchesFull("atb"));
		
	}
}

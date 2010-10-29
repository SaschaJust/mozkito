package de.unisaarland.cs.st.reposuite.rcs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.Regex;

public class GitLogParserTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAthorRegExp() {
		String author1 = "Carsten Nielsen <heycarsten@gmail.com>";
		Regex regex = GitLogParser.regex;
		assertTrue(Regex.checkRegex(author1));
		assertTrue(regex.matches(author1));
		assertTrue(regex.find(author1) != null);
		assertTrue(regex.find(author1).size() > 0);
		assertEquals("Carsten", regex.getGroup("name"));
		assertEquals("Nielsen", regex.getGroup("lastname").trim());
		assertEquals("heycarsten@gmail.com", regex.getGroup("email"));
		
		String author2 = "tinogomes <tinorj@gmail.com>";
		assertTrue(Regex.checkRegex(author2));
		assertTrue(regex.matches(author2));
		assertTrue(regex.find(author2) != null);
		assertTrue(regex.find(author2).size() > 0);
		assertEquals("tinogomes", regex.getGroup("name").trim());
		assertEquals(null, regex.getGroup("lastname"));
		assertEquals("tinorj@gmail.com", regex.getGroup("email"));
		
		String author3 = "<tinorj@gmail.com>";
		assertTrue(Regex.checkRegex(author3));
		assertTrue(regex.matches(author3));
		assertTrue(regex.find(author3) != null);
		assertTrue(regex.find(author3).size() > 0);
		assertEquals(null, regex.getGroup("name"));
		assertEquals(null, regex.getGroup("lastname"));
		assertEquals("tinorj@gmail.com", regex.getGroup("email"));
		
		String author4 = "tinogomes";
		assertTrue(Regex.checkRegex(author4));
		assertTrue(regex.matches(author4));
		assertTrue(regex.find(author4) != null);
		assertTrue(regex.find(author4).size() > 0);
		assertEquals("tinogomes", regex.getGroup("name").trim());
		assertEquals(null, regex.getGroup("lastname"));
		assertEquals(null, regex.getGroup("email"));
	}
}

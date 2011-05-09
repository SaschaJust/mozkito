package de.unisaarland.cs.st.reposuite.rcs.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;

public class GitLogParserTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAuthorRegExp() {
		String author1 = "Author: Carsten Nielsen <heycarsten@gmail.com>";
		
		Person author = GitLogParser.getAuthor(author1, 0);
		
		assertTrue(author.getFullnames() != null);
		assertEquals(1, author.getFullnames().size());
		assertTrue(author.getFullnames().contains("Carsten Nielsen"));
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("heycarsten@gmail.com"));
		assertTrue(author.getUsernames() != null);
		assertEquals(0, author.getUsernames().size());
		
		String author2 = "Author: tinogomes <tinorj@gmail.com>";
		author = GitLogParser.getAuthor(author2, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("tinorj@gmail.com"));
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("tinogomes"));
		
		String author3 = "Author: <tinorj@gmail.com>";
		author = GitLogParser.getAuthor(author3, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("tinorj@gmail.com"));
		assertTrue(author.getUsernames() != null);
		assertEquals(0, author.getUsernames().size());
		
		String author4 = "Author: tinogomes";
		author = GitLogParser.getAuthor(author4, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(0, author.getEmailAddresses().size());
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("tinogomes"));
		
		String author5 = "Author: just <just@b3cd8044-6b0a-409c-a07a-9925dc373c42>";
		author = GitLogParser.getAuthor(author5, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("just@b3cd8044-6b0a-409c-a07a-9925dc373c42"));
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("just"));
		
		String author6 = "Author: just <just>";
		author = GitLogParser.getAuthor(author6, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(0, author.getEmailAddresses().size());
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("just"));
		
	}
	
	@Test
	public void testEmailRegex() {
		String message = "hjkkjdskj ksdjfkljf;lsjdfkldsj f@lkdsaf elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5 fhdsjfjkshdfjklhsa fjsadfh jkldsahfl";
		
		Regex tmp = new Regex(GitLogParser.emailBaseRegex.getPattern());
		
		GitLogParser.emailBaseRegex = tmp;
		// tmp = GitLogParser.emailBaseRegex;
		tmp.getPattern();
		assertTrue(tmp.matches(message));
		tmp.find(message);
		String email = tmp.getGroup("email");
		assertEquals("elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5", email);
		
		message = "hjkkjdskj ksdjfkljf;lsjdfkldsj f@lkdsaf  elharo@bla.de fhdsjfjkshdfjklhsa fjsadfh jkldsahfl";
		assertTrue(tmp.matches(message));
		tmp.find(message);
		email = tmp.getGroup("email");
		assertEquals("elharo@bla.de", email);
	}
	
	@Test
	public void testOriginalIdRegex() {
		String s = "git-svn-id: http://svn.codehaus.org/jruby/trunk/jruby@7896 961051c9-f516-0410-bf72-c9f7e237a7b7";
		GitLogParser.originalIdRegex.find(s);
		assertTrue(GitLogParser.originalIdRegex.getGroup("hit") != null);
		assertEquals("7896", GitLogParser.originalIdRegex.getGroup("hit").trim());
	}
	
	@Test
	public void testRegressionBug169() {
		String message = "Author: jvanzyl <jvanzyl>\nDate:   Tue Jan 13 22:54:37 2004 +0000\n o http://jira.codehaus.org/secure/ViewIssue.jspa?key=XSTR-17\n\ngit-svn-id: file:///scratch/kim/miner_repos/xstream/svn_repo_09_03_2011/trunk@61 f887afa5-a9cb-4ae6-b411-6339e5819859";
		List<RegexGroup> groups = GitLogParser.gitLogDateFormatRegex.find(message);
		int found = 0;
		assertEquals(9, groups.size());
		for (RegexGroup group : groups) {
			if ((group.getName().equals("Z")) && (group.getMatch().equals("+0000"))) {
				++found;
			} else if ((group.getName().equals("EEE")) && (group.getMatch().equals("Tue"))) {
				++found;
			} else if ((group.getName().equals("MMM")) && (group.getMatch().equals("Jan"))) {
				++found;
			} else if ((group.getName().equals("d")) && (group.getMatch().equals("13"))) {
				++found;
			} else if ((group.getName().equals("HH")) && (group.getMatch().equals("22"))) {
				++found;
			} else if ((group.getName().equals("mm")) && (group.getMatch().equals("54"))) {
				++found;
			} else if ((group.getName().equals("ss")) && (group.getMatch().equals("37"))) {
				++found;
			} else if ((group.getName().equals("yyyy")) && (group.getMatch().equals("2004"))) {
				++found;
			}
		}
		assertEquals(8, found);
	}
}

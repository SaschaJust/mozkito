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
package org.mozkito.versions.git;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.junit.Test;
import org.mozkito.persistence.model.Person;

// TODO: Auto-generated Javadoc
/**
 * The Class GitLogParserTest.
 */
public class GitLogParserTest {
	
	/**
	 * Test author reg exp.
	 */
	@Test
	public void testAuthorRegExp() {
		final String author1 = "Author: Carsten Nielsen <heycarsten@gmail.com>";
		
		Person author = GitLogParser.getAuthor(author1, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(1, author.getFullnames().size());
		assertTrue(author.getFullnames().contains("Carsten Nielsen"));
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("heycarsten@gmail.com"));
		assertTrue(author.getUsernames() != null);
		assertEquals(0, author.getUsernames().size());
		
		final String author2 = "Author: tinogomes <tinorj@gmail.com>";
		author = GitLogParser.getAuthor(author2, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("tinorj@gmail.com"));
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("tinogomes"));
		
		final String author3 = "Author: <tinorj@gmail.com>";
		author = GitLogParser.getAuthor(author3, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("tinorj@gmail.com"));
		assertTrue(author.getUsernames() != null);
		assertEquals(0, author.getUsernames().size());
		
		final String author4 = "Author: tinogomes";
		author = GitLogParser.getAuthor(author4, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(0, author.getEmailAddresses().size());
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("tinogomes"));
		
		final String author5 = "Author: just <just@b3cd8044-6b0a-409c-a07a-9925dc373c42>";
		author = GitLogParser.getAuthor(author5, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(1, author.getEmailAddresses().size());
		assertTrue(author.getEmailAddresses().contains("just@b3cd8044-6b0a-409c-a07a-9925dc373c42"));
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("just"));
		
		final String author6 = "Author: just <just>";
		author = GitLogParser.getAuthor(author6, 0);
		assertTrue(author.getFullnames() != null);
		assertEquals(0, author.getFullnames().size());
		assertTrue(author.getEmailAddresses() != null);
		assertEquals(0, author.getEmailAddresses().size());
		assertTrue(author.getUsernames() != null);
		assertEquals(1, author.getUsernames().size());
		assertTrue(author.getUsernames().contains("just"));
		
	}
	
	/**
	 * Test email regex.
	 */
	@Test
	public void testEmailRegex() {
		String message = "hjkkjdskj ksdjfkljf;lsjdfkldsj f@lkdsaf elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5 fhdsjfjkshdfjklhsa fjsadfh jkldsahfl";
		
		final Regex tmp = new Regex(GitLogParser.emailBaseRegex.getPattern());
		
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
	
	/**
	 * Test original id regex.
	 */
	@Test
	public void testOriginalIdRegex() {
		final String s = "git-svn-id: http://svn.codehaus.org/jruby/trunk/jruby@7896 961051c9-f516-0410-bf72-c9f7e237a7b7";
		GitLogParser.originalIdRegex.find(s);
		assertTrue(GitLogParser.originalIdRegex.getGroup("hit") != null);
		assertEquals("7896", GitLogParser.originalIdRegex.getGroup("hit").trim());
	}
	
	/**
	 * Test regression bug169.
	 */
	@Test
	public void testRegressionBug169() {
		final String message = "Author: jvanzyl <jvanzyl>\nDate:   Tue Jan 13 22:54:37 2004 +0000\n o http://jira.codehaus.org/secure/ViewIssue.jspa?key=XSTR-17\n\ngit-svn-id: file:///scratch/kim/miner_repos/xstream/svn_repo_09_03_2011/trunk@61 f887afa5-a9cb-4ae6-b411-6339e5819859";
		final Match groups = GitLogParser.gitLogDateFormatRegex.find(message);
		int found = 0;
		assertEquals(8, groups.getGroupCount());
		for (final Group group : groups) {
			if (("Z".equals(group.getName())) && ("+0000".equals(group.getMatch()))) {
				++found;
			} else if (("EEE".equals(group.getName())) && ("Tue".equals(group.getMatch()))) {
				++found;
			} else if (("MMM".equals(group.getName())) && ("Jan".equals(group.getMatch()))) {
				++found;
			} else if (("d".equals(group.getName())) && ("13".equals(group.getMatch()))) {
				++found;
			} else if (("HH".equals(group.getName())) && ("22".equals(group.getMatch()))) {
				++found;
			} else if (("mm".equals(group.getName())) && ("54".equals(group.getMatch()))) {
				++found;
			} else if (("ss".equals(group.getName())) && ("37".equals(group.getMatch()))) {
				++found;
			} else if (("yyyy".equals(group.getName())) && ("2004".equals(group.getMatch()))) {
				++found;
			}
		}
		assertEquals(8, found);
	}
}

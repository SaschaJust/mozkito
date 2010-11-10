package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class JiraTrackerTest {
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOverallIdDetection() {
		String s = "[JAXEN-210] Jaxen does not cope well with numeric types other than Double";
		assertTrue(JiraIDExtractor.idRegex.matches(s));
		List<RegexGroup> find = JiraIDExtractor.idRegex.find(s);
		assertEquals(1, find.size());
		assertEquals(find.get(0).getMatch(), "210");
	}
	
	@Test
	public void testOverallIdFilter(){
		JiraTracker tracker = new JiraTracker();
		try {
			tracker.setup(
					new URI("http://jira.codehaus.org/si/jira.issueviews:issue-xml/"),
					new URI(
					"file:///Users/kim/Downloads/JAXEN_JIRA.xml"),
					"JAXEN-" + Tracker.bugIdPlaceholder + "/JAXEN-" + Tracker.bugIdPlaceholder + ".xml", null, null,
					new Long(1l), new Long(1000l));
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

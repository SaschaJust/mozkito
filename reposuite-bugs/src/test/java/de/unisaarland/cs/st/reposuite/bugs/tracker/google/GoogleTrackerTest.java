package de.unisaarland.cs.st.reposuite.bugs.tracker.google;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class GoogleTrackerTest {
	
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	@Test
	public void testFetchRegex() {
		String fetchURI = "https://code.google.com/feeds/issues/p/webtoolkit/issues/full";
		List<RegexGroup> groups = GoogleTracker.fetchRegex.find(fetchURI);
		assertEquals(2, groups.size());
		assertEquals("project", groups.get(1).getName());
		assertEquals("webtoolkit", groups.get(1).getMatch());
	}
	

}

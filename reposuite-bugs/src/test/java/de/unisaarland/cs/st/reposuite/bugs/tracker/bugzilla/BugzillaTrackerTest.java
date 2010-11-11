package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;


public class BugzillaTrackerTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCheckRAW() {
		BugzillaTracker tracker = new BugzillaTracker();
		try {
			try {
				assertFalse(tracker.checkRAW(new RawReport(1l, IOUtils.fetch(new URI(
				        "https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xnl&id=1234")))));
				assertFalse(tracker.checkRAW(new RawReport(1l, IOUtils.fetch(new URI(
				        "https://bugs.eclipse.org/bugs/show_bug.cgi?ctype=xml&id=1234")))));
			} catch (UnsupportedProtocolException e) {
				e.printStackTrace();
				fail();
			} catch (FetchException e) {
				e.printStackTrace();
				fail();
			}
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

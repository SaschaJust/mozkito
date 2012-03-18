package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.BugzillaTracker;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class ReportPersistence_NetTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings (unit = "bugs")
	public void testBugzilla() {
		final BugzillaTracker tracker = new BugzillaTracker();
		try {
			tracker.setup(new URI("https://bugzilla.mozilla.org/"), null, "show_bug.cgi?ctype=xml&id=<BUGID>", null,
			              null, 444780l, 444780l, null);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		
		final ReportLink reportLink = tracker.getLinkFromId("444780");
		final Report report = tracker.parse(reportLink);
		// final Report report = new Report(1234l);
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().save(report);
		getPersistenceUtil().commitTransaction();
	}
	
}

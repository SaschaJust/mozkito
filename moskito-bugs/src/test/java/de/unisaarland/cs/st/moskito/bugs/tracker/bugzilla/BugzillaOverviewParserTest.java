package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;

public class BugzillaOverviewParserTest {
	
	@Test
	public void testEclipse() {
		try {
			final BugzillaTracker tracker = new BugzillaTracker();
			tracker.setup(new URI("https://bugs.eclipse.org/bugs/"),
			              this.getClass().getResource(FileUtils.fileSeparator + "bugzilla_eclipse_overview.html")
			                  .toURI(), "show_bug.cgi?ctype=xml&id=<BUGID>", null, null, null, null, null);
			final BugzillaOverviewParser parser = new BugzillaOverviewParser(tracker);
			assertTrue(parser.parseOverview());
			
			int counter = 0;
			ReportLink reportLink;
			final Set<String> bugIds = new HashSet<String>();
			while ((reportLink = tracker.getNextReportLink()) != null) {
				++counter;
				bugIds.add(reportLink.getBugId());
			}
			
			assertEquals(22, counter);
			assertTrue(bugIds.contains("314163"));
			assertTrue(bugIds.contains("158767"));
			
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			fail();
		}
	}
	
	@Test
	public void testMozilla() {
		try {
			final BugzillaTracker tracker = new BugzillaTracker();
			tracker.setup(new URI("https://bugzilla.mozilla.org/"),
			              this.getClass().getResource(FileUtils.fileSeparator + "bugzilla_mozilla_overview.html")
			                  .toURI(), "show_bug.cgi?ctype=xml&id=<BUGID>", null, null, null, null, null);
			final BugzillaOverviewParser parser = new BugzillaOverviewParser(tracker);
			assertTrue(parser.parseOverview());
			
			int counter = 0;
			ReportLink reportLink;
			final Set<String> bugIds = new HashSet<String>();
			while ((reportLink = tracker.getNextReportLink()) != null) {
				++counter;
				bugIds.add(reportLink.getBugId());
			}
			
			assertEquals(1016, counter);
			assertTrue(bugIds.contains("642368"));
			assertTrue(bugIds.contains("61267"));
			
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			fail();
		}
	}
}

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
			final BugzillaOverviewParser parser = new BugzillaOverviewParser(
			                                                                 new URI("https://bugs.eclipse.org/bugs/"),
			                                                                 this.getClass()
			                                                                     .getResource(FileUtils.fileSeparator
			                                                                                          + "bugzilla_eclipse_overview.html")
			                                                                     .toURI());
			assertTrue(parser.parseOverview());
			
			int counter = 0;
			final Set<String> bugIds = new HashSet<String>();
			for (final ReportLink reportLink : parser.getReportLinks()) {
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
			final BugzillaOverviewParser parser = new BugzillaOverviewParser(
			                                                                 new URI("https://bugzilla.mozilla.org/"),
			                                                                 this.getClass()
			                                                                     .getResource(FileUtils.fileSeparator
			                                                                                          + "bugzilla_mozilla_overview.html")
			                                                                     .toURI());
			assertTrue(parser.parseOverview());
			
			int counter = 0;
			final Set<String> bugIds = new HashSet<String>();
			for (final ReportLink reportLink : parser.getReportLinks()) {
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

package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.kisa.Logger;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;

public class BugzillaOverviewParserTest {
	
	@Test
	public void testEclipse() {
		try {
			final RawReport rawReport = new RawReport(
			                                          1l,
			                                          IOUtils.fetch(this.getClass()
			                                                            .getResource(FileUtils.fileSeparator
			                                                                                 + "bugzilla_eclipse_overview.html")
			                                                            .toURI()));
			final BugzillaOverviewParser parser = new BugzillaOverviewParser();
			assertTrue(parser.parse(rawReport.getContent()));
			final Set<? extends Long> bugIds = parser.getBugIds();
			assertEquals(22, bugIds.size());
			assertTrue(bugIds.contains(314163l));
			assertTrue(bugIds.contains(158767l));
			
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
			final RawReport rawReport = new RawReport(
			                                          1l,
			                                          IOUtils.fetch(this.getClass()
			                                                            .getResource(FileUtils.fileSeparator
			                                                                                 + "bugzilla_mozilla_overview.html")
			                                                            .toURI()));
			final BugzillaOverviewParser parser = new BugzillaOverviewParser();
			assertTrue(parser.parse(rawReport.getContent()));
			final Set<? extends Long> bugIds = parser.getBugIds();
			assertEquals(1016, bugIds.size());
			assertTrue(bugIds.contains(642368l));
			assertTrue(bugIds.contains(61267l));
			
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			fail();
		}
	}
	
}

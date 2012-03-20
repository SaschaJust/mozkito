package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.ioda.FileUtils;

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
		try {
			final BugzillaTracker tracker = new BugzillaTracker();
			try {
				tracker.setup(new URI("https://bugzilla.mozilla.org/"), null, null,
				              this.getClass().getResource(FileUtils.fileSeparator + "bugzilla_eclipse_overview.html")
				                  .toURI(), "4.0.4");
			} catch (final InvalidParameterException e) {
				e.printStackTrace();
				fail();
			} catch (final URISyntaxException e) {
				e.printStackTrace();
				fail();
			}
			
			final ReportLink reportLink = new ReportLink(
			                                             new URI(
			                                                     "https://bugzilla.mozilla.org/show_bug.cgi?ctype=xml&id=444780"),
			                                             "444780");
			
			final Report report = tracker.parse(reportLink);
			// final Report report = new Report(1234l);
			getPersistenceUtil().beginTransaction();
			getPersistenceUtil().save(report);
			getPersistenceUtil().commitTransaction();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		} finally {
			
		}
	}
}

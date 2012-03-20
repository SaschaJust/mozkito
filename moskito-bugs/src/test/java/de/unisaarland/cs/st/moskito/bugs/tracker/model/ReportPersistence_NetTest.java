package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
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
				tracker.setup(new URI("https://bugzilla.mozilla.org/"), null, "show_bug.cgi?ctype=xml&id=<BUGID>",
				              null, null, 444780l, 444780l, null);
			} catch (final InvalidParameterException e) {
				e.printStackTrace();
				fail();
			} catch (final URISyntaxException e) {
				e.printStackTrace();
				fail();
			}
			final RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(444780l));
			
			final XmlReport xmlReport = tracker.createDocument(rawReport);
			final Report report = tracker.parse(xmlReport);
			// final Report report = new Report(1234l);
			getPersistenceUtil().beginTransaction();
			getPersistenceUtil().save(report);
			getPersistenceUtil().commitTransaction();
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.BugzillaTracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.BugzillaTracker_4_0_4_Test;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class ReportPersistence_NetTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings (unit = "bugs")
	public void testBugzilla() {
		try {
			RawReport rawReport = new RawReport(
			                                    1l,
			                                    IOUtils.fetch(BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator
			                                                                                                       + "bugzilla_114562.xml")
			                                                                                  .toURI()));
			final BugzillaTracker tracker = new BugzillaTracker();
			String url = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator + "bugzilla_114562.xml")
			                                             .toString();
			url = url.substring(0, url.lastIndexOf("bugzilla_114562.xml"));
			final String pattern = "bugzilla_" + Tracker.getBugidplaceholder() + ".xml";
			
			try {
				tracker.setup(new URI(url), null, pattern, null, null, 114562l, 114562l, null);
			} catch (final InvalidParameterException e) {
				e.printStackTrace();
				fail();
			} catch (final URISyntaxException e) {
				e.printStackTrace();
				fail();
			}
			rawReport = tracker.fetchSource(tracker.getLinkFromId(114562l));
			
			final XmlReport xmlReport = tracker.createDocument(rawReport);
			final Report report = tracker.parse(xmlReport);
			getPersistenceUtil().save(report);
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

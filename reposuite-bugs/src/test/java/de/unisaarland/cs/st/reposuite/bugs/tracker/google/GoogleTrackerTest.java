package de.unisaarland.cs.st.reposuite.bugs.tracker.google;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.google.gdata.client.sites.SitesService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.sites.AttachmentEntry;
import com.google.gdata.data.sites.ContentFeed;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils.FileShutdownAction;
import de.unisaarland.cs.st.reposuite.utils.Regex;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class GoogleTrackerTest {
	
	protected static final Regex dateTimeHistoryFormatRegex = new Regex(
	                                                                    "(({yyyy}\\d{4})-({MM}\\d{2})-({dd}\\d{2})T({HH}\\d{2}):({mm}[0-5]\\d):({ss}[0-5]\\d))");
	
	@AfterClass
	public static void afterClass() {
		// delete all reposuite directories and files
		Map<FileShutdownAction, Set<File>> openFiles = FileUtils.getManagedOpenFiles();
		Set<File> set = openFiles.get(FileShutdownAction.DELETE);
		if (set != null) {
			for (File f : set) {
				try {
					if (f.isFile()) {
						FileUtils.forceDelete(f);
					} else {
						FileUtils.deleteDirectory(f);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
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
	
	@Test
	public void testTmp() {
		try {
			
			SitesService service = new SitesService("unisaarland-reposuite-0.1");
			
			// IssuesQuery iQuery = new IssuesQuery(
			// new URL(
			// "http://google-web-toolkit.googlecode.com/issues/attachment?aid=4009690497676971601&amp;name=before.png&amp;token=ff33a9e71e62fc7bc0ceaccfff395de7"));
			//
			ContentFeed contentFeed = service.getFeed(new URL(
			                                                  "https://code.google.com/feeds/issues/p/google-web-toolkit/issues/full?kind=attachment&id=4369"),
			                                          ContentFeed.class);
			
			List<AttachmentEntry> entries = contentFeed.getEntries(AttachmentEntry.class);
			System.out.println(entries.size());
			for (AttachmentEntry entry : entries) {
				System.out.println(" title: " + entry.getTitle().getPlainText());
				System.out.println(" id: " + entry);
				if (entry.getParentLink() != null) {
					System.out.println(" parent id: " + entry.getParentLink().getHref());
				}
				if (entry.getSummary() != null) {
					System.out.println(" description: " + entry.getSummary().getPlainText());
				}
				System.out.println(" revision: " + entry.getRevision().getValue());
				MediaContent content = (MediaContent) entry.getContent();
				System.out.println(" src: " + content.getUri());
				System.out.println(" content type: " + content.getMimeType().getMediaType());
			}
			
			// URL feedUrl = new
			// URL("https://code.google.com/feeds/issues/p/google-web-toolkit/issues/4369/comments/full");
			//
			
			// IssueCommentsFeed resultFeed = service.getFeed(feedUrl,
			// AttachmentFeed.class);
			//
			// for (int i = 0; i < resultFeed.getEntries().size(); i++) {
			// IssueCommentsEntry commentEntry = resultFeed.getEntries().get(i);
			//
			// HTMLTextContent content = (TextContent)
			// commentEntry.getContent();
			//
			// System.out.println(content.getContent().getPlainText());
			// System.out.println(content.getClass().getCanonicalName());
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTracker() {
		File cacheDir = FileUtils.createRandomDir("test", "googletracker", FileShutdownAction.DELETE);
		try {
			if (System.getProperties().contains("test.skipnet")) {
				return;
			}
			
			GoogleTracker tracker = new GoogleTracker();
			tracker.setup(new URI("https://code.google.com/feeds/issues/p/google-web-toolkit/issues/full"), null, null,
			              null, null, 4380l, 4380l, cacheDir.getAbsolutePath());
			
			Long nextId = tracker.getNextId();
			assertEquals(4380, nextId, 0);
			URI linkFromId = tracker.getLinkFromId(nextId);
			assertEquals(new URI("4380"), linkFromId);
			RawReport rawReport = tracker.fetchSource(linkFromId);
			assert (rawReport instanceof GoogleRawContent);
			XmlReport xmlReport = tracker.createDocument(rawReport);
			assertEquals(rawReport, xmlReport);
			assert (xmlReport instanceof GoogleRawContent);
			Report report = tracker.parse(xmlReport);
			assertEquals(4380, report.getId());
			assertEquals(1, report.getAssignedTo().getUsernames().size());
			assertTrue(report.getAssignedTo().getUsernames().contains("jat@google.com"));
			assertEquals("DevPlugin", report.getCategory());
			
			assertEquals(60, report.getComments().size());
			
			assertEquals(null, report.getComponent());
			assertTrue(DateTimeUtils.parseDate("2009-12-19T15:38:51.000Z", dateTimeHistoryFormatRegex)
			                        .isEqual(report.getCreationTimestamp()));
			
			assertTrue(report.getHistory() != null);
			assertEquals(2, report.getHistory().size());
			
			assertEquals(rawReport.getFetchTime(), report.getLastFetch());
			assertEquals(new Report(0).getPriority(), report.getPriority());
			assertEquals(new Report(0).getProduct(), report.getProduct());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			assertTrue(DateTimeUtils.parseDate("2010-02-02T00:07:22.000Z", dateTimeHistoryFormatRegex)
			                        .isEqual(report.getResolutionTimestamp()));
			assertTrue(report.getResolver() != null);
			assertEquals(1, report.getResolver().getUsernames().size());
			assertTrue(report.getResolver().getUsernames().contains("jat@google.com"));
			assertEquals(new Report(0).getSeverity(), report.getSeverity());
			report.getSiblings();
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals("DevMode plug-in doesn't work in Firefox 3.6", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals(1, report.getSubmitter().getUsernames().size());
			assertTrue(report.getSubmitter().getUsernames().contains("t.broyer"));
			assertEquals("", report.getSummary());
			assertEquals(Type.RFE, report.getType());
			assertEquals(null, report.getVersion());
			assertTrue(report.getDescription().length() > 0);
			assertTrue(report.getDescription().contains("Firefox keeps saying the page needs a plugin when passing"));
			
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (FetchException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				FileUtils.deleteDirectory(cacheDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

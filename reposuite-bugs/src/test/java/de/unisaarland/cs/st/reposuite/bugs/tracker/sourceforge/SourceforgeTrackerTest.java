package de.unisaarland.cs.st.reposuite.bugs.tracker.sourceforge;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Type;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.rcs.model.Person;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;

public class SourceforgeTrackerTest {
	
	private static String url1 = "http://sourceforge.net/tracker/?group_id=97367&atid=617889";
	private static String url2 = "http://sourceforge.net/tracker/?words=tracker_browse&sort=open_date&sortdir=desc&offset=50&group_id=97367&atid=617889&assignee=&status=&category=&artgroup=&keyword=&submitter=&artifact_id=";
	
	@Test
	public void testAtIdRegex() {
		SourceforgeTracker.atIdRegex.find(url1);
		assertEquals(new Integer(1), SourceforgeTracker.atIdRegex.getGroupCount());
		assertEquals("617889", SourceforgeTracker.atIdRegex.getGroup("atid"));
		
		SourceforgeTracker.atIdRegex.find(url2);
		assertEquals(new Integer(1), SourceforgeTracker.atIdRegex.getGroupCount());
		assertEquals("617889", SourceforgeTracker.atIdRegex.getGroup("atid"));
		
		String newUrl = SourceforgeTracker.atIdRegex.replaceAll(url1, "atid=123456");
		SourceforgeTracker.atIdRegex.find(newUrl);
		assertEquals(new Integer(1), SourceforgeTracker.atIdRegex.getGroupCount());
		assertEquals("123456", SourceforgeTracker.atIdRegex.getGroup("atid"));
	}
	
	@Test
	public void testGroupIdRegex() {
		SourceforgeTracker.groupIdRegex.find(url1);
		assertEquals(new Integer(1), SourceforgeTracker.groupIdRegex.getGroupCount());
		assertEquals("97367", SourceforgeTracker.groupIdRegex.getGroup("group_id"));
		
		SourceforgeTracker.groupIdRegex.find(url2);
		assertEquals(new Integer(1), SourceforgeTracker.groupIdRegex.getGroupCount());
		assertEquals("97367", SourceforgeTracker.groupIdRegex.getGroup("group_id"));
	}
	
	@Test
	public void testIssueParser() {
		SourceforgeTracker tracker = new SourceforgeTracker();
		String url = SourceforgeTrackerTest.class.getResource(
				FileUtils.fileSeparator + "sourceforge_issue_1887104.html")
				.toString();
		url = url.substring(0, url.lastIndexOf("sourceforge_issue_1887104.html"));
		String pattern = "sourceforge_issue_" + Tracker.bugIdPlaceholder + ".html";
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 1887104l, 1887104l, null);
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(1887104l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(null, report.getAssignedTo());
			assertEquals("None", report.getCategory());
			
			assertEquals(6, report.getComments().size());
			Iterator<Comment> iterator = report.getComments().iterator();
			Comment c1 = iterator.next();
			assertEquals(2658599, c1.getId());
			Person daliboz = c1.getAuthor();
			assertTrue(daliboz != null);
			assertEquals(report,c1.getBugReport());
			assertTrue(c1.getMessage().startsWith("bumping up priority."));
			DateTime dt = DateTimeUtils.parseDate("2008-02-05 16:52:57 UTC");
			assertTrue(dt.isEqual(c1.getTimestamp()));
			
			Comment c2 = iterator.next();
			assertEquals(2658950, c2.getId());
			Person scolebourne = c2.getAuthor();
			assertTrue(scolebourne != null);
			assertEquals(report, c2.getBugReport());
			assertTrue(c2.getMessage().startsWith("Are you using v1.5.2?"));
			dt = DateTimeUtils.parseDate("2008-02-05 22:37:45 UTC");
			assertTrue(dt.isEqual(c2.getTimestamp()));
			
			Comment c3 = iterator.next();
			assertEquals(2659081, c3.getId());
			assertEquals(daliboz, c3.getAuthor());
			assertEquals(report, c3.getBugReport());
			assertTrue(c3.getMessage().startsWith("I've tried this on 1.5,"));
			dt = DateTimeUtils.parseDate("2008-02-06 00:04:30 UTC");
			assertTrue(dt.isEqual(c3.getTimestamp()));
			
			Comment c4 = iterator.next();
			assertEquals(2661926, c4.getId());
			assertEquals(scolebourne, c4.getAuthor());
			assertEquals(report, c4.getBugReport());
			assertTrue(c4.getMessage().startsWith("Fixed in svn rv 1323."));
			dt = DateTimeUtils.parseDate("2008-02-08 00:13:32 UTC");
			assertTrue(dt.isEqual(c4.getTimestamp()));
			
			Comment c5 = iterator.next();
			assertEquals(2670573, c5.getId());
			assertEquals(daliboz, c5.getAuthor());
			assertEquals(report, c5.getBugReport());
			assertTrue(c5.getMessage().startsWith("Can confirm that this is passing our unit tests"));
			dt = DateTimeUtils.parseDate("2008-02-11 19:26:23 UTC");
			assertTrue(dt.isEqual(c5.getTimestamp()));
			
			Comment c6 = iterator.next();
			assertEquals(2670632, c6.getId());
			assertEquals(daliboz, c6.getAuthor());
			assertEquals(report, c6.getBugReport());
			assertTrue(c6.getMessage().startsWith("Just noticed a difference for the Spring adjustment - though"));
			DateTime c6Dt = DateTimeUtils.parseDate("2008-02-11 20:13:00 UTC");
			assertTrue(c6Dt.isEqual(c6.getTimestamp()));
			
			assertEquals("None", report.getComponent());
			dt = DateTimeUtils.parseDate("2008-02-05 16:24:58 UTC");
			assertTrue(dt.isEqual(report.getCreationTimestamp()));
			
			assertTrue(report.getDescription().startsWith(
			"On versions 1.5+, using roundFloorCopy on one of the ambiguous times "));
			assertEquals(null, report.getExpectedBehavior());
			assertEquals(1887104, report.getId());
			assertTrue(c6Dt.isEqual(report.getLastUpdateTimestamp()));
			assertEquals(null, report.getObservedBehavior());
			assertEquals(Priority.VERY_HIGH, report.getPriority());
			assertEquals(null, report.getProduct());
			assertEquals(Resolution.UNRESOLVED, report.getResolution());
			assertEquals(null, report.getResolutionTimestamp());
			assertEquals(null, report.getResolver());
			assertEquals(null, report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status.CLOSED, report.getStatus());
			assertEquals(null, report.getStepsToReproduce());
			assertEquals("joda-time 1.5+ issues with roundFloor and DST", report.getSubject());
			assertEquals(daliboz, report.getSubmitter());
			assertEquals(null, report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals(null, report.getVersion());
			
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
		}
	}
	
	@Test
	public void testLiveOverview() {
		String liveUrl = "http://sourceforge.net/tracker/?group_id=97367&atid=617889";
		
		SourceforgeTracker tracker = new SourceforgeTracker();
		try {
			Set<Long> ids = tracker.getIdsFromHTTPUri(new URI(liveUrl));
			assertTrue(ids.size() >= 111);
			
			assertTrue(ids.contains(3192457l));
			assertTrue(ids.contains(3175612l));
			assertTrue(ids.contains(3175561l));
			assertTrue(ids.contains(3175068l));
			assertTrue(ids.contains(3161586l));
			assertTrue(ids.contains(3150326l));
			assertTrue(ids.contains(3117838l));
			assertTrue(ids.contains(3117678l));
			assertTrue(ids.contains(3105865l));
			assertTrue(ids.contains(3102760l));
			assertTrue(ids.contains(3100939l));
			assertTrue(ids.contains(3097754l));
			assertTrue(ids.contains(3090209l));
			assertTrue(ids.contains(3073905l));
			assertTrue(ids.contains(3072758l));
			assertTrue(ids.contains(3056104l));
			assertTrue(ids.contains(3048747l));
			assertTrue(ids.contains(3048468l));
			assertTrue(ids.contains(2990841l));
			assertTrue(ids.contains(2986043l));
			assertTrue(ids.contains(2984388l));
			assertTrue(ids.contains(2952991l));
			assertTrue(ids.contains(2943836l));
			assertTrue(ids.contains(2935625l));
			assertTrue(ids.contains(2927403l));
			assertTrue(ids.contains(2908360l));
			assertTrue(ids.contains(2903029l));
			assertTrue(ids.contains(2891940l));
			assertTrue(ids.contains(2889499l));
			assertTrue(ids.contains(2888113l));
			assertTrue(ids.contains(2880475l));
			assertTrue(ids.contains(2877833l));
			assertTrue(ids.contains(2865266l));
			assertTrue(ids.contains(2862109l));
			assertTrue(ids.contains(2848058l));
			assertTrue(ids.contains(2842202l));
			assertTrue(ids.contains(2825955l));
			assertTrue(ids.contains(2820871l));
			assertTrue(ids.contains(2813207l));
			assertTrue(ids.contains(2804258l));
			assertTrue(ids.contains(2788283l));
			assertTrue(ids.contains(2783325l));
			assertTrue(ids.contains(2726972l));
			assertTrue(ids.contains(2723212l));
			assertTrue(ids.contains(2721880l));
			assertTrue(ids.contains(2690370l));
			assertTrue(ids.contains(2553453l));
			assertTrue(ids.contains(2495455l));
			assertTrue(ids.contains(2487417l));
			assertTrue(ids.contains(2476980l));
			assertTrue(ids.contains(2461322l));
			assertTrue(ids.contains(2182444l));
			assertTrue(ids.contains(2111763l));
			assertTrue(ids.contains(2105134l));
			assertTrue(ids.contains(2038742l));
			assertTrue(ids.contains(2030810l));
			assertTrue(ids.contains(2025928l));
			assertTrue(ids.contains(2018795l));
			assertTrue(ids.contains(2015343l));
			assertTrue(ids.contains(2012274l));
			assertTrue(ids.contains(1945908l));
			assertTrue(ids.contains(1944307l));
			assertTrue(ids.contains(1929964l));
			assertTrue(ids.contains(1915752l));
			assertTrue(ids.contains(1887104l));
			assertTrue(ids.contains(1878425l));
			assertTrue(ids.contains(1878274l));
			assertTrue(ids.contains(1877843l));
			assertTrue(ids.contains(1855430l));
			assertTrue(ids.contains(1850115l));
			assertTrue(ids.contains(1849969l));
			assertTrue(ids.contains(1841568l));
			assertTrue(ids.contains(1839440l));
			assertTrue(ids.contains(1827409l));
			assertTrue(ids.contains(1826894l));
			assertTrue(ids.contains(1788383l));
			assertTrue(ids.contains(1788282l));
			assertTrue(ids.contains(1775054l));
			assertTrue(ids.contains(1755161l));
			assertTrue(ids.contains(1755158l));
			assertTrue(ids.contains(1747219l));
			assertTrue(ids.contains(1733614l));
			assertTrue(ids.contains(1716305l));
			assertTrue(ids.contains(1710316l));
			assertTrue(ids.contains(1707532l));
			assertTrue(ids.contains(1700269l));
			assertTrue(ids.contains(1699760l));
			assertTrue(ids.contains(1682152l));
			assertTrue(ids.contains(1682046l));
			assertTrue(ids.contains(1665180l));
			assertTrue(ids.contains(1660490l));
			assertTrue(ids.contains(1620722l));
			assertTrue(ids.contains(1592342l));
			assertTrue(ids.contains(1576727l));
			assertTrue(ids.contains(1552358l));
			assertTrue(ids.contains(1530525l));
			assertTrue(ids.contains(1517962l));
			assertTrue(ids.contains(1490975l));
			assertTrue(ids.contains(1449760l));
			assertTrue(ids.contains(1449459l));
			assertTrue(ids.contains(1437757l));
			assertTrue(ids.contains(1375249l));
			assertTrue(ids.contains(1347976l));
			assertTrue(ids.contains(1277337l));
			assertTrue(ids.contains(1195817l));
			assertTrue(ids.contains(1184542l));
			assertTrue(ids.contains(1081969l));
			assertTrue(ids.contains(1081957l));
			assertTrue(ids.contains(954058l));
			assertTrue(ids.contains(949267l));
			assertTrue(ids.contains(943071l));
			
			
		} catch (SAXException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

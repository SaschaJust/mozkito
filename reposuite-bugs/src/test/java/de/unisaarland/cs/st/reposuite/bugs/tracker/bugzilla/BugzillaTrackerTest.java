package de.unisaarland.cs.st.reposuite.bugs.tracker.bugzilla;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.reposuite.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.reposuite.bugs.tracker.RawReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.Tracker;
import de.unisaarland.cs.st.reposuite.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Priority;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Report;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Resolution;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Severity;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Type;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.rcs.model.PersonManager;
import de.unisaarland.cs.st.reposuite.utils.DateTimeUtils;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.IOUtils;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;


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
	
	@Test
	public void testDateParse(){
		String date = "2005-11-01 11:43:19 EST";
		DateTime dateTime = DateTimeUtils.parseDate(date);
		assertTrue(dateTime != null);
	}
	
	@Test
	public void testParse(){
		BugzillaTracker tracker = new BugzillaTracker();
		String url = BugzillaTrackerTest.class.getResource(FileUtils.fileSeparator + "bugzilla_114562.xml").toString();
		url = url.substring(0, url.lastIndexOf("bugzilla_114562.xml"));
		String pattern = "bugzilla_" + Tracker.bugIdPlaceholder + ".xml";
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 114562l, 114562l, null);
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(114562l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(114562, report.getId());
			assertEquals("mik.kersten", report.getAssignedTo().getUsername());
			assertEquals("Mik Kersten", report.getAssignedTo().getFullname());
			assertEquals("Tools", report.getCategory());
			SortedSet<Comment> comments = report.getComments();
			assertEquals(2, comments.size());
			assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
			
			assertEquals("mik.kersten", comments.first().getAuthor().getUsername());
			assertEquals("Mik Kersten", comments.first().getAuthor().getFullname());
			assertEquals(null, comments.first().getAuthor().getEmail());
			assertTrue(DateTimeUtils.parseDate("2005-11-01 11:52:13 EST").isEqual(
					comments.first().getTimestamp()));
			assertEquals("Test site is here: https://node1.eclipse.org/bugstest/\n\nRelated to eclipse.org bug 113042",
					comments.first().getMessage());
			
			assertEquals("mik.kersten", comments.last().getAuthor().getUsername());
			assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
			assertEquals(null, comments.last().getAuthor().getEmail());
			assertEquals("Mik Kersten", comments.last().getAuthor().getFullname());
			assertTrue(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST")
					.isEqual(
							comments.last().getTimestamp()));
			assertEquals(
					"Core support works now (adding existing reports, creating new reports).  Still some issue\nwith search (bug 115017), and rest will be broken out into seperate reports.",
					comments.last().getMessage());
			
			assertEquals("Bugzilla", report.getComponent());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:42 EST"),
					report.getCreationTimestamp());
			assertEquals("eclipse.org is moving to it", report.getDescription());
			assertEquals(null, report.getExpectedBehavior());
			
			SortedSet<HistoryElement> history = report.getHistory();
			assertEquals(0, history.size());
			
			assertEquals(rawReport.getFetchTime(), report.getLastFetch());
			assertTrue(BugzillaXMLParser.dateTimeFormat.parseDateTime("2005-11-03 23:17:37 -0500").isEqual(
					report.getLastUpdateTimestamp()));
			assertEquals(null, report.getObservedBehavior());
			assertEquals(Priority.VERY_HIGH, report.getPriority());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			
			assertEquals(Severity.ENHANCEMENT, report.getSeverity());
			assertEquals(2, report.getSiblings().size());
			assertEquals(true, report.getSiblings().contains(113042l));
			assertEquals(true, report.getSiblings().contains(115017l));
			
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals(null, report.getStepsToReproduce());
			assertEquals("add basic support for Bugzilla 2.20", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals("mik.kersten", report.getSubmitter().getUsername());
			assertEquals("Mik Kersten", report.getSubmitter().getFullname());
			assertEquals(null, report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals("unspecified", report.getVersion());
			
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
	public void testParseHistroy() {
		URL historyURL = BugzillaTracker.class.getResource(FileUtils.fileSeparator + "bugzilla_114562_history.html");
		Report report = new Report();
		try {
			BugzillaXMLParser.handleHistory(historyURL.toURI(), report, new PersonManager());
			SortedSet<HistoryElement> history = report.getHistory();
			assertEquals(3, history.size());
			Iterator<HistoryElement> hElemIter = history.iterator();
			HistoryElement hElem = hElemIter.next();
			assertEquals(1, hElem.getChangedValues().size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsername());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:43:19 EST"), hElem.getTimestamp());
			assertTrue(hElem.getChangedValues().containsKey(Report.class.getDeclaredField("priority")));
			assertEquals(BugzillaXMLParser.getPriority("P3"),
					hElem.getChangedValues().get(Report.class.getDeclaredField("priority")).getFirst());
			assertEquals(BugzillaXMLParser.getPriority("P1"),
					hElem.getChangedValues().get(Report.class.getDeclaredField("priority")).getSecond());
			
			hElem = hElemIter.next();
			assertEquals(1, hElem.getChangedValues().size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsername());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:52:13 EST"), hElem.getTimestamp());
			assertTrue(hElem.getChangedValues().containsKey(Report.class.getDeclaredField("summary")));
			assertEquals("add support for Bugzilla 2.20",
					hElem.getChangedValues().get(Report.class.getDeclaredField("summary")).getFirst());
			assertEquals("add support for Bugzilla 2 20",
					hElem.getChangedValues().get(Report.class.getDeclaredField("summary")).getSecond());
			
			hElem = hElemIter.next();
			assertEquals(3, hElem.getChangedValues().size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsername());
			assertEquals(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST"), hElem.getTimestamp());
			assertTrue(hElem.getChangedValues().containsKey(Report.class.getDeclaredField("status")));
			assertTrue(hElem.getChangedValues().containsKey(Report.class.getDeclaredField("resolution")));
			assertTrue(hElem.getChangedValues().containsKey(Report.class.getDeclaredField("summary")));
			assertEquals(BugzillaXMLParser.getStatus("NEW"),
					hElem.getChangedValues().get(Report.class.getDeclaredField("status")).getFirst());
			assertEquals(BugzillaXMLParser.getStatus("RESOLVED"),
					hElem.getChangedValues().get(Report.class.getDeclaredField("status")).getSecond());
			assertEquals(BugzillaXMLParser.getResolution(""),
					hElem.getChangedValues().get(Report.class.getDeclaredField("resolution")).getFirst());
			assertEquals(BugzillaXMLParser.getResolution("FIXED"),
					hElem.getChangedValues().get(Report.class.getDeclaredField("resolution")).getSecond());
			assertEquals("add support for Bugzilla 2 20",
					hElem.getChangedValues().get(Report.class.getDeclaredField("summary")).getFirst());
			assertEquals("add basic support for Bugzilla 2.20",
					hElem.getChangedValues().get(Report.class.getDeclaredField("summary")).getSecond());
			
			assertEquals("mik.kersten", report.getResolver().getUsername());
			assertEquals(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST"), report.getResolutionTimestamp());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testSiblingDetectionRegex(){
		String message = "Test site is here: https://node1.eclipse.org/bugstest/\n\nRelated to eclipse.org bug 113042";
		List<RegexGroup> find = BugzillaXMLParser.siblingRegex.find(message);
		assertTrue(BugzillaXMLParser.siblingRegex.matched());
		assertEquals("113042", find.get(1).getMatch());
	}
	
}

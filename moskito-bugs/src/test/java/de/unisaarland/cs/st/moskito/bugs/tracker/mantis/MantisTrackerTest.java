package de.unisaarland.cs.st.moskito.bugs.tracker.mantis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.History;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.persistence.model.PersonTuple;

public class MantisTrackerTest {
	
	private RawReport report19810;
	private RawReport report18828;
	private RawReport report8468;
	private RawReport report107;
	
	@Before
	public void setUp() throws Exception {
		
		this.report19810 = new RawReport(1l, IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator
		                                                                                  + "open-bravo-19810.html")
		                                                             .toURI()));
		this.report18828 = new RawReport(1l, IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator
		                                                                                  + "open-bravo-18828.html")
		                                                             .toURI()));
		this.report8468 = new RawReport(1l, IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator
		                                                                                 + "open-bravo-8468.html")
		                                                            .toURI()));
		this.report107 = new RawReport(107, IOUtils.fetch(getClass().getResource(FileUtils.fileSeparator
		                                                                                 + "open-bravo-107.html")
		                                                            .toURI()));
	}
	
	@Test
	public void testAttachments18828() {
		
		final MantisTracker tracker = new MantisTracker();
		String url = this.report18828.getUri().toASCIIString();
		url = url.substring(0, url.lastIndexOf("open-bravo-18828.html"));
		final String pattern = "open-bravo-" + Tracker.getBugidplaceholder() + ".html";
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 18828l, 18828l, null);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(18828l));
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		final XmlReport xmlReport = tracker.createDocument(rawReport);
		final Report report = tracker.parse(xmlReport);
		final List<AttachmentEntry> attachments = report.getAttachmentEntries();
		assertTrue(attachments.isEmpty());
	}
	
	@Test
	public void testAttachments19810() {
		
		final MantisTracker tracker = new MantisTracker();
		String url = this.report19810.getUri().toASCIIString();
		url = url.substring(0, url.lastIndexOf("open-bravo-19810.html"));
		final String pattern = "open-bravo-" + Tracker.getBugidplaceholder() + ".html";
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 19810l, 19810l, null);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(19810l));
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		final XmlReport xmlReport = tracker.createDocument(rawReport);
		final Report report = tracker.parse(xmlReport);
		final List<AttachmentEntry> attachments = report.getAttachmentEntries();
		
		final String reportLink = xmlReport.getUri().toASCIIString();
		final int index = reportLink.lastIndexOf("/");
		
		assertEquals(3, attachments.size());
		assertEquals("5008", attachments.get(0).getId());
		assertTrue(attachments.get(0) != null);
		assertTrue(attachments.get(0).getAuthor() != null);
		assertTrue(attachments.get(0).getAuthor().getUsernames().contains("alostale"));
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:39"), attachments.get(0).getTimestamp());
		assertEquals(null, attachments.get(0).getDescription());
		assertEquals("Selection_031.png", attachments.get(0).getFilename());
		assertEquals(reportLink.substring(0, index + 1) + "file_download.php?file_id=5008&type=bug",
		             attachments.get(0).getLink().toString());
		assertEquals(37363, attachments.get(0).getSize());
		
		assertEquals("5009", attachments.get(1).getId());
		assertTrue(attachments.get(1).getAuthor().getUsernames().contains("alostale"));
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:40"), attachments.get(1).getTimestamp());
		assertEquals(null, attachments.get(1).getDescription());
		assertEquals("Selection_032.png", attachments.get(1).getFilename());
		assertEquals(reportLink.substring(0, index + 1) + "file_download.php?file_id=5009&type=bug",
		             attachments.get(1).getLink().toString());
		assertEquals(150567, attachments.get(1).getSize());
		
		assertEquals("5010", attachments.get(2).getId());
		assertTrue(attachments.get(2).getAuthor().getUsernames().contains("alostale"));
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:40"), attachments.get(2).getTimestamp());
		assertEquals(null, attachments.get(2).getDescription());
		assertEquals("test.html", attachments.get(2).getFilename());
		assertEquals(reportLink.substring(0, index + 1) + "file_download.php?file_id=5010&type=bug",
		             attachments.get(2).getLink().toString());
		assertEquals(1073, attachments.get(2).getSize());
		
	}
	
	@Test
	public void testCheckRAW() {
		final MantisTracker tracker = new MantisTracker();
		assertTrue(tracker.checkRAW(this.report19810));
		assertTrue(tracker.checkRAW(this.report18828));
		assertFalse(tracker.checkRAW(this.report107));
	}
	
	@Test
	public void testCheckXML() {
		final MantisTracker tracker = new MantisTracker();
		final XmlReport xmlReport19810 = tracker.createDocument(this.report19810);
		assertTrue(tracker.checkXML(xmlReport19810));
	}
	
	@Test
	public void testCreateDocument() {
		final MantisTracker tracker = new MantisTracker();
		
		final XmlReport xmlReport19810 = tracker.createDocument(this.report19810);
		final XmlReport xmlReport18828 = tracker.createDocument(this.report18828);
		
		assertTrue(xmlReport19810 != null);
		assertTrue(xmlReport18828 != null);
	}
	
	@Test
	public void testKeywords() {
		final MantisTracker tracker = new MantisTracker();
		String url = this.report8468.getUri().toASCIIString();
		url = url.substring(0, url.lastIndexOf("open-bravo-8468.html"));
		final String pattern = "open-bravo-" + Tracker.getBugidplaceholder() + ".html";
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 8468l, 8468l, null);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(8468l));
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		final XmlReport xmlReport = tracker.createDocument(rawReport);
		final Report report = tracker.parse(xmlReport);
		final Set<String> keywords = report.getKeywords();
		assertEquals(2, keywords.size());
		assertTrue(keywords.contains("main"));
		assertTrue(keywords.contains("tictech"));
	}
	
	@Test
	public void testParse() {
		
		final MantisTracker tracker = new MantisTracker();
		
		String url = this.report19810.getUri().toASCIIString();
		url = url.substring(0, url.lastIndexOf("open-bravo-19810.html"));
		final String pattern = "open-bravo-" + Tracker.getBugidplaceholder() + ".html";
		
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 19810l, 19810l, null);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(19810l));
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		final XmlReport xmlReport = tracker.createDocument(rawReport);
		final Report report = tracker.parse(xmlReport);
		
		assertEquals(19810, report.getId());
		assertEquals("alostale", report.getAssignedTo().getUsernames().iterator().next());
		
		assertEquals("[Openbravo ERP] A. Platform", report.getCategory());
		
		final SortedSet<Comment> comments = report.getComments();
		assertEquals(2, comments.size());
		assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
		
		assertEquals(45258, comments.first().getId());
		assertEquals("hgbot", comments.first().getAuthor().getUsernames().iterator().next());
		assertTrue(comments.first().getAuthor().getFullnames().isEmpty());
		assertTrue(comments.first().getAuthor().getEmailAddresses().isEmpty());
		final DateTime dt = DateTimeUtils.parseDate("2012-02-20 10:42");
		assertTrue(dt.isEqual(comments.first().getTimestamp()));
		assertTrue(comments.first().getMessage().startsWith("Repository: erp/devel/pi"));
		
		assertEquals(45259, comments.last().getId());
		assertEquals("alostale", comments.last().getAuthor().getUsernames().iterator().next());
		assertTrue(comments.last().getAuthor().getFullnames().isEmpty());
		assertTrue(comments.last().getAuthor().getEmailAddresses().isEmpty());
		
		assertTrue(DateTimeUtils.parseDate("2012-02-20 10:44").isEqual(comments.last().getTimestamp()));
		assertTrue(comments.last().getMessage().startsWith("The fix for this issue makes"));
		assertTrue(comments.last().getMessage().endsWith("instances within memory."));
		
		assertEquals("Core", report.getComponent());
		assertEquals(DateTimeUtils.parseDate("2012-02-20 10:39"), report.getCreationTimestamp());
		assertTrue(report.getDescription().startsWith("Whenever a window in"));
		assertTrue(report.getDescription().endsWith("dump suffering this issue."));
		
		final History history = report.getHistory();
		assertFalse(history.isEmpty());
		assertEquals(5, history.size());
		
		final Iterator<HistoryElement> iterator = history.iterator();
		HistoryElement hElement = iterator.next();
		assertTrue(hElement != null);
		final Map<String, PersonTuple> changedPersonValues = hElement.getChangedPersonValues();
		assertEquals(1, changedPersonValues.size());
		assertTrue(changedPersonValues.get("assignedto") != null);
		assertTrue(changedPersonValues.get("assignedto").getNewValue().getPersons().iterator().next().getUsernames()
		                              .contains("alostale"));
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertFalse(hElement.getChangedStringValues().isEmpty());
		assertEquals("", hElement.getChangedStringValues().get("component").getOldValue());
		assertEquals("Core", hElement.getChangedStringValues().get("component").getNewValue());
		assertTrue(hElement.getChangedEnumValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().isEmpty());
		assertTrue(hElement.getChangedEnumValues().isEmpty());
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertFalse(hElement.getChangedStringValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().containsKey("scmfixversion"));
		assertEquals("", hElement.getChangedStringValues().get("scmfixversion").getOldValue());
		assertEquals("http://code.openbravo.com/erp/devel/pi/rev/c3105cd9c9c4ebd834baceababedddf88de4fdb2",
		             hElement.getChangedStringValues().get("scmfixversion").getNewValue());
		
		assertFalse(hElement.getChangedEnumValues().isEmpty());
		assertEquals(2, hElement.getChangedEnumValues().size());
		assertTrue(hElement.getChangedEnumValues().containsKey("status"));
		assertTrue(hElement.getChangedEnumValues().containsKey("resolution"));
		assertEquals(Status.NEW, hElement.getChangedEnumValues().get("status").getOldValue());
		assertEquals(Status.CLOSED, hElement.getChangedEnumValues().get("status").getNewValue());
		
		assertEquals(Resolution.UNRESOLVED, hElement.getChangedEnumValues().get("resolution").getOldValue());
		assertEquals(Resolution.RESOLVED, hElement.getChangedEnumValues().get("resolution").getNewValue());
		
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().isEmpty());
		assertTrue(hElement.getChangedEnumValues().isEmpty());
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		hElement = iterator.next();
		assertTrue(hElement.getChangedDateValues().isEmpty());
		assertTrue(hElement.getChangedStringValues().isEmpty());
		assertFalse(hElement.getChangedEnumValues().isEmpty());
		assertEquals(1, hElement.getChangedEnumValues().size());
		assertTrue(hElement.getChangedEnumValues().containsKey("status"));
		assertEquals(Status.CLOSED, hElement.getChangedEnumValues().get("status").getOldValue());
		assertEquals(Status.CLOSED, hElement.getChangedEnumValues().get("status").getNewValue());
		assertTrue(hElement.getChangedPersonValues().isEmpty());
		
		assertEquals(rawReport.getFetchTime(), report.getLastFetch());
		assertTrue(DateTimeUtils.parseDate("2012-02-21 17:24").isEqual(report.getLastUpdateTimestamp()));
		
		assertEquals(Priority.VERY_HIGH, report.getPriority());
		assertEquals(Resolution.RESOLVED, report.getResolution());
		assertEquals(Severity.MAJOR, report.getSeverity());
		//
		assertEquals(0, report.getSiblings().size());
		
		assertEquals(Status.CLOSED, report.getStatus());
		assertEquals("0019810: ViewComponent memory leak", report.getSubject());
		assertTrue(report.getSubmitter() != null);
		assertEquals("alostale", report.getSubmitter().getUsernames().iterator().next());
		assertTrue(report.getSubmitter().getFullnames().isEmpty());
		assertEquals("0019810: ViewComponent memory leak", report.getSummary());
		assertEquals(Type.BUG, report.getType());
		assertEquals("", report.getVersion());
		assertTrue(report.getKeywords().isEmpty());
	}
	
	@Test
	public void testSiblings() {
		final MantisTracker tracker = new MantisTracker();
		String url = this.report18828.getUri().toASCIIString();
		url = url.substring(0, url.lastIndexOf("open-bravo-18828.html"));
		final String pattern = "open-bravo-" + Tracker.getBugidplaceholder() + ".html";
		
		try {
			tracker.setup(new URI(url), null, pattern, null, null, 18828l, 18828l, null);
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(18828l));
		} catch (final FetchException e) {
			e.printStackTrace();
			fail();
		} catch (final UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		final XmlReport xmlReport = tracker.createDocument(rawReport);
		final Report report = tracker.parse(xmlReport);
		final SortedSet<Long> siblings = report.getSiblings();
		assertEquals(2, siblings.size());
		assertTrue(siblings.contains(19022l));
		assertTrue(siblings.contains(18893l));
	}
	
}
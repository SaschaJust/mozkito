/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.ProxyConfig;
import net.ownhero.dev.ioda.container.RawContent;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
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
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Class BugzillaTracker_4_0_4_Test.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class BugzillaTracker_4_0_4_Test {
	
	/** The uri114562. */
	private URI             uri114562;
	
	/** The uri642368. */
	private URI             uri642368;
	
	/** The uri153429. */
	private URI             uri153429;
	
	/** The uri1234. */
	private URI             uri1234;
	
	/** The uri642368history. */
	private URI             uri642368history;
	
	/** The fetch uri. */
	private URI             fetchURI;
	
	/** The tracker. */
	private BugzillaTracker tracker;
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		
		this.uri1234 = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator + "eclipse_1234.xml")
		                                               .toURI();
		
		this.uri114562 = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator + "bugzilla_114562.xml")
		                                                 .toURI();
		
		this.uri153429 = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator + "bugzilla_153429.xml")
		                                                 .toURI();
		this.uri642368 = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator + "bugzilla_642368.xml")
		                                                 .toURI();
		this.uri642368history = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator
		                                                                             + "bugzilla_mozilla_642368_history.html")
		                                                        .toURI();
		this.fetchURI = new URI("https://bugs.eclipse.org/bugs/");
		
		this.tracker = new BugzillaTracker();
		try {
			// TODO temporary enabling http proxy
			// this.tracker.setup(this.fetchURI, null, null, this.fetchURI, "4.0.4", null);
			this.tracker.setup(this.fetchURI, null, null, this.fetchURI, "4.0.4", new ProxyConfig("localhost", 3128));
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Tear down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test4_0_5 plus.
	 */
	@Test
	public void test4_0_5PLUS() {
		
		final ReportLink reportLink = new ReportLink(this.uri642368, "642368");
		
		final Report report = this.tracker.parse(reportLink);
		
		assertEquals("642368", report.getId());
		assertEquals(null, report.getAssignedTo());
		assertEquals("Components", report.getCategory());
		assertEquals("Rhino", report.getProduct());
		
		final SortedSet<Comment> comments = report.getComments();
		assertEquals(0, comments.size());
		
		assertEquals("Core", report.getComponent());
		assertEquals(DateTimeUtils.parseDate("2011-03-16 21:04 PDT"), report.getCreationTimestamp());
		assertTrue(report.getDescription().startsWith("User-Agent:"));
		assertTrue(report.getDescription().endsWith("ReferenceError: \"adblock\" is not defined."));
		
		final History history = report.getHistory();
		assertTrue(history.isEmpty());
		
		assertEquals(null, report.getLastUpdateTimestamp());
		
		assertEquals(Priority.UNKNOWN, report.getPriority());
		assertEquals(Resolution.UNRESOLVED, report.getResolution());
		assertEquals(Severity.CRITICAL, report.getSeverity());
		
		assertEquals(0, report.getSiblings().size());
		
		assertEquals(Status.UNCONFIRMED, report.getStatus());
		assertEquals("Envjs.connection Can't find method java.io.FilterInputStream.read([C,number,number) (env.rhino.js#1631)",
		             report.getSubject());
		assertTrue(report.getSubmitter() != null);
		assertEquals("rhino", report.getSubmitter().getUsernames().iterator().next());
		assertEquals("rhino", report.getSubmitter().getFullnames().iterator().next());
		assertEquals(null, report.getSummary());
		assertEquals(Type.BUG, report.getType());
		assertEquals("1.7R1", report.getVersion());
		assertTrue(report.getKeywords().isEmpty());
		
	}
	
	/**
	 * Test attachments.
	 */
	@Test
	public void testAttachments() {
		
		final ReportLink reportLink = new ReportLink(this.uri153429, "153429");
		final Report report = this.tracker.parse(reportLink);
		final List<AttachmentEntry> attachments = report.getAttachmentEntries();
		assertEquals(11, attachments.size());
		assertEquals("80909", attachments.get(0).getId());
		assertTrue(attachments.get(0).getAuthor().getUsernames().contains("Allan_Godding"));
		assertEquals(DateTimeUtils.parseDate("2007-10-22 17:04:30 -0400"), attachments.get(0).getDeltaTS());
		assertEquals("Patch for Eclipse Testing Framework", attachments.get(0).getDescription());
		assertEquals("bug153429_patch", attachments.get(0).getFilename());
		assertEquals(this.fetchURI.toASCIIString() + "attachment.cgi?id=80909", attachments.get(0).getLink().toString());
		assertEquals("text/plain", attachments.get(0).getMime());
		assertEquals(13568, attachments.get(0).getSize());
		assertEquals(DateTimeUtils.parseDate("2007-10-22 17:04:00 -0400"), attachments.get(0).getTimestamp());
		assertEquals("81997", attachments.get(1).getId());
		assertTrue(attachments.get(1).getAuthor().getUsernames().contains("Allan_Godding"));
		assertEquals(DateTimeUtils.parseDate("2007-11-08 12:27:18 -0500"), attachments.get(1).getDeltaTS());
		assertEquals("code for Eclipse Test Framework", attachments.get(1).getDescription());
		assertEquals("junit.zip", attachments.get(1).getFilename());
		assertEquals(this.fetchURI.toASCIIString() + "attachment.cgi?id=81997", attachments.get(1).getLink().toString());
		assertEquals("application/zip", attachments.get(1).getMime());
		assertEquals(195784, attachments.get(1).getSize());
		assertEquals(DateTimeUtils.parseDate("2007-11-02 16:03:00 -0400"), attachments.get(1).getTimestamp());
		assertEquals("82463", attachments.get(2).getId());
		assertEquals("124184", attachments.get(3).getId());
		assertEquals("124239", attachments.get(4).getId());
		assertEquals("125173", attachments.get(5).getId());
		assertEquals("151364", attachments.get(6).getId());
		assertEquals("152344", attachments.get(7).getId());
		assertEquals("152352", attachments.get(8).getId());
		assertEquals("153160", attachments.get(9).getId());
		assertEquals("153208", attachments.get(10).getId());
		
	}
	
	/**
	 * Test check raw.
	 */
	@Test
	public void testCheckRAW() {
		final BugzillaParser bugzillaParser = new BugzillaParser_4_0_4();
		
		try {
			RawContent rawContent = IOUtils.fetch(this.uri1234);
			assertFalse(bugzillaParser.checkRAW(rawContent.getContent()));
			rawContent = IOUtils.fetch(this.uri114562);
			assertTrue(bugzillaParser.checkRAW(rawContent.getContent()));
		} catch (final Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Test check xml.
	 */
	@Test
	public void testCheckXML() {
		try {
			final BugzillaParser bugzillaParser = new BugzillaParser_4_0_4();
			final RawContent rawContent = IOUtils.fetch(this.uri114562);
			final XmlReport xmlReport = bugzillaParser.createDocument(rawContent);
			assertTrue(bugzillaParser.checkXML(xmlReport));
		} catch (final Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Test date parse.
	 */
	@Test
	public void testDateParse() {
		final String date = "2005-11-01 11:43:19 EST";
		final DateTime dateTime = DateTimeUtils.parseDate(date);
		assertTrue(dateTime != null);
	}
	
	/**
	 * Test keywords.
	 */
	@Test
	public void testKeywords() {
		
		final ReportLink reportLink = new ReportLink(this.uri153429, "153429");
		final Report report = this.tracker.parse(reportLink);
		final Set<String> keywords = report.getKeywords();
		assertEquals(1, keywords.size());
		assertTrue(keywords.contains("plan"));
	}
	
	/**
	 * Test mozilla history.
	 */
	@Test
	public void testMozillaHistory() {
		
		try {
			// final URL historyURL = new URL("https://bugs.eclipse.org/bugs/show_activity.cgi?id=114562");
			
			final BugzillaHistoryParser_4_0_4 historyParser = new BugzillaHistoryParser_4_0_4(this.uri642368history,
			                                                                                  "642368");
			if (!historyParser.parse()) {
				fail();
			}
			final SortedSet<HistoryElement> historyElements = historyParser.getHistory();
			final History history = new History("642368");
			for (final HistoryElement hElem : historyElements) {
				history.add(hElem);
			}
			assertEquals(1, history.size());
			final Iterator<HistoryElement> hElemIter = history.iterator();
			final HistoryElement hElem = hElemIter.next();
			assertEquals(3, hElem.size());
			assertEquals("rhino", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2011-03-16 21:08:29 PDT "), hElem.getTimestamp());
			assertTrue(hElem.contains("component"));
			assertTrue(hElem.contains("version"));
			assertTrue(hElem.contains("product"));
			
			assertEquals("JavaScript Engine", hElem.get("component").getFirst());
			assertEquals("Core", hElem.get("component").getSecond());
			
			assertEquals("unspecified", hElem.get("version").getFirst());
			assertEquals("1.7R1", hElem.get("version").getSecond());
			
			assertEquals("Core", hElem.get("product").getFirst());
			assertEquals("Rhino", hElem.get("product").getSecond());
			
		} catch (final SecurityException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			fail();
		}
	}
	
	/**
	 * Test parse.
	 */
	@Test
	public void testParse() {
		
		final ReportLink reportLink = new ReportLink(this.uri114562, "114562");
		final Report report = this.tracker.parse(reportLink);
		assertEquals("114562", report.getId());
		assertEquals("mik.kersten", report.getAssignedTo().getUsernames().iterator().next());
		assertEquals("Mik Kersten", report.getAssignedTo().getFullnames().iterator().next());
		assertEquals("Mylyn", report.getCategory());
		
		final SortedSet<Comment> comments = report.getComments();
		assertEquals(2, comments.size());
		assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
		
		assertEquals(555035, comments.first().getId());
		assertEquals("mik.kersten", comments.first().getAuthor().getUsernames().iterator().next());
		assertEquals("Mik Kersten", comments.first().getAuthor().getFullnames().iterator().next());
		assertTrue(comments.first().getAuthor().getEmailAddresses().isEmpty());
		final DateTime dt = DateTimeUtils.parseDate("2005-11-01 11:52:13 EST");
		assertTrue(dt.isEqual(comments.first().getTimestamp()));
		assertEquals("Test site is here: https://node1.eclipse.org/bugstest/\n\nRelated to eclipse.org bug 113042",
		             comments.first().getMessage());
		
		assertEquals(557496, comments.last().getId());
		assertEquals("mik.kersten", comments.last().getAuthor().getUsernames().iterator().next());
		assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
		assertTrue(comments.last().getAuthor().getEmailAddresses().isEmpty());
		assertEquals("Mik Kersten", comments.last().getAuthor().getFullnames().iterator().next());
		assertTrue(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST").isEqual(comments.last().getTimestamp()));
		assertEquals("Core support works now (adding existing reports, creating new reports).  Still some issue\nwith search (bug 115017), and rest will be broken out into seperate reports.",
		             comments.last().getMessage());
		
		assertEquals("Bugzilla", report.getComponent());
		assertEquals(DateTimeUtils.parseDate("2005-11-01 11:42 EST"), report.getCreationTimestamp());
		assertEquals("eclipse.org is moving to it", report.getDescription());
		
		final History history = report.getHistory();
		assertTrue(history.isEmpty());
		
		assertTrue(DateTimeUtils.parseDate("2005-11-03 23:17:37 -0500").isEqual(report.getLastUpdateTimestamp()));
		
		assertEquals(Priority.VERY_HIGH, report.getPriority());
		assertEquals(Resolution.RESOLVED, report.getResolution());
		assertEquals(Severity.ENHANCEMENT, report.getSeverity());
		
		assertEquals(2, report.getSiblings().size());
		assertEquals(true, report.getSiblings().contains("113042"));
		assertEquals(true, report.getSiblings().contains("115017"));
		
		assertEquals(Status.CLOSED, report.getStatus());
		assertEquals("add basic support for Bugzilla 2.20", report.getSubject());
		assertTrue(report.getSubmitter() != null);
		assertEquals("mik.kersten", report.getSubmitter().getUsernames().iterator().next());
		assertEquals("Mik Kersten", report.getSubmitter().getFullnames().iterator().next());
		assertEquals(null, report.getSummary());
		assertEquals(Type.RFE, report.getType());
		assertEquals("unspecified", report.getVersion());
		for (final String keyword : report.getKeywords()) {
			System.err.println("`" + keyword + "`");
		}
		assertTrue(report.getKeywords().isEmpty());
		
	}
	
	/**
	 * Test parse history.
	 */
	@Test
	public void testParseHistory() {
		
		try {
			// final URL historyURL = new URL("https://bugs.eclipse.org/bugs/show_activity.cgi?id=114562");
			
			final String url = BugzillaTracker_4_0_4_Test.class.getResource(FileUtils.fileSeparator
			                                                                        + "bugzilla_114562_history.html")
			                                                   .toString();
			final BugzillaHistoryParser_4_0_4 historyParser = new BugzillaHistoryParser_4_0_4(new URI(url), "114562");
			if (!historyParser.parse()) {
				fail();
			}
			final SortedSet<HistoryElement> historyElements = historyParser.getHistory();
			final History history = new History("114562");
			for (final HistoryElement hElem : historyElements) {
				history.add(hElem);
			}
			final DateTime resolutionTimestamp = historyParser.getResolutionTimestamp();
			final Person resolver = historyParser.getResolver();
			
			assertEquals(3, history.size());
			final Iterator<HistoryElement> hElemIter = history.iterator();
			HistoryElement hElem = hElemIter.next();
			assertEquals(1, hElem.size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:43:19 EST"), hElem.getTimestamp());
			assertTrue(hElem.contains("priority"));
			assertEquals(Priority.NORMAL, history.getOldValue("priority", hElem));
			assertEquals(BugzillaParser.getPriority("P1"), hElem.get("priority").getSecond());
			hElem = hElemIter.next();
			assertEquals(1, hElem.size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:52:13 EST"), hElem.getTimestamp());
			assertTrue(hElem.contains("summary"));
			assertEquals("add support for Bugzilla 2.20", history.getOldValue("summary", hElem));
			assertEquals("add support for Bugzilla 2 20", hElem.get("summary").getSecond());
			hElem = hElemIter.next();
			assertEquals(3, hElem.size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST"), hElem.getTimestamp());
			assertTrue(hElem.contains("status"));
			assertTrue(hElem.contains("resolution"));
			assertTrue(hElem.contains("summary"));
			assertEquals(BugzillaParser.getStatus("NEW"), history.getOldValue("status", hElem));
			assertEquals(BugzillaParser.getStatus("RESOLVED"), hElem.get("status").getSecond());
			assertEquals(BugzillaParser.getResolution("---"), history.getOldValue("resolution", hElem));
			assertEquals(BugzillaParser.getResolution("FIXED"), hElem.get("resolution").getSecond());
			assertEquals("add support for Bugzilla 2 20", history.getOldValue("summary", hElem));
			assertEquals("add basic support for Bugzilla 2.20", hElem.get("summary").getSecond());
			assertEquals("mik.kersten", resolver.getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST"), resolutionTimestamp);
		} catch (final SecurityException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			fail();
		} catch (final URISyntaxException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			fail();
		}
	}
}

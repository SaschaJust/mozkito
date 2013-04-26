/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/
package org.mozkito.issues.tracker.bugzilla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.elements.Type;
import org.mozkito.issues.model.AttachmentEntry;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.XmlReport;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.utilities.datastructures.RawContent;
import org.mozkito.utilities.datetime.DateTimeUtils;
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.utilities.io.IOUtils;

/**
 * The Class BugzillaParser_4_0_4_Test.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class BugzillaParser_4_0_4_Test {
	
	/** The uri114562. */
	private URI                  uri114562;
	
	/** The uri642368. */
	private URI                  uri642368;
	
	/** The uri153429. */
	private URI                  uri153429;
	
	/** The uri1234. */
	private URI                  uri1234;
	
	/** The fetch uri. */
	private URI                  fetchURI;
	
	/** The parser. */
	private BugzillaParser_4_0_4 parser;
	
	private IssueTracker         issueTracker;
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.issueTracker = new IssueTracker();
		this.uri1234 = getClass().getResource(FileUtils.fileSeparator + "eclipse_1234.xml").toURI();
		
		this.uri114562 = getClass().getResource(FileUtils.fileSeparator + "bugzilla_114562.xml").toURI();
		
		this.uri153429 = getClass().getResource(FileUtils.fileSeparator + "bugzilla_153429.xml").toURI();
		this.uri642368 = getClass().getResource(FileUtils.fileSeparator + "bugzilla_642368.xml").toURI();
		this.fetchURI = new URI("https://issues.eclipse.org/issues/");
		this.parser = new BugzillaParser_4_0_4(new PersonFactory());
		final BugzillaTracker tracker = new BugzillaTracker(new IssueTracker(), this.parser.getPersonFactory());
		tracker.setUri(this.fetchURI);
		this.parser.setTracker(tracker);
	}
	
	/**
	 * Test4_0_5 plus.
	 */
	@Test
	public void test4_0_5PLUS() {
		
		final ReportLink reportLink = new ReportLink(this.uri642368, "642368");
		
		final Report report = this.parser.setContext(this.issueTracker, reportLink);
		assertNotNull(report);
		assertEquals("642368", this.parser.getId());
		assertEquals(null, this.parser.getAssignedTo());
		assertEquals("Components", this.parser.getCategory());
		assertEquals("Rhino", this.parser.getProduct());
		
		final SortedSet<Comment> comments = this.parser.getComments();
		assertEquals(0, comments.size());
		
		assertEquals("Core", this.parser.getComponent());
		assertEquals(DateTimeUtils.parseDate("2011-03-16 21:04 PDT"), this.parser.getCreationTimestamp());
		assertTrue(this.parser.getDescription().startsWith("User-Agent:"));
		assertTrue(this.parser.getDescription().endsWith("ReferenceError: \"adblock\" is not defined."));
		
		this.parser.parseHistoryElements(report.getHistory());
		assertTrue(report.getHistory().isEmpty());
		
		assertEquals(null, this.parser.getLastUpdateTimestamp());
		
		assertEquals(Priority.UNKNOWN, this.parser.getPriority());
		assertEquals(Resolution.UNRESOLVED, this.parser.getResolution());
		assertEquals(Severity.CRITICAL, this.parser.getSeverity());
		
		assertEquals(0, this.parser.getSiblings().size());
		
		assertEquals(Status.UNCONFIRMED, this.parser.getStatus());
		assertEquals("Envjs.connection Can't find method java.io.FilterInputStream.read([C,number,number) (env.rhino.js#1631)",
		             this.parser.getSubject());
		assertTrue(this.parser.getSubmitter() != null);
		assertEquals("rhino", this.parser.getSubmitter().getUsernames().iterator().next());
		assertEquals("rhino", this.parser.getSubmitter().getFullnames().iterator().next());
		assertEquals(null, this.parser.getSummary());
		assertEquals(Type.BUG, this.parser.getType());
		assertEquals("1.7R1", this.parser.getVersion());
		assertTrue(this.parser.getKeywords().isEmpty());
		
	}
	
	/**
	 * Test attachments.
	 */
	@Test
	public void testAttachments() {
		
		final ReportLink reportLink = new ReportLink(this.uri153429, "153429");
		final Report report = this.parser.setContext(this.issueTracker, reportLink);
		assertNotNull(report);
		final List<AttachmentEntry> attachments = this.parser.getAttachmentEntries();
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
		try {
			RawContent rawContent = IOUtils.fetch(this.uri1234);
			assertFalse(this.parser.checkRAW(rawContent.getContent()));
			rawContent = IOUtils.fetch(this.uri114562);
			assertTrue(this.parser.checkRAW(rawContent.getContent()));
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
			final RawContent rawContent = IOUtils.fetch(this.uri114562);
			final XmlReport xmlReport = this.parser.createDocument(rawContent);
			assertTrue(this.parser.checkXML(xmlReport));
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
		assertNotNull(dateTime);
	}
	
	/**
	 * Test keywords.
	 */
	@Test
	public void testKeywords() {
		
		final ReportLink reportLink = new ReportLink(this.uri153429, "153429");
		final Report report = this.parser.setContext(this.issueTracker, reportLink);
		assertNotNull(report);
		final Set<String> keywords = this.parser.getKeywords();
		assertEquals(1, keywords.size());
		assertTrue(keywords.contains("plan"));
	}
	
	/**
	 * Test parse.
	 */
	@Test
	public void testParse() {
		
		final ReportLink reportLink = new ReportLink(this.uri114562, "114562");
		final Report report = this.parser.setContext(this.issueTracker, reportLink);
		assertNotNull(report);
		assertEquals("114562", this.parser.getId());
		assertEquals("mik.kersten", this.parser.getAssignedTo().getUsernames().iterator().next());
		assertEquals("Mik Kersten", this.parser.getAssignedTo().getFullnames().iterator().next());
		assertEquals("Mylyn", this.parser.getCategory());
		
		final SortedSet<Comment> comments = this.parser.getComments();
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
		
		assertEquals("Bugzilla", this.parser.getComponent());
		assertEquals(DateTimeUtils.parseDate("2005-11-01 11:42 EST"), this.parser.getCreationTimestamp());
		assertEquals("eclipse.org is moving to it", this.parser.getDescription());
		
		this.parser.parseHistoryElements(report.getHistory());
		assertTrue(report.getHistory().isEmpty());
		
		assertTrue(DateTimeUtils.parseDate("2005-11-03 23:17:37 -0500").isEqual(this.parser.getLastUpdateTimestamp()));
		
		assertEquals(Priority.VERY_HIGH, this.parser.getPriority());
		assertEquals(Resolution.RESOLVED, this.parser.getResolution());
		assertEquals(Severity.ENHANCEMENT, this.parser.getSeverity());
		
		assertEquals(2, this.parser.getSiblings().size());
		assertEquals(true, this.parser.getSiblings().contains("113042"));
		assertEquals(true, this.parser.getSiblings().contains("115017"));
		
		assertEquals(Status.CLOSED, this.parser.getStatus());
		assertEquals("add basic support for Bugzilla 2.20", this.parser.getSubject());
		assertTrue(this.parser.getSubmitter() != null);
		assertEquals("mik.kersten", this.parser.getSubmitter().getUsernames().iterator().next());
		assertEquals("Mik Kersten", this.parser.getSubmitter().getFullnames().iterator().next());
		assertEquals(null, this.parser.getSummary());
		assertEquals(Type.RFE, this.parser.getType());
		assertEquals("unspecified", this.parser.getVersion());
		
		assertTrue(this.parser.getKeywords().isEmpty());
		
	}
}

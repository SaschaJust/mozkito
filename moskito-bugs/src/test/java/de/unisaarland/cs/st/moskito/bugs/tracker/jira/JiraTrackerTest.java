/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.exceptions.FetchException;
import net.ownhero.dev.ioda.exceptions.UnsupportedProtocolException;
import net.ownhero.dev.regex.RegexGroup;

import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.RawReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.XmlReport;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.jira.JiraIDExtractor;
import de.unisaarland.cs.st.moskito.bugs.tracker.jira.JiraTracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.jira.JiraXMLParser;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.History;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;

public class JiraTrackerTest {
	
	private static final URL overViewUrl = JiraTrackerTest.class.getResource(FileUtils.fileSeparator + "JAXEN_JIRA.xml");
	private static URL       url177      = JiraTrackerTest.class.getResource(FileUtils.fileSeparator + "JIRA-177.xml");
	private static String    baseURL     = url177.toString();
	private static String    baseDirURL  = baseURL.substring(0, url177.toString().lastIndexOf("JIRA-177.xml"));
	private static String    pattern     = "JIRA-" + Tracker.bugIdPlaceholder + ".xml";
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAttachments() {
		JiraTracker tracker = new JiraTracker();
		try {
			tracker.setup(new URI(baseDirURL), null, pattern, null, null, new Long(451l), new Long(451l), null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		
		RawReport rawReport = null;
		try {
			rawReport = tracker.fetchSource(tracker.getLinkFromId(451l));
		} catch (FetchException e) {
			e.printStackTrace();
			fail();
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		}
		XmlReport xmlReport = tracker.createDocument(rawReport);
		Element rootElement = tracker.getRootElement(xmlReport);
		
		List<AttachmentEntry> attachments = JiraXMLParser.extractAttachments(rootElement, tracker);
		assertEquals(1, attachments.size());
		
		assertEquals("38639", attachments.get(0).getId());
		assertEquals(0, attachments.get(0).getAuthor().getUsernames().size());
		assertTrue(attachments.get(0).getAuthor().getEmailAddresses().contains("andreas.bartelt@gmail.com"));
		assertEquals(null, attachments.get(0).getDeltaTS());
		assertEquals(null, attachments.get(0).getDescription());
		assertEquals(".classpath", attachments.get(0).getFilename());
		assertEquals(baseDirURL.toString() + "secure/attachment/38639/.classpath", attachments.get(0).getLink()
		                                                                                      .toString());
		assertEquals(null, attachments.get(0).getMime());
		assertEquals(8818, attachments.get(0).getSize());
		assertEquals(DateTimeUtils.parseDate("2008-12-08 09:51:06 -0600"), attachments.get(0).getTimestamp());
	}
	
	@Test
	public void testFromOverviewParse() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			
			tracker.setup(new URI(baseDirURL), overViewUrl.toURI(), pattern, null, null, new Long(1l), new Long(1000l),
			              null);
			
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(177l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(177, report.getId());
			assertEquals(null, report.getAssignedTo());
			assertEquals(null, report.getCategory());
			SortedSet<Comment> comments = report.getComments();
			assertEquals(16, comments.size());
			assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
			
			assertEquals("elharo", comments.first().getAuthor().getUsernames().iterator().next());
			assertTrue(comments.first().getAuthor().getEmailAddresses().isEmpty());
			assertTrue(comments.first().getAuthor().getFullnames().isEmpty());
			assertTrue(DateTimeUtils.parseDate("Wed, 3 Jan 2007 12:28:54 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(comments.first().getTimestamp()));
			assertEquals("<p>You specified this against 1.0. Have you tried 1.1? We've fixed a lot of bugs since 1.0. </p>\n\n<p>The issue is not immediately apparent to me. If you had a test case, that would help. </p>",
			             comments.first().getMessage());
			
			assertEquals("elharo", comments.last().getAuthor().getUsernames().iterator().next());
			assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
			assertTrue(comments.last().getAuthor().getEmailAddresses().isEmpty());
			assertTrue(comments.last().getAuthor().getFullnames().isEmpty());
			assertTrue(DateTimeUtils.parseDate("Sat, 6 Jan 2007 05:51:31 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(comments.last().getTimestamp()));
			assertEquals("<p>Fixed. </p>", comments.last().getMessage());
			
			assertEquals("core", report.getComponent());
			assertEquals(DateTimeUtils.parseDate("Wed, 3 Jan 2007 11:22:12 -0600", JiraXMLParser.dateTimeFormatRegex),
			             report.getCreationTimestamp());
			assertEquals("<p>There is at least one scenario "
			                     + "where calling the getText() method on the Jaxen XPath AST classes "
			                     + "can result in an invalid XPath query string, such that Jaxen "
			                     + "cannot be asked to create a new BaseXPath from the result of getText()"
			                     + ".</p>\n\n<p>1) A LiteralExpr where the value contains a "
			                     + "\" (double quote) character.  The resulting getText() looks like:</p>\n\n<p>\"\"\""
			                     + "  (3 double quotes).</p>\n\n<p>The original XPath query string was:</p>\n\n<p>'\"'"
			                     + "   (single quote, double quote, single quote)</p>\n\n<p>And example of a problematic query "
			                     + "from the PMD project is the optimizations.xml/SimplifyStartsWith rule, which uses XPath:<"
			                     + "/p>\n\n<p>//PrimaryExpression<br/>\n [PrimaryPrefix/Name<br/>\n  <span "
			                     + "class=\"error\">&#91;ends-with(@Image, &#39;.startsWith&#39;)"
			                     + "&#93;</span>]<br/>\n [PrimarySuffix/Arguments/ArgumentList<br/"
			                     + ">\n  /Expression/PrimaryExpression/PrimaryPrefix<br/>\n  /Literal<br/>\n   <span class=\"error\""
			                     + ">&#91;string-length(@Image)=3&#93;</span><br/>\n   <span class=\"error\">&#91;"
			                     + "starts-with(@Image, &#39;&quot;&#39;)&#93;</span><br/>\n   <span "
			                     + "class=\"error\">&#91;ends-with(@Image, &#39;&quot;&#39;)&#93;</span></p>",
			             report.getDescription());
			History history = report.getHistory();
			assertEquals(0, history.size());
			
			assertEquals(rawReport.getFetchTime(), report.getLastFetch());
			assertTrue(DateTimeUtils.parseDate("Sat, 6 Jan 2007 05:51:31 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(report.getLastUpdateTimestamp()));
			assertEquals(Priority.NORMAL, report.getPriority());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			assertTrue(DateTimeUtils.parseDate("Sat, 6 Jan 2007 05:51:31 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(report.getResolutionTimestamp()));
			assertEquals(comments.first().getAuthor().getUsernames().iterator().next(), report.getResolver()
			                                                                                  .getUsernames()
			                                                                                  .iterator().next());
			assertEquals(new Report(0).getSeverity(), report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals("[JAXEN-177] Expression.getText() returns invalid XPath query strings", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals("rgustav", report.getSubmitter().getUsernames().iterator().next());
			assertEquals("Ryan Gustafson", report.getSubmitter().getFullnames().iterator().next());
			assertEquals("Expression.getText() returns invalid XPath query strings", report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals("1.1", report.getVersion());
			
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (FetchException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetHistoryURL() {
		try {
			String historyURL = JiraTracker.getHistoryURL(new URI(
			                                                      "http://jira.codehaus.org/si/jira.issueviews:issue-xml/JAXEN-210/JAXEN-210.xml"));
			assertEquals("http://jira.codehaus.org/browse/JAXEN-210?page=com.atlassian.jira.plugin.system.issuetabpanels:changehistory-tabpanel#issue-tabs",
			             historyURL);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testOverallIdDetection() {
		String s = "[JAXEN-210] Jaxen does not cope well with numeric types other than Double";
		assertTrue(JiraIDExtractor.idRegex.matches(s));
		List<RegexGroup> find = JiraIDExtractor.idRegex.find(s);
		assertEquals(2, find.size());
		assertEquals(find.get(1).getMatch(), "210");
	}
	
	@Test
	public void testOverallIdFilter() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			tracker.setup(new URI(baseDirURL), overViewUrl.toURI(), pattern, null, null, new Long(1l), new Long(1000l),
			              null);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParse() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			
			tracker.setup(new URI(baseDirURL), null, pattern, null, null, new Long(1l), new Long(1000l), null);
			
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(177l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(177, report.getId());
			assertEquals(null, report.getAssignedTo());
			assertEquals(null, report.getCategory());
			SortedSet<Comment> comments = report.getComments();
			assertEquals(16, comments.size());
			assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
			
			assertEquals("elharo", comments.first().getAuthor().getUsernames().iterator().next());
			assertTrue(comments.first().getAuthor().getEmailAddresses().isEmpty());
			assertTrue(comments.first().getAuthor().getFullnames().isEmpty());
			assertTrue(DateTimeUtils.parseDate("Wed, 3 Jan 2007 12:28:54 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(comments.first().getTimestamp()));
			assertEquals("<p>You specified this against 1.0. Have you tried 1.1? We've fixed a lot of bugs since 1.0. </p>\n\n<p>The issue is not immediately apparent to me. If you had a test case, that would help. </p>",
			             comments.first().getMessage());
			
			assertEquals("elharo", comments.last().getAuthor().getUsernames().iterator().next());
			assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
			assertTrue(comments.last().getAuthor().getEmailAddresses().isEmpty());
			assertTrue(comments.last().getAuthor().getFullnames().isEmpty());
			assertTrue(DateTimeUtils.parseDate("Sat, 6 Jan 2007 05:51:31 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(comments.last().getTimestamp()));
			assertEquals("<p>Fixed. </p>", comments.last().getMessage());
			
			assertEquals("core", report.getComponent());
			assertEquals(DateTimeUtils.parseDate("Wed, 3 Jan 2007 11:22:12 -0600", JiraXMLParser.dateTimeFormatRegex),
			             report.getCreationTimestamp());
			assertEquals("<p>There is at least one scenario "
			                     + "where calling the getText() method on the Jaxen XPath AST classes "
			                     + "can result in an invalid XPath query string, such that Jaxen "
			                     + "cannot be asked to create a new BaseXPath from the result of getText()"
			                     + ".</p>\n\n<p>1) A LiteralExpr where the value contains a "
			                     + "\" (double quote) character.  The resulting getText() looks like:</p>\n\n<p>\"\"\""
			                     + "  (3 double quotes).</p>\n\n<p>The original XPath query string was:</p>\n\n<p>'\"'"
			                     + "   (single quote, double quote, single quote)</p>\n\n<p>And example of a problematic query "
			                     + "from the PMD project is the optimizations.xml/SimplifyStartsWith rule, which uses XPath:<"
			                     + "/p>\n\n<p>//PrimaryExpression<br/>\n [PrimaryPrefix/Name<br/>\n  <span "
			                     + "class=\"error\">&#91;ends-with(@Image, &#39;.startsWith&#39;)"
			                     + "&#93;</span>]<br/>\n [PrimarySuffix/Arguments/ArgumentList<br/"
			                     + ">\n  /Expression/PrimaryExpression/PrimaryPrefix<br/>\n  /Literal<br/>\n   <span class=\"error\""
			                     + ">&#91;string-length(@Image)=3&#93;</span><br/>\n   <span class=\"error\">&#91;"
			                     + "starts-with(@Image, &#39;&quot;&#39;)&#93;</span><br/>\n   <span "
			                     + "class=\"error\">&#91;ends-with(@Image, &#39;&quot;&#39;)&#93;</span></p>",
			             report.getDescription());
			
			History history = report.getHistory();
			assertEquals(0, history.size());
			
			assertEquals(rawReport.getFetchTime(), report.getLastFetch());
			assertTrue(DateTimeUtils.parseDate("Sat, 6 Jan 2007 05:51:31 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(report.getLastUpdateTimestamp()));
			assertEquals(Priority.NORMAL, report.getPriority());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			assertTrue(DateTimeUtils.parseDate("Sat, 6 Jan 2007 05:51:31 -0600", JiraXMLParser.dateTimeFormatRegex)
			                        .isEqual(report.getResolutionTimestamp()));
			assertEquals(comments.first().getAuthor().getUsernames().iterator().next(), report.getResolver()
			                                                                                  .getUsernames()
			                                                                                  .iterator().next());
			assertEquals(new Report(0).getSeverity(), report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals("[JAXEN-177] Expression.getText() returns invalid XPath query strings", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals("rgustav", report.getSubmitter().getUsernames().iterator().next());
			assertEquals("Ryan Gustafson", report.getSubmitter().getFullnames().iterator().next());
			assertEquals("Expression.getText() returns invalid XPath query strings", report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals("1.1", report.getVersion());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (FetchException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParseHistory() {
		Report report = new Report(9551);
		URL url = JiraTrackerTest.class.getResource(FileUtils.fileSeparator + "JIRA-9551_history.html");
		try {
			JiraXMLParser.handleHistory(url.toURI(), report);
			History history = report.getHistory();
			assertEquals(1, history.size());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testParseLinks() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			
			tracker.setup(new URI(baseDirURL), null, pattern, null, null, new Long(1l), new Long(10000l), null);
			
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(9551l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(9551, report.getId());
			assertEquals(null, report.getAssignedTo());
			assertEquals(null, report.getCategory());
			SortedSet<Comment> comments = report.getComments();
			assertEquals(12, comments.size());
			assertEquals(2, report.getSiblings().size());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testXStream42() {
		JiraTracker tracker = new JiraTracker();
		try {
			tracker.setup(new URI("http://jira.codehaus.org/si/jira.issueviews:issue-xml/"), null,
			              "XSTR-<BUGID>/XSTR-<BUGID>.xml", null, null, 42l, 42l, null);
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(42l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			tracker.checkXML(xmlReport);
			tracker.parse(xmlReport);
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (FetchException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}

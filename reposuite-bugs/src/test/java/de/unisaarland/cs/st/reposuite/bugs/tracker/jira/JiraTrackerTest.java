package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.SortedSet;

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
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Status;
import de.unisaarland.cs.st.reposuite.bugs.tracker.model.Type;
import de.unisaarland.cs.st.reposuite.exceptions.FetchException;
import de.unisaarland.cs.st.reposuite.exceptions.UnsupportedProtocolException;
import de.unisaarland.cs.st.reposuite.utils.FileUtils;
import de.unisaarland.cs.st.reposuite.utils.RegexGroup;

public class JiraTrackerTest {
	
	private static final URL overViewUrl = JiraTrackerTest.class
	                                             .getResource(FileUtils.fileSeparator + "JAXEN_JIRA.xml");
	private static URL       url177      = JiraTrackerTest.class.getResource(FileUtils.fileSeparator + "JAXEN-177.xml");
	private static String    baseURL     = url177.toString();
	private static String    baseDirURL  = baseURL.substring(0, url177.toString().lastIndexOf("JAXEN-177.xml"));
	private static String    pattern     = "JAXEN-" + Tracker.bugIdPlaceholder + ".xml";
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testFromOverviewParse() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			
			tracker.setup(new URI(baseDirURL), overViewUrl.toURI(), pattern, null, null, new Long(1l), new Long(1000l));
			
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(177l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(177, report.getId());
			assertEquals(null, report.getAssignedTo());
			assertEquals(null, report.getCategory());
			SortedSet<Comment> comments = report.getComments();
			assertEquals(16, comments.size());
			assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
			
			assertEquals("elharo", comments.first().getAuthor().getUsername());
			assertEquals(null, comments.first().getAuthor().getEmail());
			assertEquals(null, comments.first().getAuthor().getFullname());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Wed, 3 Jan 2007 12:28:54 -0600").isEqual(
			        comments.first().getTimestamp()));
			assertEquals(
			        "<p>You specified this against 1.0. Have you tried 1.1? We've fixed a lot of bugs since 1.0. </p>\n\n<p>The issue is not immediately apparent to me. If you had a test case, that would help. </p>",
			        comments.first().getMessage());
			
			assertEquals("elharo", comments.last().getAuthor().getUsername());
			assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
			assertEquals(null, comments.last().getAuthor().getEmail());
			assertEquals(null, comments.last().getAuthor().getFullname());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Sat, 6 Jan 2007 05:51:31 -0600").isEqual(
			        comments.last().getTimestamp()));
			assertEquals("<p>Fixed. </p>", comments.last().getMessage());
			
			assertEquals("core", report.getComponent());
			assertEquals(JiraXMLHandler.dateTimeFormat.parseDateTime("Wed, 3 Jan 2007 11:22:12 -0600"),
			        report.getCreationTimestamp());
			assertEquals(
			        "<p>There is at least one scenario "
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
			assertEquals(null, report.getExpectedBehavior());
			
			SortedSet<HistoryElement> history = report.getHistory();
			assertEquals(0, history.size());
			
			assertEquals(rawReport.getFetchTime(), report.getLastFetch());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Sat, 6 Jan 2007 05:51:31 -0600").isEqual(
			        report.getLastUpdateTimestamp()));
			assertEquals(null, report.getObservedBehavior());
			assertEquals(Priority.NORMAL, report.getPriority());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Sat, 6 Jan 2007 05:51:31 -0600").isEqual(
			        report.getResolutionTimestamp()));
			assertEquals(comments.first().getAuthor().getUsername(), report.getResolver().getUsername());
			assertEquals(null, report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals(null, report.getStepsToReproduce());
			assertEquals("[JAXEN-177] Expression.getText() returns invalid XPath query strings", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals("rgustav", report.getSubmitter().getUsername());
			assertEquals("Ryan Gustafson", report.getSubmitter().getFullname());
			assertEquals("Expression.getText() returns invalid XPath query strings", report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals("1.1", report.getVersion());
			
		} catch (UnsupportedProtocolException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (FetchException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testOverallIdDetection() {
		String s = "[JAXEN-210] Jaxen does not cope well with numeric types other than Double";
		assertTrue(JiraIDExtractor.idRegex.matches(s));
		List<RegexGroup> find = JiraIDExtractor.idRegex.find(s);
		assertEquals(1, find.size());
		assertEquals(find.get(0).getMatch(), "210");
	}
	
	@Test
	public void testOverallIdFilter() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			tracker.setup(new URI(baseDirURL), overViewUrl.toURI(), pattern, null, null, new Long(1l), new Long(1000l));
		} catch (InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testParse() {
		JiraTracker tracker = new JiraTracker();
		
		try {
			
			tracker.setup(new URI(baseDirURL), null, pattern, null, null, new Long(1l), new Long(1000l));
			
			RawReport rawReport = tracker.fetchSource(tracker.getLinkFromId(177l));
			XmlReport xmlReport = tracker.createDocument(rawReport);
			Report report = tracker.parse(xmlReport);
			
			assertEquals(177, report.getId());
			assertEquals(null, report.getAssignedTo());
			assertEquals(null, report.getCategory());
			SortedSet<Comment> comments = report.getComments();
			assertEquals(16, comments.size());
			assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
			
			assertEquals("elharo", comments.first().getAuthor().getUsername());
			assertEquals(null, comments.first().getAuthor().getEmail());
			assertEquals(null, comments.first().getAuthor().getFullname());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Wed, 3 Jan 2007 12:28:54 -0600").isEqual(
			        comments.first().getTimestamp()));
			assertEquals(
			        "<p>You specified this against 1.0. Have you tried 1.1? We've fixed a lot of bugs since 1.0. </p>\n\n<p>The issue is not immediately apparent to me. If you had a test case, that would help. </p>",
			        comments.first().getMessage());
			
			assertEquals("elharo", comments.last().getAuthor().getUsername());
			assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
			assertEquals(null, comments.last().getAuthor().getEmail());
			assertEquals(null, comments.last().getAuthor().getFullname());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Sat, 6 Jan 2007 05:51:31 -0600").isEqual(
			        comments.last().getTimestamp()));
			assertEquals("<p>Fixed. </p>", comments.last().getMessage());
			
			assertEquals("core", report.getComponent());
			assertEquals(JiraXMLHandler.dateTimeFormat.parseDateTime("Wed, 3 Jan 2007 11:22:12 -0600"),
			        report.getCreationTimestamp());
			assertEquals(
			        "<p>There is at least one scenario "
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
			assertEquals(null, report.getExpectedBehavior());
			
			SortedSet<HistoryElement> history = report.getHistory();
			assertEquals(0, history.size());
			
			assertEquals(rawReport.getFetchTime(), report.getLastFetch());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Sat, 6 Jan 2007 05:51:31 -0600").isEqual(
			        report.getLastUpdateTimestamp()));
			assertEquals(null, report.getObservedBehavior());
			assertEquals(Priority.NORMAL, report.getPriority());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			assertTrue(JiraXMLHandler.dateTimeFormat.parseDateTime("Sat, 6 Jan 2007 05:51:31 -0600").isEqual(
			        report.getResolutionTimestamp()));
			assertEquals(comments.first().getAuthor().getUsername(), report.getResolver().getUsername());
			assertEquals(null, report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals(null, report.getStepsToReproduce());
			assertEquals("[JAXEN-177] Expression.getText() returns invalid XPath query strings", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals("rgustav", report.getSubmitter().getUsername());
			assertEquals("Ryan Gustafson", report.getSubmitter().getFullname());
			assertEquals("Expression.getText() returns invalid XPath query strings", report.getSummary());
			assertEquals(Type.BUG, report.getType());
			assertEquals("1.1", report.getVersion());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (InvalidParameterException e) {
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
	
}

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
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.SortedSet;

import net.ownhero.dev.ioda.ProxyConfig;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
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

/**
 * The Class JiraTracker_NetTest.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class JiraTracker_NetTest {
	
	/** The tracker. */
	private JiraTracker tracker;
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.tracker = new JiraTracker();
		try {
			this.tracker.setup(new URI("http://jira.codehaus.org"), null, null, "XPR", new ProxyConfig("localhost",
			                                                                                           3128, false));
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
	/**
	 * Test attachments.
	 */
	@Test
	public void testAttachments() {
		
		try {
			final ReportLink reportLink = new ReportLink(
			                                             new URI(
			                                                     "jira.codehaus.org/si/jira.issueviews:issue-xml/XPR-451/XPR-451.xml"),
			                                             "XPR-451");
			final Report report = this.tracker.parse(reportLink);
			assert (report != null);
			final List<AttachmentEntry> attachments = report.getAttachmentEntries();
			assertEquals(1, attachments.size());
			
			assertEquals("38639", attachments.get(0).getId());
			assertEquals(0, attachments.get(0).getAuthor().getUsernames().size());
			assertTrue(attachments.get(0).getAuthor().getEmailAddresses().contains("andreas.bartelt@gmail.com"));
			assertTrue(attachments.get(0).getAuthor().getFullnames().contains("Andreas Bartelt"));
			assertEquals(null, attachments.get(0).getDeltaTS());
			assertEquals(null, attachments.get(0).getDescription());
			assertEquals(".classpath", attachments.get(0).getFilename());
			assertEquals("http://jira.codehaus.org/secure/attachment/38639/.classpath", attachments.get(0).getLink()
			                                                                                       .toString());
			assertEquals("application/octet-stream", attachments.get(0).getMime());
			assertEquals(8818, attachments.get(0).getSize());
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Test from overview parse.
	 * 
	 * @throws InvalidParameterException
	 *             the invalid parameter exception
	 */
	@Test
	public void testFromOverviewParse() throws InvalidParameterException {
		try {
			final ReportLink reportLink = new ReportLink(
			                                             new URI(
			                                                     "jira.codehaus.org/si/jira.issueviews:issue-xml/JAXEN-177/JAXEN-177.xml"),
			                                             "JAXEN-177");
			final Report report = this.tracker.parse(reportLink);
			assert (report != null);
			assertEquals("JAXEN-177", report.getId());
			assertEquals(null, report.getAssignedTo());
			assertEquals(null, report.getCategory());
			final SortedSet<Comment> comments = report.getComments();
			assertEquals(16, comments.size());
			assertTrue(comments.first().getTimestamp().isBefore(comments.last().getTimestamp()));
			
			assertEquals("elharo", comments.first().getAuthor().getUsernames().iterator().next());
			assertTrue(comments.first().getAuthor().getEmailAddresses().isEmpty());
			assertEquals("Elliotte Rusty Harold", comments.first().getAuthor().getFullnames().iterator().next());
			assertTrue(comments.first().getMessage().startsWith("You specified this against 1.0. Have you tried 1.1?"));
			
			assertEquals("elharo", comments.last().getAuthor().getUsernames().iterator().next());
			assertEquals(comments.first().getAuthor(), comments.last().getAuthor());
			assertTrue(comments.last().getAuthor().getEmailAddresses().isEmpty());
			assertEquals("Elliotte Rusty Harold", comments.last().getAuthor().getFullnames().iterator().next());
			assertEquals("Fixed.", comments.last().getMessage().trim());
			
			assertEquals("core", report.getComponent());
			assertTrue(report.getDescription().startsWith("There is at least one scenario"));
			final History history = report.getHistory();
			assertEquals(2, history.size());
			
			HistoryElement hElem = history.first();
			assertTrue(hElem.get("version") != null);
			assertEquals("1.0", hElem.get("version").getFirst());
			assertEquals("", hElem.get("version").getSecond());
			
			hElem = history.last();
			assertEquals(Status.NEW, hElem.get("status").getFirst());
			assertTrue(hElem.get("status") != null);
			assertEquals(Status.CLOSED, hElem.get("status").getSecond());
			
			assertTrue(hElem.get("resolution") != null);
			assertEquals(Resolution.UNKNOWN, hElem.get("resolution").getFirst());
			assertEquals(Resolution.RESOLVED, hElem.get("resolution").getSecond());
			
			assertEquals(Priority.NORMAL, report.getPriority());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			
			final String username = comments.first().getAuthor().getUsernames().iterator().next();
			
			assertEquals(username, report.getResolver().getUsernames().iterator().next());
			assertEquals(Severity.MAJOR, report.getSeverity());
			assertEquals(0, report.getSiblings().size());
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals("Expression.getText() returns invalid XPath query strings", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals("rgustav", report.getSubmitter().getUsernames().iterator().next());
			assertEquals("Ryan Gustafson", report.getSubmitter().getFullnames().iterator().next());
			assertTrue(report.getSummary().startsWith("There is at least one scenario where calling"));
			assertEquals(Type.BUG, report.getType());
			assertEquals("1.1", report.getVersion());
			
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}

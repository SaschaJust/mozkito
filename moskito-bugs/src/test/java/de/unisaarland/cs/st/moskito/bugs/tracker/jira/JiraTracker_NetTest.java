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
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.AttachmentEntry;
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
			this.tracker.setup(new URI("http://jira.codehaus.org"), null, null, "XPR", null);
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
	
	@Test
	public void testOverview() {
		
		final Set<ReportLink> reportLinks = this.tracker.getReportLinks();
		assertTrue(reportLinks.size() >= 462);
	}
}

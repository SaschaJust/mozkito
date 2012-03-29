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
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.exceptions.InvalidParameterException;
import de.unisaarland.cs.st.moskito.bugs.tracker.ReportLink;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;

/**
 * The Class GoogleTracker_NetTest.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GoogleTracker_NetTest {
	
	/** The Constant dateTimeHistoryFormatRegex. */
	protected static final Regex dateTimeHistoryFormatRegex = new Regex(
	                                                                    "(({yyyy}\\d{4})-({MM}\\d{2})-({dd}\\d{2})T({HH}\\d{2}):({mm}[0-5]\\d):({ss}[0-5]\\d))");
	
	/**
	 * Test fetch regex.
	 */
	@Test
	public void testFetchRegex() {
		String fetchURI = "https://code.google.com/feeds/issues/p/webtoolkit/issues/full";
		final Regex regex = new Regex(GoogleTracker.fetchRegexPattern);
		List<RegexGroup> groups = regex.find(fetchURI);
		assertTrue(groups.size() > 1);
		assertEquals("webtoolkit", regex.getGroup("project"));
		
		fetchURI = "http://code.google.com/p/google-web-toolkit/issues/list";
		groups = regex.find(fetchURI);
		assertTrue(groups.size() > 1);
		assertEquals("google-web-toolkit", regex.getGroup("project"));
	}
	
	/**
	 * Test tracker.
	 */
	@Test
	public void testParse() {
		try {
			
			final GoogleTracker tracker = new GoogleTracker();
			tracker.testSetup("google-web-toolkit");
			
			final Report report = tracker.parse(new ReportLink(null, "4380"));
			assertEquals("4380", report.getId());
			assertEquals(1, report.getAssignedTo().getUsernames().size());
			assertTrue(report.getAssignedTo().getUsernames().contains("jat@google.com"));
			assertEquals("DevPlugin", report.getCategory());
			
			assertEquals(60, report.getComments().size());
			
			assertEquals(null, report.getComponent());
			assertTrue(DateTimeUtils.parseDate("2009-12-19T15:38:51.000Z", dateTimeHistoryFormatRegex)
			                        .isEqual(report.getCreationTimestamp()));
			
			assertTrue(report.getHistory() != null);
			assertEquals(2, report.getHistory().size());
			
			assertEquals(null, report.getPriority());
			assertEquals(new Report("0").getProduct(), report.getProduct());
			assertEquals(Resolution.RESOLVED, report.getResolution());
			assertTrue(DateTimeUtils.parseDate("2010-02-02T00:07:22.000Z", dateTimeHistoryFormatRegex)
			                        .isEqual(report.getResolutionTimestamp()));
			assertTrue(report.getResolver() != null);
			assertEquals(1, report.getResolver().getEmailAddresses().size());
			assertTrue(report.getResolver().getEmailAddresses().contains("jat@google.com"));
			assertEquals(null, report.getSeverity());
			assert report.getSiblings() != null;
			assertEquals(0, report.getSiblings().size());
			assertEquals(Status.CLOSED, report.getStatus());
			assertEquals("DevMode plug-in doesn't work in Firefox 3.6", report.getSubject());
			assertTrue(report.getSubmitter() != null);
			assertEquals(1, report.getSubmitter().getFullnames().size());
			assertTrue(report.getSubmitter().getFullnames().contains("t.broyer"));
			assertEquals(report.getDescription(), report.getSummary());
			assertEquals(Type.RFE, report.getType());
			assertEquals(null, report.getVersion());
			assertTrue(report.getDescription().length() > 0);
			assertTrue(report.getDescription().contains("Firefox keeps saying the page needs a plugin when"));
			
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} finally {
			
		}
	}
}

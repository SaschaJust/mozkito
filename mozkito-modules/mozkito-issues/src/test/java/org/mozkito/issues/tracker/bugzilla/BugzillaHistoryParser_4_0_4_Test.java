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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.Iterator;
import java.util.SortedSet;

import net.ownhero.dev.ioda.DateTimeUtils;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.issues.tracker.elements.Priority;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.issues.tracker.model.IssueTracker;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.model.Person;

// TODO: Auto-generated Javadoc
/**
 * The Class BugzillaHistoryParser_4_0_4_Test.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class BugzillaHistoryParser_4_0_4_Test {
	
	/** The uri642368history. */
	private URI uri642368history;
	
	/** The uri114562history. */
	private URI uri114562history;
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		
		this.uri642368history = getClass().getResource(FileUtils.fileSeparator + "bugzilla_mozilla_642368_history.html")
		                                  .toURI();
		this.uri114562history = getClass().getResource(FileUtils.fileSeparator + "bugzilla_114562_history.html")
		                                  .toURI();
		
	}
	
	/**
	 * Test mozilla history.
	 */
	@Test
	public void testMozillaHistory() {
		
		try {
			// final URL historyURL = new URL("https://issues.eclipse.org/issues/show_activity.cgi?id=114562");
			
			final BugzillaHistoryParser_4_0_4 historyParser = new BugzillaHistoryParser_4_0_4(this.uri642368history,
			                                                                                  "642368");
			final Report report = new Report(new IssueTracker(), "642368");
			if (!historyParser.parse(report.getHistory())) {
				fail();
			}
			final SortedSet<HistoryElement> historyElements = historyParser.getHistory();
			assertEquals(1, historyElements.size());
			assertEquals(report.getHistory().size(), historyElements.size());
			final Iterator<HistoryElement> hElemIter = report.getHistory().iterator();
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
				Logger.error(e);
			}
			fail();
		}
	}
	
	/**
	 * Test parse history.
	 */
	@Test
	public void testParseHistory() {
		
		final BugzillaHistoryParser_4_0_4 historyParser = new BugzillaHistoryParser_4_0_4(this.uri114562history,
		                                                                                  "114562");
		
		try {
			
			final Report report = new Report(new IssueTracker(), "114562");
			if (!historyParser.parse(report.getHistory())) {
				fail();
			}
			
			final DateTime resolutionTimestamp = historyParser.getResolutionTimestamp();
			final Person resolver = historyParser.getResolver();
			
			assertEquals(3, report.getHistory().size());
			final Iterator<HistoryElement> hElemIter = report.getHistory().iterator();
			HistoryElement hElem = hElemIter.next();
			assertEquals(1, hElem.size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:43:19 EST"), hElem.getTimestamp());
			assertTrue(hElem.contains("priority"));
			assertEquals(Priority.NORMAL, report.getHistory().getOldValue("priority", hElem));
			assertEquals(BugzillaParser.getPriority("P1"), hElem.get("priority").getSecond());
			hElem = hElemIter.next();
			assertEquals(1, hElem.size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-01 11:52:13 EST"), hElem.getTimestamp());
			assertTrue(hElem.contains("summary"));
			assertEquals("add support for Bugzilla 2.20", report.getHistory().getOldValue("summary", hElem));
			assertEquals("add support for Bugzilla 2 20", hElem.get("summary").getSecond());
			hElem = hElemIter.next();
			assertEquals(3, hElem.size());
			assertEquals("mik.kersten", hElem.getAuthor().getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST"), hElem.getTimestamp());
			assertTrue(hElem.contains("status"));
			assertTrue(hElem.contains("resolution"));
			assertTrue(hElem.contains("summary"));
			assertEquals(BugzillaParser.getStatus("NEW"), report.getHistory().getOldValue("status", hElem));
			assertEquals(BugzillaParser.getStatus("RESOLVED"), hElem.get("status").getSecond());
			assertEquals(BugzillaParser.getResolution("---"), report.getHistory().getOldValue("resolution", hElem));
			assertEquals(BugzillaParser.getResolution("FIXED"), hElem.get("resolution").getSecond());
			assertEquals("add support for Bugzilla 2 20", report.getHistory().getOldValue("summary", hElem));
			assertEquals("add basic support for Bugzilla 2.20", hElem.get("summary").getSecond());
			assertEquals("mik.kersten", resolver.getUsernames().iterator().next());
			assertEquals(DateTimeUtils.parseDate("2005-11-03 23:17:37 EST"), resolutionTimestamp);
		} catch (final SecurityException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			fail();
		}
	}
	
}

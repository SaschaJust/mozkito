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
package org.mozkito.issues.tracker.bugzilla;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.junit.Test;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.bugzilla.BugzillaOverviewParser;


/**
 * The Class BugzillaOverviewParserTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class BugzillaOverviewParserTest {
	
	/**
	 * Test eclipse.
	 */
	@Test
	public void testEclipse() {
		try {
			final BugzillaOverviewParser parser = new BugzillaOverviewParser(
			                                                                 new URI("https://issues.eclipse.org/issues/"),
			                                                                 this.getClass()
			                                                                     .getResource(FileUtils.fileSeparator
			                                                                                          + "bugzilla_eclipse_overview.html")
			                                                                     .toURI(), null);
			assertTrue(parser.parseOverview());
			
			int counter = 0;
			final Set<String> bugIds = new HashSet<String>();
			for (final ReportLink reportLink : parser.getReportLinks()) {
				++counter;
				bugIds.add(reportLink.getBugId());
			}
			assertEquals(22, counter);
			assertTrue(bugIds.contains("314163"));
			assertTrue(bugIds.contains("158767"));
			
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			fail();
		}
	}
	
	/**
	 * Test mozilla.
	 */
	@Test
	public void testMozilla() {
		try {
			final BugzillaOverviewParser parser = new BugzillaOverviewParser(
			                                                                 new URI("https://bugzilla.mozilla.org/"),
			                                                                 this.getClass()
			                                                                     .getResource(FileUtils.fileSeparator
			                                                                                          + "bugzilla_mozilla_overview.html")
			                                                                     .toURI(), null);
			assertTrue(parser.parseOverview());
			
			int counter = 0;
			final Set<String> bugIds = new HashSet<String>();
			for (final ReportLink reportLink : parser.getReportLinks()) {
				++counter;
				bugIds.add(reportLink.getBugId());
			}
			assertEquals(1016, counter);
			assertTrue(bugIds.contains("642368"));
			assertTrue(bugIds.contains("61267"));
			
		} catch (final Exception e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			fail();
		}
	}
}

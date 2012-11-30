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
package org.mozkito.issues.tracker.model;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.Test;

import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.bugzilla.BugzillaTracker;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class ReportPersistence_NetTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
@DatabaseSettings (unit = "issues")
public class ReportPersistence_NetTest extends DatabaseTest {
	
	/**
	 * Test bugzilla.
	 */
	@Test
	public void testBugzilla() {
		try {
			final BugzillaTracker tracker = new BugzillaTracker();
			try {
				tracker.setup(new URI("https://bugzilla.mozilla.org/"), null, null,
				              this.getClass().getResource(FileUtils.fileSeparator + "bugzilla_eclipse_overview.html")
				                  .toURI(), "4.0.4", null);
			} catch (final InvalidParameterException e) {
				e.printStackTrace();
				fail();
			} catch (final URISyntaxException e) {
				e.printStackTrace();
				fail();
			}
			
			final ReportLink reportLink = new ReportLink(
			                                             new URI(
			                                                     "https://bugzilla.mozilla.org/show_bug.cgi?ctype=xml&id=444780"),
			                                             "444780");
			
			final Report report = tracker.parse(reportLink);
			// final Report report = new Report(1234l);
			getPersistenceUtil().beginTransaction();
			getPersistenceUtil().save(report);
			getPersistenceUtil().commitTransaction();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
}

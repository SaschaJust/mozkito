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
package org.mozkito.issues.tracker.jira;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.junit.Before;
import org.junit.Test;
import org.mozkito.issues.exceptions.InvalidParameterException;
import org.mozkito.issues.tracker.ReportLink;
import org.mozkito.issues.tracker.model.IssueTracker;

// TODO: Auto-generated Javadoc
/**
 * The Class JiraTracker_NetTest.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
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
		this.tracker = new JiraTracker(new IssueTracker());
		try {
			this.tracker.setup(new URI("http://jira.codehaus.org"), null, null, "XPR");
		} catch (final InvalidParameterException e) {
			e.printStackTrace();
			fail();
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Test overview.
	 */
	@Test
	public void testOverview() {
		
		final Set<ReportLink> reportLinks = this.tracker.getReportLinks();
		if (Logger.logInfo()) {
			Logger.info(String.valueOf(reportLinks.size()));
		}
		assertTrue(reportLinks.size() >= 462);
	}
}

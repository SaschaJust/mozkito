/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.bugs.tracker;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TrackerTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testRegex(){
		String url = "http://jira.codehaus.org/si/jira.issueviews:issue-xml/JAXEN-" + Tracker.bugIdPlaceholder
		+ "/JAXEN-" + Tracker.bugIdPlaceholder + ".xml";
		assertTrue(Tracker.bugIdRegex.matches(url));
		String result = Tracker.bugIdRegex.replaceAll(url, "210");
		assertEquals("http://jira.codehaus.org/si/jira.issueviews:issue-xml/JAXEN-210/JAXEN-210.xml", result);
	}
	
}

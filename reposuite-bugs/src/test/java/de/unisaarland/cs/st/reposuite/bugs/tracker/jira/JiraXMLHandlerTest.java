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
package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.ownhero.dev.ioda.DateTimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JiraXMLHandlerTest {
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDateFormat() {
		String dateString = "Fri, 5 Nov 2010 08:24:16 -0500";
		DateTime time = DateTimeUtils.parseDate(dateString, JiraXMLParser.dateTimeFormatRegex);
		assertTrue(time != null);
		assertEquals(DateTimeConstants.FRIDAY, time.getDayOfWeek());
		assertEquals(DateTimeConstants.NOVEMBER, time.getMonthOfYear());
		assertEquals("2010", time.year().getAsString());
		assertEquals(5, time.getDayOfMonth());
		assertEquals(13, time.getHourOfDay());
		assertEquals(24, time.getMinuteOfHour());
		assertEquals(16, time.getSecondOfMinute());
	}
}

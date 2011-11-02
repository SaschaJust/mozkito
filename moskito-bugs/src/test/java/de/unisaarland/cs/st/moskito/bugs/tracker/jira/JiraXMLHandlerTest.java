/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.jira;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.ownhero.dev.ioda.DateTimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.jira.JiraXMLParser;

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

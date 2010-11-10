package de.unisaarland.cs.st.reposuite.bugs.tracker.jira;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
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
		DateTime time = JiraXMLHandler.dateTimeFormat.parseDateTime(dateString);
		assertTrue(time != null);
		assertEquals(DateTimeConstants.FRIDAY, time.getDayOfWeek());
		assertEquals(DateTimeConstants.NOVEMBER, time.getMonthOfYear());
		assertEquals("2010", time.year().getAsString());
		assertEquals(5, time.getDayOfMonth());
		assertEquals(14, time.getHourOfDay());
		assertEquals(24, time.getMinuteOfHour());
		assertEquals(16, time.getSecondOfMinute());
		assertEquals(DateTimeZone.getDefault(), time.getZone());
	}
}

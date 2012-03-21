/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * The Class HistoryTest.
 *
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class HistoryTest {
	
	/**
	 * Sets the up before class.
	 *
	 * @throws Exception the exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	/**
	 * Tear down after class.
	 *
	 * @throws Exception the exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	/** The report. */
	private Report                  report;
	
	/** The formatter. */
	private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Sets the up.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setUp() throws Exception {
		this.report = new Report("1");
		this.report.setCreationTimestamp(this.formatter.parseDateTime("2010-01-10 13:23:12"));
		this.report.setComponent("Model");
		this.report.setDescription("Some default description");
		this.report.setLastFetch(new DateTime());
		this.report.setSummary("Some default summary");
		this.report.setStatus(Status.CLOSED);
		this.report.setSubmitter(new Person("just", "Sascha Just", "sascha.just@st.cs.uni-saarland.de"));
		this.report.addComment(new Comment(1, new Person("kim", "Kim Herzig", null),
		                                   this.formatter.parseDateTime("2010-01-12 09:35:11"), "Some default comment"));
		this.report.addComment(new Comment(2, new Person("just", "Sascha Just", null),
		                                   this.formatter.parseDateTime("2010-01-13 19:19:53"),
		                                   "Some default comment 2"));
		HistoryElement element = new HistoryElement(this.report.getId(), new Person("doe", "John Doe", "foo@bar.com"),
		                                            this.formatter.parseDateTime("2010-01-11 21:12:23"));
		element.addChangedValue("assignedTo", null, new Person("kim", "Kim Herzig", "herzig@cs.uni-saarland.de"));
		element.addChangedValue("priority", new Report("0").getPriority(), Priority.HIGH);
		element.addChangedValue("status", new Report("0").getStatus(), Status.NEW);
		this.report.addHistoryElement(element);
		
		element = new HistoryElement(this.report.getId(), new Person("kim", "Kim Herzig", null),
		                             this.formatter.parseDateTime("2010-01-15 01:59:26"));
		element.addChangedValue("resolution", new Report("0").getResolution(), Resolution.RESOLVED);
		element.addChangedValue("status", Status.NEW, Status.FEEDBACK);
		this.report.addHistoryElement(element);
	}
	
	/**
	 * Tear down.
	 *
	 * @throws Exception the exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for.
	 *
	 * {@link de.unisaarland.cs.st.moskito.bugs.tracker.model.History#rollback(de.unisaarland.cs.st.moskito.bugs.tracker.model.Report, org.joda.time.DateTime)}
	 * .
	 */
	@Test
	public final void testRollback() {
	}
	
	/**
	 * Test method for {@link Report#timewarp(org.joda.time.Interval, String)}
	 */
	@Test
	public final void testTimewarpInterval() {
		
	}
	
	/**
	 * Test method for {@link Report#timewarp(org.joda.time.DateTime)}
	 */
	@Test
	public final void testTimewarpTimestamp() {
		final Report warpedReport = this.report.timewarp(this.formatter.parseDateTime("2010-01-12 10:44:00"));
		assertEquals(1, warpedReport.getComments().size());
		assertEquals(Status.NEW, warpedReport.getStatus());
	}
	
}

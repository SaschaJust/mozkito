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
package org.mozkito.issues.model;

import static org.junit.Assert.assertEquals;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.model.Comment;
import org.mozkito.issues.model.HistoryElement;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.persistence.model.Person;

// TODO: Auto-generated Javadoc
/**
 * The Class HistoryTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class HistoryTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The report. */
	private Report                  report;
	
	/** The formatter. */
	private final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		final IssueTracker issueTracker = new IssueTracker();
		this.report = new Report(issueTracker, "1");
		this.report.setCreationTimestamp(this.formatter.parseDateTime("2010-01-10 13:23:12"));
		this.report.setComponent("Model");
		this.report.setDescription("Some default description");
		this.report.setLastFetch(new DateTime());
		this.report.setSummary("Some default summary");
		this.report.setStatus(Status.CLOSED);
		this.report.setSubmitter(new Person("just", "Sascha Just", "sascha.just@mozkito.org"));
		this.report.addComment(new Comment(1, new Person("kim", "Kim Herzig", null),
		                                   this.formatter.parseDateTime("2010-01-12 09:35:11"), "Some default comment"));
		this.report.addComment(new Comment(2, new Person("just", "Sascha Just", null),
		                                   this.formatter.parseDateTime("2010-01-13 19:19:53"),
		                                   "Some default comment 2"));
		HistoryElement element = new HistoryElement(this.report.getHistory(), new Person("doe", "John Doe",
		                                                                                 "foo@bar.com"),
		                                            this.formatter.parseDateTime("2010-01-11 21:12:23"));
		element.addChangedValue("assignedTo", new Person("", null, null), new Person("kim", "Kim Herzig",
		                                                                             "herzig@mozkito.org"));
		element.addChangedValue("priority", new Report(issueTracker, "0").getPriority(), Priority.HIGH);
		element.addChangedValue("status", new Report(issueTracker, "0").getStatus(), Status.NEW);
		
		element = new HistoryElement(this.report.getHistory(), new Person("kim", "Kim Herzig", null),
		                             this.formatter.parseDateTime("2010-01-15 01:59:26"));
		element.addChangedValue("resolution", new Report(issueTracker, "0").getResolution(), Resolution.RESOLVED);
		element.addChangedValue("status", Status.NEW, Status.FEEDBACK);
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

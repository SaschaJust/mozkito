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
package org.mozkito.persistence;

import org.joda.time.DateTime;
import org.junit.Test;

import org.mozkito.issues.tracker.elements.Status;
import org.mozkito.issues.tracker.model.Comment;
import org.mozkito.issues.tracker.model.HistoryElement;
import org.mozkito.issues.tracker.model.Report;
import org.mozkito.persistence.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class OpenJPA_NetTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@DatabaseSettings (unit = "issues")
public class OrphanPersonTest extends DatabaseTest {
	
	/**
	 * Test orphan person.
	 */
	@Test
	public void testOrphanPerson() {
		
		final PersistenceUtil persistenceUtil = getPersistenceUtil();
		
		final Person submitter = new Person("yokolet", "Yoko Harada", null);
		final Person historyAuthor1 = new Person("yokolet", null, null);
		final Person historyAuthor2 = new Person(null, "Yoko Harada", null);
		final Person commentAuthor2 = new Person("yokolet", null, null);
		
		final Report report = new Report("1");
		report.setSubmitter(submitter);
		
		HistoryElement element = new HistoryElement(report.getId(), historyAuthor1, new DateTime());
		element.addChangedValue("status", new Report("0").getStatus(), Status.ASSIGNED);
		report.addHistoryElement(element);
		
		element = new HistoryElement("1", historyAuthor2, new DateTime());
		element.addChangedValue("status", Status.ASSIGNED, Status.CLOSED);
		report.addHistoryElement(element);
		
		report.addComment(new Comment(2, commentAuthor2, new DateTime(), "comment2"));
		
		persistenceUtil.beginTransaction();
		persistenceUtil.save(report);
		persistenceUtil.commitTransaction();
	}
}

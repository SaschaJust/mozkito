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
package de.unisaarland.cs.st.moskito.persistence;

import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Comment;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class OpenJPA_NetTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OpenJPAUtil.createTestSessionFactory("bugs");
		
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		try {
			OpenJPAUtil.getInstance().shutdown();
		} catch (UninitializedDatabaseException e) {
			
		}
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOrphanPerson() {
		try {
			PersistenceUtil persistenceUtil = OpenJPAUtil.getInstance();
			
			Person submitter = new Person("yokolet", "Yoko Harada", null);
			Person historyAuthor1 = new Person("yokolet", null, null);
			Person historyAuthor2 = new Person(null, "Yoko Harada", null);
			Person commentAuthor2 = new Person("yokolet", null, null);
			
			Report report = new Report(1);
			report.setSubmitter(submitter);
			
			HistoryElement element = new HistoryElement(report.getId(), historyAuthor1, new DateTime());
			element.addChangedValue("status", new Report(0).getStatus(), Status.ASSIGNED);
			report.addHistoryElement(element);
			
			element = new HistoryElement(1, historyAuthor2, new DateTime());
			element.addChangedValue("status", Status.ASSIGNED, Status.CLOSED);
			report.addHistoryElement(element);
			
			report.addComment(new Comment(2, commentAuthor2, new DateTime(), "comment2"));
			
			persistenceUtil.beginTransaction();
			persistenceUtil.save(report);
			persistenceUtil.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}

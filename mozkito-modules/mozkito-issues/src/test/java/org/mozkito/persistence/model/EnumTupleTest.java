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

package org.mozkito.persistence.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mozkito.issues.model.HistoryElement;
import org.mozkito.issues.model.IssueTracker;
import org.mozkito.issues.model.Report;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.TestEnum;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class EnumTupleTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@DatabaseSettings (unit = "issues")
public class EnumTupleTest extends DatabaseTest {
	
	/**
	 * Test.
	 */
	@Test
	public final void test() {
		final PersistenceUtil util = getPersistenceUtil();
		final Report report = new Report(new IssueTracker(), "abcdefg");
		final HistoryElement element = new HistoryElement(report.getHistory(),
		                                                  new Person("me", "really me", "me@me.com"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		                                                  new DateTime());
		
		final EnumTuple tuple = new EnumTuple(TestEnum.ABC, TestEnum.GHI);
		element.addChangedValue("test", tuple.getOldValue(), tuple.getNewValue()); //$NON-NLS-1$
		assertEquals(TestEnum.class, tuple.getEnumClass());
		
		util.beginTransaction();
		try {
			util.save(element);
		} catch (final Throwable t) {
			fail(t.toString());
		}
		
		try {
			if (util.activeTransaction()) {
				util.commitTransaction();
			}
		} catch (final Throwable t) {
			fail(t.toString());
		}
		
		final Criteria<HistoryElement> criteria = util.createCriteria(HistoryElement.class);
		final List<HistoryElement> list = util.load(criteria);
		
		final HistoryElement loadedElement = list.iterator().next();
		
		assertFalse("List may not be empty and should contain the stored HistoryElement", list.isEmpty()); //$NON-NLS-1$
		assertEquals("Database must contain exactly one HistoryElement.", 1, list.size()); //$NON-NLS-1$
		
		final Map<String, EnumTuple> values = loadedElement.getChangedEnumValues();
		
		assertEquals("There has to be exactly one tuple in the changed enum values map.", 1, values.size()); //$NON-NLS-1$
		assertTrue("The tuple that has to be added as 'test' has to be in the map.", values.containsKey("test")); //$NON-NLS-1$ //$NON-NLS-2$
		
		final EnumTuple loadedTuple = values.get("test"); //$NON-NLS-1$
		
		assertNotNull("Loaded tuple must not be null.", loadedTuple); //$NON-NLS-1$
		assertEquals("Loaded tuple must equal saved one.", tuple, loadedTuple); //$NON-NLS-1$
		
		assertNotNull(loadedTuple.getEnumClass());
		assertEquals(tuple.getEnumClass(), loadedTuple.getEnumClass());
		assertEquals(TestEnum.class, loadedTuple.getEnumClass());
		
		assertNotNull(loadedTuple.getEnumClassName());
		assertEquals(tuple.getEnumClassName(), loadedTuple.getEnumClassName());
		
		assertNotNull(loadedTuple.getOldValue());
		assertEquals(tuple.getOldValue(), loadedTuple.getOldValue());
		
		assertNotNull(loadedTuple.getNewValue());
		assertEquals(tuple.getNewValue(), loadedTuple.getNewValue());
		
		assertNotNull(loadedTuple.getOldStringValue());
		assertEquals(tuple.getOldStringValue(), loadedTuple.getOldStringValue());
		
		assertNotNull(loadedTuple.getNewStringValue());
		assertEquals(tuple.getNewStringValue(), loadedTuple.getNewStringValue());
	}
}

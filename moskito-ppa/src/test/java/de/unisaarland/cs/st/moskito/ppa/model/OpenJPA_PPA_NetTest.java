/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.ppa.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class OpenJPA_PPA_NetTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings (unit = "ppa")
	public void test() {
		
		getPersistenceUtil().beginTransaction();
		JavaElementFactory elementFactory = new JavaElementFactory();
		final JavaElementLocationSet cache = new JavaElementLocationSet(elementFactory);
		final JavaElementLocation classDefinition = cache.addClassDefinition("a.A", "a.java", 0, 30, 123, 5);
		final DateTime now = new DateTime();
		
		final Person p = new Person("kim", "", "");
		final RCSTransaction transaction = RCSTransaction.createTransaction("1", "", now, p, "1", getPersistenceUtil());
		transaction.setBranch(BranchFactory.getMasterBranch(getPersistenceUtil()));
		final RCSFile file = new RCSFileManager().createFile("a.java", transaction);
		final RCSRevision rev = new RCSRevision(transaction, file, ChangeType.Added);
		final JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDefinition, rev);
		getPersistenceUtil().save(transaction);
		getPersistenceUtil().save(op);
		getPersistenceUtil().commitTransaction();
		getPersistenceUtil().beginTransaction();
		
		final Criteria<JavaChangeOperation> criteria = getPersistenceUtil().createCriteria(JavaChangeOperation.class);
		final List<JavaChangeOperation> list = getPersistenceUtil().load(criteria);
		assertEquals(1, list.size());
		getPersistenceUtil().commitTransaction();
	}
	
}

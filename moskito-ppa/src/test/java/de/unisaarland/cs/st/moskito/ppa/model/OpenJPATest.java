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
package de.unisaarland.cs.st.moskito.ppa.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.exceptions.UninitializedDatabaseException;
import de.unisaarland.cs.st.moskito.persistence.Criteria;
import de.unisaarland.cs.st.moskito.persistence.OpenJPAUtil;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementFactory;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocation;
import de.unisaarland.cs.st.moskito.ppa.model.JavaElementLocationSet;
import de.unisaarland.cs.st.moskito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.moskito.rcs.elements.RCSFileManager;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSRevision;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class OpenJPATest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		OpenJPAUtil.createTestSessionFactory("ppa");
		JavaElementFactory.init(OpenJPAUtil.getInstance());
	}
	
	@Test
	public void test() {
		try {
			PersistenceUtil persistenceUtil = OpenJPAUtil.getInstance();
			
			persistenceUtil.beginTransaction();
			JavaElementLocationSet cache = new JavaElementLocationSet();
			JavaElementLocation classDefinition = cache.addClassDefinition("a.A", "a.java", 0, 30, 123, 5);
			DateTime now = new DateTime();
			
			Person p = new Person("kim", "", "");
			RCSTransaction transaction = RCSTransaction.createTransaction("1", "", now, p, "1");
			transaction.setBranch(new RCSBranch("master"));
			RCSFile file = new RCSFileManager().createFile("a.java", transaction);
			RCSRevision rev = new RCSRevision(transaction, file, ChangeType.Added);
			JavaChangeOperation op = new JavaChangeOperation(ChangeType.Added, classDefinition, rev);
			persistenceUtil.save(transaction);
			persistenceUtil.save(op);
			persistenceUtil.commitTransaction();
			persistenceUtil.beginTransaction();
			
			Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class);
			List<JavaChangeOperation> list = persistenceUtil.load(criteria);
			assertEquals(1, list.size());
		} catch (UninitializedDatabaseException e) {
			e.printStackTrace();
			fail();
		}
		
	}
	
}

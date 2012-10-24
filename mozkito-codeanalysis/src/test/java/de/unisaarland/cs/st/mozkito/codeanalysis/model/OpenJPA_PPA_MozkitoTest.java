/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.mozkito.persistence.Criteria;
import de.unisaarland.cs.st.mozkito.persistence.model.Person;
import de.unisaarland.cs.st.mozkito.testing.MoskitoTest;
import de.unisaarland.cs.st.mozkito.testing.annotation.DatabaseSettings;
import de.unisaarland.cs.st.mozkito.versions.BranchFactory;
import de.unisaarland.cs.st.mozkito.versions.elements.ChangeType;
import de.unisaarland.cs.st.mozkito.versions.elements.RCSFileManager;
import de.unisaarland.cs.st.mozkito.versions.model.RCSBranch;
import de.unisaarland.cs.st.mozkito.versions.model.RCSFile;
import de.unisaarland.cs.st.mozkito.versions.model.RCSRevision;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

public class OpenJPA_PPA_MozkitoTest extends MoskitoTest {
	
	@Test
	@DatabaseSettings (unit = "codeanalysis")
	public void test() {
		
		getPersistenceUtil().beginTransaction();
		final JavaElementFactory elementFactory = new JavaElementFactory();
		final JavaElementLocationSet cache = new JavaElementLocationSet(elementFactory);
		final JavaElementLocation classDefinition = cache.addClassDefinition("a.A", "a.java", 0, 30, 123, 5);
		final DateTime now = new DateTime();
		
		final Person p = new Person("kim", "", "");
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		final RCSBranch masterBranch = branchFactory.getMasterBranch();
		
		final RCSTransaction transaction = RCSTransaction.createTransaction("1", "", now, p, "1");
		masterBranch.setHead(transaction);
		
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
	
	@Test
	@DatabaseSettings (unit = "codeanalysis")
	public void testTMP() {
		final JavaMethodDefinition def = new JavaMethodDefinition("parenName", "methodName", new ArrayList<String>(0),
		                                                          false);
		getPersistenceUtil().beginTransaction();
		getPersistenceUtil().saveOrUpdate(def);
		final long generatedId = def.getGeneratedId();
		getPersistenceUtil().commitTransaction();
		final JavaElement elem = getPersistenceUtil().loadById(generatedId, JavaElement.class);
		assert (elem != null);
		final JavaMethodDefinition dbdef = (JavaMethodDefinition) elem;
		assertEquals(def.getFullQualifiedName(), dbdef.getFullQualifiedName());
	}
	
}

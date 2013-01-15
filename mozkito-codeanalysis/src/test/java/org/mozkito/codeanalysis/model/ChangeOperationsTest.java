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
package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.RevDependencyGraph;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class ChangeOperationsTest.
 */
public class ChangeOperationsTest {
	
	/**
	 * Test.
	 * 
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		
		final RevDependencyGraph revDependencyGraph = new RevDependencyGraph();
		revDependencyGraph.addBranch(Branch.MASTER_BRANCH_NAME, "hash");
		final VersionArchive versionArchive = new VersionArchive(revDependencyGraph);
		final ChangeSet changeSet = new ChangeSet(versionArchive, "hash", "hubba hubba hopp!", new DateTime(),
		                                          new Person("kim", null, null), "143");
		
		final JavaTypeDefinition javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		
		final JavaElementFactory elementFactory = new JavaElementFactory();
		final JavaElementLocationSet set = new JavaElementLocationSet(elementFactory);
		final JavaElementLocation anonymousClassLocation = set.addAnonymousClassDefinition(javaType,
		                                                                                   "org.mozkito.codeanalysis.model.TestClass$1",
		                                                                                   "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                                   20, 23, 43674, 20);
		final JavaElementLocation classLocation = set.addClassDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                 "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                 10, 200, 463, 11);
		
		final JavaElementLocation methodDefinitionLocation = set.addMethodDefinition("org.mozkito.codeanalysis.model.TestClass",
		                                                                             "test",
		                                                                             new ArrayList<String>(0),
		                                                                             "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                             34, 56, 7854, 34, true);
		
		final JavaElementLocation methodCallLocation = set.addMethodCall("org.mozkito.codeanalysis.model.TestClass",
		                                                                 "test",
		                                                                 new ArrayList<String>(0),
		                                                                 "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                 methodDefinitionLocation.getElement(), 34, 56,
		                                                                 7854);
		
		final Handle handle = new Handle(versionArchive);
		handle.assignRevision(new Revision(changeSet, handle, ChangeType.Added),
		                      "org/mozkito/codeanalysis/model/TestClass.java");
		
		final JavaChangeOperation addAnonClassDefOp = new JavaChangeOperation(ChangeType.Added, anonymousClassLocation,
		                                                                      new Revision(changeSet, handle,
		                                                                                   ChangeType.Added));
		final JavaChangeOperation delClassDefOp = new JavaChangeOperation(ChangeType.Deleted, classLocation,
		                                                                  new Revision(changeSet, handle,
		                                                                               ChangeType.Deleted));
		final JavaChangeOperation addClassDefOp = new JavaChangeOperation(ChangeType.Added, classLocation,
		                                                                  new Revision(changeSet, handle,
		                                                                               ChangeType.Added));
		
		final JavaChangeOperation delCallOp = new JavaChangeOperation(ChangeType.Deleted, methodCallLocation,
		                                                              new Revision(changeSet, handle,
		                                                                           ChangeType.Deleted));
		
		final JavaChangeOperation addCallOp = new JavaChangeOperation(ChangeType.Added, methodCallLocation,
		                                                              new Revision(changeSet, handle, ChangeType.Added));
		
		final ChangeOperations ops = new ChangeOperations();
		assertTrue(ops.add(addAnonClassDefOp));
		assertTrue(ops.add(delClassDefOp));
		Collection<JavaChangeOperation> operations = ops.getOperations();
		assertTrue(operations.contains(delClassDefOp));
		assertTrue(ops.add(addClassDefOp));
		assertTrue(ops.add(delCallOp));
		assertTrue(ops.add(addCallOp));
		assertFalse(ops.add(addCallOp));
		
		operations = ops.getOperations();
		assertTrue(operations.contains(addAnonClassDefOp));
		assertFalse(operations.contains(delClassDefOp));
		assertTrue(operations.contains(addClassDefOp));
		assertTrue(operations.contains(delCallOp));
		assertTrue(operations.contains(addCallOp));
	}
}

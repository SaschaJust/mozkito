package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.time.DateTime;
import org.junit.Test;

import org.mozkito.persistence.model.Person;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSRevision;
import org.mozkito.versions.model.RCSTransaction;

public class ChangeOperationsTest {
	
	@Test
	public void test() {
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
		final RCSTransaction rCSTransaction = new RCSTransaction("hash", "hubba hubba hopp!", new DateTime(),
		                                                         new Person("kim", null, null), "143");
		final RCSFile rcsFile = new RCSFile("org/mozkito/codeanalysis/model/TestClass.java", rCSTransaction);
		final JavaChangeOperation addAnonClassDefOp = new JavaChangeOperation(ChangeType.Added, anonymousClassLocation,
		                                                                      new RCSRevision(rCSTransaction, rcsFile,
		                                                                                      ChangeType.Added));
		final JavaChangeOperation delClassDefOp = new JavaChangeOperation(ChangeType.Deleted, classLocation,
		                                                                  new RCSRevision(rCSTransaction, rcsFile,
		                                                                                  ChangeType.Deleted));
		final JavaChangeOperation addClassDefOp = new JavaChangeOperation(ChangeType.Added, classLocation,
		                                                                  new RCSRevision(rCSTransaction, rcsFile,
		                                                                                  ChangeType.Added));
		
		final JavaChangeOperation delCallOp = new JavaChangeOperation(ChangeType.Deleted, methodCallLocation,
		                                                              new RCSRevision(rCSTransaction, rcsFile,
		                                                                              ChangeType.Deleted));
		
		final JavaChangeOperation addCallOp = new JavaChangeOperation(ChangeType.Added, methodCallLocation,
		                                                              new RCSRevision(rCSTransaction, rcsFile,
		                                                                              ChangeType.Added));
		
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

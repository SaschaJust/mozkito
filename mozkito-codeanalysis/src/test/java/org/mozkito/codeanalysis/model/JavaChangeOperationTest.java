package org.mozkito.codeanalysis.model;

import static org.junit.Assert.assertEquals;

import org.jdom2.Element;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.persistence.ModelStorage;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.File;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.Transaction;

public class JavaChangeOperationTest {
	
	private Transaction         transaction;
	private JavaElementLocation    anonymousClassLocation;
	private JavaElementLocationSet set;
	private File                rcsFile;
	private JavaChangeOperation    op;
	private JavaElementFactory     elementFactory;
	
	@Before
	public void before() {
		final JavaTypeDefinition javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		
		this.elementFactory = new JavaElementFactory();
		this.set = new JavaElementLocationSet(this.elementFactory);
		this.anonymousClassLocation = this.set.addAnonymousClassDefinition(javaType,
		                                                                   "org.mozkito.codeanalysis.model.TestClass$1",
		                                                                   "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                   20, 23, 43674, 20);
		this.transaction = new Transaction("hash", "hubba hubba hopp!", new DateTime(),
		                                      new Person("kim", null, null), "143");
		this.rcsFile = new File("org/mozkito/codeanalysis/model/TestClass.java", this.transaction);
		this.op = new JavaChangeOperation(ChangeType.Added, this.anonymousClassLocation,
		                                  new Revision(this.transaction, this.rcsFile, ChangeType.Added));
	}
	
	@Test
	public void testToString() {
		assertEquals("0: Added <path = org/mozkito/codeanalysis/model/TestClass.java, element: org.mozkito.codeanalysis.model.TestClass$1, transaction: hash>",
		             this.op.toString());
	}
	
	@Test
	public void testXML() {
		final Element xmlOp = this.op.getXMLRepresentation();
		assertEquals(this.op,
		             JavaChangeOperation.fromXMLRepresentation(xmlOp, new ModelStorage<String, Transaction>() {
			             
			             @Override
			             public Transaction getById(final String id) {
				             return JavaChangeOperationTest.this.transaction;
			             }
		             }, this.elementFactory));
	}
	
}

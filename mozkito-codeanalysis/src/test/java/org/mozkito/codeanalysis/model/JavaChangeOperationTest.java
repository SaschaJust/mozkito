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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.jdom2.Element;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.persistence.ModelStorage;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;
import org.mozkito.versions.model.Revision;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class JavaChangeOperationTest.
 */
public class JavaChangeOperationTest {
	
	/** The r cs transaction. */
	private ChangeSet              changeSet;
	
	/** The anonymous class location. */
	private JavaElementLocation    anonymousClassLocation;
	
	/** The set. */
	private JavaElementLocationSet set;
	
	/** The rcs file. */
	private Handle                 handle;
	
	/** The op. */
	private JavaChangeOperation    op;
	
	/** The element factory. */
	private JavaElementFactory     elementFactory;
	
	/**
	 * Before.
	 * 
	 * @throws IOException
	 */
	@Before
	public void before() throws IOException {
		final JavaTypeDefinition javaType = new JavaTypeDefinition("org.mozkito.codeanalysis.model.TestClass");
		
		this.elementFactory = new JavaElementFactory();
		this.set = new JavaElementLocationSet(this.elementFactory);
		this.anonymousClassLocation = this.set.addAnonymousClassDefinition(javaType,
		                                                                   "org.mozkito.codeanalysis.model.TestClass$1",
		                                                                   "org/mozkito/codeanalysis/model/TestClass.java",
		                                                                   20, 23, 43674, 20);
		
		final BranchFactory branchFactory = new BranchFactory(null);
		final RevDependencyGraph revDependencyGraph = new RevDependencyGraph();
		revDependencyGraph.addBranch(branchFactory.getMasterBranch().getName(), "hash");
		
		final VersionArchive versionArchive = new VersionArchive(branchFactory, revDependencyGraph);
		
		this.changeSet = new ChangeSet(versionArchive, "hash", "hubba hubba hopp!", new DateTime(), new Person("kim",
		                                                                                                       null,
		                                                                                                       null),
		                               "143");
		
		this.handle = new Handle(versionArchive);
		this.handle.assignRevision(new Revision(this.changeSet, this.handle, ChangeType.Modified),
		                           "org/mozkito/codeanalysis/model/TestClass.java");
		this.op = new JavaChangeOperation(ChangeType.Added, this.anonymousClassLocation, new Revision(this.changeSet,
		                                                                                              this.handle,
		                                                                                              ChangeType.Added));
		
	}
	
	/**
	 * Test to string.
	 */
	@Test
	public void testToString() {
		assertEquals("0: Added <path = org/mozkito/codeanalysis/model/TestClass.java, element: org.mozkito.codeanalysis.model.TestClass$1, transaction: hash>",
		             this.op.toString());
	}
	
	/**
	 * Test xml.
	 */
	@Test
	public void testXML() {
		final Element xmlOp = this.op.getXMLRepresentation();
		assertEquals(this.op, JavaChangeOperation.fromXMLRepresentation(xmlOp, new ModelStorage<String, ChangeSet>() {
			
			@Override
			public ChangeSet getById(final String id) {
				return JavaChangeOperationTest.this.changeSet;
			}
		}, this.elementFactory));
	}
	
}

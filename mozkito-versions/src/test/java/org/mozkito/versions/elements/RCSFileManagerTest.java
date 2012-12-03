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

package org.mozkito.versions.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class RCSFileManagerTest.
 */
public class RCSFileManagerTest {
	
	static {
		KanuniAgent.initialize();
	}
	
	/** The file manager. */
	private RCSFileManager fileManager;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		this.fileManager = new RCSFileManager();
	}
	
	/**
	 * Test add file.
	 */
	@Test
	public void testAddFile() {
		final String fileName = "a/b/c/d.java";
		final RCSFile rcsFile = new RCSFile(fileName, new RCSTransaction("0", "hhfhdsjkfh", new DateTime(),
		                                                                 new Person("kim", null, null), null));
		this.fileManager.addFile(rcsFile);
		assertEquals(rcsFile, this.fileManager.getFile(fileName));
	}
	
	/**
	 * Test create file.
	 */
	@Test
	public void testCreateFile() {
		final String fileName = "a/b/c/d.java";
		final RCSTransaction transaction = new RCSTransaction("0", "hhfhdsjkfh", new DateTime(),
		                                                      new Person("kim", null, null), null);
		final RCSFile rcsFile = new RCSFile(fileName, transaction);
		assertEquals(rcsFile, this.fileManager.createFile(fileName, transaction));
	}
	
	/**
	 * Test get file.
	 */
	@Test
	public void testGetFile() {
		assertTrue(this.fileManager.getFile("a.java") == null);
	}
	
}

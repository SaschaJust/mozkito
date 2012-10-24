/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package de.unisaarland.cs.st.mozkito.versions.model;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.mozkito.persistence.model.Person;
import de.unisaarland.cs.st.mozkito.versions.elements.ChangeType;
import de.unisaarland.cs.st.mozkito.versions.model.RCSFile;
import de.unisaarland.cs.st.mozkito.versions.model.RCSRevision;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class RCSFileTest {
	
	@Test
	public void testHiddenFileName() {
		/*
	 * @formatter:off
	 * 
	 *    *   5
     *    |\
     *    | *   4
     *    | |\
     *    | | * 3 <<---- Adding file Hidden.java
     *    | |/
     *    | * 2
     *    * | 1
     *    |/
     *    * 0
	 * 
	 */
	
	//@formatter:on
		
		final Person person = new Person("kim", "", "");
		
		final RCSTransaction t_0 = new RCSTransaction("0", "", new DateTime(), person, "");
		final RCSFile file = new RCSFile("public.java", t_0);
		new RCSRevision(t_0, file, ChangeType.Added);
		
		final RCSTransaction t_1 = new RCSTransaction("1", "", new DateTime(), person, "");
		new RCSRevision(t_1, file, ChangeType.Modified);
		t_1.setBranchParent(t_0);
		
		final RCSTransaction t_2 = new RCSTransaction("2", "", new DateTime(), person, "");
		new RCSRevision(t_2, file, ChangeType.Modified);
		t_1.setBranchParent(t_0);
		
		final RCSTransaction t_3 = new RCSTransaction("3", "", new DateTime(), person, "");
		final RCSFile hiddenFile = new RCSFile("hidden.java", t_3);
		new RCSRevision(t_3, hiddenFile, ChangeType.Added);
		t_1.setBranchParent(t_2);
		
		final RCSTransaction t_4 = new RCSTransaction("4", "", new DateTime(), person, "");
		new RCSRevision(t_4, hiddenFile, ChangeType.Modified);
		t_1.setBranchParent(t_2);
		t_1.setMergeParent(t_3);
		
		final RCSTransaction t_5 = new RCSTransaction("5", "", new DateTime(), person, "");
		new RCSRevision(t_5, file, ChangeType.Modified);
		t_1.setBranchParent(t_1);
		t_1.setMergeParent(t_4);
		
		assertEquals("hidden.java", hiddenFile.getPath(t_5));
		
	}
	
	@Test
	public void testHiddenFileName2() {
		/*
	 * @formatter:off
	 * 
	 *    *   5
     *    |\
     *    | *   4
     *    | |\
     *    | | * 3 <<---- Renaming file Hidden.java to moreHidden.java
     *    | |/
     *    | * 2 <<---- Adding file Hidden.java
     *    * | 1
     *    |/
     *    * 0
	 * 
	 */
	
	//@formatter:on
		
		final Person person = new Person("kim", "", "");
		
		final RCSTransaction t_0 = new RCSTransaction("0", "", new DateTime(), person, "");
		final RCSFile file = new RCSFile("public.java", t_0);
		new RCSRevision(t_0, file, ChangeType.Added);
		
		final RCSTransaction t_1 = new RCSTransaction("1", "", new DateTime(), person, "");
		new RCSRevision(t_1, file, ChangeType.Modified);
		t_1.setBranchParent(t_0);
		
		final RCSTransaction t_2 = new RCSTransaction("2", "", new DateTime(), person, "");
		final RCSFile hiddenFile = new RCSFile("hidden.java", t_2);
		new RCSRevision(t_2, hiddenFile, ChangeType.Added);
		t_1.setBranchParent(t_0);
		
		final RCSTransaction t_3 = new RCSTransaction("3", "", new DateTime(), person, "");
		hiddenFile.assignTransaction(t_3, "moreHidden.java");
		new RCSRevision(t_3, hiddenFile, ChangeType.Renamed);
		t_1.setBranchParent(t_2);
		
		final RCSTransaction t_4 = new RCSTransaction("4", "", new DateTime(), person, "");
		new RCSRevision(t_4, hiddenFile, ChangeType.Modified);
		t_1.setBranchParent(t_2);
		t_1.setMergeParent(t_3);
		
		final RCSTransaction t_5 = new RCSTransaction("5", "", new DateTime(), person, "");
		new RCSRevision(t_5, file, ChangeType.Modified);
		t_1.setBranchParent(t_1);
		t_1.setMergeParent(t_4);
		
		assertEquals("moreHidden.java", hiddenFile.getPath(t_5));
		
	}
}

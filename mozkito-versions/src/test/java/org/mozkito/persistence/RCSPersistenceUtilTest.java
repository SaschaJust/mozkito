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

package org.mozkito.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mozkito.persistence.model.Person;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.collections.ChangeSetSet.TransactionSetOrder;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class RCSPersistenceUtilTest.
 */
@DatabaseSettings (unit = "versions", options = ConnectOptions.DROP_AND_CREATE_DATABASE)
public class RCSPersistenceUtilTest extends DatabaseTest {
	
	/** The t_280b1b. */
	private ChangeSet t_280b1b;
	
	/** The t_702abf. */
	private ChangeSet t_702abf;
	
	/** The t_cce07f. */
	private ChangeSet t_cce07f;
	
	/** The t_94f8b9. */
	private ChangeSet t_94f8b9;
	
	/** The t_5813ab. */
	private ChangeSet t_5813ab;
	
	/** The t_8bc067. */
	private ChangeSet t_8bc067;
	
	/** The t_9f6f10. */
	private ChangeSet t_9f6f10;
	
	/** The t_6bfee3. */
	private ChangeSet t_6bfee3;
	
	/** The t_45702d. */
	private ChangeSet t_45702d;
	
	/** The t_9c7c6d. */
	private ChangeSet t_9c7c6d;
	
	/** The t_d52295. */
	private ChangeSet t_d52295;
	
	/**
	 * Setup.
	 * 
	 * @param persistenceUtil
	 *            the persistence util
	 * @return the list
	 */
	private List<ChangeSet> setup(final PersistenceUtil persistenceUtil) {
		/*
		 * @formatter:off
		 * 
		 *    *   280b1b8695286699770c5da85204e1718f7f4b66
         *    |\
         *    | * 702abfed3f8ca043b2636efd31c14ba7552603dd
         *    | *   cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2
         *    | |\
         *    | | * 94f8b9f16e9f3d423225b28619281a5ecf877275
         *    | * | 5813ab7d15c9c97ff45a44e051f8e9776a1f7e42
         *    | * | 8bc0679ca73760e68c0c27b54dc2855de34c1bdb
         *    | |/
         *    | *   9f6f106cdc16effd8c093defd47f1626195d03db
         *    | |\
         *    | | * 6bfee30b10fb0498f3d70f383814a669939bb1c7
         *    | |/
         *    | * 45702d2a094554789dc51bd23869ed5ddd8822a6
         *    * | 9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59
         *    |/
         *    * d522956171853fc2d7ca106d9c8d2b93e82df9d3
		 * 
		 */
		
		
		
		//@formatter:on
		
		final DateTime now = new DateTime();
		final Person person = new Person("kim", null, null);
		final BranchFactory branchFactory = new BranchFactory(persistenceUtil);
		final Branch rCSBranch = branchFactory.getMasterBranch();
		long index = 0;
		
		final Branch otherBranch = branchFactory.getBranch("otherBranch");
		final ChangeSet otherT = new ChangeSet("280b1b8695286699770c5da85204e1718fXXXXXX", "", now, person,
		                                                 null);
		otherT.addBranch(otherBranch, 0l);
		otherBranch.setHead(otherT);
		final ChangeSet otherT2 = new ChangeSet("702abfed3f8ca043b2636efd31c14ba75XXXXXX", "", now, person,
		                                                  null);
		otherT2.addBranch(otherBranch, -1l);
		
		final ChangeSet otherT3 = new ChangeSet("cce07fdcb9f3a0efcd67c75de60d5608c6XXXXXX", "", now, person,
		                                                  null);
		otherT3.addBranch(otherBranch, -1l);
		
		persistenceUtil.beginTransaction();
		persistenceUtil.saveOrUpdate(otherBranch);
		persistenceUtil.saveOrUpdate(otherT);
		persistenceUtil.saveOrUpdate(otherT2);
		persistenceUtil.saveOrUpdate(otherT3);
		
		final LinkedList<ChangeSet> tList = new LinkedList<ChangeSet>();
		
		this.t_280b1b = new ChangeSet("280b1b8695286699770c5da85204e1718f7f4b66", "", now, person, null);
		rCSBranch.setHead(this.t_280b1b);
		this.t_280b1b.addBranch(rCSBranch, index);
		tList.add(this.t_280b1b);
		
		this.t_702abf = new ChangeSet("702abfed3f8ca043b2636efd31c14ba7552603dd", "", now, person, null);
		this.t_702abf.addBranch(rCSBranch, --index);
		tList.add(this.t_702abf);
		
		this.t_cce07f = new ChangeSet("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2", "", now, person, null);
		this.t_cce07f.addBranch(rCSBranch, --index);
		tList.add(this.t_cce07f);
		
		this.t_94f8b9 = new ChangeSet("94f8b9f16e9f3d423225b28619281a5ecf877275", "", now, person, null);
		this.t_94f8b9.addBranch(rCSBranch, --index);
		tList.add(this.t_94f8b9);
		
		this.t_5813ab = new ChangeSet("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42", "", now, person, null);
		this.t_5813ab.addBranch(rCSBranch, --index);
		tList.add(this.t_5813ab);
		
		this.t_8bc067 = new ChangeSet("8bc0679ca73760e68c0c27b54dc2855de34c1bdb", "", now, person, null);
		this.t_8bc067.addBranch(rCSBranch, --index);
		tList.add(this.t_8bc067);
		
		this.t_9f6f10 = new ChangeSet("9f6f106cdc16effd8c093defd47f1626195d03db", "", now, person, null);
		this.t_9f6f10.addBranch(rCSBranch, --index);
		tList.add(this.t_9f6f10);
		
		this.t_6bfee3 = new ChangeSet("6bfee30b10fb0498f3d70f383814a669939bb1c7", "", now, person, null);
		this.t_6bfee3.addBranch(rCSBranch, --index);
		tList.add(this.t_6bfee3);
		
		this.t_45702d = new ChangeSet("45702d2a094554789dc51bd23869ed5ddd8822a6", "", now, person, null);
		this.t_45702d.addBranch(rCSBranch, --index);
		tList.add(this.t_45702d);
		
		this.t_9c7c6d = new ChangeSet("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59", "", now, person, null);
		this.t_9c7c6d.addBranch(rCSBranch, --index);
		tList.add(this.t_9c7c6d);
		
		this.t_d52295 = new ChangeSet("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "", now, person, null);
		this.t_d52295.addBranch(rCSBranch, --index);
		tList.add(this.t_d52295);
		
		for (final ChangeSet t : tList) {
			persistenceUtil.saveOrUpdate(t);
		}
		persistenceUtil.saveOrUpdate(rCSBranch);
		persistenceUtil.commitTransaction();
		return tList;
	}
	
	/**
	 * Test branch asc.
	 */
	@Test
	public void testBranchASC() {
		
		final List<ChangeSet> tList = setup(getPersistenceUtil());
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		
		final Iterator<ChangeSet> iterator = RCSPersistenceUtil.getTransactions(getPersistenceUtil(),
		                                                                             branchFactory.getMasterBranch(),
		                                                                             TransactionSetOrder.ASC)
		                                                            .iterator();
		int gindex = tList.size();
		
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(--gindex), iterator.next());
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * Test branch desc.
	 */
	@Test
	public void testBranchDESC() {
		
		final List<ChangeSet> tList = setup(getPersistenceUtil());
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		
		final Iterator<ChangeSet> iterator = RCSPersistenceUtil.getTransactions(getPersistenceUtil(),
		                                                                             branchFactory.getMasterBranch(),
		                                                                             TransactionSetOrder.DESC)
		                                                            .iterator();
		int gindex = -1;
		
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(tList.get(++gindex), iterator.next());
		assertFalse(iterator.hasNext());
		
	}
	
}

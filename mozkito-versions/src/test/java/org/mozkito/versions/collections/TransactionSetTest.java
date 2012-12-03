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

package org.mozkito.versions.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import org.mozkito.persistence.model.Person;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import org.mozkito.versions.model.RCSBranch;
import org.mozkito.versions.model.RCSTransaction;

// TODO: Auto-generated Javadoc
/**
 * The Class TransactionSetTest.
 */
public class TransactionSetTest {
	
	/** The t list. */
	private List<RCSTransaction> tList;
	
	/** The other t. */
	private RCSTransaction       otherT;
	
	/** The other branch. */
	private RCSBranch            otherBranch;
	
	/** The person. */
	private Person               person;
	
	/** The index. */
	private long                 index;
	
	/** The branch. */
	private RCSBranch            branch;
	
	/**
	 * Setup.
	 */
	@Before
	public void setup() {
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
		this.person = new Person("kim", null, null);
		final BranchFactory branchFactory = new BranchFactory(null);
		this.branch = branchFactory.getMasterBranch();
		this.index = 0;
		
		this.otherBranch = branchFactory.getBranch("otherBranch");
		this.otherT = new RCSTransaction("280b1b8695286699770c5da85204e1718fXXXXXX", "", now, this.person, null);
		this.otherT.addBranch(this.otherBranch, 0l);
		this.otherBranch.setHead(this.otherT);
		
		this.tList = new LinkedList<RCSTransaction>();
		
		final RCSTransaction t_280b1b = new RCSTransaction("280b1b8695286699770c5da85204e1718f7f4b66", "", now,
		                                                   this.person, null);
		this.branch.setHead(t_280b1b);
		t_280b1b.addBranch(this.branch, this.index);
		this.tList.add(t_280b1b);
		
		final RCSTransaction t_702abf = new RCSTransaction("702abfed3f8ca043b2636efd31c14ba7552603dd", "", now,
		                                                   this.person, null);
		t_702abf.addBranch(this.branch, --this.index);
		this.tList.add(t_702abf);
		
		final RCSTransaction t_cce07f = new RCSTransaction("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2", "", now,
		                                                   this.person, null);
		t_cce07f.addBranch(this.branch, --this.index);
		this.tList.add(t_cce07f);
		
		final RCSTransaction t_94f8b9 = new RCSTransaction("94f8b9f16e9f3d423225b28619281a5ecf877275", "", now,
		                                                   this.person, null);
		t_94f8b9.addBranch(this.branch, --this.index);
		this.tList.add(t_94f8b9);
		
		final RCSTransaction t_5813ab = new RCSTransaction("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42", "", now,
		                                                   this.person, null);
		t_5813ab.addBranch(this.branch, --this.index);
		this.tList.add(t_5813ab);
		
		final RCSTransaction t_8bc067 = new RCSTransaction("8bc0679ca73760e68c0c27b54dc2855de34c1bdb", "", now,
		                                                   this.person, null);
		t_8bc067.addBranch(this.branch, --this.index);
		this.tList.add(t_8bc067);
		
		final RCSTransaction t_9f6f10 = new RCSTransaction("9f6f106cdc16effd8c093defd47f1626195d03db", "", now,
		                                                   this.person, null);
		t_9f6f10.addBranch(this.branch, --this.index);
		this.tList.add(t_9f6f10);
		
		final RCSTransaction t_6bfee3 = new RCSTransaction("6bfee30b10fb0498f3d70f383814a669939bb1c7", "", now,
		                                                   this.person, null);
		t_6bfee3.addBranch(this.branch, --this.index);
		this.tList.add(t_6bfee3);
		
		final RCSTransaction t_45702d = new RCSTransaction("45702d2a094554789dc51bd23869ed5ddd8822a6", "", now,
		                                                   this.person, null);
		t_45702d.addBranch(this.branch, --this.index);
		this.tList.add(t_45702d);
		
		final RCSTransaction t_9c7c6d = new RCSTransaction("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59", "", now,
		                                                   this.person, null);
		t_9c7c6d.addBranch(this.branch, --this.index);
		this.tList.add(t_9c7c6d);
		
		final RCSTransaction t_d52295 = new RCSTransaction("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "", now,
		                                                   this.person, null);
		t_d52295.addBranch(this.branch, --this.index);
		this.tList.add(t_d52295);
	}
	
	/**
	 * Test add fail.
	 */
	@Test
	public void testAddFail() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.add(this.tList.get(0));
		try {
			tSet.add(this.otherT);
			fail();
		} catch (final IllegalArgumentException ignore) {
			// ignore
		}
	}
	
	/**
	 * Test asc.
	 */
	@Test
	public void testAsc() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.ASC);
		tSet.addAll(this.tList);
		
		final Iterator<RCSTransaction> iterator = tSet.iterator();
		int index = this.tList.size();
		
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(--index), iterator.next());
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * Test clear.
	 */
	@Test
	public void testClear() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		tSet.clear();
		assertTrue(tSet.isEmpty());
	}
	
	/**
	 * Test contains.
	 */
	@Test
	public void testContains() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		for (final RCSTransaction t : this.tList) {
			assertTrue(tSet.contains(t));
		}
	}
	
	/**
	 * Test contains all.
	 */
	@Test
	public void testContainsAll() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		assertTrue(tSet.containsAll(this.tList));
		this.tList.remove(2);
		assertTrue(tSet.containsAll(this.tList));
		final RCSTransaction infinityTransaction = new RCSTransaction("inifinity", "none", new DateTime(), this.person,
		                                                              "");
		infinityTransaction.addBranch(this.branch, --this.index);
		this.tList.add(infinityTransaction);
		assertFalse(tSet.containsAll(this.tList));
	}
	
	/**
	 * Test desc.
	 */
	@Test
	public void testDesc() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		
		final Iterator<RCSTransaction> iterator = tSet.iterator();
		int index = -1;
		
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.tList.get(++index), iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	/**
	 * Test first.
	 */
	@Test
	public void testFirst() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		assertEquals(this.tList.get(0), tSet.first());
	}
	
	/**
	 * Test head tail set.
	 */
	@Test
	public void testHeadTailSet() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		final SortedSet<RCSTransaction> headSet = tSet.headSet(this.tList.get(6));
		final SortedSet<RCSTransaction> tailSet = tSet.tailSet(this.tList.get(6));
		assertEquals(6, headSet.size());
		assertTrue(headSet.containsAll(this.tList.subList(0, 6)));
		assertEquals(this.tList.size() - 6, tailSet.size());
		assertTrue(tailSet.containsAll(this.tList.subList(6, this.tList.size())));
	}
	
	/**
	 * Test last.
	 */
	@Test
	public void testLast() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		assertEquals(this.tList.get(this.tList.size() - 1), tSet.last());
	}
	
	/**
	 * Test remove.
	 */
	@Test
	public void testRemove() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		final RCSTransaction t7 = this.tList.get(7);
		assertTrue(tSet.remove(t7));
		final RCSTransaction infinityTransaction = new RCSTransaction("inifinity", "none", new DateTime(), this.person,
		                                                              "");
		infinityTransaction.addBranch(this.branch, --this.index);
		assertFalse(tSet.remove(infinityTransaction));
		assertEquals(this.tList.size() - 1, tSet.size());
		assertFalse(tSet.contains(t7));
		assertFalse(tSet.contains(infinityTransaction));
	}
	
	/**
	 * Test remove all.
	 */
	@Test
	public void testRemoveAll() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		assertTrue(tSet.removeAll(this.tList));
		assertTrue(tSet.isEmpty());
		
		tSet.addAll(this.tList);
		final RCSTransaction infinityTransaction = new RCSTransaction("inifinity", "none", new DateTime(), this.person,
		                                                              "");
		infinityTransaction.addBranch(this.branch, --this.index);
		final List<RCSTransaction> t2List = new LinkedList<>();
		t2List.addAll(this.tList);
		t2List.add(infinityTransaction);
		assertTrue(tSet.removeAll(t2List));
		assertTrue(tSet.isEmpty());
		
		assertFalse(tSet.removeAll(this.tList));
		
		tSet.addAll(this.tList);
		final RCSTransaction removed = this.tList.remove(7);
		assertTrue(tSet.removeAll(this.tList));
		assertFalse(tSet.isEmpty());
		assertTrue(tSet.contains(removed));
		
	}
	
	/**
	 * Test retain all.
	 */
	@Test
	public void testRetainAll() {
		final TransactionSet tSet = new TransactionSet(TransactionSetOrder.DESC);
		tSet.addAll(this.tList);
		assertTrue(tSet.retainAll(this.tList.subList(1, 3)));
		assertEquals(2, tSet.size());
	}
	
}

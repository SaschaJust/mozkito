package de.unisaarland.cs.st.mozkito.versions.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.mozkito.persistence.model.Person;
import de.unisaarland.cs.st.mozkito.versions.BranchFactory;
import de.unisaarland.cs.st.mozkito.versions.collections.TransactionSet;
import de.unisaarland.cs.st.mozkito.versions.collections.TransactionSet.TransactionSetOrder;
import de.unisaarland.cs.st.mozkito.versions.model.RCSBranch;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

public class TransactionSetTest {
	
	private List<RCSTransaction> tList;
	private RCSTransaction       otherT;
	private RCSBranch            otherBranch;
	
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
		final Person person = new Person("kim", null, null);
		final BranchFactory branchFactory = new BranchFactory(null);
		final RCSBranch branch = branchFactory.getMasterBranch();
		long index = 0;
		
		this.otherBranch = branchFactory.getBranch("otherBranch");
		this.otherT = RCSTransaction.createTransaction("280b1b8695286699770c5da85204e1718fXXXXXX", "", now, person,
		                                               null);
		this.otherT.addBranch(this.otherBranch, 0l);
		this.otherBranch.setHead(this.otherT);
		
		this.tList = new LinkedList<RCSTransaction>();
		
		final RCSTransaction t_280b1b = RCSTransaction.createTransaction("280b1b8695286699770c5da85204e1718f7f4b66",
		                                                                 "", now, person, null);
		branch.setHead(t_280b1b);
		t_280b1b.addBranch(branch, index);
		this.tList.add(t_280b1b);
		
		final RCSTransaction t_702abf = RCSTransaction.createTransaction("702abfed3f8ca043b2636efd31c14ba7552603dd",
		                                                                 "", now, person, null);
		t_702abf.addBranch(branch, --index);
		this.tList.add(t_702abf);
		
		final RCSTransaction t_cce07f = RCSTransaction.createTransaction("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2",
		                                                                 "", now, person, null);
		t_cce07f.addBranch(branch, --index);
		this.tList.add(t_cce07f);
		
		final RCSTransaction t_94f8b9 = RCSTransaction.createTransaction("94f8b9f16e9f3d423225b28619281a5ecf877275",
		                                                                 "", now, person, null);
		t_94f8b9.addBranch(branch, --index);
		this.tList.add(t_94f8b9);
		
		final RCSTransaction t_5813ab = RCSTransaction.createTransaction("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42",
		                                                                 "", now, person, null);
		t_5813ab.addBranch(branch, --index);
		this.tList.add(t_5813ab);
		
		final RCSTransaction t_8bc067 = RCSTransaction.createTransaction("8bc0679ca73760e68c0c27b54dc2855de34c1bdb",
		                                                                 "", now, person, null);
		t_8bc067.addBranch(branch, --index);
		this.tList.add(t_8bc067);
		
		final RCSTransaction t_9f6f10 = RCSTransaction.createTransaction("9f6f106cdc16effd8c093defd47f1626195d03db",
		                                                                 "", now, person, null);
		t_9f6f10.addBranch(branch, --index);
		this.tList.add(t_9f6f10);
		
		final RCSTransaction t_6bfee3 = RCSTransaction.createTransaction("6bfee30b10fb0498f3d70f383814a669939bb1c7",
		                                                                 "", now, person, null);
		t_6bfee3.addBranch(branch, --index);
		this.tList.add(t_6bfee3);
		
		final RCSTransaction t_45702d = RCSTransaction.createTransaction("45702d2a094554789dc51bd23869ed5ddd8822a6",
		                                                                 "", now, person, null);
		t_45702d.addBranch(branch, --index);
		this.tList.add(t_45702d);
		
		final RCSTransaction t_9c7c6d = RCSTransaction.createTransaction("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59",
		                                                                 "", now, person, null);
		t_9c7c6d.addBranch(branch, --index);
		this.tList.add(t_9c7c6d);
		
		final RCSTransaction t_d52295 = RCSTransaction.createTransaction("d522956171853fc2d7ca106d9c8d2b93e82df9d3",
		                                                                 "", now, person, null);
		t_d52295.addBranch(branch, --index);
		this.tList.add(t_d52295);
	}
	
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
	
}

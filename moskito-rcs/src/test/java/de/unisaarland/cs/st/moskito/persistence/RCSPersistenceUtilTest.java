package de.unisaarland.cs.st.moskito.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.collections.TransactionSet.TransactionSetOrder;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

/**
 * The Class RCSPersistenceUtilTest.
 */
public class RCSPersistenceUtilTest extends MoskitoTest {
	
	/** The t_280b1b. */
	private RCSTransaction t_280b1b;
	
	/** The t_702abf. */
	private RCSTransaction t_702abf;
	
	/** The t_cce07f. */
	private RCSTransaction t_cce07f;
	
	/** The t_94f8b9. */
	private RCSTransaction t_94f8b9;
	
	/** The t_5813ab. */
	private RCSTransaction t_5813ab;
	
	/** The t_8bc067. */
	private RCSTransaction t_8bc067;
	
	/** The t_9f6f10. */
	private RCSTransaction t_9f6f10;
	
	/** The t_6bfee3. */
	private RCSTransaction t_6bfee3;
	
	/** The t_45702d. */
	private RCSTransaction t_45702d;
	
	/** The t_9c7c6d. */
	private RCSTransaction t_9c7c6d;
	
	/** The t_d52295. */
	private RCSTransaction t_d52295;
	
	/**
	 * Setup.
	 *
	 * @param persistenceUtil the persistence util
	 * @return the list
	 */
	private List<RCSTransaction> setup(final PersistenceUtil persistenceUtil) {
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
		final RCSBranch branch = branchFactory.getMasterBranch();
		long index = 0;
		
		final RCSBranch otherBranch = branchFactory.getBranch("otherBranch");
		final RCSTransaction otherT = RCSTransaction.createTransaction("280b1b8695286699770c5da85204e1718fXXXXXX", "",
		                                                               now, person, null);
		otherT.addBranch(otherBranch, 0l);
		otherBranch.setHead(otherT);
		final RCSTransaction otherT2 = RCSTransaction.createTransaction("702abfed3f8ca043b2636efd31c14ba75XXXXXX", "",
		                                                                now, person, null);
		otherT2.addBranch(otherBranch, -1l);
		
		final RCSTransaction otherT3 = RCSTransaction.createTransaction("cce07fdcb9f3a0efcd67c75de60d5608c6XXXXXX", "",
		                                                                now, person, null);
		otherT3.addBranch(otherBranch, -1l);
		
		persistenceUtil.beginTransaction();
		persistenceUtil.saveOrUpdate(otherBranch);
		persistenceUtil.saveOrUpdate(otherT);
		persistenceUtil.saveOrUpdate(otherT2);
		persistenceUtil.saveOrUpdate(otherT3);
		
		final LinkedList<RCSTransaction> tList = new LinkedList<RCSTransaction>();
		
		this.t_280b1b = RCSTransaction.createTransaction("280b1b8695286699770c5da85204e1718f7f4b66", "", now, person,
		                                                 null);
		branch.setHead(this.t_280b1b);
		this.t_280b1b.addBranch(branch, index);
		tList.add(this.t_280b1b);
		
		this.t_702abf = RCSTransaction.createTransaction("702abfed3f8ca043b2636efd31c14ba7552603dd", "", now, person,
		                                                 null);
		this.t_702abf.addBranch(branch, --index);
		tList.add(this.t_702abf);
		
		this.t_cce07f = RCSTransaction.createTransaction("cce07fdcb9f3a0efcd67c75de60d5608c63cb5c2", "", now, person,
		                                                 null);
		this.t_cce07f.addBranch(branch, --index);
		tList.add(this.t_cce07f);
		
		this.t_94f8b9 = RCSTransaction.createTransaction("94f8b9f16e9f3d423225b28619281a5ecf877275", "", now, person,
		                                                 null);
		this.t_94f8b9.addBranch(branch, --index);
		tList.add(this.t_94f8b9);
		
		this.t_5813ab = RCSTransaction.createTransaction("5813ab7d15c9c97ff45a44e051f8e9776a1f7e42", "", now, person,
		                                                 null);
		this.t_5813ab.addBranch(branch, --index);
		tList.add(this.t_5813ab);
		
		this.t_8bc067 = RCSTransaction.createTransaction("8bc0679ca73760e68c0c27b54dc2855de34c1bdb", "", now, person,
		                                                 null);
		this.t_8bc067.addBranch(branch, --index);
		tList.add(this.t_8bc067);
		
		this.t_9f6f10 = RCSTransaction.createTransaction("9f6f106cdc16effd8c093defd47f1626195d03db", "", now, person,
		                                                 null);
		this.t_9f6f10.addBranch(branch, --index);
		tList.add(this.t_9f6f10);
		
		this.t_6bfee3 = RCSTransaction.createTransaction("6bfee30b10fb0498f3d70f383814a669939bb1c7", "", now, person,
		                                                 null);
		this.t_6bfee3.addBranch(branch, --index);
		tList.add(this.t_6bfee3);
		
		this.t_45702d = RCSTransaction.createTransaction("45702d2a094554789dc51bd23869ed5ddd8822a6", "", now, person,
		                                                 null);
		this.t_45702d.addBranch(branch, --index);
		tList.add(this.t_45702d);
		
		this.t_9c7c6d = RCSTransaction.createTransaction("9c7c6d1ef4ffe95dfcbaf850f869d6742d16bd59", "", now, person,
		                                                 null);
		this.t_9c7c6d.addBranch(branch, --index);
		tList.add(this.t_9c7c6d);
		
		this.t_d52295 = RCSTransaction.createTransaction("d522956171853fc2d7ca106d9c8d2b93e82df9d3", "", now, person,
		                                                 null);
		this.t_d52295.addBranch(branch, --index);
		tList.add(this.t_d52295);
		
		for (final RCSTransaction t : tList) {
			persistenceUtil.saveOrUpdate(t);
		}
		persistenceUtil.saveOrUpdate(branch);
		persistenceUtil.commitTransaction();
		return tList;
	}
	
	/**
	 * Test branch asc.
	 */
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testBranchASC() {
		
		final List<RCSTransaction> tList = setup(getPersistenceUtil());
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		
		final Iterator<RCSTransaction> iterator = RCSPersistenceUtil.getTransactions(getPersistenceUtil(),
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
	@DatabaseSettings (unit = "rcs")
	public void testBranchDESC() {
		
		final List<RCSTransaction> tList = setup(getPersistenceUtil());
		final BranchFactory branchFactory = new BranchFactory(getPersistenceUtil());
		
		final Iterator<RCSTransaction> iterator = RCSPersistenceUtil.getTransactions(getPersistenceUtil(),
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
	
	/**
	 * Test previous desc.
	 */
	@Test
	@DatabaseSettings (unit = "rcs")
	public void testPreviousDESC() {
		
		setup(getPersistenceUtil());
		final RCSTransaction t = getPersistenceUtil().loadById(this.t_9f6f10.getId(), RCSTransaction.class);
		
		@SuppressWarnings ("deprecation")
		final Iterator<RCSTransaction> iterator = RCSPersistenceUtil.getPreviousTransactions(getPersistenceUtil(), t,
		                                                                                     TransactionSetOrder.DESC)
		                                                            .iterator();
		
		assertTrue(iterator.hasNext());
		assertEquals(this.t_6bfee3, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.t_45702d, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.t_9c7c6d, iterator.next());
		assertTrue(iterator.hasNext());
		assertEquals(this.t_d52295, iterator.next());
		assertFalse(iterator.hasNext());
		
	}
}

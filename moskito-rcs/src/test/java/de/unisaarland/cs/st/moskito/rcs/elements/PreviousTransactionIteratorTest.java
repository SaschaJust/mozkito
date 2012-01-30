/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.rcs.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.model.Person;
import de.unisaarland.cs.st.moskito.rcs.BranchFactory;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

@DatabaseSettings (unit = "rcs")
public class PreviousTransactionIteratorTest extends MoskitoTest {
	
	private Person         person1;
	private Person         person2;
	private RCSTransaction x;
	private RCSTransaction y;
	private RCSTransaction z;
	private RCSBranch      master;
	
	/**
	 * @formatter:off
	 * 
	 *                Z |\ | P | |\ | | O | | | | | N | | |\ | | M \ | | | | | |
	 *                L | | K | | | | | | | J | | | | | / | I |/ | |/| | H | | |
	 *                | G | | |/ | F | |\ | | \ | | \| | E | | | D | | C | | | B
	 *                | | / | / |/ X
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void complexTest() {
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-01T00:00:02.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction c = RCSTransaction.createTransaction("c", "", new DateTime("1999-04-01T00:00:03.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction d = RCSTransaction.createTransaction("d", "", new DateTime("1999-04-01T00:00:04.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction e = RCSTransaction.createTransaction("e", "", new DateTime("1999-04-01T00:00:05.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction f = RCSTransaction.createTransaction("f", "", new DateTime("1999-04-01T00:00:06.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction g = RCSTransaction.createTransaction("g", "", new DateTime("1999-04-01T00:00:07.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction h = RCSTransaction.createTransaction("h", "", new DateTime("1999-04-01T00:00:09.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction i = RCSTransaction.createTransaction("i", "", new DateTime("1999-04-01T00:00:10.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction j = RCSTransaction.createTransaction("j", "", new DateTime("1999-04-01T00:00:11.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction k = RCSTransaction.createTransaction("k", "", new DateTime("1999-04-01T00:00:12.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction l = RCSTransaction.createTransaction("l", "", new DateTime("1999-04-01T00:00:13.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction m = RCSTransaction.createTransaction("m", "", new DateTime("1999-04-01T00:00:14.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction n = RCSTransaction.createTransaction("n", "", new DateTime("1999-04-01T00:00:15.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction o = RCSTransaction.createTransaction("o", "", new DateTime("1999-04-01T00:00:16.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction p = RCSTransaction.createTransaction("p", "", new DateTime("1999-04-01T00:00:17.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		RCSBranch dBranch = new RCSBranch("dBranch");
		dBranch.setBegin(d);
		dBranch.setEnd(o);
		
		RCSBranch hBranch = new RCSBranch("hBranch");
		hBranch.setBegin(h);
		hBranch.setEnd(p);
		
		this.x.setBranch(this.master);
		this.x.addChild(b);
		this.x.addChild(d);
		
		b.setBranch(this.master);
		b.addParent(this.x);
		b.addChild(c);
		
		c.setBranch(this.master);
		c.addParent(b);
		c.addChild(f);
		
		d.setBranch(dBranch);
		d.addParent(this.x);
		d.addChild(e);
		
		e.setBranch(dBranch);
		e.addParent(d);
		e.addChild(f);
		e.addChild(l);
		
		f.setBranch(this.master);
		f.addParent(c);
		f.addParent(e);
		f.addChild(g);
		f.addChild(h);
		
		g.setBranch(this.master);
		g.addParent(f);
		g.addChild(this.z);
		
		h.setBranch(hBranch);
		h.addParent(f);
		h.addChild(i);
		h.addChild(n);
		
		i.setBranch(hBranch);
		i.addParent(h);
		i.addChild(j);
		
		j.setBranch(hBranch);
		j.addParent(i);
		j.addChild(k);
		
		k.setBranch(hBranch);
		k.addParent(j);
		k.addChild(p);
		
		l.setBranch(dBranch);
		l.addParent(e);
		l.addChild(m);
		
		m.setBranch(dBranch);
		m.addParent(l);
		m.addChild(n);
		
		n.setBranch(dBranch);
		n.addParent(m);
		n.addParent(h);
		n.addChild(o);
		
		o.setBranch(dBranch);
		o.addParent(n);
		o.addChild(p);
		
		p.setBranch(hBranch);
		p.addParent(o);
		p.addParent(k);
		p.addChild(this.z);
		
		this.z.setBranch(this.master);
		this.z.addParent(p);
		this.z.addParent(g);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(g, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(p, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(k, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(o, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(j, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(n, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(i, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(m, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(h, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(l, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(f, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(e, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(c, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(d, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
	}
	
	/**
	 * @formatter:off
	 * 
	 *                z |\ | h | | | d | |\ e | | | f | | | g | | | | | b | |/
	 *                |/| a | | y |/ x
	 * 
	 * 
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void crossBranchTest() {
		
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-01T00:00:02.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction d = RCSTransaction.createTransaction("d", "", new DateTime("1999-04-01T00:00:04.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction e = RCSTransaction.createTransaction("e", "", new DateTime("1999-04-01T00:00:05.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction f = RCSTransaction.createTransaction("f", "", new DateTime("1999-04-01T00:00:06.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction g = RCSTransaction.createTransaction("g", "", new DateTime("1999-04-01T00:00:07.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction h = RCSTransaction.createTransaction("h", "", new DateTime("1999-04-01T00:00:09.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		RCSBranch yBranch = new RCSBranch("yBranch");
		yBranch.setBegin(this.y);
		yBranch.setEnd(h);
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(g);
		
		this.z.setBranch(this.master);
		this.z.addParent(h);
		this.z.addParent(e);
		
		h.setBranch(yBranch);
		h.addChild(this.z);
		h.addParent(d);
		
		d.setBranch(yBranch);
		d.addChild(f);
		d.addParent(f);
		d.addParent(g);
		
		e.setBranch(this.master);
		e.addChild(this.z);
		e.addParent(a);
		
		f.setBranch(yBranch);
		f.addChild(d);
		f.addParent(this.y);
		
		g.setBranch(bBranch);
		g.addChild(d);
		g.addParent(b);
		
		b.setBranch(bBranch);
		b.addChild(g);
		b.addParent(a);
		
		a.setBranch(this.master);
		a.addChild(e);
		a.addChild(b);
		a.addParent(this.x);
		
		this.y.setBranch(yBranch);
		this.y.addChild(f);
		this.y.addParent(this.x);
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(this.y);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(e, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(h, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(d, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(f, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(g, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
	}
	
	/**
	 * @formatter:off
	 * 
	 *                z |\ | \ y c |\ / | b a | |/ x
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void doubleBranchIterator2Test() {
		
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-02T00:00:01.000+00:00"),
				this.person2, "", getPersistenceUtil());
		RCSTransaction c = RCSTransaction.createTransaction("c", "", new DateTime("1999-04-03T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(b);
		this.x.addChild(c);
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(b);
		
		RCSBranch cBranch = new RCSBranch("cBranch");
		cBranch.setBegin(c);
		cBranch.setEnd(c);
		
		a.setBranch(this.master);
		a.addParent(this.x);
		a.addChild(this.y);
		
		b.setBranch(bBranch);
		b.addParent(this.x);
		b.addChild(this.y);
		b.addChild(c);
		
		c.setBranch(cBranch);
		c.addParent(b);
		c.addChild(this.z);
		
		this.y.setBranch(this.master);
		this.y.addParent(a);
		this.y.addParent(b);
		this.y.addChild(this.z);
		
		this.z.setBranch(this.master);
		this.z.addParent(this.y);
		this.z.addParent(c);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(c, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * @formatter:off
	 * 
	 *                z |\ | \ y c |\ | | b | | |/ a/| |/ x
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void doubleBranchIterator3Test() {
		
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-02T00:00:01.000+00:00"),
				this.person2, "", getPersistenceUtil());
		RCSTransaction c = RCSTransaction.createTransaction("c", "", new DateTime("1999-04-03T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(b);
		this.x.addChild(c);
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(b);
		
		RCSBranch cBranch = new RCSBranch("cBranch");
		cBranch.setBegin(c);
		cBranch.setEnd(c);
		
		a.setBranch(this.master);
		a.addParent(this.x);
		a.addChild(this.y);
		a.addChild(c);
		
		b.setBranch(bBranch);
		b.addParent(this.x);
		b.addChild(this.y);
		
		c.setBranch(cBranch);
		c.addParent(a);
		c.addChild(this.z);
		
		this.y.setBranch(this.master);
		this.y.addParent(a);
		this.y.addParent(b);
		this.y.addChild(this.z);
		
		this.z.setBranch(this.master);
		this.z.addParent(this.y);
		this.z.addParent(c);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(c, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * @formatter:off
	 * 
	 *                z |\ | \ y c |\ | | b | | | d | | | | |/ a/| |/ x
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void doubleBranchIterator4Test() {
		
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-02T00:00:01.000+00:00"),
				this.person2, "", getPersistenceUtil());
		RCSTransaction d = RCSTransaction.createTransaction("d", "", new DateTime("1999-04-03T00:00:00.500+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction c = RCSTransaction.createTransaction("c", "", new DateTime("1999-04-03T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(b);
		this.x.addChild(c);
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(b);
		
		RCSBranch cBranch = new RCSBranch("cBranch");
		cBranch.setBegin(d);
		cBranch.setEnd(c);
		
		a.setBranch(this.master);
		a.addParent(this.x);
		a.addChild(this.y);
		a.addChild(d);
		
		b.setBranch(bBranch);
		b.addParent(this.x);
		b.addChild(this.y);
		
		c.setBranch(cBranch);
		c.addParent(d);
		c.addChild(this.z);
		
		d.setBranch(cBranch);
		d.addParent(a);
		d.addChild(c);
		
		this.y.setBranch(this.master);
		this.y.addParent(a);
		this.y.addParent(b);
		this.y.addChild(this.z);
		
		this.z.setBranch(this.master);
		this.z.addParent(this.y);
		this.z.addParent(c);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(c, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(d, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * @formatter:off
	 * 
	 *                z |\ | \ y | |\ c | b | a |/ |/ x
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void doubleBranchIteratorTest() {
		
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-02T00:00:01.000+00:00"),
				this.person2, "", getPersistenceUtil());
		RCSTransaction c = RCSTransaction.createTransaction("c", "", new DateTime("1999-04-03T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(b);
		this.x.addChild(c);
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(b);
		
		RCSBranch cBranch = new RCSBranch("cBranch");
		cBranch.setBegin(c);
		cBranch.setEnd(c);
		
		a.setBranch(this.master);
		a.addParent(this.x);
		a.addChild(this.y);
		
		b.setBranch(bBranch);
		b.addParent(this.x);
		b.addChild(this.y);
		
		c.setBranch(cBranch);
		c.addParent(this.x);
		c.addChild(this.z);
		
		this.y.setBranch(this.master);
		this.y.addParent(a);
		this.y.addParent(b);
		this.y.addChild(this.z);
		
		this.z.setBranch(this.master);
		this.z.addParent(this.y);
		this.z.addParent(c);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(c, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * @formatter:off z |\ y | | | a | |\| | b | | |/ x
	 * 
	 */
	@Test
	public void patchTest1() {
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-02T00:00:01.000+00:00"),
				this.person2, "", getPersistenceUtil());
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(b);
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(b);
		
		b.setBranch(bBranch);
		b.addChild(a);
		b.addChild(this.z);
		b.addParent(this.x);
		
		a.setBranch(this.master);
		a.addChild(this.y);
		a.addParent(b);
		a.addParent(this.x);
		
		this.y.setBranch(this.master);
		this.y.addChild(this.z);
		this.y.addParent(a);
		
		this.z.setBranch(this.master);
		this.z.addParent(this.y);
		this.z.addParent(b);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
		
	}
	
	/**
	 * @formatter:off z |\ y | | | | b |/| a | | | |/ x
	 * 
	 */
	@Test
	public void patchTest2() {
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		RCSTransaction b = RCSTransaction.createTransaction("b", "", new DateTime("1999-04-02T00:00:01.000+00:00"),
				this.person2, "", getPersistenceUtil());
		
		RCSBranch bBranch = new RCSBranch("bBranch");
		bBranch.setBegin(b);
		bBranch.setEnd(b);
		
		this.x.setBranch(this.master);
		this.x.addChild(a);
		this.x.addChild(b);
		
		b.setBranch(bBranch);
		b.addChild(this.z);
		b.addParent(a);
		b.addParent(this.x);
		
		a.setBranch(this.master);
		a.addChild(this.y);
		a.addChild(b);
		a.addParent(this.x);
		
		this.y.setBranch(this.master);
		this.y.addChild(this.z);
		this.y.addParent(a);
		
		this.z.setBranch(this.master);
		this.z.addParent(this.y);
		this.z.addParent(b);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(b, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
		
	}
	
	@Before
	public void setUp() {
		this.master = BranchFactory.getMasterBranch(getPersistenceUtil());
		this.person1 = new Person("kim", "", "");
		this.person2 = new Person("just", "", "");
		this.x = RCSTransaction.createTransaction("x", "", new DateTime("1999-01-01T00:00:01.000+00:00"), this.person1,
				"",getPersistenceUtil());
		this.y = RCSTransaction.createTransaction("y", "", new DateTime("2000-01-01T00:00:01.000+00:00"), this.person2,
				"",getPersistenceUtil());
		this.z = RCSTransaction.createTransaction("z", "", new DateTime("2000-01-02T00:00:01.000+00:00"), this.person1,
				"",getPersistenceUtil());
	}
	
	/**
	 * @formatter:off z | y | x
	 * 
	 *                Simple iterator test.
	 */
	@Test
	public void simpleIteratorTest() {
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		this.x.setBranch(this.master);
		this.y.setBranch(this.master);
		this.x.addChild(this.y);
		this.y.addParent(this.x);
		this.z.setBranch(this.master);
		this.y.addChild(this.z);
		this.z.addParent(this.y);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
	}
	
	/**
	 * @formatter:off
	 * 
	 *                z |\ | a y | |/ x
	 * 
	 *                Simple merge iterator test.
	 */
	@Test
	public void simpleMergeIteratorTest() {
		this.master.setBegin(this.x);
		this.master.setEnd(this.z);
		
		RCSTransaction a = RCSTransaction.createTransaction("a", "", new DateTime("1999-04-01T00:00:01.000+00:00"),
				this.person1, "", getPersistenceUtil());
		
		this.x.setBranch(this.master);
		this.y.setBranch(this.master);
		this.x.addChild(this.y);
		this.y.addParent(this.x);
		this.z.setBranch(this.master);
		this.y.addChild(this.z);
		this.z.addParent(this.y);
		
		RCSBranch aBranch = new RCSBranch("aBranch");
		aBranch.setBegin(a);
		aBranch.addMergedIn(this.z.getId());
		aBranch.setEnd(a);
		a.setBranch(aBranch);
		a.addParent(this.x);
		this.x.addChild(a);
		a.addChild(this.z);
		this.z.addParent(a);
		
		Iterator<RCSTransaction> iterator = this.z.getPreviousTransactions();
		assertTrue(iterator.hasNext());
		assertEquals(this.y, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(a, iterator.next());
		
		assertTrue(iterator.hasNext());
		assertEquals(this.x, iterator.next());
		
		assertFalse(iterator.hasNext());
	}
	
}

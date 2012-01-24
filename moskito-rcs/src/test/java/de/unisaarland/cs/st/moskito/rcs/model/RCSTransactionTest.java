/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.rcs.model;

import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.persistence.model.Person;

public class RCSTransactionTest {
	
	private Person         person1;
	private Person         person2;
	private RCSTransaction x;
	private RCSTransaction y;
	private RCSTransaction z;
	
	@Before
	public void setUp() {
		this.person1 = new Person("kim", "", "");
		this.person2 = new Person("just", "", "");
		this.x = new RCSTransaction("x", "", new DateTime("2000-01-01T00:00:01.000+00:00"), this.person1, "");
		this.y = new RCSTransaction("y", "", new DateTime("2000-01-02T00:00:01.000+00:00"), this.person2, "");
		this.z = new RCSTransaction("z", "", new DateTime("1999-01-01T00:00:01.000+00:00"), this.person1, "");
	}
	
	@Test
	public void testCompareComplicated() {
		/* @formatter:off
		 * M
		 * |\    |
		 * L \   |
		 * |\ \  |
		 * | | | |
		 * | | K |
		 * | J | |
		 * | |\| I
		 * | | H |
		 * | G | |
		 * | | |/
		 * | | F
		 * E | |
		 * | | |
		 * | D |
		 * | |/
		 * | C
		 * B |
		 * |/
		 * A
		 * |
		 * Z
		 */
		
		RCSTransaction a = new RCSTransaction("a", "", new DateTime("2000-01-01T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction b = new RCSTransaction("b", "", new DateTime("2000-01-02T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction c = new RCSTransaction("c", "", new DateTime("2000-01-03T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction d = new RCSTransaction("d", "", new DateTime("2000-01-04T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction e = new RCSTransaction("e", "", new DateTime("2000-01-05T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction f = new RCSTransaction("f", "", new DateTime("2000-01-06T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction g = new RCSTransaction("g", "", new DateTime("2000-01-07T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction h = new RCSTransaction("h", "", new DateTime("2000-01-08T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction i = new RCSTransaction("i", "", new DateTime("2000-01-09T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction j = new RCSTransaction("j", "", new DateTime("2000-01-10T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction k = new RCSTransaction("k", "", new DateTime("2000-01-11T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction l = new RCSTransaction("l", "", new DateTime("2000-01-12T00:00:01.000+00:00"), this.person1, "");
		RCSTransaction m = new RCSTransaction("m", "", new DateTime("2000-01-13T00:00:01.000+00:00"), this.person1, "");
		
		RCSBranch branch_c = new RCSBranch("branch_a");
		branch_c.setBegin(c);
		branch_c.setEnd(j);
		branch_c.setMergedIn(l.getId());
		
		RCSBranch branch_f = new RCSBranch("branch_c");
		branch_f.setBegin(f);
		branch_f.setEnd(k);
		branch_f.setMergedIn(m.getId());
		
		RCSBranch branch_i = new RCSBranch("branch_f");
		branch_i.setBegin(i);
		
		RCSBranch.getMasterBranch().setBegin(this.z);
		
		a.setBranch(RCSBranch.getMasterBranch());
		a.addParent(this.z);
		a.addChild(b);
		a.addChild(c);
		
		b.setBranch(RCSBranch.getMasterBranch());
		b.addParent(a);
		b.addChild(e);
		
		c.setBranch(branch_c);
		c.addParent(a);
		c.addChild(d);
		c.addChild(f);
		
		d.setBranch(branch_c);
		d.addParent(c);
		d.addChild(g);
		
		e.setBranch(RCSBranch.getMasterBranch());
		e.addParent(b);
		e.addChild(l);
		
		f.setBranch(branch_f);
		f.addParent(c);
		f.addChild(h);
		f.addChild(i);
		
		g.setBranch(branch_c);
		g.addParent(d);
		g.addChild(j);
		
		h.setBranch(branch_f);
		h.addParent(f);
		h.addChild(j);
		h.addChild(k);
		
		i.setBranch(branch_i);
		i.addParent(f);
		
		j.setBranch(branch_c);
		j.addParent(g);
		j.addParent(h);
		j.addChild(l);
		
		k.setBranch(branch_f);
		k.addParent(h);
		k.addChild(m);
		
		l.setBranch(RCSBranch.getMasterBranch());
		l.addParent(e);
		l.addParent(j);
		l.addChild(m);
		
		m.setBranch(RCSBranch.getMasterBranch());
		m.addParent(k);
		m.addParent(l);
		
		assertTrue(a.compareTo(a) == 0);
		assertTrue(a.compareTo(b) < 0);
		assertTrue(a.compareTo(c) < 0);
		assertTrue(a.compareTo(d) < 0);
		assertTrue(a.compareTo(e) < 0);
		assertTrue(a.compareTo(f) < 0);
		assertTrue(a.compareTo(g) < 0);
		assertTrue(a.compareTo(h) < 0);
		assertTrue(a.compareTo(i) < 0);
		assertTrue(a.compareTo(j) < 0);
		assertTrue(a.compareTo(k) < 0);
		assertTrue(a.compareTo(l) < 0);
		assertTrue(a.compareTo(m) < 0);
		
		assertTrue(b.compareTo(a) > 0);
		assertTrue(b.compareTo(b) == 0);
		assertTrue(b.compareTo(c) < 0);
		assertTrue(b.compareTo(d) < 0);
		assertTrue(b.compareTo(e) < 0);
		assertTrue(b.compareTo(f) < 0);
		assertTrue(b.compareTo(g) < 0);
		assertTrue(b.compareTo(h) < 0);
		assertTrue(b.compareTo(i) < 0);
		assertTrue(b.compareTo(j) < 0);
		assertTrue(b.compareTo(k) < 0);
		assertTrue(b.compareTo(l) < 0);
		assertTrue(b.compareTo(m) < 0);
		
		assertTrue(c.compareTo(a) > 0);
		assertTrue(c.compareTo(b) > 0);
		assertTrue(c.compareTo(c) == 0);
		assertTrue(c.compareTo(d) < 0);
		assertTrue(c.compareTo(e) > 0);
		assertTrue(c.compareTo(f) < 0);
		assertTrue(c.compareTo(g) < 0);
		assertTrue(c.compareTo(h) < 0);
		assertTrue(c.compareTo(i) < 0);
		assertTrue(c.compareTo(j) < 0);
		assertTrue(c.compareTo(k) < 0);
		assertTrue(c.compareTo(l) < 0);
		assertTrue(c.compareTo(m) < 0);
		
		assertTrue(d.compareTo(a) > 0);
		assertTrue(d.compareTo(b) > 0);
		assertTrue(d.compareTo(c) > 0);
		assertTrue(d.compareTo(d) == 0);
		assertTrue(d.compareTo(e) > 0);
		assertTrue(d.compareTo(f) < 0);
		assertTrue(d.compareTo(g) < 0);
		assertTrue(d.compareTo(h) < 0);
		assertTrue(d.compareTo(i) < 0);
		assertTrue(d.compareTo(j) < 0);
		assertTrue(d.compareTo(k) < 0);
		assertTrue(d.compareTo(l) < 0);
		assertTrue(d.compareTo(m) < 0);
		
		assertTrue(e.compareTo(a) > 0);
		assertTrue(e.compareTo(b) > 0);
		assertTrue(e.compareTo(c) < 0);
		assertTrue(e.compareTo(d) < 0);
		assertTrue(e.compareTo(e) == 0);
		assertTrue(e.compareTo(f) < 0);
		assertTrue(e.compareTo(g) < 0);
		assertTrue(e.compareTo(h) < 0);
		assertTrue(e.compareTo(i) < 0);
		assertTrue(e.compareTo(j) < 0);
		assertTrue(e.compareTo(k) < 0);
		assertTrue(e.compareTo(l) < 0);
		assertTrue(e.compareTo(m) < 0);
		
		assertTrue(f.compareTo(a) > 0);
		assertTrue(f.compareTo(b) > 0);
		assertTrue(f.compareTo(c) > 0);
		assertTrue(f.compareTo(d) > 0);
		assertTrue(f.compareTo(e) > 0);
		assertTrue(f.compareTo(f) == 0);
		assertTrue(f.compareTo(g) > 0);
		assertTrue(f.compareTo(h) < 0);
		assertTrue(f.compareTo(i) < 0);
		assertTrue(f.compareTo(j) > 0);
		assertTrue(f.compareTo(k) < 0);
		assertTrue(f.compareTo(l) > 0);
		assertTrue(f.compareTo(m) < 0);
		
		assertTrue(g.compareTo(a) > 0);
		assertTrue(g.compareTo(b) > 0);
		assertTrue(g.compareTo(c) > 0);
		assertTrue(g.compareTo(d) > 0);
		assertTrue(g.compareTo(e) > 0);
		assertTrue(g.compareTo(f) < 0);
		assertTrue(g.compareTo(g) == 0);
		assertTrue(g.compareTo(h) < 0);
		assertTrue(g.compareTo(i) < 0);
		assertTrue(g.compareTo(j) < 0);
		assertTrue(g.compareTo(k) < 0);
		assertTrue(g.compareTo(l) < 0);
		assertTrue(g.compareTo(m) < 0);
		
		assertTrue(h.compareTo(a) > 0);
		assertTrue(h.compareTo(b) > 0);
		assertTrue(h.compareTo(c) > 0);
		assertTrue(h.compareTo(d) > 0);
		assertTrue(h.compareTo(e) > 0);
		assertTrue(h.compareTo(f) > 0);
		assertTrue(h.compareTo(g) > 0);
		assertTrue(h.compareTo(h) == 0);
		assertTrue(h.compareTo(i) < 0);
		assertTrue(h.compareTo(j) > 0);
		assertTrue(h.compareTo(k) < 0);
		assertTrue(h.compareTo(l) > 0);
		assertTrue(h.compareTo(m) < 0);
		
		assertTrue(i.compareTo(a) > 0);
		assertTrue(i.compareTo(b) > 0);
		assertTrue(i.compareTo(c) > 0);
		assertTrue(i.compareTo(d) > 0);
		assertTrue(i.compareTo(e) > 0);
		assertTrue(i.compareTo(f) > 0);
		assertTrue(i.compareTo(g) > 0);
		assertTrue(i.compareTo(h) > 0);
		assertTrue(i.compareTo(i) == 0);
		assertTrue(i.compareTo(j) > 0);
		assertTrue(i.compareTo(k) > 0);
		assertTrue(i.compareTo(l) > 0);
		assertTrue(i.compareTo(m) > 0);
		
		assertTrue(j.compareTo(a) > 0);
		assertTrue(j.compareTo(b) > 0);
		assertTrue(j.compareTo(c) > 0);
		assertTrue(j.compareTo(d) > 0);
		assertTrue(j.compareTo(e) > 0);
		assertTrue(j.compareTo(f) < 0);
		assertTrue(j.compareTo(g) > 0);
		assertTrue(j.compareTo(h) < 0);
		assertTrue(j.compareTo(i) < 0);
		assertTrue(j.compareTo(j) == 0);
		assertTrue(j.compareTo(k) < 0);
		assertTrue(j.compareTo(l) < 0);
		assertTrue(j.compareTo(m) < 0);
		
		assertTrue(k.compareTo(a) > 0);
		assertTrue(k.compareTo(b) > 0);
		assertTrue(k.compareTo(c) > 0);
		assertTrue(k.compareTo(d) > 0);
		assertTrue(k.compareTo(e) > 0);
		assertTrue(k.compareTo(f) > 0);
		assertTrue(k.compareTo(g) > 0);
		assertTrue(k.compareTo(h) > 0);
		assertTrue(k.compareTo(i) < 0);
		assertTrue(k.compareTo(j) > 0);
		assertTrue(k.compareTo(k) == 0);
		assertTrue(k.compareTo(l) > 0);
		assertTrue(k.compareTo(m) < 0);
		
		assertTrue(l.compareTo(a) > 0);
		assertTrue(l.compareTo(b) > 0);
		assertTrue(l.compareTo(c) > 0);
		assertTrue(l.compareTo(d) > 0);
		assertTrue(l.compareTo(e) > 0);
		assertTrue(l.compareTo(f) < 0);
		assertTrue(l.compareTo(g) > 0);
		assertTrue(l.compareTo(h) < 0);
		assertTrue(l.compareTo(i) < 0);
		assertTrue(l.compareTo(j) > 0);
		assertTrue(l.compareTo(k) < 0);
		assertTrue(l.compareTo(l) == 0);
		assertTrue(l.compareTo(m) < 0);
		
		assertTrue(m.compareTo(a) > 0);
		assertTrue(m.compareTo(b) > 0);
		assertTrue(m.compareTo(c) > 0);
		assertTrue(m.compareTo(d) > 0);
		assertTrue(m.compareTo(e) > 0);
		assertTrue(m.compareTo(f) > 0);
		assertTrue(m.compareTo(g) > 0);
		assertTrue(m.compareTo(h) > 0);
		assertTrue(m.compareTo(i) < 0);
		assertTrue(m.compareTo(j) > 0);
		assertTrue(m.compareTo(k) > 0);
		assertTrue(m.compareTo(l) > 0);
		assertTrue(m.compareTo(m) == 0);
	}
	
	@Test
	public void testCompareInClosedBranch(){
		/*@formatter:off
		 * Y
		 * |\
		 * T |
		 * | X
		 * |/
		 * Z
		 */
		
		RCSTransaction t = new RCSTransaction("t", "", new DateTime("2000-01-01T08:00:01.000+00:00"), this.person1, "");
		
		RCSBranch.getMasterBranch().setBegin(this.z);
		RCSBranch branch = new RCSBranch("branch");
		
		this.x.setBranch(branch);
		this.y.setBranch(RCSBranch.getMasterBranch());
		this.z.setBranch(RCSBranch.getMasterBranch());
		t.setBranch(RCSBranch.getMasterBranch());
		
		this.z.addChild(this.x);
		this.x.addParent(this.z);
		
		this.z.addChild(t);
		t.addParent(this.z);
		
		this.x.addChild(this.y);
		this.y.addParent(this.x);
		
		t.addChild(this.y);
		this.y.addParent(t);
		
		branch.setBegin(this.x);
		branch.setEnd(this.x);
		branch.setMergedIn("y");
		
		assertTrue(this.z.compareTo(this.x) < 0);
		assertTrue(t.compareTo(this.x) < 0);
		assertTrue(this.y.compareTo(this.x) > 0);
		assertTrue(this.y.compareTo(t) > 0);
		assertTrue(this.y.compareTo(this.z) > 0);
	}
	
	@Test
	public void testCompareInOpenBranch(){
		
		/*@formatter:off
		 * X Y
		 * |/
		 * Z
		 */
		
		RCSBranch.getMasterBranch().setBegin(
				new RCSTransaction("start", "", new DateTime("2008-01-01T00:00:01.000+00:00"),
						this.person1, ""));
		RCSBranch openBranch = new RCSBranch("openBranch");
		this.z.addChild(this.x);
		this.x.addParent(this.z);
		
		this.z.addChild(this.y);
		this.y.addParent(this.z);
		
		this.z.setBranch(RCSBranch.getMasterBranch());
		this.x.setBranch(RCSBranch.getMasterBranch());
		this.y.setBranch(openBranch);
		openBranch.setBegin(this.y);
		
		assertTrue(this.x.compareTo(this.y) < 0);
		
	}
	
	@Test
	public void testCompareInSameBranch() {
		
		/*@formatter:off
		 * Y
		 * |
		 * X
		 */
		
		RCSBranch.getMasterBranch().setBegin(this.z);
		
		this.x.setBranch(RCSBranch.getMasterBranch());
		this.y.setBranch(RCSBranch.getMasterBranch());
		this.x.addChild(this.y);
		assertTrue(this.x.compareTo(this.y) < 0);
		
		RCSBranch.getMasterBranch().setBegin(this.x);
		assertTrue(this.x.compareTo(this.y) < 0);
		
		RCSBranch.getMasterBranch().setEnd(this.y);
		assertTrue(this.x.compareTo(this.y) < 0);
		
	}
}

package de.unisaarland.cs.st.reposuite.untangling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;



public class ArtificialBlobGeneratorTest {
	
	@Test
	public void testTransitiveClosure() {
		Set<Set<String>> data = new HashSet<Set<String>>();
		Set<String> l1 = new HashSet<String>();
		l1.add("a");
		l1.add("b");
		data.add(l1);
		
		Set<String> l2 = new HashSet<String>();
		l2.add("b");
		l2.add("c");
		data.add(l2);
		
		Set<String> l3 = new HashSet<String>();
		l3.add("d");
		l3.add("e");
		data.add(l3);
		
		Set<String> l4 = new HashSet<String>();
		l4.add("e");
		l4.add("f");
		data.add(l4);
		
		Set<String> l5 = new HashSet<String>();
		l5.add("f");
		l5.add("g");
		data.add(l5);
		
		Set<Set<String>> transitiveClosure = ArtificialBlobGenerator.transitiveClosure(data);
		assertTrue(transitiveClosure != null);
		assertEquals(9, transitiveClosure.size());
		assertTrue(transitiveClosure.contains(l1));
		assertTrue(transitiveClosure.contains(l2));
		assertTrue(transitiveClosure.contains(l3));
		assertTrue(transitiveClosure.contains(l4));
		assertTrue(transitiveClosure.contains(l5));
		
		Set<String> l1l2 = new HashSet<String>();
		l1l2.addAll(l1);
		l1l2.addAll(l2);
		assertTrue(transitiveClosure.contains(l1l2));
		
		Set<String> l3l4 = new HashSet<String>();
		l3l4.addAll(l3);
		l3l4.addAll(l4);
		assertTrue(transitiveClosure.contains(l3l4));
		
		Set<String> l4l5 = new HashSet<String>();
		l4l5.addAll(l4);
		l4l5.addAll(l5);
		assertTrue(transitiveClosure.contains(l4l5));
		
		Set<String> l3l4l5 = new HashSet<String>();
		l3l4l5.addAll(l3);
		l3l4l5.addAll(l4);
		l3l4l5.addAll(l5);
		assertTrue(transitiveClosure.contains(l3l4l5));
	}
	
}

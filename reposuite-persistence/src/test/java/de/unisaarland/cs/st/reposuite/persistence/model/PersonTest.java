package de.unisaarland.cs.st.reposuite.persistence.model;

import org.junit.Test;

public class PersonTest {
	
	@Test
	public void testEmailContructor() {
		new Person("test", "test user", "elharo@6c29f813-dae2-4a2d-94c1-d0531c44c0a5");
		new Person("test", "test user", "elharo@test-domain.de");
	}
	
}

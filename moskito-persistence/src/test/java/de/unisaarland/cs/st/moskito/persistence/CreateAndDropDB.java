package de.unisaarland.cs.st.moskito.persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

@DatabaseSettings (database = "just_test", unit = "persistence")
public class CreateAndDropDB extends MoskitoTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("before class");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("after class");
	}
	
	@Test
	public void test() {
		// fail("Not yet implemented");
	}
	
}

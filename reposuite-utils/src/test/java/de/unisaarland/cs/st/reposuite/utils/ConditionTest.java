/**
 * 
 */
package de.unisaarland.cs.st.reposuite.utils;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ConditionTest {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Test method for
	 * {@link de.unisaarland.cs.st.reposuite.utils.Condition#noneNull(java.util.Collection)}
	 * .
	 */
	@Test
	public void testNoneNullCollection() {
		List<Map<?, ?>> list = new LinkedList<Map<?, ?>>();
		Map<String, Object[]> map = new HashMap<String, Object[]>();
		Object[] array = { new Object(), null, null };
		map.put("array1", array);
		list.add(map);
		try {
			Condition
			        .noneNull(list,
			                "In this test, the list contains a map of <String, Object[]>. None of the objects may be null in a recursive check.");
			
		} catch (AssertionError e) {
			System.err.println(e);
			return;
		}
		fail();
	}
	
}

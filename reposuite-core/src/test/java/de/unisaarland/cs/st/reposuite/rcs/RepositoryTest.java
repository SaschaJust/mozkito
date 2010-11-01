package de.unisaarland.cs.st.reposuite.rcs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RepositoryTest {
	
	// [scheme:][//authority][path][?query][#fragment]
	// [user-info@]host[:port]
	
	private URI originalNoUser;
	private URI originalUser;
	
	@Before
	public void setUp() throws Exception {
		originalNoUser = new URI("http://www.st.cs.uni-saarland.de");
		originalUser = new URI("http://user@www.st.cs.uni-saarland.de");
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testDifferentUsername() {
		URI encoded = Repository.encodeUsername(originalUser, "kim");
		assertFalse(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
		assertEquals("kim", encoded.getUserInfo());
		assertEquals(originalUser.getScheme(), encoded.getScheme());
		assertEquals(originalUser.getPath(), encoded.getPath());
		assertEquals(originalUser.getQuery(), encoded.getQuery());
		assertEquals(originalUser.getFragment(), encoded.getFragment());
		assertEquals(originalUser.getHost(), encoded.getHost());
		assertEquals(originalUser.getPort(), encoded.getPort());
	}
	
	@Test
	public void testEmptyUsername() {
		URI encoded = Repository.encodeUsername(originalUser, "");
		assertFalse(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
		assertEquals("", encoded.getUserInfo());
		assertEquals(originalUser.getScheme(), encoded.getScheme());
		assertEquals(originalUser.getPath(), encoded.getPath());
		assertEquals(originalUser.getQuery(), encoded.getQuery());
		assertEquals(originalUser.getFragment(), encoded.getFragment());
		assertEquals(originalUser.getHost(), encoded.getHost());
		assertEquals(originalUser.getPort(), encoded.getPort());
	}
	
	@Test
	public void testNoUsername() {
		URI encoded = Repository.encodeUsername(originalNoUser, "kim");
		assertFalse(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
		assertEquals("kim", encoded.getUserInfo());
		assertEquals(originalNoUser.getScheme(), encoded.getScheme());
		assertEquals(originalNoUser.getPath(), encoded.getPath());
		assertEquals(originalNoUser.getQuery(), encoded.getQuery());
		assertEquals(originalNoUser.getFragment(), encoded.getFragment());
		assertEquals(originalNoUser.getHost(), encoded.getHost());
		assertEquals(originalNoUser.getPort(), encoded.getPort());
	}
	
	@Test
	public void testNullUsername() {
		URI encoded = Repository.encodeUsername(originalNoUser, null);
		assertTrue(encoded.equals(originalNoUser));
		assertFalse(encoded.equals(originalUser));
		
		encoded = Repository.encodeUsername(originalUser, null);
		assertTrue(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
	}
	
	@Test
	public void testSameUsername() {
		URI encoded = Repository.encodeUsername(originalUser, "user");
		assertTrue(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
	}
	
}

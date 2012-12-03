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
package org.mozkito.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.ioda.URIUtils;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * The Class URIUtilsTest.
 */
public class URIUtilsTest {
	
	/** The original user. */
	private static URI originalUser;
	
	/** The original no user. */
	private static URI originalNoUser;
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		try {
			URIUtilsTest.originalUser = new URI("http://user@www.st.cs.uni-saarland.de");
			URIUtilsTest.originalNoUser = new URI("http://www.st.cs.uni-saarland.de");
		} catch (final URISyntaxException e) {
			fail();
		}
		
	}
	
	/**
	 * Test different username.
	 */
	@Test
	public void testDifferentUsername() {
		final URI encoded = URIUtils.encodeUsername(URIUtilsTest.originalUser, "kim");
		assertFalse(encoded.equals(URIUtilsTest.originalUser));
		assertFalse(encoded.equals(URIUtilsTest.originalNoUser));
		assertEquals("kim", encoded.getUserInfo());
		assertEquals(URIUtilsTest.originalUser.getScheme(), encoded.getScheme());
		assertEquals(URIUtilsTest.originalUser.getPath(), encoded.getPath());
		assertEquals(URIUtilsTest.originalUser.getQuery(), encoded.getQuery());
		assertEquals(URIUtilsTest.originalUser.getFragment(), encoded.getFragment());
		assertEquals(URIUtilsTest.originalUser.getHost(), encoded.getHost());
		assertEquals(URIUtilsTest.originalUser.getPort(), encoded.getPort());
	}
	
	/**
	 * Test empty username.
	 */
	@Test
	public void testEmptyUsername() {
		final URI encoded = URIUtils.encodeUsername(URIUtilsTest.originalUser, "");
		assertFalse(encoded.equals(URIUtilsTest.originalUser));
		assertFalse(encoded.equals(URIUtilsTest.originalNoUser));
		assertEquals("", encoded.getUserInfo());
		assertEquals(URIUtilsTest.originalUser.getScheme(), encoded.getScheme());
		assertEquals(URIUtilsTest.originalUser.getPath(), encoded.getPath());
		assertEquals(URIUtilsTest.originalUser.getQuery(), encoded.getQuery());
		assertEquals(URIUtilsTest.originalUser.getFragment(), encoded.getFragment());
		assertEquals(URIUtilsTest.originalUser.getHost(), encoded.getHost());
		assertEquals(URIUtilsTest.originalUser.getPort(), encoded.getPort());
	}
	
	/**
	 * Test no username.
	 */
	@Test
	public void testNoUsername() {
		final URI encoded = URIUtils.encodeUsername(URIUtilsTest.originalNoUser, "kim");
		assertFalse(encoded.equals(URIUtilsTest.originalUser));
		assertFalse(encoded.equals(URIUtilsTest.originalNoUser));
		assertEquals("kim", encoded.getUserInfo());
		assertEquals(URIUtilsTest.originalNoUser.getScheme(), encoded.getScheme());
		assertEquals(URIUtilsTest.originalNoUser.getPath(), encoded.getPath());
		assertEquals(URIUtilsTest.originalNoUser.getQuery(), encoded.getQuery());
		assertEquals(URIUtilsTest.originalNoUser.getFragment(), encoded.getFragment());
		assertEquals(URIUtilsTest.originalNoUser.getHost(), encoded.getHost());
		assertEquals(URIUtilsTest.originalNoUser.getPort(), encoded.getPort());
	}
	
	/**
	 * Test null username.
	 */
	@Test
	public void testNullUsername() {
		URI encoded = URIUtils.encodeUsername(URIUtilsTest.originalNoUser, null);
		assertEquals(URIUtilsTest.originalNoUser, encoded);
		assertFalse(encoded.equals(URIUtilsTest.originalUser));
		
		encoded = URIUtils.encodeUsername(URIUtilsTest.originalUser, null);
		assertEquals(URIUtilsTest.originalUser, encoded);
		assertFalse(encoded.equals(URIUtilsTest.originalNoUser));
	}
	
	/**
	 * Test same username.
	 */
	@Test
	public void testSameUsername() {
		final URI encoded = URIUtils.encodeUsername(URIUtilsTest.originalUser, "user");
		assertEquals(URIUtilsTest.originalUser, encoded);
		assertFalse(encoded.equals(URIUtilsTest.originalNoUser));
	}
	
	/**
	 * Test ur i2 string.
	 */
	@Test
	public void testURI2String() {
		final File file = new File("/tmp/");
		final URI uri = file.toURI();
		assertEquals("file:///tmp/", URIUtils.Uri2String(uri));
		
		try {
			final String uriString = "https://st.cs.uni-saarland.de";
			assertEquals(uriString, URIUtils.Uri2String(new URI(uriString)));
		} catch (final URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

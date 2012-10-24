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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import net.ownhero.dev.ioda.URIUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public class URIUtilsTest {
	
	private static URI originalUser;
	private static URI originalNoUser;
	
	@BeforeClass
	public static void beforeClass() {
		try {
			originalUser = new URI("http://user@www.st.cs.uni-saarland.de");
			originalNoUser = new URI("http://www.st.cs.uni-saarland.de");
		} catch (URISyntaxException e) {
			fail();
		}
		
	}
	
	@Test
	public void testDifferentUsername() {
		URI encoded = URIUtils.encodeUsername(originalUser, "kim");
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
		URI encoded = URIUtils.encodeUsername(originalUser, "");
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
		URI encoded = URIUtils.encodeUsername(originalNoUser, "kim");
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
		URI encoded = URIUtils.encodeUsername(originalNoUser, null);
		assertTrue(encoded.equals(originalNoUser));
		assertFalse(encoded.equals(originalUser));
		
		encoded = URIUtils.encodeUsername(originalUser, null);
		assertTrue(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
	}
	
	@Test
	public void testSameUsername() {
		URI encoded = URIUtils.encodeUsername(originalUser, "user");
		assertTrue(encoded.equals(originalUser));
		assertFalse(encoded.equals(originalNoUser));
	}
	
	@Test
	public void testURI2String() {
		File file = new File("/tmp/");
		URI uri = file.toURI();
		assertEquals("file:///tmp/", URIUtils.Uri2String(uri));
		
		try {
			assertEquals("https://st.cs.uni-saarland.de", URIUtils.Uri2String(new URI("https://st.cs.uni-saarland.de")));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		}
	}
	
}

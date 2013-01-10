/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.mappings.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mozkito.mappings.utils.Enumeration;
import org.mozkito.mappings.utils.EnumerationEntry;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class EnumerationTest {
	
	private static String enumText;
	
	/**
	 * Before class.
	 */
	@BeforeClass
	public static void beforeClass() {
		final InputStream inputStream = EnumerationTest.class.getResourceAsStream("/enum_test.txt"); //$NON-NLS-1$
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
			final StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append(FileUtils.lineSeparator);
			}
			
			enumText = builder.toString();
		} catch (final IOException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(EnumerationTest.class);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// ignore
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// ignore
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Enumeration#extract(java.lang.String)}.
	 */
	@Test
	public final void testExtractString() {
		final Collection<Enumeration> enumerations = Enumeration.extract(enumText);
		
		assertNotNull(enumerations);
		assertEquals(7, enumerations.size());
		final Iterator<Enumeration> iterator = enumerations.iterator();
		
		Enumeration enumeration = iterator.next();
		
		assertNotNull(enumeration.getType());
		assertEquals(Enumeration.Type.ALPHABETIC, enumeration.getType());
		
		final List<EnumerationEntry> entries = enumeration.getEnumerationEntries();
		
		assertNotNull(entries);
		assertEquals(2, entries.size());
		
		final EnumerationEntry entry = entries.get(0);
		
		final String identifier = entry.getIdentifier();
		
		assertNotNull(identifier);
		assertEquals("a", identifier);
		
		assertEquals(enumeration, entry.getParent());
		
		String text = enumeration.getText();
		
		int length = 54;
		String beginning = "field changing"; //$NON-NLS-1$
		String ending = "name additionally"; //$NON-NLS-1$
		
		assertNotNull(text);
		assertEquals(length, text.length());
		assertEquals(beginning, text.substring(0, beginning.length()));
		assertEquals(ending, text.substring(text.length() - ending.length(), text.length()));
		
		enumeration.getText();
		
		assertTrue(iterator.hasNext());
		enumeration = iterator.next();
		
		assertNotNull(enumeration.getType());
		assertEquals(Enumeration.Type.ALPHABETIC, enumeration.getType());
		
		text = enumeration.getText();
		
		length = 54;
		beginning = "by wrong"; //$NON-NLS-1$
		ending = "name additionally"; //$NON-NLS-1$
		
		assertNotNull(text);
		assertEquals(length, text.length());
		assertEquals(beginning, text.substring(0, beginning.length()));
		assertEquals(ending, text.substring(text.length() - ending.length(), text.length()));
		
		enumeration.getText();
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Enumeration#extract(java.lang.String, int, int)}.
	 */
	@Test
	public final void testExtractStringIntInt() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for
	 * {@link org.mozkito.mappings.utils.Enumeration#extract(java.lang.String, org.mozkito.mappings.utils.Enumeration.Type[])}
	 * .
	 */
	@Test
	public final void testExtractStringTypeArray() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for
	 * {@link org.mozkito.mappings.utils.Enumeration#extract(java.lang.String, org.mozkito.mappings.utils.Enumeration.Type[], int, int)}
	 * .
	 */
	@Test
	public final void testExtractStringTypeArrayIntInt() {
		fail("Not yet implemented");
	}
}

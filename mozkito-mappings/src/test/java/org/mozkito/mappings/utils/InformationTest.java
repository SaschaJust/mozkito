/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.mappings.utils;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class InformationTest {
	
	public static String text = null;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final InputStream stream = InformationTest.class.getResourceAsStream("/org/mozkito/mappings/enum_test.txt"); //$NON-NLS-1$
		final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		final StringBuilder builder = new StringBuilder();
		String line = null;
		
		while ((line = reader.readLine()) != null) {
			builder.append(line).append(FileUtils.lineSeparator);
		}
		
		text = builder.toString();
		reader.close();
		stream.close();
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.err.println(InformationTest.class.getSimpleName() + " completed."); //$NON-NLS-1$
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#enumerations(java.lang.String)}.
	 */
	@Test
	public final void testEnumerations() {
		final List<Map<String, String>> enumerations = Information.enumerations(text);
		int i = 0;
		
		for (final Map<String, String> map : enumerations) {
			System.err.println(String.format("===== Set number (%s) =====", ++i));
			
			int j = 0;
			
			for (final String bullet : map.keySet()) {
				System.err.println(String.format("Item %3s - Bullet: %s - Text: %s", ++j, bullet, map.get(bullet)));
			}
		}
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#hyperlinks(java.lang.String)}.
	 */
	@Test
	public final void testHyperlinks() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#itemizations(java.lang.String)}.
	 */
	@Test
	public final void testItemizations() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#language(java.lang.String)}.
	 */
	@Test
	public final void testLanguage() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#lineCount(java.lang.String)}.
	 */
	@Test
	public final void testLineCount() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#sentenceCount(java.lang.String)}.
	 */
	@Test
	public final void testSentenceCount() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#sentences(java.lang.String)}.
	 */
	@Test
	public final void testSentences() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#topics(java.lang.String)}.
	 */
	@Test
	public final void testTopics() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#wordCount(java.lang.String)}.
	 */
	@Test
	public final void testWordCount() {
		fail("Not yet implemented");
	}
}

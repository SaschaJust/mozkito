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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.AfterClass;
import org.junit.Assert;
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
		
		InformationTest.text = builder.toString();
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
		final Collection<Enumeration> enumerations = Information.enumerations(InformationTest.text);
		int i = 0;
		
		for (final Enumeration map : enumerations) {
			System.err.println(String.format("===== Set number (%s) =====", ++i));
			
			int j = 0;
			
			for (final EnumerationEntry bullet : map) {
				System.err.println(String.format("Item %3s - Bullet: %s", ++j, bullet));
			}
		}
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#hyperlinks(java.lang.String)}.
	 */
	@Test
	public final void testHyperlinks() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#itemizations(java.lang.String)}.
	 */
	@Test
	public final void testItemizations() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#language(java.lang.String)}.
	 */
	@Test
	public final void testLanguage() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#lineCount(java.lang.String)}.
	 */
	@Test
	public final void testLineCount() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#sentenceCount(java.lang.String)}.
	 */
	@Test
	public final void testSentenceCount() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#sentences(java.lang.String)}.
	 */
	@Test
	public final void testSentences() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#topics(java.lang.String)}.
	 */
	@Test
	public final void testTopics() {
		Assert.fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.Information#wordCount(java.lang.String)}.
	 */
	@Test
	public final void testWordCount() {
		Assert.fail("Not yet implemented");
	}
}

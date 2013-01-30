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

import static org.junit.Assert.fail;
import net.ownhero.dev.ioda.JavaUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * The Class TextMetadataTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TextMetadataTest {
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(TextMetadataTest.class);
	}
	
	/**
	 * Sets the up.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Before
	public void setUp() throws Exception {
		// ignore
	}
	
	/**
	 * Tear down.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@After
	public void tearDown() throws Exception {
		// ignore
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.TextMetadata#lines(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testLines() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.TextMetadata#paragraphs(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testParagraphs() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.TextMetadata#sentences(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testSentences() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.TextMetadata#topics(java.util.Iterator, int)}.
	 */
	@Test
	@Ignore
	public final void testTopics() {
		fail("Not yet implemented");
	}
	
	/**
	 * Test method for {@link org.mozkito.mappings.utils.TextMetadata#words(java.lang.String)}.
	 */
	@Test
	@Ignore
	public final void testWords() {
		fail("Not yet implemented");
	}
}

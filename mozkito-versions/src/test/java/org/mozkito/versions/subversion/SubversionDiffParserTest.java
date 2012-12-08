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
package org.mozkito.versions.subversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.ioda.FileUtils;

import org.junit.Test;

import difflib.Chunk;

/**
 * The Class SubversionDiffParserTest.
 */
public class SubversionDiffParserTest {
	
	/**
	 * Test.
	 */
	@Test
	public void testGetLineNumbers() {
		final List<String> lines = new LinkedList<>();
		lines.add("a");
		lines.add("b");
		lines.add("c");
		final Chunk chunk = new Chunk(10, 3, lines);
		final HashSet<Integer> lineNumbers = SubversionDiffParser.getLineNumbers(chunk);
		assertNotNull(lineNumbers);
		assertEquals(3, lineNumbers.size());
		assertTrue(lineNumbers.contains(11));
		assertTrue(lineNumbers.contains(12));
		assertTrue(lineNumbers.contains(13));
	}
	
	/**
	 * Test lines to string.
	 */
	@Test
	public void testLinesToString() {
		final List<String> lines = new LinkedList<>();
		lines.add("a");
		lines.add("b");
		lines.add("c");
		final String line = SubversionDiffParser.linesToString(lines);
		assertEquals(new StringBuilder().append("a").append(FileUtils.lineSeparator).append("b")
		                                .append(FileUtils.lineSeparator).append("c").toString(), line);
	}
}

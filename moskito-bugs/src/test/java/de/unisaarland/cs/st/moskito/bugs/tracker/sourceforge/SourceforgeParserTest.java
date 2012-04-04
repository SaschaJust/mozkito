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
 *******************************************************************************/
package de.unisaarland.cs.st.moskito.bugs.tracker.sourceforge;

import static org.junit.Assert.assertEquals;

import java.util.regex.Pattern;

import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.Regex;

import org.junit.Test;

/**
 * The Class SourceforgeParserTest.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class SourceforgeParserTest {
	
	/**
	 * Test attachment id regex.
	 */
	@Test
	public void testAttachmentIdRegex() {
		final String link = "<a href=\"/tracker/download.php?group_id=97367&amp;atid=617889&amp;file_id=336228&amp;aid=2825955\">Download</a>";
		final Match find = new Regex(SourceforgeParser.fileIdPattern.getPattern()).find(link);
		assert (find != null);
		assertEquals(1, find.getGroupCount());
		assertEquals("336228", find.getGroup(1).getMatch());
	}
	
	/**
	 * Test html comment regex.
	 */
	@Test
	public void testHTMLCommentRegex() {
		final String s = "<!-- google_ad_section_start -->\n\"JodaTest.java\"<!-- google_ad_section_end -->";
		final Regex htmlCommentRegex = new Regex(SourceforgeParser.htmlCommentRegex.getPattern(), Pattern.MULTILINE
		        | Pattern.DOTALL);
		String result = htmlCommentRegex.removeAll(s);
		result = result.replaceAll("\"", "");
		assertEquals("JodaTest.java", result.trim());
	}
	
}

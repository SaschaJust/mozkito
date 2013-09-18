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

package org.mozkito.utilities.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.utilities.io.FileUtils;

/**
 * The Class TextSeparatorTest.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class TextSeparatorTest {
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getClassName() {
		return JavaUtils.getHandle(TextSeparatorTest.class);
	}
	
	/**
	 * Test method for {@link org.mozkito.utilities.commons.text.TextSeparator#indentationBlocks(java.lang.String)}.
	 */
	@Test
	public final void testIndentationBlocks() {
		final String text = "There is however the problem we mention in section 1.2.3. This might turn out to be an invalid match as well.\n" //$NON-NLS-1$
		        + "We want to check if we match the following as well:\n" //$NON-NLS-1$
		        + "i. for one \n" //$NON-NLS-1$
		        + "	for one1\n" //$NON-NLS-1$
		        + "ii. second\n" //$NON-NLS-1$
		        + "	second1\n" //$NON-NLS-1$
		        + "    second2\n" //$NON-NLS-1$
		        + "iii. third\n" //$NON-NLS-1$
		        + "    third1\n" //$NON-NLS-1$
		        + "    third2\n" //$NON-NLS-1$
		        + "	third3\n" //$NON-NLS-1$
		        + "iiii. and last\n" //$NON-NLS-1$
		        + "	and last1\n" //$NON-NLS-1$
		        + "and this is no enumeration anymore. We just might throw in a date like 01.02.03 and check for"; //$NON-NLS-1$
		
		final List<String> blocks = TextSeparator.indentationBlocks(text);
		
		assertNotNull(blocks);
		assertEquals(9, blocks.size());
		
		String block = blocks.get(0);
		String beginning = "There is however"; //$NON-NLS-1$
		String ending = "i. for one "; //$NON-NLS-1$
		
		assertNotNull(block);
		assertEquals(171, block.length());
		assertEquals(beginning, block.substring(0, beginning.length()));
		assertEquals(ending, block.substring(block.length() - ending.length(), block.length()));
		
		block = blocks.get(1);
		beginning = "for one1"; //$NON-NLS-1$
		ending = "for one1"; //$NON-NLS-1$
		
		assertNotNull(block);
		assertEquals(8, block.length());
		assertEquals(beginning, block.substring(0, beginning.length()));
		assertEquals(ending, block.substring(block.length() - ending.length(), block.length()));
		
		block = blocks.get(3);
		beginning = "second1"; //$NON-NLS-1$
		ending = "second2"; //$NON-NLS-1$
		
		assertNotNull(block);
		assertEquals(14, block.length());
		assertEquals(beginning, block.substring(0, beginning.length()));
		assertEquals(ending, block.substring(block.length() - ending.length(), block.length()));
		
		block = blocks.get(5);
		beginning = "third1"; //$NON-NLS-1$
		ending = "third3"; //$NON-NLS-1$
		
		assertNotNull(block);
		assertEquals(18, block.length());
		assertEquals(beginning, block.substring(0, beginning.length()));
		assertEquals(ending, block.substring(block.length() - ending.length(), block.length()));
		
		block = blocks.get(7);
		beginning = "and last1"; //$NON-NLS-1$
		ending = "and last1"; //$NON-NLS-1$
		
		assertNotNull(block);
		assertEquals(9, block.length());
		assertEquals(beginning, block.substring(0, beginning.length()));
		assertEquals(ending, block.substring(block.length() - ending.length(), block.length()));
		
		block = blocks.get(8);
		beginning = "and this is no enumeration anymore."; //$NON-NLS-1$
		ending = "We just might throw in a date like 01.02.03 and check for"; //$NON-NLS-1$
		
		assertNotNull(block);
		assertEquals(93, block.length());
		assertEquals(beginning, block.substring(0, beginning.length()));
		assertEquals(ending, block.substring(block.length() - ending.length(), block.length()));
	}
	
	/**
	 * Test method for {@link org.mozkito.utilities.commons.text.TextSeparator#lines(java.lang.String)}.
	 */
	@Test
	public final void testLines() {
		final String line = "abc" + FileUtils.lineSeparator + "def" + '\n' + "ghi" + "\r\n" + "jkl"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		final List<String> lines = TextSeparator.lines(line);
		assertEquals("abc", lines.get(0)); //$NON-NLS-1$
		assertEquals("def", lines.get(1)); //$NON-NLS-1$
		assertEquals("ghi", lines.get(2)); //$NON-NLS-1$
		assertEquals("jkl", lines.get(3)); //$NON-NLS-1$
	}
	
	/**
	 * Test method for {@link org.mozkito.utilities.commons.text.TextSeparator#paragraphs(java.lang.String)}.
	 */
	@Test
	public final void testParagraphs() {
		final String text = "Lorem ipsum dolor sit amet," //$NON-NLS-1$
		        + "\n" //$NON-NLS-1$
		        + "consectetur adipisicing elit," //$NON-NLS-1$
		        + "\n" //$NON-NLS-1$
		        + "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua." //$NON-NLS-1$
		        + "\n\n" //$NON-NLS-1$
		        + "Ut enim ad minim veniam," //$NON-NLS-1$
		        + "\n" //$NON-NLS-1$
		        + "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat." //$NON-NLS-1$
		        + "\r\n\r\n" //$NON-NLS-1$
		        + "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur." //$NON-NLS-1$
		        + "\r\n" + "Excepteur sint occaecat cupidatat non proident," + "\r\n" //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		        + "sunt in culpa qui officia deserunt mollit anim id est laborum."; //$NON-NLS-1$
		
		final List<String> paragraphs = TextSeparator.paragraphs(text);
		
		String paragraph = paragraphs.get(0);
		String beginning = "Lorem ipsum dolor"; //$NON-NLS-1$
		String ending = "magna aliqua."; //$NON-NLS-1$
		
		assertNotNull(paragraph);
		assertEquals(124, paragraph.length());
		assertEquals(beginning, paragraph.substring(0, beginning.length()));
		assertEquals(ending, paragraph.substring(paragraph.length() - ending.length(), paragraph.length()));
		
		paragraph = paragraphs.get(1);
		beginning = "Ut enim ad minim veniam"; //$NON-NLS-1$
		ending = "ea commodo consequat."; //$NON-NLS-1$
		
		assertNotNull(paragraph);
		assertEquals(107, paragraph.length());
		assertEquals(beginning, paragraph.substring(0, beginning.length()));
		assertEquals(ending, paragraph.substring(paragraph.length() - ending.length(), paragraph.length()));
		
		paragraph = paragraphs.get(2);
		beginning = "Duis aute irure"; //$NON-NLS-1$
		ending = "anim id est laborum."; //$NON-NLS-1$
		
		assertNotNull(paragraph);
		assertEquals(215, paragraph.length());
		assertEquals(beginning, paragraph.substring(0, beginning.length()));
		assertEquals(ending, paragraph.substring(paragraph.length() - ending.length(), paragraph.length()));
		
	}
	
	/**
	 * Test method for {@link org.mozkito.utilities.commons.text.TextSeparator#sentences(java.lang.String)}.
	 */
	@Test
	public final void testSentences() {
		final String text = "Filler text (also placeholder text or dummy text) is text that shares some characteristics of a real written text, but is random or otherwise generated. It may be used to display a sample of fonts, generate text for testing, or to spoof an e-mail spam filter. The process of using filler text is sometimes called Greeking, although the text itself may be nonsense, or largely Latin, as in Lorem ipsum."; //$NON-NLS-1$
		final List<String> sentences = TextSeparator.sentences(text);
		
		String sentence = sentences.get(0);
		String beginning = "Filler text ("; //$NON-NLS-1$
		String ending = "otherwise generated."; //$NON-NLS-1$
		
		assertNotNull(sentence);
		assertEquals(152, sentence.length());
		assertEquals(beginning, sentence.substring(0, beginning.length()));
		assertEquals(ending, sentence.substring(sentence.length() - ending.length(), sentence.length()));
		
		sentence = sentences.get(1);
		beginning = "It may be used"; //$NON-NLS-1$
		ending = "spam filter."; //$NON-NLS-1$
		
		assertNotNull(sentence);
		assertEquals(106, sentence.length());
		assertEquals(beginning, sentence.substring(0, beginning.length()));
		assertEquals(ending, sentence.substring(sentence.length() - ending.length(), sentence.length()));
		
		sentence = sentences.get(2);
		beginning = "The process of using"; //$NON-NLS-1$
		ending = "in Lorem ipsum."; //$NON-NLS-1$
		
		assertNotNull(sentence);
		assertEquals(141, sentence.length());
		assertEquals(beginning, sentence.substring(0, beginning.length()));
		assertEquals(ending, sentence.substring(sentence.length() - ending.length(), sentence.length()));
	}
	
	/**
	 * Test method for {@link org.mozkito.utilities.commons.text.TextSeparator#words(java.lang.String)}.
	 */
	@Test
	public final void testWords() {
		final String text = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."; //$NON-NLS-1$
		
		final List<String> words = TextSeparator.words(text);
		
		assertNotNull(words);
		assertEquals(36, words.size());
		assertEquals("Lorem", words.get(0)); //$NON-NLS-1$
		assertEquals("amet", words.get(4)); //$NON-NLS-1$
		assertEquals("consectetur", words.get(5)); //$NON-NLS-1$
		assertEquals("aliquip", words.get(31)); //$NON-NLS-1$
	}
}

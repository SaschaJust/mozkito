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

package org.mozkito.mappings.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jregex.REFlags;

import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

/**
 * The Class TextSeparator.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public final class TextSeparator {
	
	/** The Constant SENTENCE_MODEL. */
	static final SentenceModel    SENTENCE_MODEL    = new MedlineSentenceModel();
	
	static final TokenizerFactory TOKENIZER_FACTORY = IndoEuropeanTokenizerFactory.INSTANCE;
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public static String getHandle() {
		return JavaUtils.getHandle(TextSeparator.class);
	}
	
	/**
	 * Indentation blocks.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static List<String> indentationBlocks(final String text) {
		return indentationBlocks(text, 4);
	}
	
	/**
	 * Indentation blocks.
	 * 
	 * @param text
	 *            the text
	 * @param ts
	 *            the ts
	 * @return the string[]
	 */
	public static List<String> indentationBlocks(final String text,
	                                             final int ts) {
		final List<String> ret = new LinkedList<>();
		final List<String> lines = lines(text);
		int indent = -1;
		
		StringBuilder builder = new StringBuilder();
		
		for (final String line : lines) {
			int newIndent = 0;
			final byte[] bytes = line.getBytes();
			int i = 0;
			for (i = 0; i < line.length(); ++i) {
				if (bytes[i] == ' ') {
					++newIndent;
				} else if (bytes[i] == '\t') {
					newIndent += ts;
				} else {
					if (indent < 0) {
						indent = newIndent;
					} else if (newIndent != indent) {
						indent = newIndent;
						ret.add(builder.toString());
						builder = new StringBuilder();
					}
					builder.append(line.substring(i));
					break;
				}
			}
			
		}
		
		ret.add(builder.toString());
		return ret;
	}
	
	/**
	 * Lines.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static List<String> lines(final String text) {
		return Arrays.asList(text.split("(\r\n|\r|\n)")); //$NON-NLS-1$
	}
	
	/**
	 * Paragraphs.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static List<String> paragraphs(final String text) {
		final Regex regex = new Regex("({LINEBREAK}\r\n|\r|\n)\\s*{\\LINEBREAK}", REFlags.MULTILINE); //$NON-NLS-1$ 
		return Arrays.asList(regex.tokenize(text));
	}
	
	/**
	 * Sentences.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static List<String> sentences(final String text) {
		final List<String> sentences = new LinkedList<>();
		
		final List<String> tokenList = new ArrayList<String>();
		final List<String> whiteList = new ArrayList<String>();
		final Tokenizer tokenizer = TextSeparator.TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
		tokenizer.tokenize(tokenList, whiteList);
		
		final String[] tokens = new String[tokenList.size()];
		final String[] whites = new String[whiteList.size()];
		tokenList.toArray(tokens);
		whiteList.toArray(whites);
		final int[] sentenceBoundaries = TextSeparator.SENTENCE_MODEL.boundaryIndices(tokens, whites);
		
		int sentStartTok = 0;
		int sentEndTok = 0;
		for (final int sentenceBoundarie : sentenceBoundaries) {
			sentEndTok = sentenceBoundarie;
			
			final StringBuilder builder = new StringBuilder();
			
			for (int j = sentStartTok; j <= sentEndTok; j++) {
				builder.append(tokens[j] + whites[j + 1]);
			}
			
			sentStartTok = sentEndTok + 1;
			
			sentences.add(builder.toString().replaceAll("[\r\n]", " ").trim()); //$NON-NLS-1$//$NON-NLS-2$
		}
		
		return sentences;
	}
	
	/**
	 * Words.
	 * 
	 * @param text
	 *            the text
	 * @return the string[]
	 */
	public static List<String> words(final String text) {
		final List<String> list = new LinkedList<>();
		final Regex regex = new Regex("\\w+"); //$NON-NLS-1$
		final MultiMatch multiMatch = regex.findAll(text);
		for (final Match match : multiMatch) {
			list.add(match.getFullMatch().getMatch());
		}
		return list;
	}
	
}

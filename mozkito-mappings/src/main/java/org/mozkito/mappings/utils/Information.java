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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Pattern;

import jregex.REFlags;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.IOUtils;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;
import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.CharSequenceLowercase;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelSequence;

import com.aliasi.sentences.MedlineSentenceModel;
import com.aliasi.sentences.SentenceModel;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.Tokenizer;
import com.aliasi.tokenizer.TokenizerFactory;

/**
 * The Class Information.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Information {
	
	/**
	 * The Enum Language.
	 */
	public enum Language {
		
		/** The ENGLISH. */
		ENGLISH,
		/** The GERMAN. */
		GERMAN;
	}
	
	private static final class StringInstanceIterator implements Iterator<Instance> {
		
		private Iterator<String> iterator;
		
		/**
         * 
         */
		public StringInstanceIterator(final Iterator<String> stringIterator) {
			// PRECONDITIONS
			
			try {
				this.iterator = stringIterator;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			// PRECONDITIONS
			
			try {
				return this.iterator.hasNext();
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Instance next() {
			// PRECONDITIONS
			
			try {
				final Instance carrier = new Instance(this.iterator.next(), null, null, null);
				return carrier;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			// PRECONDITIONS
			
			try {
				throw new UnsupportedOperationException();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/*
	 * single-char alphabetic identifiers (uppercase and lowercase) as well as single/double digits
	 */
	/** The Constant ACTUAL_ENUM_IDENTIFIER. */
	@SuppressWarnings ("unused")
	private static final String   ACTUAL_ENUM_IDENTIFIER     = "([a-zA-Z]|[1-9][0-9]?)";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          //$NON-NLS-1$
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	/*
	 * X) and X ) X. X.) X] (X) and ( X ) [X] {X} X:
	 */
	/** The Constant ALPHA_ENUM_BULLET. */
	private static final String   ALPHA_ENUM_BULLET          = "({BULLET}[a-zA-Z] ?\\)|[a-zA-Z]\\.|[a-zA-Z]\\.\\)|[a-zA-Z]\\]|\\( ?[a-zA-Z] ?\\)|\\[ ?[a-zA-Z] ?\\]|\\{ ?[a-zA-Z] ?\\}|[a-zA-Z]: )";
	
	/** The Constant ALPHA_ENUM_RELAXED_PATTERN. */
	private static final String   ALPHA_ENUM_RELAXED_PATTERN = "({BULLET}(?:%s0|%s>))";
	/*
	 * enums start: - either at the beginning of a line or - after a whitespace enums end: - end of string - next bullet
	 * - empty line if previous enum items were not separated by empty lines - line with different indentation then the
	 * previous items
	 */
	
	/** The Constant ENUMERATION_PATTERN. */
	public static final String    ENUMERATION_PATTERN        = "(^|\\s)" /*
																		  * start at beginning of line or after
																		  * whitespace
																		  */
	                                                                 + "({bullet}([a-zA-Z]|[1-9][0-9]?) ?\\)|([a-zA-Z]|[1-9][0-9]?)\\.|([a-zA-Z]|[1-9][0-9]?)\\.\\)|([a-zA-Z]|[1-9][0-9]?)\\]|\\( ?([a-zA-Z]|[1-9][0-9]?) ?\\)|\\[ ?([a-zA-Z]|[1-9][0-9]?) ?\\]|\\{ ?([a-zA-Z]|[1-9][0-9]?) ?\\}|([a-zA-Z]|[1-9][0-9]?):\\s)" /*
																																																																															   * next
																																																																															   * thing
																																																																															   * has
																																																																															   * to
																																																																															   * be
																																																																															   * the
																																																																															   * enumeration
																																																																															   * identifier
																																																																															   * according
																																																																															   * to
																																																																															   * the
																																																																															   * definition
																																																																															   * above
																																																																															   */
	                                                                 + "({text}.*?)" /* followed by some random text */
	                                                                 + "([\\s\\n]+(([a-zA-Z]|[1-9][0-9]?) ?\\)|([a-zA-Z]|[1-9][0-9]?)\\.|([a-zA-Z]|[1-9][0-9]?)\\.\\)|([a-zA-Z]|[1-9][0-9]?)\\]|\\( ?([a-zA-Z]|[1-9][0-9]?) ?\\)|\\[ ?([a-zA-Z]|[1-9][0-9]?) ?\\]|\\{ ?([a-zA-Z]|[1-9][0-9]?) ?\\}|([a-zA-Z]|[1-9][0-9]?):\\s)|\\n)";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             /*
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * followed
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * by
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * either
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * the
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * next
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * bullet
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * or
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * the
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * line
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    * end
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																												    */
	
	/** The Constant NUMBER_ENUM_BULLET. */
	private static final String   NUMBER_ENUM_BULLET         = "[1-9][0-9]? ?\\)|[1-9][0-9]?\\.|[1-9][0-9]?\\.\\)|[1-9][0-9]?\\]|\\( ?[1-9][0-9]? ?\\)|\\[ ?[1-9][0-9]? ?\\]|\\{ ?[1-9][0-9]? ?\\}|[1-9][0-9]?: ";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //$NON-NLS-1$
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	/** The Constant ROMAN_ENUM_BULLET. */
	private static final String   ROMAN_ENUM_BULLET          = "(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\)|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx)\\.|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx)\\.\\)|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx)\\]|\\( ?(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\)|\\[ ?(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\]|\\{ ?(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\}|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx): ";
	
	static final SentenceModel    SENTENCE_MODEL             = new MedlineSentenceModel();
	
	static final TokenizerFactory TOKENIZER_FACTORY          = IndoEuropeanTokenizerFactory.INSTANCE;
	
	/** The Constant URL_PATTERN. */
	private static final String   URL_PATTERN                = "({URL}[a-z](?:[-a-z0-9\\+\\.])*:(?:\\/\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:])*@)?(?:\\[(?:(?:(?:[0-9a-f]{1,4}:){6}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|::(?:[0-9a-f]{1,4}:){5}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){4}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4}:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){3}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,2}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){2}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,3}[0-9a-f]{1,4})?::[0-9a-f]{1,4}:(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,4}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4})?::[0-9a-f]{1,4}|(?:(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{1,4})?::)|v[0-9a-f]+[-a-z0-9\\._~!\\$&'\\(\\)\\*\\+,;=:]+)\\]|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}|(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=@])*)(?::[0-9]*)?(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*|\\/(?:(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*)?|(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*|(?!(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])))(?:\\?(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])|[\\x{E000}-\\x{F8FF}\\x{F0000}-\\x{FFFFD}|\\x{100000}-\\x{10FFFD}\\/\\?])*)?(?:\\#(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])|[\\/\\?])*)?)"; //$NON-NLS-1$
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
	/**
	 * By topic.
	 * 
	 * @param text
	 *            the text
	 * @param topics
	 *            the topics
	 * @return the list
	 */
	public static final List<String> byTopic(final String text,
	                                         final String[] topics) {
		return new LinkedList<>();
	}
	
	/**
	 * Enumerations.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final List<Map<String, String>> enumerations(final String text) {
		final String theString = text;
		
		findAlphaEnumSpots(text);
		new LinkedList<>();
		new LinkedList<>();
		
		new Regex(NUMBER_ENUM_BULLET, REFlags.IGNORE_CASE);
		new Regex(ROMAN_ENUM_BULLET, REFlags.IGNORE_CASE);
		final Regex regex = new Regex(ENUMERATION_PATTERN);
		
		regex.find(theString);
		
		// while ((match != null)) {
		//			final int nextStart = match.getGroup("text").end(); //$NON-NLS-1$
		//			map.put(match.getGroup("bullet").getMatch(), match.getGroup("text").getMatch()); //$NON-NLS-1$ //$NON-NLS-2$
		//
		// if (nextStart < (theString.length() - 1)) {
		// theString = theString.substring(nextStart);
		// match = regex.find(theString);
		// } else {
		// match = null;
		// }
		// }
		
		/*
		 * find all number-enumerations and character enumerations, i.e. a), b), c) and 1), 2), 3) and I), II), III),
		 * IV) and store them into separate lists of maps.
		 */
		
		/*
		 * check basic enumeration conditions. E.g. there has to be at least 2 items to have a valid enumeration. If we
		 * just got one, we have to do a relaxed search for the logical successor. If we don't find any valid regions,
		 * we will discard all matches and return an empty map.
		 */
		
		/*
		 * check indicator validity! Index has to be increasing. If index is increasing, but not continuously, we
		 * probably missed out on an item. E.g. we found a) and c), there is a high probability that there is an item b)
		 * in the text. We can use this information to search with a much less restricted algorithm for b) in the area
		 * from a).begin() to c.begin(). This might be caused by overlapping a) and b), e.g. because the author mistyped
		 * 'b)' into 'b0'. Also check for multiple enumerations. We might find something like 1), 2), 3), 1., 2., a],
		 * b], c]
		 */
		
		/*
		 * Check termination of enumerations. The question when enumerations end is difficult to answer. We will make
		 * the following assumptions here: Enumerations obviously end at the end of the text</li> <li>The textual
		 * separation of items is assumed to be the same for the majority (<50%) of the items. I.e. If most enumerations
		 * are separated by an empty line we can use this information to terminate the last item. If enumerations span
		 * across one line, the last item will end at either the end of line or the end of the current
		 * sentence--whatever comes first. <li> </ul>
		 */
		
		// TODO regex are only good to determine if there are any enumerations in the text body, but not to precisely
		// determine the bounds. This should be implemented manually by some rather sophisticated algorithm.
		return new LinkedList<Map<String, String>>() {
			
			{
				add(new HashMap<String, String>());
			}
		};
	}
	
	/**
	 * Find alpha enum spots.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	private static final List<Map<String, Integer>> findAlphaEnumSpots(final String text) {
		
		final List<Map<String, Integer>> list = new LinkedList<>();
		final Regex alphaBulletRegex = new Regex(ALPHA_ENUM_BULLET, REFlags.IGNORE_CASE);
		
		final Map<String, Integer> currentMap = new HashMap<>();
		
		char lastBullet = 0;
		
		final MultiMatch multiMatch = alphaBulletRegex.findAll(text);
		int i = 0;
		for (final Match match : multiMatch) {
			final char currentBullet = match.getFullMatch().getMatch().charAt(0);
			final int currentStart = match.getFullMatch().start();
			
			if (lastBullet > 0) {
				if (currentBullet == (char) (lastBullet + 1)) {
					// found valid next successor
					
					// we can safely add this to our map
					currentMap.put("" + currentBullet, currentStart); //$NON-NLS-1$
				} else if (currentBullet > lastBullet) {
					// found successor, but missed at least one on the way there
					final int fromPosition = currentMap.get("" + lastBullet); //$NON-NLS-1$
					final int toPosition = currentStart;
					
					for (int bulletChar = lastBullet + 1; bulletChar < currentBullet; ++bulletChar) {
						final Integer intermediatePosition = lookupRelaxedAlphaEnum(text, fromPosition, toPosition,
						                                                            (char) bulletChar);
						if (intermediatePosition != null) {
							currentMap.put("" + currentBullet, intermediatePosition); //$NON-NLS-1$
						} else {
							// did not find the actual enum bullet with relaxed search
							// TODO what to do now?
							lookaheadAlphaEnum(currentBullet, multiMatch, i + 1);
						}
					}
				} else {
					// probably found the next enumeration or invalid match
					
					// if this is 'a'||'A'
					
					// do look-ahead
				}
			} else {
				if ((currentBullet == 'a') || (currentBullet == 'A')) {
					// found beginning
					currentMap.put("" + currentBullet, currentStart); //$NON-NLS-1$
					lastBullet = currentBullet;
				} else {
					// look-ahead if we find something like b) and c) and do relaxed search for a)
				}
			}
			++i;
		}
		
		list.add(currentMap);
		
		return list;
	}
	
	/**
	 * Hyperlinks.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final List<URL> hyperlinks(final String text) {
		final Regex regex = new Regex(URL_PATTERN);
		
		final MultiMatch multiMatch = regex.findAll(text);
		
		final List<URL> list = new ArrayList<>(multiMatch.size());
		
		for (final Match match : multiMatch) {
			try {
				list.add(new URL(match.getGroup("URL").getMatch())); //$NON-NLS-1$
			} catch (final MalformedURLException ignore) {
				// skip
			}
		}
		return list;
	}
	
	/**
	 * Itemizations.
	 * 
	 * @param text
	 *            the text
	 * @return the tuple
	 */
	public static final List<Tuple<String, List<String>>> itemizations(final String text) {
		return new ArrayList(1) {
			
			{
				add(new Tuple("-", new LinkedList<>()));}};//$NON-NLS-1$
	}
	
	/**
	 * Language.
	 * 
	 * @param text
	 *            the text
	 * @return the language
	 */
	public static final Language language(final String text) {
		return Language.ENGLISH;
	}
	
	/**
	 * Line count.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int lineCount(final String text) {
		return 0;
	}
	
	/**
	 * Lookahead alpha enum.
	 * 
	 * @param currentBullet
	 *            the current bullet
	 * @param multiMatch
	 *            the multi match
	 * @param i
	 *            the i
	 * @return the list
	 */
	private static List<Tuple<String, Integer>> lookaheadAlphaEnum(final char currentBullet,
	                                                               final MultiMatch multiMatch,
	                                                               final int i) {
		// PRECONDITIONS
		
		try {
			final char refLowerBullet = Character.toLowerCase(currentBullet);
			final char refUpperBullet = Character.toUpperCase(currentBullet);
			final List<Tuple<String, Integer>> list = new LinkedList<>();
			
			// TODO prefer same case bullets over other case
			if (multiMatch.size() >= i) {
				int j = 1;
				for (int index = i; index < multiMatch.size(); ++index) {
					final Group group = multiMatch.getMatch(index).getGroup("BULLET"); //$NON-NLS-1$
					if (group != null) {
						final char matchBullet = group.getMatch().charAt(0);
						if ((matchBullet == (refLowerBullet + j)) || (matchBullet == (refUpperBullet + j))) {
							list.add(new Tuple<String, Integer>(group.getMatch(), group.start()));
						}
					}
				}
				++j;
			}
			
			return list;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Lookup relaxed alpha enum.
	 * 
	 * @param text
	 *            the text
	 * @param fromPosition
	 *            the from position
	 * @param toPosition
	 *            the to position
	 * @param bulletChar
	 *            the bullet char
	 * @return the integer
	 */
	private static Integer lookupRelaxedAlphaEnum(final String text,
	                                              final int fromPosition,
	                                              final int toPosition,
	                                              final char bulletChar) {
		// PRECONDITIONS
		
		try {
			final String subString = text.substring(fromPosition, toPosition);
			final Regex regex = new Regex(ALPHA_ENUM_RELAXED_PATTERN, REFlags.IGNORE_CASE);
			
			final MultiMatch multiMatch = regex.findAll(subString);
			
			if (multiMatch != null) {
				if (multiMatch.size() > 1) {
					// TODO decide what to do if we find multiple matches with relaxed search.
					// we might just render this invalid and return null
					return null;
				} else {
					return fromPosition + multiMatch.iterator().next().getFullMatch().start();
				}
			} else {
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	public static void main(final String[] args) {
		final ArrayList<String> strings = new ArrayList<>(3);
		strings.add("Richard Hawes (1797–1877) was a United States Representative from Kentucky and the second Confederate Governor of Kentucky. Originally a Whig, Hawes became a Democrat following the dissolution of the Whig party in the 1850s. At the outbreak of the American Civil War, Hawes was a supporter of Kentucky's doctrine of armed neutrality. When the Commonwealth's neutrality was breached in September 1861, Hawes fled to Virginia and enlisted as a brigade commissary under Confederate general Humphrey Marshall. He was elected Confederate governor of the Commonwealth following the late George W. Johnson's death at the Battle of Shiloh. Hawes and the Confederate government traveled with Braxton Bragg's Army of Tennessee, and when Bragg invaded Kentucky in October 1862, he captured Frankfort and held an inauguration ceremony for Hawes. The ceremony was interrupted, however, by forces under Union general Don Carlos Buell, and the Confederates were driven from the Commonwealth following the Battle of Perryville. Hawes relocated to Virginia, where he continued to lobby President Jefferson Davis to attempt another invasion of Kentucky. Following the war, he returned to his home in Paris, Kentucky, swore an oath of allegiance to the Union, and was allowed to return to his law practice.");
		strings.add("Clem Hill (1877–1945) was an Australian cricketer who played 49 Test matches as a specialist batsman between 1896 and 1912. He captained the Australian team in ten Tests, winning five and losing five. A prolific run scorer, Hill scored 3,412 runs in Test cricket—a world record at the time of his retirement—at an average of 39.21 per innings, including seven centuries. In 1902, Hill was the first batsman to make 1,000 Test runs in a calendar year, a feat that would not be repeated for 45 years. His innings of 365 scored against New South Wales for South Australia in 1900–01 was a Sheffield Shield record for 27 years. His Test cricket career ended in controversy after he was involved in a brawl with cricket administrator and fellow Test selector Peter McAlister in 1912. He was one of the \"Big Six\", a group of leading Australian cricketers who boycotted the 1912 Triangular Tournament in England when the players were stripped of the right to appoint the tour manager. The boycott effectively ended his Test career. After retiring from cricket, Hill worked in the horse racing industry as a stipendiary steward and later as a handicapper for races including the Caulfield Cup.");
		strings.add("The rings of Uranus were discovered on March 10, 1977, by James L. Elliot, Edward W. Dunham, and Douglas J. Mink. Two additional rings were discovered in 1986 by the Voyager 2 spacecraft, and two outer rings were found in 2003–2005 by the Hubble Space Telescope. A number of faint dust bands and incomplete arcs may exist between the main rings. The rings are extremely dark—the Bond albedo of the rings' particles does not exceed 2%. They are likely composed of water ice with the addition of some dark radiation-processed organics. The majority of Uranus's rings are opaque and only a few kilometres wide. The ring system contains little dust overall; it consists mostly of large bodies 0.2–20 m in diameter. The relative lack of dust in the ring system is due to aerodynamic drag from the extended Uranian exosphere—corona. The rings of Uranus are thought to be relatively young, at not more than 600 million years. The mechanism that confines the narrow rings is not well understood. The Uranian ring system probably originated from the collisional fragmentation of a number of moons that once existed around the planet. After colliding, the moons broke up into numerous particles, which survived as narrow and optically dense rings only in strictly confined zones of maximum stability.");
		
		final ParallelTopicModel topics = topics(strings.iterator());
		System.err.println(topics.getNumTopics());
	}
	
	public static final String[] removeStopwords(final String[] words) {
		return new String[0];
	}
	
	/**
	 * Sentence count.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int sentenceCount(final String text) {
		return sentences(text).size();
	}
	
	/**
	 * Sentences.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final List<String> sentences(final String text) {
		
		final List<String> sentences = new LinkedList<>();
		
		final List<String> tokenList = new ArrayList<String>();
		final List<String> whiteList = new ArrayList<String>();
		final Tokenizer tokenizer = TOKENIZER_FACTORY.tokenizer(text.toCharArray(), 0, text.length());
		tokenizer.tokenize(tokenList, whiteList);
		
		final String[] tokens = new String[tokenList.size()];
		final String[] whites = new String[whiteList.size()];
		tokenList.toArray(tokens);
		whiteList.toArray(whites);
		final int[] sentenceBoundaries = SENTENCE_MODEL.boundaryIndices(tokens, whites);
		
		int sentStartTok = 0;
		int sentEndTok = 0;
		for (int i = 0; i < sentenceBoundaries.length; ++i) {
			sentEndTok = sentenceBoundaries[i];
			
			final StringBuilder builder = new StringBuilder();
			
			for (int j = sentStartTok; j <= sentEndTok; j++) {
				builder.append(tokens[j] + whites[j + 1]);
			}
			
			sentStartTok = sentEndTok + 1;
			
			sentences.add(builder.toString().replaceAll("[\r\n]", " ")); //$NON-NLS-1$//$NON-NLS-2$
		}
		
		return sentences;
	}
	
	public static final String[] stem(final String[] tokens) {
		return new String[0];
	}
	
	public static final String[] tokenize(final String text) {
		return new String[0];
	}
	
	/**
	 * Topics.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final ParallelTopicModel topics(final Iterator<String> text) {
		
		try {
			// Begin by importing documents from text to feature sequences
			final ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
			
			// Pipes: lowercase, tokenize, remove stopwords, map to features
			pipeList.add(new CharSequenceLowercase());
			pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
			final File file = FileUtils.createRandomFile(FileShutdownAction.DELETE);
			final URL resource = Information.class.getResource("/org/mozkito/mappings/stoplist_en.txt");
			try {
				IOUtils.copyInputStream(resource.openStream(), new FileOutputStream(file));
			} catch (final IOException e1) {
				if (Logger.logError()) {
					Logger.error(e1);
				}
				throw new UnrecoverableError(e1);
			}
			pipeList.add(new TokenSequenceRemoveStopwords(file, "UTF-8", false, false, false));
			pipeList.add(new TokenSequence2FeatureSequence());
			
			final InstanceList instances = new InstanceList(new SerialPipes(pipeList));
			
			try {
				instances.addThruPipe(new StringInstanceIterator(text)); // data, label, name fields
				
				// Create a model with 100 topics, alpha_t = 0.01, beta_w = 0.01
				// Note that the first parameter is passed as the sum over topics, while
				// the second is the parameter for a single dimension of the Dirichlet prior.
				final int numTopics = 10;
				final ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
				
				model.addInstances(instances);
				
				// Use two parallel samplers, which each look at one half the corpus and combine
				// statistics after every iteration.
				model.setNumThreads(2);
				
				// Run the model for 50 iterations and stop (this is for testing only,
				// for real applications, use 1000 to 2000 iterations)
				model.setNumIterations(50);
				model.estimate();
				
				// Show the words and topics in the first instance
				
				// The data alphabet maps word IDs to strings
				final Alphabet dataAlphabet = instances.getDataAlphabet();
				
				final FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
				final LabelSequence topics = model.getData().get(0).topicSequence;
				
				Formatter out = new Formatter(new StringBuilder(), Locale.US);
				for (int position = 0; position < tokens.getLength(); position++) {
					out.format("%s-%d ", dataAlphabet.lookupObject(tokens.getIndexAtPosition(position)),
					           topics.getIndexAtPosition(position));
				}
				System.out.println(out);
				
				// Estimate the topic distribution of the first instance,
				// given the current Gibbs state.
				final double[] topicDistribution = model.getTopicProbabilities(0);
				
				// Get an array of sorted sets of word ID/count pairs
				final ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
				
				// Show top 5 words in topics with proportions for the first document
				for (int topic = 0; topic < numTopics; topic++) {
					final Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
					
					out = new Formatter(new StringBuilder(), Locale.US);
					out.format("%d\t%.3f\t", topic, topicDistribution[topic]);
					int rank = 0;
					while (iterator.hasNext() && (rank < 5)) {
						final IDSorter idCountPair = iterator.next();
						out.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()),
						           idCountPair.getWeight());
						rank++;
					}
					System.out.println(out);
				}
				
				// Create a new instance with high probability of topic 0
				final StringBuilder topicZeroText = new StringBuilder();
				final Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();
				
				int rank = 0;
				while (iterator.hasNext() && (rank < 5)) {
					final IDSorter idCountPair = iterator.next();
					topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
					rank++;
				}
				
				// Create a new instance named "test instance" with empty target and source fields.
				final InstanceList testing = new InstanceList(instances.getPipe());
				testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));
				
				final TopicInferencer inferencer = model.getInferencer();
				final double[] testProbabilities = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
				System.out.println("0\t" + testProbabilities[0]);
				
				return model;
			} catch (final IOException e) {
				throw new UnrecoverableError(e);
			}
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Word count.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int wordCount(final String text) {
		return 0;
	}
	
}

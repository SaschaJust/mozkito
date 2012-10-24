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
package de.unisaarland.cs.st.moskito.mapping.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jregex.REFlags;
import net.ownhero.dev.ioda.Tuple;
import net.ownhero.dev.regex.Group;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

/**
 * The Class Information.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
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
	
	/*
	 * single-char alphabetic identifiers (uppercase and lowercase) as well as single/double digits
	 */
	/** The Constant ACTUAL_ENUM_IDENTIFIER. */
	@SuppressWarnings ("unused")
	private static final String ACTUAL_ENUM_IDENTIFIER     = "([a-zA-Z]|[1-9][0-9]?)";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          //$NON-NLS-1$
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
	/** The Constant URL_PATTERN. */
	private static final String URL_PATTERN                = "({URL}[a-z](?:[-a-z0-9\\+\\.])*:(?:\\/\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:])*@)?(?:\\[(?:(?:(?:[0-9a-f]{1,4}:){6}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|::(?:[0-9a-f]{1,4}:){5}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){4}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:[0-9a-f]{1,4}:[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){3}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,2}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:){2}(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,3}[0-9a-f]{1,4})?::[0-9a-f]{1,4}:(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,4}[0-9a-f]{1,4})?::(?:[0-9a-f]{1,4}:[0-9a-f]{1,4}|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3})|(?:(?:[0-9a-f]{1,4}:){0,5}[0-9a-f]{1,4})?::[0-9a-f]{1,4}|(?:(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{1,4})?::)|v[0-9a-f]+[-a-z0-9\\._~!\\$&'\\(\\)\\*\\+,;=:]+)\\]|(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])(?:\\.(?:[0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])){3}|(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=@])*)(?::[0-9]*)?(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*|\\/(?:(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*)?|(?:(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))+)(?:\\/(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@]))*)*|(?!(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])))(?:\\?(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])|[\\x{E000}-\\x{F8FF}\\x{F0000}-\\x{FFFFD}|\\x{100000}-\\x{10FFFD}\\/\\?])*)?(?:\\#(?:(?:%[0-9a-f][0-9a-f]|[-a-z0-9\\._~\\x{A0}-\\x{D7FF}\\x{F900}-\\x{FDCF}\\x{FDF0}-\\x{FFEF}\\x{10000}-\\x{1FFFD}\\x{20000}-\\x{2FFFD}\\x{30000}-\\x{3FFFD}\\x{40000}-\\x{4FFFD}\\x{50000}-\\x{5FFFD}\\x{60000}-\\x{6FFFD}\\x{70000}-\\x{7FFFD}\\x{80000}-\\x{8FFFD}\\x{90000}-\\x{9FFFD}\\x{A0000}-\\x{AFFFD}\\x{B0000}-\\x{BFFFD}\\x{C0000}-\\x{CFFFD}\\x{D0000}-\\x{DFFFD}\\x{E1000}-\\x{EFFFD}!\\$&'\\(\\)\\*\\+,;=:@])|[\\/\\?])*)?)"; //$NON-NLS-1$
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
	/*
	 * X) and X ) X. X.) X] (X) and ( X ) [X] {X} X:
	 */
	/** The Constant ALPHA_ENUM_BULLET. */
	private static final String ALPHA_ENUM_BULLET          = "({BULLET}[a-zA-Z] ?\\)|[a-zA-Z]\\.|[a-zA-Z]\\.\\)|[a-zA-Z]\\]|\\( ?[a-zA-Z] ?\\)|\\[ ?[a-zA-Z] ?\\]|\\{ ?[a-zA-Z] ?\\}|[a-zA-Z]: )";
	
	/** The Constant NUMBER_ENUM_BULLET. */
	private static final String NUMBER_ENUM_BULLET         = "[1-9][0-9]? ?\\)|[1-9][0-9]?\\.|[1-9][0-9]?\\.\\)|[1-9][0-9]?\\]|\\( ?[1-9][0-9]? ?\\)|\\[ ?[1-9][0-9]? ?\\]|\\{ ?[1-9][0-9]? ?\\}|[1-9][0-9]?: ";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                //$NON-NLS-1$
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             
	/** The Constant ROMAN_ENUM_BULLET. */
	private static final String ROMAN_ENUM_BULLET          = "(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\)|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx)\\.|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx)\\.\\)|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx)\\]|\\( ?(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\)|\\[ ?(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\]|\\{ ?(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx) ?\\}|(i|ii|iii|iv|v|vi|vii|viii|ix|x|xi|xii|xiii|xiv|xv|xvi|xvii|xviii|xix|xviiii|xx): ";
	
	/** The Constant ALPHA_ENUM_RELAXED_PATTERN. */
	private static final String ALPHA_ENUM_RELAXED_PATTERN = "({BULLET}(?:%s0|%s>))";
	/*
	 * enums start: - either at the beginning of a line or - after a whitespace enums end: - end of string - next bullet
	 * - empty line if previous enum items were not separated by empty lines - line with different indentation then the
	 * previous items
	 */
	
	/** The Constant ENUMERATION_PATTERN. */
	public static final String  ENUMERATION_PATTERN        = "(^|\\s)" /* start at beginning of line or after whitespace */
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
				} else {
					// look-ahead if we find something like b) and c) and do relaxed search for a)
				}
			}
			
			lastBullet = currentBullet;
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
	public static final Tuple<String, List<String>> itemizations(final String text) {
		return new Tuple("-", new LinkedList<>()); //$NON-NLS-1$
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
	
	/**
	 * Sentence count.
	 * 
	 * @param text
	 *            the text
	 * @return the int
	 */
	public static final int sentenceCount(final String text) {
		return 0;
	}
	
	/**
	 * Sentences.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final List<String> sentences(final String text) {
		return new LinkedList<>();
	}
	
	/**
	 * Topics.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static final List<String> topics(final String text) {
		return new LinkedList<>();
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

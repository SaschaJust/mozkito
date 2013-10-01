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

package org.mozkito.infozilla.filters.sourcecode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.Ostermiller.util.CSVParser;

import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.source.SourceCode;
import org.mozkito.infozilla.model.source.SourceCode.Type;

/**
 * The JavaSourceCodeFilter class implements the InfozillaFilter interface for JAVA source code structural elements.
 * 
 * @author Nicolas Bettenburg
 * 
 */
public class JavaSourceCodeFilter extends SourceCodeFilter {
	
	/**
	 * Given a List of Code Regions transform that list to a minimal including set.
	 * 
	 * @param regionList
	 *            a List of Code Regions that should be minimized
	 * @return a minimal inclusion set of Code Regions.
	 */
	public static List<SourceCode> makeMinimalSet(final List<SourceCode> regionList) {
		// Create a copy of the Code Region List
		final List<SourceCode> sortedRegionList = new ArrayList<SourceCode>(regionList);
		// Sort it Ascending (by start position)
		java.util.Collections.sort(sortedRegionList, new Comparator<SourceCode>() {
			
			/**
			 * {@inheritDoc}
			 * 
			 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(final SourceCode o1,
			                   final SourceCode o2) {
				PRECONDITIONS: {
					// none
				}
				
				try {
					return o1.getStartPosition().compareTo(o2.getStartPosition());
				} finally {
					POSTCONDITIONS: {
						// none
					}
				}
			}
		});
		
		// This will hold the minimal set
		final List<SourceCode> minimalSet = new ArrayList<SourceCode>();
		
		// For each Element, see if it is contained in any previous element
		for (int i = 0; i < sortedRegionList.size(); i++) {
			final SourceCode thisRegion = sortedRegionList.get(i);
			boolean contained = false;
			for (int j = 0; j < i; j++) {
				final SourceCode thatRegion = sortedRegionList.get(j);
				if (thatRegion.getEndPosition() >= thisRegion.getEndPosition()) {
					contained = true;
				}
			}
			if (!contained) {
				minimalSet.add(thisRegion);
			}
		}
		return minimalSet;
	}
	
	/** Stores the codePatterns read from Java_CodeDB.txt */
	private final HashMap<String, Regex>  codePatterns       = new HashMap<>();
	
	/** Stores the code pattern options, read from Java_CodeDB.txt */
	private final HashMap<String, String> codePatternOptions = new HashMap<>();
	
	/** The option length. */
	private final int                     OPTION_LENGTH      = 3;
	
	/**
	 * Standard Constructor.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	public JavaSourceCodeFilter(final EnhancedReport enhancedReport) {
		super(enhancedReport);
		
		try {
			readCodePatterns(new InputStreamReader(getClass().getResourceAsStream("/Java_CodeDB.txt")));
		} catch (final Exception e) {
			throw new RuntimeException("Error while reading Java Source Code Patterns!");
		}
		
	}
	
	/**
	 * Overloaded Constructor.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 * @param filename
	 *            the name of the file to read Code Patterns from.
	 */
	public JavaSourceCodeFilter(final EnhancedReport enhancedReport, final String filename) {
		super(enhancedReport);
		
		try {
			readCodePatterns(new FileReader(filename));
		} catch (final Exception e) {
			throw new RuntimeException("Error while reading Java Source Code Patterns!");
		}
	}
	
	/**
	 * Overloaded Constructor.
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 * @param fileurl
	 *            a URL to a file to read Code Patterns from.
	 */
	public JavaSourceCodeFilter(final EnhancedReport enhancedReport, final URL fileurl) {
		super(enhancedReport);
		
		try {
			readCodePatterns(new InputStreamReader(fileurl.openStream()));
		} catch (final Exception e) {
			throw new RuntimeException("Error while reading Java Source Code Patterns!");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#apply(java.util.List, org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	protected void apply(final List<SourceCode> results,
	                     final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			enhancedReport.setCodeFragments(results);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * findMatch() returns the offset where the next closing is found. If not found return 0
	 * 
	 * @param where
	 *            the where
	 * @param opening
	 *            the opening
	 * @param closing
	 *            the closing
	 * @param start
	 *            the start
	 * @return the int
	 */
	private int findMatch(final String where,
	                      final char opening,
	                      final char closing,
	                      final int start) {
		final String region = where.substring(start);
		int level = 0;
		int position = 0;
		for (final char c : region.toCharArray()) {
			position = position + 1;
			if (c == opening) {
				level = level + 1;
			}
			if (c == closing) {
				if (level == 0) {
					return position;
				}
				level = level - 1;
			}
		}
		return 0;
	}
	
	/**
	 * Get a List of Source Code Regions contained in a given Text {@link s}.
	 * 
	 * @param s
	 *            the Text we shall look inside for Source Code
	 * @param minimalSet
	 *            the minimal set
	 * @return a List of Source Code occurrences as {@link SourceCode}s
	 */
	private List<SourceCode> getSourceCodes(final String s,
	                                        final boolean minimalSet) {
		final List<SourceCode> codeRegions = new ArrayList<SourceCode>();
		// for each keyword-pattern pair find the corresponding occurrences!
		for (final String keyword : this.codePatterns.keySet()) {
			final String patternOptions = this.codePatternOptions.get(keyword);
			
			if (patternOptions.contains("MATCH")) {
				final Regex regex = this.codePatterns.get(keyword);
				final MultiMatch list = regex.findAll(s);
				
				if (list != null) {
					for (final Match matches : list) {
						final int offset = findMatch(s, '{', '}', matches.getFullMatch().end());
						final SourceCode foundRegion = new SourceCode(
						                                              s.substring(matches.getFullMatch().start(),
						                                                          matches.getFullMatch().end() + offset),
						                                              Type.JAVA, matches.getFullMatch().start(),
						                                              matches.getFullMatch().end() + offset);
						codeRegions.add(foundRegion);
					}
				}
			} else {
				final Regex regex = this.codePatterns.get(keyword);
				final MultiMatch list = regex.findAll(s);
				
				if (list != null) {
					for (final Match matches : list) {
						final SourceCode foundRegion = new SourceCode(matches.getFullMatch().getMatch(), Type.JAVA,
						                                              matches.getFullMatch().start(),
						                                              matches.getFullMatch().end());
						codeRegions.add(foundRegion);
					}
				}
			}
			
		}
		
		if (minimalSet) {
			return makeMinimalSet(codeRegions);
		}
		return codeRegions;
	}
	
	/**
	 * Read in some Code Patterns from a file named {@link filename}.
	 * 
	 * @param reader
	 *            the reader
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void readCodePatterns(final Reader reader) throws IOException {
		try (final BufferedReader fileInput = new BufferedReader(reader)) {
			// Read patterns from the file
			String inputLine = null;
			while ((inputLine = fileInput.readLine()) != null) {
				// Input comes in the format: "keyword","PATTERN","OPTIONS"
				// A line can be commented out by using //
				if (!"//".equalsIgnoreCase(inputLine.substring(0, 2))) {
					// we use ostermillers CSV Parser for sake of ease
					final String[][] parsedLine = CSVParser.parse(inputLine);
					final String keyword = parsedLine[0][0];
					final String pattern = parsedLine[0][1];
					// Check if we have some options
					if (parsedLine[0].length == this.OPTION_LENGTH) {
						final String options = parsedLine[0][2];
						this.codePatternOptions.put(keyword, options);
					} else {
						this.codePatternOptions.put(keyword, "");
					}
					final Regex someRegex = new Regex(pattern);
					this.codePatterns.put(keyword, someRegex);
				}
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.filters.sourcecode.SourceCodeFilter#runFilter(java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#runFilter(java.lang.String)
	 */
	@Override
	protected List<SourceCode> runFilter(final String inputText) {
		// Find all Code Regions in the given Text inputText - by default we
		// want the minimal set
		// which means the outer most syntactical elements spanning all the
		// discovered code.
		final List<SourceCode> codeRegions = getSourceCodes(inputText, true);
		
		final List<SourceCode> list = new ArrayList<>(codeRegions.size());
		for (final SourceCode code : codeRegions) {
			list.add(code);
		}
		// Return the found source code regions
		return list;
	}
	
}

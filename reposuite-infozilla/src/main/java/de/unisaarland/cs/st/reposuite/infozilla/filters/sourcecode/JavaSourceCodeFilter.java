/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 * JavaSourceCodeFilter.java
 * 
 * @author Nicolas Bettenburg � 2009-2010, all rights reserved.
 ******************************************************************** 
 *         This file is part of infoZilla. * * InfoZilla is non-free software:
 *         you may not redistribute it * and/or modify it without the permission
 *         of the original author. * * InfoZilla is distributed in the hope that
 *         it will be useful, * but WITHOUT ANY WARRANTY; without even the
 *         implied warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *         PURPOSE. *
 ******************************************************************** 
 * 
 */

package de.unisaarland.cs.st.reposuite.infozilla.filters.sourcecode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;

import com.Ostermiller.util.CSVParser;

import de.unisaarland.cs.st.reposuite.infozilla.filters.FilterTextRemover;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaArguments;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaSettings;

/**
 * The JavaSourceCodeFilter class implements the InfozillaFilter interface for
 * JAVA source code structural elements. 
 * @author Nicolas Bettenburg
 *
 */
public class JavaSourceCodeFilter extends SourceCodeFilter {
	
	/**
	 * Given a List of Code Regions transform that list to a minimal including set
	 * @param regionList a List of Code Regions that should be minimized
	 * @return a minimal inclusion set of Code Regions.
	 */
	public static List<CodeRegion> makeMinimalSet(final List<CodeRegion> regionList) {
		// Create a copy of the Code Region List
		List<CodeRegion> sortedRegionList = new ArrayList<CodeRegion>(regionList);
		// Sort it Ascending (by start position)
		java.util.Collections.sort(sortedRegionList);
		// This will hold the minimal set
		List<CodeRegion> minimalSet = new ArrayList<CodeRegion>();
		
		// For each Element, see if it is contained in any previous element
		for (int i = 0; i < sortedRegionList.size(); i++) {
			CodeRegion thisRegion = sortedRegionList.get(i);
			boolean contained = false;
			for (int j = 0; j < i; j++) {
				CodeRegion thatRegion = sortedRegionList.get(j);
				if (thatRegion.end >= thisRegion.end) {
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
	private final HashMap<String, Pattern> codePatterns;
	
	/** Stores the code pattern options, read from Java_CodeDB.txt */
	private final HashMap<String, String>  codePatternOptions;
	
	/** The classes own textRemover */
	private FilterTextRemover              textRemover;
	
	/**
	 * Standard Constructor
	 */
	public JavaSourceCodeFilter() {
		this.codePatterns = new HashMap<String, Pattern>();
		this.codePatternOptions = new HashMap<String, String>();
	}
	
	/**
	 * Overloaded Constructor
	 * @param filename the name of the file to read Code Patterns from.
	 */
	public JavaSourceCodeFilter(final String filename) {
		this.codePatterns = new HashMap<String, Pattern>();
		this.codePatternOptions = new HashMap<String, String>();
		try {
			readCodePatterns(filename);
		} catch (Exception e) {
			System.err.println("Error while reading Java Source Code Patterns!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Overloaded Constructor
	 * @param fileurl a URL to a file to read Code Patterns from.
	 */
	public JavaSourceCodeFilter(final URL fileurl) {
		this.codePatterns = new HashMap<String, Pattern>();
		this.codePatternOptions = new HashMap<String, String>();
		try {
			readCodePatterns(fileurl.openStream());
		} catch (Exception e) {
			System.err.println("Error while reading Java Source Code Patterns!");
			e.printStackTrace();
		}
	}
	
	/**
	 * findMatch() returns the offset where the next closing is found. If not found return 0
	 */
	private int findMatch(final String where,
	                      final char opening,
	                      final char closing,
	                      final int start) {
		String region = where.substring(start);
		int level = 0;
		int position = 0;
		for (char c : region.toCharArray()) {
			position = position + 1;
			if (c == opening) {
				level = level + 1;
			}
			if (c == closing) {
				if (level == 0) {
					return position;
				} else {
					level = level - 1;
				}
			}
		}
		return 0;
	}
	
	/**
	 * Get a List of Source Code Regions contained in a given Text {@link s}
	 * @param s the Text we shall look inside for Source Code
	 * @return a List of Source Code Occurences as {@link CodeRegion}s
	 */
	private List<CodeRegion> getCodeRegions(final String s,
	                                        final boolean minimalSet) {
		List<CodeRegion> codeRegions = new ArrayList<CodeRegion>();
		// for each keyword-pattern pair find the corresponding occurences!
		for (String keyword : this.codePatterns.keySet()) {
			this.codePatterns.get(keyword);
			String patternOptions = this.codePatternOptions.get(keyword);
			if (patternOptions.contains("MATCH")) {
				Regex regex = new Regex(this.codePatterns.get(keyword).pattern());
				List<List<RegexGroup>> list = regex.findAll(s);
				
				for (List<RegexGroup> matches : list) {
					int offset = findMatch(s, '{', '}', matches.get(0).end());
					CodeRegion foundRegion = new CodeRegion(matches.get(0).start(), matches.get(0).end() + offset,
					                                        keyword, s.substring(matches.get(0).start(), matches.get(0)
					                                                                                            .end()
					                                                + offset));
					codeRegions.add(foundRegion);
				}
			} else {
				Regex regex = new Regex(this.codePatterns.get(keyword).pattern());
				List<List<RegexGroup>> list = regex.findAll(s);
				
				for (List<RegexGroup> matches : list) {
					CodeRegion foundRegion = new CodeRegion(matches.get(0).start(), matches.get(0).end(), keyword,
					                                        matches.get(0).getMatch());
					codeRegions.add(foundRegion);
				}
			}
			
		}
		
		if (minimalSet) {
			return makeMinimalSet(codeRegions);
		} else {
			return codeRegions;
		}
	}
	
	@Override
	public String getOutputText() {
		return this.textRemover.doDelete();
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Read in some Code Patterns from an input stream name {@link instream}
	 * @param instream the input stream to read the code patterns from
	 * @throws Exception if something goes wrong with I/O
	 */
	private void readCodePatterns(final InputStream instream) throws Exception {
		BufferedReader fileInput = new BufferedReader(new InputStreamReader(instream));
		// Read patterns from the file
		String inputLine = null;
		while ((inputLine = fileInput.readLine()) != null) {
			// Input comes in the format: "keyword","PATTERN","OPTIONS"
			// A line can be commented out by using //
			if (!inputLine.substring(0, 2).equalsIgnoreCase("//")) {
				// we use Ostermillers CSV Parser for sake of ease
				String[][] parsedLine = CSVParser.parse(inputLine);
				String keyword = parsedLine[0][0];
				String pattern = parsedLine[0][1];
				// Check if we have some options
				if (parsedLine[0].length == 3) {
					String options = parsedLine[0][2];
					this.codePatternOptions.put(keyword, options);
				} else {
					this.codePatternOptions.put(keyword, "");
				}
				Pattern somePattern = Pattern.compile(pattern);
				this.codePatterns.put(keyword, somePattern);
			}
		}
	}
	
	/**
	 * Read in some Code Patterns from a file named {@link filename}
	 * @param filename the full qualified filename from which to read the code patterns.
	 * @throws Exception if there did something go wrong with I/O
	 */
	private void readCodePatterns(final String filename) throws Exception {
		BufferedReader fileInput = new BufferedReader(new FileReader(filename));
		// Read patterns from the file
		String inputLine = null;
		while ((inputLine = fileInput.readLine()) != null) {
			// Input comes in the format: "keyword","PATTERN","OPTIONS"
			// A line can be commented out by using //
			if (!inputLine.substring(0, 2).equalsIgnoreCase("//")) {
				// we use ostermillers CSV Parser for sake of ease
				String[][] parsedLine = CSVParser.parse(inputLine);
				String keyword = parsedLine[0][0];
				String pattern = parsedLine[0][1];
				// Check if we have some options
				if (parsedLine[0].length == 3) {
					String options = parsedLine[0][2];
					this.codePatternOptions.put(keyword, options);
				} else {
					this.codePatternOptions.put(keyword, "");
				}
				Pattern somePattern = Pattern.compile(pattern);
				this.codePatterns.put(keyword, somePattern);
			}
		}
	}
	
	@Override
	public void register(final InfozillaSettings settings,
	                     final InfozillaArguments infozillaArguments,
	                     final boolean isRequired) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<CodeRegion> runFilter(final String inputText) {
		// Initialize a TextRemover
		this.textRemover = new FilterTextRemover(inputText);
		
		// Find all Code Regions in the given Text inputText - by default we
		// want the minimal set
		// which means the outer most syntactical elements spanning all the
		// discovered code.
		List<CodeRegion> codeRegions = getCodeRegions(inputText, true);
		
		// Mark the found Regions for deletion
		for (CodeRegion region : codeRegions) {
			this.textRemover.markForDeletion(region.start, region.end);
		}
		
		// Return the found source code regions
		return codeRegions;
	}
	
}

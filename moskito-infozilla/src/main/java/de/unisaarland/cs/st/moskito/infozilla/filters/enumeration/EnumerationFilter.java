/*******************************************************************************
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
 ******************************************************************************/
package de.unisaarland.cs.st.moskito.infozilla.filters.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.moskito.infozilla.filters.FilterTextRemover;
import de.unisaarland.cs.st.moskito.infozilla.filters.InfozillaFilter;
import de.unisaarland.cs.st.moskito.infozilla.model.itemization.Itemization;
import de.unisaarland.cs.st.moskito.infozilla.settings.InfozillaArguments;
import de.unisaarland.cs.st.moskito.infozilla.settings.InfozillaSettings;

public class EnumerationFilter extends InfozillaFilter {
	
	private FilterTextRemover textRemover;
	private String            processedText = "";
	
	/**
	 * Create an Itemization by a given regions
	 * 
	 * @param startline
	 *            the line where some enumeration starts
	 * @param endline
	 *            the last line where an enumerator symbol was found
	 * @param text
	 *            the complete text where the enumeration is in
	 * @return an {@link Itemization}
	 */
	private Itemization createEnumeration(final int startline,
	                                      final int endline,
	                                      final String text) {
		assert (startline >= 0);
		assert (endline >= 0);
		
		String[] lines = text.split("[\n\r]");
		List<String> enumLines = new ArrayList<String>();
		
		// All lines from start to end added
		for (int i = startline; i < endline; i++) {
			enumLines.add(lines[i]);
			filterLine(i, text);
		}
		// and all lines from endline till the end of the paragraph!
		int lastline = endline;
		for (int i = endline; i < lines.length; i++) {
			if (lines[i].length() == 0) {
				break;
			} else {
				enumLines.add(lines[i]);
				filterLine(i, text);
				lastline = i;
			}
		}
		
		// Calculate start position
		int eStart = 0;
		for (int i = 0; i < startline; i++) {
			eStart = eStart + lines[i].length() + 1;
		}
		// Calculate end position
		int eEnd = eStart;
		for (int i = startline; i < lastline; i++) {
			eEnd = eEnd + lines[i].length() + 1;
		}
		eEnd = eEnd + lines[lastline].length();
		
		Itemization e = new Itemization(enumLines, startline, lastline);
		e.setEnumStart(eStart);
		e.setEnumEnd(eEnd);
		return e;
	}
	
	/**
	 * Used internally to remove enumeration lines later
	 * 
	 * @param lineNum
	 * @param text
	 */
	private void filterLine(final int lineNum,
	                        final String text) {
		// System.err.println("Called to filter line number: " + lineNum);
		String[] lines = text.split("[\n\r]");
		
		// Calculate start position
		int start = 0;
		for (int i = 0; i < lineNum; i++) {
			start = start + lines[i].length() + 1;
		}
		// Calculate end position
		int end = start + lines[lineNum].length();
		/*
		 * System.err.println("Filtering Line: " + lines[lineNum]); System.err.println("Range: " + start + " - " +
		 * (end+1)); System.err.println(textRemover.getText().substring(start, (end+1))); System.out.println("}}}}" +
		 * textRemover.getText() + "{{{{\n\n");
		 */
		// Mark this range for deletion
		this.textRemover.markForDeletion(start, end + 1);
	}
	
	/**
	 * Retrieve all Enumerations that have some alphabetical letter as enumerator
	 * 
	 * @param s
	 *            The text to look inside for character enumerations
	 * @return a List of {@link Itemization}s
	 */
	private List<Itemization> getCharEnums(final String s) {
		List<Itemization> foundEnumerations = new ArrayList<Itemization>();
		
		// RegEx for Enumerations Start
		// like A A. A) A.) a a. a) a.) (A) (a) etc.
		final String regex_EnumStart = "^\\(?([a-zA-Z])(\\.|\\.\\)|\\))[a-zA-Z \t].*";
		Pattern.compile(regex_EnumStart);
		
		// Split the input into lines
		String[] lines = s.split("[\n\r]");
		
		int enumStart = -1;
		int enumEnd = -1;
		int previousEnumLineFound = -1;
		String lastFoundEnumSymbol = "";
		int symbolCount = 0;
		
		// For each Line
		for (int i = 0; i < lines.length; i++) {
			
			// Remove trailing and leading spaces
			String line = lines[i].trim();
			
			// See if the line looks like some enumeration stuff
			if (line.matches(regex_EnumStart)) {
				symbolCount++;
				// Initialize the start of an Itemization if none was found
				// before!
				if (enumStart < 0) {
					enumStart = i;
				}
				
				// Store the Symbol we found this time
				String foundEnumSymbol = "";
				Regex regex = new Regex(regex_EnumStart);
				List<List<RegexGroup>> findAll = regex.findAll(line);
				
				for (List<RegexGroup> list : findAll) {
					foundEnumSymbol = list.get(1).getMatch();
				}
				
				// Check whether the Symbol is an increase over the previous
				// Symbol
				if (foundEnumSymbol.compareTo(lastFoundEnumSymbol) > 0) {
					// If we have an increase, add all line between the last
					// enum line
					// and the current line (i) to the array of found lines
					// (lineBuffer)
					enumEnd = i;
					
				} else {
					// This indicates a new Itemization has started.
					// So we need to add all lines starting from the previous
					// enum line found
					// until the end of the paragraph (empty line);
					if (enumEnd < 0) {
						enumEnd = previousEnumLineFound;
					}
					if (symbolCount > 1) {
						Itemization someEnum = createEnumeration(enumStart, enumEnd, s);
						foundEnumerations.add(someEnum);
					}
					// Reset the counters
					enumStart = i;
					enumEnd = -1;
					symbolCount = 0;
				}
				
				lastFoundEnumSymbol = foundEnumSymbol;
				previousEnumLineFound = i;
			}
		}
		
		// At the end put all remaining gathered lines into an Itemization
		if (enumEnd < 0) {
			enumEnd = previousEnumLineFound;
		}
		
		if ((enumStart >= 0) && (symbolCount > 1)) {
			Itemization lastEnumeration = createEnumeration(enumStart, enumEnd, s);
			foundEnumerations.add(lastEnumeration);
		}
		// Return the list of found Enumerations!
		return foundEnumerations;
	}
	
	/**
	 * Runs this method to extract all Character- and Number Enumerations as well as Itemizations
	 * 
	 * @param s
	 *            The Text to look inside for enumerations and itemizations
	 * @return a List of all {@Itemization}s found.
	 */
	private List<Itemization> getEnumerationsAndItemizations(final String s) {
		// Create an empty list to put the extracted enumerations in
		List<Itemization> enumerations = new ArrayList<Itemization>();
		
		// Initialize the text filter with the text "s" we are given
		this.setProcessedText(s);
		
		// Process all types of enumerations, while extracting already found
		// items
		List<Itemization> charEnums = this.getCharEnums(s);
		
		List<Itemization> numEnums = this.getNumEnums(s);
		
		List<Itemization> itemizations = this.getItemizations(s);
		
		// All add discovered items to the final list
		enumerations.addAll(charEnums);
		enumerations.addAll(numEnums);
		enumerations.addAll(itemizations);
		
		// Return the list of enumerations - now this.processedText contains the
		// final text
		// after all processing steps (this can be used later on)
		return enumerations;
	}
	
	/**
	 * Find all Itemizations in a given Text
	 * 
	 * @param s
	 *            The text to look inside for itemizations
	 * @return a list of {@link Itemization}s for each Itemization
	 */
	private List<Itemization> getItemizations(final String s) {
		// All found itemizations will be stored in this list
		List<Itemization> foundItemizations = new ArrayList<Itemization>();
		
		// Split the input into lines
		String[] lines = s.split("[\n\r]");
		
		// Setup some counters to keep track of itemizations
		int itemizeLineCounter = 0;
		int itemizeBegin = -1;
		int itemizeEnd = -1;
		int lastItemizeEnd = -1;
		
		// For each Line
		for (int i = 0; i < lines.length; i++) {
			// Remove trailing and leading spaces
			String line = lines[i].trim();
			
			// See if the line looks like some enumeration stuff
			if (line.startsWith("- ")) {
				// If this is a itemization line, then ...
				itemizeLineCounter++;
				
				// If we haven't seen any itemization before
				if (itemizeBegin < 0) {
					itemizeBegin = i;
				}
				
				lastItemizeEnd = i;
				
			} else {
				// If this is no itemization line, then ...
				
				// if it is an empty line then
				if (line.length() == 0) {
					// create an itemization if there are at least 2 itemize
					// items
					if ((itemizeBegin >= 0) && (itemizeLineCounter > 1)) {
						// Create a new itemization
						itemizeEnd = lastItemizeEnd;
						Itemization itemization = createEnumeration(itemizeBegin, itemizeEnd, s);
						foundItemizations.add(itemization);
					}
					// And reset the counters
					itemizeBegin = -1;
					lastItemizeEnd = -1;
					itemizeEnd = -1;
					itemizeLineCounter = 0;
				}
			}
		}
		
		// If there is any remaining itemization at the end
		if ((itemizeBegin >= 0) && (itemizeLineCounter > 1)) {
			// Create a new itemization
			itemizeEnd = lastItemizeEnd;
			Itemization itemization = createEnumeration(itemizeBegin, itemizeEnd, s);
			foundItemizations.add(itemization);
		}
		
		// Return the list of found Enumerations!
		return foundItemizations;
	}
	
	/**
	 * Retrieve all Enumerations that have some number as enumerator
	 * 
	 * @param s
	 *            The text to look inside for number enumerations
	 * @return a List of {@link Itemization}s
	 */
	private List<Itemization> getNumEnums(final String s) {
		List<Itemization> foundEnumerations = new ArrayList<Itemization>();
		
		// RegEx for Enumerations Start
		// like 1 1. 1) 1.) (1) 1-
		final String regex_EnumStart = "^\\(?([0-9]+)(\\.|\\.\\)|\\)|\\-)[a-zA-Z \t].*";
		Pattern.compile(regex_EnumStart);
		
		// Split the input into lines
		String[] lines = s.split("[\n\r]");
		
		int enumStart = -1;
		int enumEnd = -1;
		int previousEnumLineFound = -1;
		int lastFoundEnumSymbol = -1;
		int symbolCount = 0;
		
		// For each Line
		for (int i = 0; i < lines.length; i++) {
			
			// Remove trailing and leading spaces
			String line = lines[i].trim();
			
			// See if the line looks like some enumeration stuff
			if (line.matches(regex_EnumStart)) {
				symbolCount++;
				
				// Initialize the start of an Itemization if none was found
				// before!
				if (enumStart < 0) {
					enumStart = i;
				}
				
				// Store the Symbol we found this time
				int foundEnumSymbol = -1;
				Regex regex = new Regex(regex_EnumStart);
				List<List<RegexGroup>> list = regex.findAll(line);
				for (List<RegexGroup> matches : list) {
					try {
						foundEnumSymbol = Integer.valueOf(matches.get(1).getMatch());
					} catch (NumberFormatException e) {
						foundEnumSymbol = Integer.MAX_VALUE;
					}
				}
				
				// Check whether the Symbol is an increase over the previous
				// Symbol
				if (foundEnumSymbol > lastFoundEnumSymbol) {
					// If we have an increase, add all line between the last
					// enum line
					// and the current line (i) to the array of found lines
					// (lineBuffer)
					enumEnd = i;
					
				} else {
					// This indicates a new Itemization has started.
					// So we need to add all lines starting from the previous
					// enum line found
					// until the end of the paragraph (empty line);
					if (enumEnd < 0) {
						enumEnd = previousEnumLineFound;
					}
					if (symbolCount > 1) {
						Itemization someEnum = createEnumeration(enumStart, enumEnd, s);
						foundEnumerations.add(someEnum);
					}
					// Reset the counters
					enumStart = i;
					enumEnd = -1;
					symbolCount = 0;
				}
				
				lastFoundEnumSymbol = foundEnumSymbol;
				previousEnumLineFound = i;
			}
		}
		
		// At the end put all remaining gathered lines into an Itemization
		if (enumEnd < 0) {
			enumEnd = previousEnumLineFound;
		}
		
		if ((enumStart >= 0) && (symbolCount > 1)) {
			Itemization lastEnumeration = createEnumeration(enumStart, enumEnd, s);
			foundEnumerations.add(lastEnumeration);
		}
		// Return the list of found Enumerations!
		return foundEnumerations;
	}
	
	@Override
	public String getOutputText() {
		return getProcessedText();
	}
	
	/**
	 * get the text after being processed by getEnumerationsAndItemizations() method this is initially empty string
	 * 
	 * @return a String that contains the text after being processed.
	 */
	private String getProcessedText() {
		if (this.textRemover == null) {
			System.err.println("We need an Instance of FilterTextRemover first!");
			System.err.println("Make sure you call setProcessedText before getProcessedText!");
			System.exit(1);
		}
		this.processedText = this.textRemover.doDelete();
		return this.processedText;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void register(final InfozillaSettings settings,
	                     final InfozillaArguments infozillaArguments,
	                     final boolean isRequired) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Itemization> runFilter(final String inputText) {
		return getEnumerationsAndItemizations(inputText);
	}
	
	// This method shall only be used internally !!
	private void setProcessedText(final String processedText) {
		this.textRemover = new FilterTextRemover(processedText);
	}
}

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
package de.unisaarland.cs.st.moskito.infozilla.filters.stacktrace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;
import de.unisaarland.cs.st.moskito.infozilla.filters.FilterTextRemover;
import de.unisaarland.cs.st.moskito.infozilla.model.stacktrace.TalkbackEntry;
import de.unisaarland.cs.st.moskito.infozilla.model.stacktrace.TalkbackTrace;

/**
 * The Class TalkBackFilter.
 */
public class TalkBackFilter extends StackTraceFilter {
	
	/** The text remover. */
	private FilterTextRemover textRemover;
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.filters.stacktrace.StackTraceFilter#getOutputText()
	 */
	@Override
	public String getOutputText() {
		return this.textRemover.doDelete();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.filters.stacktrace.StackTraceFilter#runFilter(java.lang.String)
	 */
	@Override
	public List<TalkbackTrace> runFilter(final String inputText) {
		this.textRemover = new FilterTextRemover(inputText);
		
		final List<TalkbackTrace> foundTraces = new ArrayList<TalkbackTrace>();
		final List<String> talkbackLines = new ArrayList<String>();
		
		/*
		 * This regular expression can be used to filter TalkBackStacktrace expressions ( ([ \\n\\r]*(?:.*)(?:::)(?:.*)[
		 * \\n\\r]*\\[.*?,?[ \\n\\r]*line[ \\n\\r]*[0-9]+\\]) (?:[ \\n\\r]*.*[ \\n\\r]*\\[.*?,?[ \\n\\r]*line[
		 * \\n\\r]*[0-9]+\\]) (?:[ \\n\\r]*.*?\\(\\)) (?:[ \\n\\r]*.*?\\+[ \\n\\r]*[0-9]x[0-9a-zA-Z]+[
		 * \\n\\r]*\\([0-9]x[0-9a-zA-Z]+\\)) (?:[ \\n\\r]*[0-9]x[0-9a-zA-Z]+) ){2,}
		 */
		
		final String classmethodline = "([ \\n\\r]*(?:.*)(?:::)(?:.*)[ \\n\\r]*\\[.*?,?[ \\n\\r]*line[ \\n\\r]*[0-9]+\\])";
		final String methodline = "(?:[ \\n\\r]*.*[ \\n\\r]*\\[.*?,?[ \\n\\r]*line[ \\n\\r]*[0-9]+\\])";
		final String methodcallline = "([ \\n\\r]*[^ ]*?\\(\\)[ ]*[\\n\\r])";
		final String libraryline = "(?:[ \\n\\r]*.*?\\+[ \\n\\r]*[0-9]x[0-9a-zA-Z]+[ \\n\\r]*\\([0-9]x[0-9a-zA-Z]+\\))";
		final String addressline = "(?:[ \\n\\r]*[0-9]x[0-9a-zA-Z]+)";
		
		final String trace = "^(" + classmethodline + "|" + methodcallline + "|" + methodline + "|" + libraryline + "|"
		        + addressline + "){2,}";
		
		// Compile the patterns for reuse
		final Pattern p_cml = Pattern.compile("^(" + classmethodline + ")", Pattern.MULTILINE);
		final Pattern p_mcl = Pattern.compile("^(" + methodcallline + ")", Pattern.MULTILINE);
		final Pattern p_ml = Pattern.compile("^(" + methodline + ")", Pattern.MULTILINE);
		final Pattern p_ll = Pattern.compile("^(" + libraryline + ")", Pattern.MULTILINE);
		final Pattern p_al = Pattern.compile("^(" + addressline + ")", Pattern.MULTILINE);
		Pattern.compile(trace, Pattern.MULTILINE);
		final Regex rptl1 = new Regex(trace, Pattern.MULTILINE);
		
		// Find all talkback lines
		final MultiMatch list = rptl1.findAll(inputText);
		for (final Match matches : list) {
			talkbackLines.add(matches.getFullMatch().getMatch().trim());
			this.textRemover.markForDeletion(matches.getFullMatch().start(), matches.getFullMatch().end());
		}
		
		// From each set of talkback lines create a talkback trace
		for (final String line : talkbackLines) {
			String tmp = line;
			boolean hasMore = true;
			Matcher m = null;
			
			final List<TalkbackEntry> entries = new ArrayList<TalkbackEntry>();
			while (hasMore) {
				// We assume there are no more talkback lines. If there are
				// matches this will be set to true.
				hasMore = false;
				
				// Check line for class method line
				m = p_cml.matcher(tmp);
				if (m.find()) {
					if (m.start() == 0) {
						// Format the line
						final String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						// Split into name and location
						final String[] info = tbline.split("\\[");
						final TalkbackEntry anEntry = new TalkbackEntry(info[0], info[1].replaceAll("\\]", ""),
						                                                TalkbackEntry.CLASSMETHODLINE);
						
						// Add the entry to the talkback entries list
						entries.add(anEntry);
						
						// Remove this talback entry line from the string that
						// is to
						// be processed
						tmp = tmp.substring(m.end());
						
						// Continue looking for further lines
						hasMore = true;
						continue;
					}
				}
				// Check line for address line
				m = p_al.matcher(tmp);
				if (m.find()) {
					if (m.start() == 0) {
						// Format the line
						final String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						final TalkbackEntry anEntry = new TalkbackEntry(tbline, tbline, TalkbackEntry.ADDRESSLINE);
						
						// Add the entry to the talkback entries list
						entries.add(anEntry);
						
						// Remove this talback entry line from the string that
						// is to
						// be processed
						tmp = tmp.substring(m.end());
						
						// Continue looking for further lines
						hasMore = true;
						continue;
					}
				}
				// Check line for method call line
				m = p_mcl.matcher(tmp);
				if (m.find()) {
					if (m.start() == 0) {
						// Format the line
						final String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						final TalkbackEntry anEntry = new TalkbackEntry(tbline, "", TalkbackEntry.METHODCALLLINE);
						
						// Add the entry to the talkback entries list
						entries.add(anEntry);
						
						// Remove this talback entry line from the string that
						// is to
						// be processed
						tmp = tmp.substring(m.end());
						
						// Continue looking for further lines
						hasMore = true;
						continue;
					}
				}
				// Check line for method line
				m = p_ml.matcher(tmp);
				if (m.find()) {
					if (m.start() == 0) {
						// Format the line
						final String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						// Split into name and location
						final String[] info = tbline.split("\\[");
						final TalkbackEntry anEntry = new TalkbackEntry(info[0], info[1].replaceAll("\\]", ""),
						                                                TalkbackEntry.METHODLINE);
						
						// Add the entry to the talkback entries list
						entries.add(anEntry);
						
						// Remove this talback entry line from the string that
						// is to
						// be processed
						tmp = tmp.substring(m.end());
						
						// Continue looking for further lines
						hasMore = true;
						continue;
					}
				}
				// Check line for library line
				m = p_ll.matcher(tmp);
				if (m.find()) {
					if (m.start() == 0) {
						// Format the line
						final String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						// Split into name and location
						final String[] info = tbline.split("\\(");
						final TalkbackEntry anEntry = new TalkbackEntry(info[0], info[1].replaceAll("\\)", ""),
						                                                TalkbackEntry.LIBRARYLINE);
						
						// Add the entry to the talkback entries list
						entries.add(anEntry);
						
						// Remove this talback entry line from the string that
						// is to
						// be processed
						tmp = tmp.substring(m.end());
						
						// Continue looking for further lines
						hasMore = true;
						continue;
					}
				}
			}
			if (entries.size() > 0) {
				final TalkbackTrace tbTrace = new TalkbackTrace(entries);
				foundTraces.add(tbTrace);
			}
		}
		
		return foundTraces;
	}
	
}

/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.infozilla.filters.stacktrace;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.ownhero.dev.regex.Regex;
import net.ownhero.dev.regex.RegexGroup;
import de.unisaarland.cs.st.reposuite.infozilla.filters.FilterTextRemover;
import de.unisaarland.cs.st.reposuite.infozilla.model.stacktrace.TalkbackEntry;
import de.unisaarland.cs.st.reposuite.infozilla.model.stacktrace.TalkbackTrace;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaArguments;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaSettings;

public class TalkBackFilter extends StackTraceFilter {
	
	private FilterTextRemover textRemover;
	
	@Override
	public String getOutputText() {
		return this.textRemover.doDelete();
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
	public List<TalkbackTrace> runFilter(final String inputText) {
		this.textRemover = new FilterTextRemover(inputText);
		
		List<TalkbackTrace> foundTraces = new ArrayList<TalkbackTrace>();
		List<String> talkbackLines = new ArrayList<String>();
		
		/*
		 * This regular expression can be used to filter TalkBackStacktrace
		 * expressions ( ([ \\n\\r]*(?:.*)(?:::)(?:.*)[ \\n\\r]*\\[.*?,?[
		 * \\n\\r]*line[ \\n\\r]*[0-9]+\\]) (?:[ \\n\\r]*.*[ \\n\\r]*\\[.*?,?[
		 * \\n\\r]*line[ \\n\\r]*[0-9]+\\]) (?:[ \\n\\r]*.*?\\(\\)) (?:[
		 * \\n\\r]*.*?\\+[ \\n\\r]*[0-9]x[0-9a-zA-Z]+[
		 * \\n\\r]*\\([0-9]x[0-9a-zA-Z]+\\)) (?:[ \\n\\r]*[0-9]x[0-9a-zA-Z]+)
		 * ){2,}
		 */
		
		String classmethodline = "([ \\n\\r]*(?:.*)(?:::)(?:.*)[ \\n\\r]*\\[.*?,?[ \\n\\r]*line[ \\n\\r]*[0-9]+\\])";
		String methodline = "(?:[ \\n\\r]*.*[ \\n\\r]*\\[.*?,?[ \\n\\r]*line[ \\n\\r]*[0-9]+\\])";
		String methodcallline = "([ \\n\\r]*[^ ]*?\\(\\)[ ]*[\\n\\r])";
		String libraryline = "(?:[ \\n\\r]*.*?\\+[ \\n\\r]*[0-9]x[0-9a-zA-Z]+[ \\n\\r]*\\([0-9]x[0-9a-zA-Z]+\\))";
		String addressline = "(?:[ \\n\\r]*[0-9]x[0-9a-zA-Z]+)";
		
		String trace = "^(" + classmethodline + "|" + methodcallline + "|" + methodline + "|" + libraryline + "|"
		        + addressline + "){2,}";
		
		// Compile the patterns for reuse
		Pattern p_cml = Pattern.compile("^(" + classmethodline + ")", Pattern.MULTILINE);
		Pattern p_mcl = Pattern.compile("^(" + methodcallline + ")", Pattern.MULTILINE);
		Pattern p_ml = Pattern.compile("^(" + methodline + ")", Pattern.MULTILINE);
		Pattern p_ll = Pattern.compile("^(" + libraryline + ")", Pattern.MULTILINE);
		Pattern p_al = Pattern.compile("^(" + addressline + ")", Pattern.MULTILINE);
		Pattern.compile(trace, Pattern.MULTILINE);
		Regex rptl1 = new Regex(trace, Pattern.MULTILINE);
		
		// Find all talkback lines
		List<List<RegexGroup>> list = rptl1.findAll(inputText);
		for (List<RegexGroup> matches : list) {
			talkbackLines.add(matches.get(0).getMatch().trim());
			this.textRemover.markForDeletion(matches.get(0).start(), matches.get(0).end());
		}
		
		// From each set of talkback lines create a talkback trace
		for (String line : talkbackLines) {
			String tmp = line;
			boolean hasMore = true;
			Matcher m = null;
			
			List<TalkbackEntry> entries = new ArrayList<TalkbackEntry>();
			while (hasMore) {
				// We assume there are no more talkback lines. If there are
				// matches this will be set to true.
				hasMore = false;
				
				// Check line for class method line
				m = p_cml.matcher(tmp);
				if (m.find()) {
					if (m.start() == 0) {
						// Format the line
						String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						// Split into name and location
						String[] info = tbline.split("\\[");
						TalkbackEntry anEntry = new TalkbackEntry(info[0], info[1].replaceAll("\\]", ""),
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
						String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						TalkbackEntry anEntry = new TalkbackEntry(tbline, tbline, TalkbackEntry.ADDRESSLINE);
						
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
						String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						TalkbackEntry anEntry = new TalkbackEntry(tbline, "", TalkbackEntry.METHODCALLLINE);
						
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
						String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						// Split into name and location
						String[] info = tbline.split("\\[");
						TalkbackEntry anEntry = new TalkbackEntry(info[0], info[1].replaceAll("\\]", ""),
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
						String tbline = m.group().replaceAll("[\\n\\r]", "").trim();
						
						// Split into name and location
						String[] info = tbline.split("\\(");
						TalkbackEntry anEntry = new TalkbackEntry(info[0], info[1].replaceAll("\\)", ""),
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
				TalkbackTrace tbTrace = new TalkbackTrace(entries);
				foundTraces.add(tbTrace);
			}
		}
		
		return foundTraces;
	}
	
}

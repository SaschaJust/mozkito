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
package org.mozkito.infozilla.filters.patch;

import java.util.ArrayList;
import java.util.List;

import net.ownhero.dev.kisa.Logger;
import net.ownhero.dev.regex.Match;
import net.ownhero.dev.regex.MultiMatch;
import net.ownhero.dev.regex.Regex;

import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.infozilla.model.patch.PatchHunk;
import org.mozkito.infozilla.model.patch.PatchTextElement;
import org.mozkito.infozilla.model.patch.PatchTextElement.Type;

/**
 * The Class PatchParser.
 */
public class PatchParser implements IPatchParser {
	
	/** The Constant HUNK_HEADER_PATTERN. */
	public static final String HUNK_HEADER_PATTERN = "@@\\s+-(\\d+),(\\d+)\\s+\\+(\\d+),(\\d+)\\s+@@";
	
	/**
	 * Find and extract all Hunks in a Patch.
	 * 
	 * @param lines
	 *            A set of Patch Lines
	 * @param start
	 *            The line to start looking for Hunks
	 * @return a List<PatchHunk> of Hunks that were found
	 */
	private List<PatchHunk> findAllHunks(final String[] lines,
	                                     final int start) {
		final List<PatchHunk> foundHunks = new ArrayList<PatchHunk>();
		final String lineSep = System.getProperty("line.separator");
		int hStart = start - 1;
		boolean hasMore = true;
		while (hasMore) {
			hStart = findNextHunkHeader(lines, hStart + 1);
			// Check if there are more Hunks
			if (hStart == -1) {
				// If there are no more Hunks then we are finished
				if (Logger.logDebug()) {
					Logger.debug("<>>> No More Hunks found! Finished!");
				}
				hasMore = false;
			} else {
				// If there are then look for the next Hunk start
				if (Logger.logDebug()) {
					Logger.debug("<>>> Hunk Start is " + hStart);
				}
				final int nextHunkStart = findNextHunkHeader(lines, hStart + 1);
				int searchEnd = 0;
				if (nextHunkStart == -1) {
					if (Logger.logDebug()) {
						Logger.debug("<>>> There are no more Hunks!");
					}
					// If there is no next Hunk we can process until the end
					searchEnd = lines.length;
					hasMore = false;
				} else {
					if (Logger.logDebug()) {
						Logger.debug("<>>> There are more Hunks left!");
					}
					// Otherwise we will look only until the next Hunk beginning
					searchEnd = nextHunkStart - 1;
				}
				if (Logger.logDebug()) {
					Logger.debug("<>>> Will look for HunkLines from " + (hStart + 1) + " to " + (searchEnd - 1));
				}
				String hunktext = "";
				for (int i = hStart; i < searchEnd; i++) {
					if (Logger.logDebug()) {
						Logger.debug("<>>> Checking if Hunkline: " + lines[i]);
					}
					if (isHunkLine(lines[i])) {
						if (Logger.logDebug()) {
							Logger.debug("<>>> Yes it is!");
						}
						hunktext = hunktext + lines[i] + lineSep;
					} else {
						if (i < (searchEnd - 1)) {
							if (isHunkLine(lines[i + 1])) {
								if (Logger.logDebug()) {
									Logger.debug("<>>> No But next line is!");
								}
								hunktext = hunktext + lines[i] + lineSep;
							} else {
								// we are done
								if (Logger.logDebug()) {
									Logger.debug("<>>> No it is not and niether is the next one! We should stop here!");
								}
								searchEnd = i;
							}
						}
					}
				}
				// Kill last newline
				if (hunktext.length() > 1) {
					hunktext = hunktext.substring(0, hunktext.length() - 1);
				}
				
				final PatchHunk hunk = parseHunk(hunktext);
				if (hunk != null) {
					foundHunks.add(hunk);
				}
				hStart = nextHunkStart - 1;
			}
		}
		return foundHunks;
	}
	
	/**
	 * Find the first line that starts with a given String.
	 * 
	 * @param text
	 *            The text the line we look for starts with
	 * @param lines
	 *            An Array of lines
	 * @param start
	 *            The line number to start the search with
	 * @return The index of the first line starting at {@link start} or -1 if there is no such line
	 */
	private int findFirstLineBeginningWith(final String text,
	                                       final String[] lines,
	                                       final int start) {
		int found = -1;
		for (int i = start; i < lines.length; i++) {
			if (lines[i].startsWith(text)) {
				found = i;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Find the first line that starts with a given String.
	 * 
	 * @param text
	 *            The text the line we look for starts with
	 * @param lines
	 *            An Array of lines
	 * @param start
	 *            The line number to start the search with
	 * @return The first line starting at {@link start} or an empty String if there is no such line
	 */
	private String findFirstLineBeginningWithS(final String text,
	                                           final String[] lines,
	                                           final int start) {
		String found = "";
		for (int i = start; i < lines.length; i++) {
			if (lines[i].startsWith(text)) {
				found = lines[i];
				break;
			}
		}
		return found;
	}
	
	/**
	 * Find the next Hunk start beginning from {@link start} in a set of {@link lines}.
	 * 
	 * @param lines
	 *            The lines to search for the next Hunk start.
	 * @param start
	 *            The line from which to start looking for the next Hunk.
	 * @return The linenumber where the next Hunk start was found.
	 */
	private int findNextHunkHeader(final String[] lines,
	                               final int start) {
		int found = -1;
		for (int i = start; i < lines.length; i++) {
			// Find the next line that matches @@ -X,Y +XX,YY @@
			if (lines[i].matches("^@@\\s\\-\\d+,\\d+\\s\\+\\d+,\\d+\\s@@$")) {
				found = i;
				break;
			}
		}
		return found;
	}
	
	/**
	 * Find the next line among the set of lines[] that looks like a Patch Index: line.
	 * 
	 * @param lines
	 *            the lines in which to look for the patch start.
	 * @param start
	 *            the starting line number from where to start the search.
	 * @return the linenumber where we found the next patch start from {@link start}.
	 */
	private int findNextIndex(final String[] lines,
	                          final int start) {
		int found = -1;
		for (int i = start; i < (lines.length - 1); i++) {
			// Find the next line that starts with "Index: "
			if (lines[i].startsWith("Index: ")) {
				// Check if the following line starts with "====="
				if (lines[i + 1].startsWith("====")) {
					found = i;
					break;
				}
				
			}
		}
		return found;
	}
	
	/**
	 * Checks whether the given line is a line that belongs to a hunk or not.
	 * 
	 * @param line
	 *            the line to check for being a Hunk Line.
	 * @return true if the {@link line} is a Hunk line, false otherwise.
	 */
	private boolean isHunkLine(final String line) {
		final boolean isHunkLine = (!line.isEmpty() && (('+' == line.charAt(0)) || ('-' == line.charAt(0))
		        || (' ' == line.charAt(0)) || (PatchTextElement.NBSP == line.charAt(0)))); // char 160 = &nbsp;
		return isHunkLine;
	}
	
	/**
	 * Parses a given text for all Patches inside using a 2 line lookahead Fuzzy Parser approach.
	 * 
	 * @param text
	 *            The text to extract Patches from.
	 * @return a list of found Patches.
	 */
	public List<Patch> parseForPatches(final String text) {
		// Start with an empty list of Patches
		final List<Patch> foundPatches = new ArrayList<Patch>();
		
		// First Partition the whole given text into sections starting with
		// Index:
		// The parts of the partition mark on potential patch
		final List<String> indexPartition = partitionByIndex(text);
		
		// For each potential patch area split into header and a list of
		// potential hunks
		for (final String potentialPatch : indexPartition) {
			final String[] lines = potentialPatch.split("[\n\r]");
			
			final Patch patch = new Patch();
			// Gather Header Information of the Patch
			final String pIndex = findFirstLineBeginningWithS("Index: ", lines, 0);
			patch.setIndex(pIndex);
			final String pOrig = findFirstLineBeginningWithS("--- ", lines, 0);
			patch.setOriginalFile(pOrig);
			final String pModi = findFirstLineBeginningWithS("+++ ", lines, 0);
			patch.setModifiedFile(pModi);
			
			// Find the first Hunk Header
			final int pModiNum = findFirstLineBeginningWith("+++ ", lines, 0);
			final int firstHunkLine = findNextHunkHeader(lines, pModiNum + 1);
			
			// If there is no Hunk then the patch is invalid!
			if (firstHunkLine == -1) {
				break;
			}
			
			// Now we can add the complete Header
			String header = "";
			for (int i = 0; i < (firstHunkLine - 1); i++) {
				header = header + lines[i] + System.getProperty("line.separator");
			}
			header = header + lines[firstHunkLine - 1];
			// patch.setHeader(header);
			
			// Discover all Hunks!
			final List<PatchHunk> hunks = findAllHunks(lines, firstHunkLine);
			
			// And add the Hunks to the List of Hunks for this patch
			for (final PatchHunk hunk : hunks) {
				SANITY: {
					assert hunk != null;
					assert hunk.getStartPosition() != null;
					assert hunk.getStartPosition() >= 0;
					assert hunk.getEndPosition() != null;
					assert hunk.getStartPosition() < hunk.getEndPosition();
				}
				
				patch.setStartPosition(patch.getStartPosition() == null
				                                                       ? hunk.getStartPosition()
				                                                       : Math.min(patch.getStartPosition(),
				                                                                  hunk.getStartPosition()));
				patch.setStartPosition(patch.getEndPosition() == null
				                                                     ? hunk.getEndPosition()
				                                                     : Math.max(patch.getEndPosition(),
				                                                                hunk.getEndPosition()));
				patch.addHunk(hunk);
			}
			
			if (!patch.getHunks().isEmpty()) {
				foundPatches.add(patch);
			}
		}
		
		// Locate the Patches in the Source Code
		for (final Patch p : foundPatches) {
			SANITY: {
				assert p != null;
				assert p != p.getHunks();
				assert !p.getHunks().isEmpty();
			}
			
			final Integer patchStart = p.getHunks().iterator().next().getStartPosition();
			final Integer patchEnd = p.getHunks().listIterator(p.getHunks().size()).previous().getEndPosition();
			// final int patchEnd = text.lastIndexOf(p.getHunks().get(p.getHunks().size() - 1).getText())
			// + p.getHunks().get(p.getHunks().size() - 1).getText().length();
			
			p.setStartPosition(patchStart);
			p.setEndPosition(patchEnd);
		}
		
		// Here is the patch we found
		return foundPatches;
	}
	
	/**
	 * Parses the hunk.
	 * 
	 * @param text
	 *            the text
	 * @return the patch hunk
	 */
	private PatchHunk parseHunk(final String text) {
		PRECONDITIONS: {
			if (text == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			if (text.isEmpty()) {
				return null;
			}
			
			SANITY: {
				assert text.charAt(0) == '@';
			}
			
			final Regex regex = new Regex(PatchParser.HUNK_HEADER_PATTERN);
			final MultiMatch multiMatch = regex.findAll(text);
			
			SANITY: {
				assert multiMatch != null : String.format("Regular expression '%s' did not match: %s",
				                                          PatchParser.HUNK_HEADER_PATTERN, text);
				assert multiMatch.size() == 1;
			}
			
			final Match match = multiMatch.iterator().next();
			final PatchHunk.Builder builder = new PatchHunk.Builder();
			
			SANITY: {
				assert match.getGroupCount() == 4;
			}
			
			final int oldStart = Integer.parseInt(match.getGroup(1).getMatch());
			final int oldEnd = Integer.parseInt(match.getGroup(2).getMatch());
			final int newStart = Integer.parseInt(match.getGroup(3).getMatch());
			final int newEnd = Integer.parseInt(match.getGroup(4).getMatch());
			
			builder.newStart(newStart).oldStart(oldStart).newEnd(newEnd).oldEnd(oldEnd).create();
			
			SANITY: {
				assert (match.getFullMatch().end() + 1) < text.length();
			}
			
			final String string = text.substring(match.getFullMatch().end() + 1);
			final String[] split = string.split("\\r\\n|\\r|\\n");
			
			SANITY: {
				assert split != null;
				assert split.length > 0;
			}
			
			PatchTextElement previousElement = null;
			for (final String line : split) {
				SANITY: {
					assert !line.isEmpty();
				}
				
				switch (line.charAt(0)) {
					case ' ':
					case PatchTextElement.NBSP:
						builder.addElement(previousElement = new PatchTextElement(Type.CONTEXT, line.substring(1)));
						break;
					case '+':
						builder.addElement(previousElement = new PatchTextElement(Type.ADDED, line.substring(1)));
						break;
					case '-':
						builder.addElement(previousElement = new PatchTextElement(Type.REMOVED, line.substring(1)));
						break;
					default:
						if (previousElement != null) {
							previousElement.setText(previousElement.getText() + ' ' + line);
							if (Logger.logWarn()) {
								Logger.warn("Assuming line break. Adding '%s' to previous line resulting in: %s", line,
								            previousElement.getText());
							}
						} else {
							assert false : String.format("Hunk starts with unsupported character '%s' (%s).",
							                             line.charAt(0), (int) line.charAt(0));
						}
				}
			}
			
			return builder.create();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Splits a text into a List of possible Patches.
	 * 
	 * @param text
	 *            The text to split into patches.
	 * @return a List of possible Patches.
	 */
	private List<String> partitionByIndex(final String text) {
		// This will be a list of all potential Patch Areas
		final List<String> indexPartition = new ArrayList<String>();
		
		// Split the complete text into single lines to work with
		final String[] lines = text.split("[\n\r]");
		
		// When we start we think there are more Patches inside ;)
		boolean hasMore = true;
		
		// We start at the very beginning of our text
		int idxStart = -1;
		
		// Find all areas
		while (hasMore) {
			idxStart = findNextIndex(lines, idxStart + 1);
			if (idxStart == -1) {
				// if there is no next start we are done
				hasMore = false;
			} else {
				// otherwise see if there is another index
				final int idxEnd = findNextIndex(lines, idxStart + 1);
				if (idxEnd == -1) {
					// add the whole range because there is no more next idx
					// start
					String range = "";
					for (int i = idxStart; i < (lines.length - 1); i++) {
						range = range + lines[i] + System.getProperty("line.separator");
					}
					range = range + lines[lines.length - 1];
					indexPartition.add(range);
				} else {
					// there is another index start so add the range to the
					// partition
					String range = "";
					for (int i = idxStart; i < idxEnd; i++) {
						range = range + lines[i] + System.getProperty("line.separator");
					}
					indexPartition.add(range);
					
					// and set the new idxStart to end !
					idxStart = idxEnd - 1;
				}
			}
		}
		
		return indexPartition;
	}
}

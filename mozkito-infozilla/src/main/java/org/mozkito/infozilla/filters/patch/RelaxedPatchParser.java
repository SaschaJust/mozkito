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

import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.infozilla.model.patch.PatchHunk;
import org.mozkito.infozilla.model.patch.Patch;

/**
 * The Class RelaxedPatchParser.
 */
public class RelaxedPatchParser {
	
	// Uncomment the following line if you need debug output
	/** The Constant debug. */
	private static final boolean DEBUG = false;
	
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
				if (RelaxedPatchParser.DEBUG) {
					// If there are no more Hunks then we are finished
					System.out.println("<>>> No More Hunks found! Finished!");
				}
				hasMore = false;
			} else {
				// If there are then look for the next Hunk start
				if (RelaxedPatchParser.DEBUG) {
					System.out.println("<>>> Hunk Start is " + hStart);
				}
				final int nextHunkStart = findNextHunkHeader(lines, hStart + 1);
				int searchEnd = 0;
				if (nextHunkStart == -1) {
					if (RelaxedPatchParser.DEBUG) {
						System.out.println("<>>> There are no more Hunks!");
					}
					// If there is no next Hunk we can process until the end
					searchEnd = lines.length;
					hasMore = false;
				} else {
					if (RelaxedPatchParser.DEBUG) {
						System.out.println("<>>> There are more Hunks left!");
					}
					// Otherwise we will look only until the next Hunk beginning
					searchEnd = nextHunkStart - 1;
				}
				if (RelaxedPatchParser.DEBUG) {
					System.out.println("<>>> Will look for HunkLines from " + (hStart + 1) + " to " + (searchEnd - 1));
				}
				String hunktext = "";
				for (int i = hStart + 1; i < searchEnd; i++) {
					if (RelaxedPatchParser.DEBUG) {
						System.out.println("<>>> Checking if Hunkline: " + lines[i]);
					}
					if (isHunkLine(lines[i])) {
						if (RelaxedPatchParser.DEBUG) {
							System.out.println("<>>> Yes it is!");
						}
						hunktext = hunktext + lines[i] + lineSep;
					} else {
						if (i < (searchEnd - 1)) {
							if (isHunkLine(lines[i + 1])) {
								if (RelaxedPatchParser.DEBUG) {
									System.out.println("<>>> No But next line is!");
								}
								hunktext = hunktext + lines[i] + lineSep;
							} else {
								// we are done
								if (RelaxedPatchParser.DEBUG) {
									System.out.println("<>>> No it is not and niether is the next one! We should stop here!");
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
				foundHunks.add(new PatchHunk(hunktext));
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
			if (lines[i].matches("^@@\\s\\-\\d+,\\d+\\s\\+\\d+,\\d+\\s@@.*?$")) {
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
			// Find the next line that starts with "+++ "
			if (lines[i].startsWith("--- ")) {
				// Check if the following line starts with "--- "
				if (lines[i + 1].startsWith("+++ ")) {
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
		final boolean isHunkLine = ((line.startsWith("+")) || (line.startsWith("-")) || (line.startsWith(" ")));
		return isHunkLine;
	}
	
	/**
	 * Parses a given text for all Patches inside using a 2 line lookahead Fuzzy Parser approach.
	 * 
	 * @param text
	 *            The text to extract Patches from.
	 * @return a list of found patches
	 */
	public List<? extends Patch> parseForPatches(final String text) {
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
			patch.setHeader(header);
			
			// Discover all Hunks!
			final List<PatchHunk> hunks = findAllHunks(lines, firstHunkLine);
			
			// And add the Hunks to the List of Hunks for this patch
			for (final PatchHunk h : hunks) {
				patch.addHunk(h);
			}
			foundPatches.add(patch);
		}
		
		// Locate the Patches in the Source Code
		for (final Patch p : foundPatches) {
			final Patch u = (Patch) p;
			final int patchStart = text.indexOf(u.getHeader());
			
			final int patchEnd = text.lastIndexOf(u.getHunks().get(u.getHunks().size() - 1).getText())
			        + u.getHunks().get(u.getHunks().size() - 1).getText().length();
			
			u.setStartPosition(patchStart);
			u.setEndPosition(patchEnd);
		}
		
		// Here is the patch we found
		return foundPatches;
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

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
/**
 * 
 */
package de.unisaarland.cs.st.moskito.infozilla.model.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.ownhero.dev.regex.Regex;

/**
 * The Class UnifiedDiff.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class UnifiedDiff extends Patch {
	
	/** The index. */
	private String                index        = "";
	
	/** The original file. */
	private String                originalFile = "";
	
	/** The modified file. */
	private String                modifiedFile = "";
	
	/** The header. */
	private String                header       = "";
	
	/** The start position. */
	private int                   startPosition;
	
	/** The end position. */
	private int                   endPosition;
	
	/** The hunks. */
	private final List<PatchHunk> hunks;
	
	/**
	 * Instantiates a new unified diff.
	 */
	public UnifiedDiff() {
		this.hunks = new ArrayList<PatchHunk>();
		this.startPosition = 0;
		this.endPosition = 0;
	}
	
	/**
	 * Instantiates a new unified diff.
	 * 
	 * @param s
	 *            the s
	 */
	public UnifiedDiff(final int s) {
		this.hunks = new ArrayList<PatchHunk>();
		this.startPosition = s;
	}
	
	/**
	 * Instantiates a new unified diff.
	 * 
	 * @param s
	 *            the s
	 * @param e
	 *            the e
	 */
	public UnifiedDiff(final int s, final int e) {
		this.hunks = new ArrayList<PatchHunk>();
		this.startPosition = s;
		this.endPosition = e;
	}
	
	/**
	 * Adds the hunk.
	 * 
	 * @param hunk
	 *            the hunk
	 */
	public void addHunk(final PatchHunk hunk) {
		this.hunks.add(hunk);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.model.Inlineable#getEndPosition()
	 */
	@Override
	public int getEndPosition() {
		return this.endPosition;
	}
	
	/**
	 * Gets the header.
	 * 
	 * @return the header
	 */
	public String getHeader() {
		return this.header;
	}
	
	/**
	 * Gets the hunks.
	 * 
	 * @return the hunks
	 */
	public List<PatchHunk> getHunks() {
		return this.hunks;
	}
	
	/**
	 * Gets the index.
	 * 
	 * @return the index
	 */
	public String getIndex() {
		if (this.index.length() > 7) {
			return (this.index.substring(7, this.index.length()));
		}
		return this.index;
	}
	
	/**
	 * Gets the modified file.
	 * 
	 * @return the modified file
	 */
	public String getModifiedFile() {
		return PlusMinusLineToFilename(this.modifiedFile);
	}
	
	/**
	 * Gets the original file.
	 * 
	 * @return the original file
	 */
	public String getOriginalFile() {
		return PlusMinusLineToFilename(this.originalFile);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.infozilla.model.Inlineable#getStartPosition()
	 */
	@Override
	public int getStartPosition() {
		return this.startPosition;
	}
	
	/**
	 * Plus minus line to filename.
	 * 
	 * @param input
	 *            the input
	 * @return the string
	 */
	public String PlusMinusLineToFilename(final String input) {
		String temp = input;
		final String pmreg = "([-]{3}|[+]{3})([ \\r\\n\\t]({filename}.*?)[ \\t])";
		final Regex regex = new Regex(pmreg, Pattern.MULTILINE);
		
		if ((regex.find(input)) != null) {
			temp = regex.getGroup("filename").trim();
		}
		
		return temp;
	}
	
	/**
	 * Sets the end position.
	 * 
	 * @param endPosition
	 *            the new end position
	 */
	public void setEndPosition(final int endPosition) {
		this.endPosition = endPosition;
	}
	
	/**
	 * Sets the header.
	 * 
	 * @param header
	 *            the new header
	 */
	public void setHeader(final String header) {
		this.header = header;
	}
	
	/**
	 * Sets the index.
	 * 
	 * @param index
	 *            the new index
	 */
	public void setIndex(final String index) {
		this.index = index;
	}
	
	/**
	 * Sets the modified file.
	 * 
	 * @param modifiedFile
	 *            the new modified file
	 */
	public void setModifiedFile(final String modifiedFile) {
		this.modifiedFile = modifiedFile;
	}
	
	/**
	 * Sets the original file.
	 * 
	 * @param originalFile
	 *            the new original file
	 */
	public void setOriginalFile(final String originalFile) {
		this.originalFile = originalFile;
	}
	
	/**
	 * Sets the start position.
	 * 
	 * @param startPosition
	 *            the new start position
	 */
	public void setStartPosition(final int startPosition) {
		this.startPosition = startPosition;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String s = "";
		final String lineSep = System.getProperty("line.separator");
		s = s + this.index + lineSep;
		s = s + "ORIGINAL=" + getOriginalFile() + lineSep;
		s = s + "MODIFIED=" + getModifiedFile() + lineSep;
		s = s + "#HUNKS=" + Integer.valueOf(this.hunks.size()) + lineSep;
		return s;
	}
	
}

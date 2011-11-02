/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class UnifiedDiff extends Patch {
	
	private String                index        = "";
	private String                originalFile = "";
	private String                modifiedFile = "";
	private String                header       = "";
	private int                   startPosition;
	private int                   endPosition;
	
	private final List<PatchHunk> hunks;
	
	public UnifiedDiff() {
		this.hunks = new ArrayList<PatchHunk>();
		this.startPosition = 0;
		this.endPosition = 0;
	}
	
	public UnifiedDiff(final int s) {
		this.hunks = new ArrayList<PatchHunk>();
		this.startPosition = s;
	}
	
	public UnifiedDiff(final int s, final int e) {
		this.hunks = new ArrayList<PatchHunk>();
		this.startPosition = s;
		this.endPosition = e;
	}
	
	public void addHunk(final PatchHunk hunk) {
		this.hunks.add(hunk);
	}
	
	public int getEndPosition() {
		return this.endPosition;
	}
	
	public String getHeader() {
		return this.header;
	}
	
	public List<PatchHunk> getHunks() {
		return this.hunks;
	}
	
	public String getIndex() {
		if (this.index.length() > 7) {
			return (this.index.substring(7, this.index.length()));
		} else {
			return this.index;
		}
	}
	
	public String getModifiedFile() {
		return PlusMinusLineToFilename(this.modifiedFile);
	}
	
	public String getOriginalFile() {
		return PlusMinusLineToFilename(this.originalFile);
	}
	
	public int getStartPosition() {
		return this.startPosition;
	}
	
	public String PlusMinusLineToFilename(final String input) {
		String temp = input;
		String pmreg = "([-]{3}|[+]{3})([ \\r\\n\\t]({filename}.*?)[ \\t])";
		Regex regex = new Regex(pmreg, Pattern.MULTILINE);
		
		if ((regex.find(input)) != null) {
			temp = regex.getGroup("filename").trim();
		}
		
		return temp;
	}
	
	public void setEndPosition(final int endPosition) {
		this.endPosition = endPosition;
	}
	
	public void setHeader(final String header) {
		this.header = header;
	}
	
	public void setIndex(final String index) {
		this.index = index;
	}
	
	public void setModifiedFile(final String modifiedFile) {
		this.modifiedFile = modifiedFile;
	}
	
	public void setOriginalFile(final String originalFile) {
		this.originalFile = originalFile;
	}
	
	public void setStartPosition(final int startPosition) {
		this.startPosition = startPosition;
	}
	
	@Override
	public String toString() {
		String s = "";
		String lineSep = System.getProperty("line.separator");
		s = s + this.index + lineSep;
		s = s + "ORIGINAL=" + getOriginalFile() + lineSep;
		s = s + "MODIFIED=" + getModifiedFile() + lineSep;
		s = s + "#HUNKS=" + Integer.valueOf(this.hunks.size()) + lineSep;
		return s;
	}
	
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.infozilla.model.patch;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class UnifiedDiff extends Patch {
	
	class Hunk {
		
		Hunk() {
			
		}
	}
	
	private final String     originalFile = "";
	private final String     modifiedFile = "";
	private final String     header       = "";
	
	private final List<Hunk> hunks        = new LinkedList<UnifiedDiff.Hunk>();
	
	/**
	 * 
	 */
	public UnifiedDiff() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the header
	 */
	public String getHeader() {
		return this.header;
	}
	
	/**
	 * @return the hunks
	 */
	public List<Hunk> getHunks() {
		return this.hunks;
	}
	
	/**
	 * @return the modifiedFile
	 */
	public String getModifiedFile() {
		return this.modifiedFile;
	}
	
	/**
	 * @return the originalFile
	 */
	public String getOriginalFile() {
		return this.originalFile;
	}
	
}

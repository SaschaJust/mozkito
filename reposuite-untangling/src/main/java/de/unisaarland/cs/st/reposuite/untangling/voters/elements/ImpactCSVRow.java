package de.unisaarland.cs.st.reposuite.untangling.voters.elements;

import java.util.HashSet;
import java.util.Set;

import de.unisaarland.cs.st.reposuite.utils.Regex;

/**
 * The Class ImpactCSVRow.
 */
public class ImpactCSVRow {
	
	/** The rev. */
	private final long     rev;
	
	/** The next rev. */
	private final long     nextRev;
	
	/** The num diff. */
	private final long     numDiff;
	
	/** The diff meth. */
	private final String[] diffMeth;
	
	/** The diff class. */
	private Set<String>    diffClass  = null;
	
	/** The anon regex. */
	protected Regex        anonRegex  = new Regex("({anaon}(\\.\\d)+$)");
	
	/** The inner regex. */
	protected Regex        innerRegex = new Regex("({inner}\\$.+)");
	
	/**
	 * Instantiates a new impact csv row.
	 * 
	 * @param rev
	 *            the rev
	 * @param nextRev
	 *            the next rev
	 * @param numDiff
	 *            the num diff
	 * @param diffMeth
	 *            the diff meth
	 */
	public ImpactCSVRow(final long rev, final long nextRev, final long numDiff, final String[] diffMeth) {
		this.rev = rev;
		this.nextRev = nextRev;
		this.numDiff = numDiff;
		this.diffMeth = diffMeth;
	}
	
	/**
	 * Gets the diff class.
	 * 
	 * @return the diff class
	 */
	public String[] getDiffClass() {
		if (diffClass == null) {
			diffClass = new HashSet<String>();
			for (String meth : diffMeth) {
				int index = meth.indexOf("@");
				String clean_meth = meth;
				if (index >= 0) {
					clean_meth = meth.substring(0, index);
				}
				// check if anonymous class
				clean_meth = anonRegex.removeAll(clean_meth);
				clean_meth = innerRegex.removeAll(clean_meth);
				diffClass.add(clean_meth.trim());
				
			}
		}
		String[] result = diffClass.toArray(new String[diffClass.size()]);
		return result;
	}
	
	/**
	 * Gets the diff meth.
	 * 
	 * @return the diff meth
	 */
	public String[] getDiffMeth() {
		String[] result = new String[diffMeth.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = diffMeth[i].replace("@", ".");
		}
		
		return result;
	}
	
	/**
	 * Gets the next rev.
	 * 
	 * @return the next rev
	 */
	public long getNextRev() {
		return nextRev;
	}
	
	/**
	 * Gets the num diff.
	 * 
	 * @return the num diff
	 */
	public long getNumDiff() {
		return numDiff;
	}
	
	/**
	 * Gets the rev.
	 * 
	 * @return the rev
	 */
	public long getRev() {
		return rev;
	}
	
}

package de.unisaarland.cs.st.reposuite.infozilla.filters.patch;

import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.Elements.Patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.filters.FilterTextRemover;

/**
 * This Filter class acts as an interface for the PatchParser class
 * that has to be instantiated before using it.
 * @author Nicolas Bettenburg
 *
 */
public class UnifiedDiffPatchFilter extends PatchFilter {
	
	private FilterTextRemover textRemover;
	private boolean           relaxed = false;
	
	public UnifiedDiffPatchFilter() {
	}
	
	public String getOutputText() {
		return this.textRemover.doDelete();
	}
	
	/**
	 * Filter a list of {@link Patch}es from a text {@link s}
	 * @param text the text we should look for patches inside
	 * @return a List of {@link Patch}es.
	 */
	private List<Patch> getPatches(final String text) {
		// Setup Helper classes
		this.textRemover = new FilterTextRemover(text);
		
		// Find Patches
		List<Patch> foundPatches = null;
		if (isRelaxed()) {
			RelaxedPatchParser pp = new RelaxedPatchParser();
			foundPatches = pp.parseForPatches(text);
		} else {
			PatchParser pp = new PatchParser();
			foundPatches = pp.parseForPatches(text);
		}
		
		// Filter them out
		for (Patch patch : foundPatches) {
			this.textRemover.markForDeletion(patch.getStartPosition(), patch.getEndPosition());
		}
		return foundPatches;
	}
	
	public boolean isRelaxed() {
		return this.relaxed;
	}
	
	public List<Patch> runFilter(final String inputText) {
		return getPatches(inputText);
	}
	
	public void setRelaxed(final boolean relaxed) {
		this.relaxed = relaxed;
	}
}

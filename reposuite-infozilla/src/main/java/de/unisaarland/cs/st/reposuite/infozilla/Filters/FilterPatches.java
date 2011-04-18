package de.unisaarland.cs.st.reposuite.infozilla.Filters;

import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.Elements.Patch.Patch;

/**
 * This Filter class acts as an interface for the PatchParser class
 * that has to be instantiated before using it.
 * @author Nicolas Bettenburg
 *
 */
public class FilterPatches implements IFilter {
	
	private FilterTextRemover textRemover;
	private boolean           relaxed = false;
	
	public FilterPatches() {
	}
	
	/**
	 * Filter a list of {@link Patch}es from a text {@link s}
	 * @param text the text we should look for patches inside
	 * @return a List of {@link Patch}es.
	 */
	private List<Patch> getPatches(String text) {
		// Setup Helper classes
		textRemover = new FilterTextRemover(text);
		
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
			textRemover.markForDeletion(patch.getStartPosition(), patch.getEndPosition());
		}
		return foundPatches;
	}
	
	public String getOutputText() {
		return textRemover.doDelete();
	}
	
	public List<Patch> runFilter(String inputText) {
		return getPatches(inputText);
	}
	
	public boolean isRelaxed() {
		return relaxed;
	}
	
	public void setRelaxed(boolean relaxed) {
		this.relaxed = relaxed;
	}
}

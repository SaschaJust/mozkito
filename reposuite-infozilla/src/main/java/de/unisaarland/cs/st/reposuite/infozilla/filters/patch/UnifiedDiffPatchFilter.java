package de.unisaarland.cs.st.reposuite.infozilla.filters.patch;

import java.util.List;

import de.unisaarland.cs.st.reposuite.infozilla.filters.FilterTextRemover;
import de.unisaarland.cs.st.reposuite.infozilla.model.patch.Patch;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaArguments;
import de.unisaarland.cs.st.reposuite.infozilla.settings.InfozillaSettings;

/**
 * This InfozillaFilter class acts as an interface for the PatchParser class
 * that has to be instantiated before using it.
 * @author Nicolas Bettenburg
 *
 */
public class UnifiedDiffPatchFilter extends PatchFilter {
	
	private FilterTextRemover textRemover;
	private boolean           relaxed = false;
	
	public UnifiedDiffPatchFilter() {
	}
	
	@Override
	public String getOutputText() {
		return this.textRemover.doDelete();
	}
	
	/**
	 * InfozillaFilter a list of {@link Patch}es from a text {@link s}
	 * @param text the text we should look for patches inside
	 * @return a List of {@link Patch}es.
	 */
	@SuppressWarnings ("unchecked")
	private List<Patch> getPatches(final String text) {
		// Setup Helper classes
		this.textRemover = new FilterTextRemover(text);
		
		// Find Patches
		List<Patch> foundPatches = null;
		if (isRelaxed()) {
			RelaxedPatchParser pp = new RelaxedPatchParser();
			foundPatches = (List<Patch>) pp.parseForPatches(text);
		} else {
			PatchParser pp = new PatchParser();
			foundPatches = (List<Patch>) pp.parseForPatches(text);
		}
		
		// InfozillaFilter them out
		for (Patch patch : foundPatches) {
			this.textRemover.markForDeletion(patch.getStartPosition(), patch.getEndPosition());
		}
		return foundPatches;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isRelaxed() {
		return this.relaxed;
	}
	
	@Override
	public void register(final InfozillaSettings settings,
	                     final InfozillaArguments infozillaArguments,
	                     final boolean isRequired) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Patch> runFilter(final String inputText) {
		return getPatches(inputText);
	}
	
	public void setRelaxed(final boolean relaxed) {
		this.relaxed = relaxed;
	}
}

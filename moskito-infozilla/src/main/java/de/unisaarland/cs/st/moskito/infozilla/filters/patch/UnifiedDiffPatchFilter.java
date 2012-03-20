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
package de.unisaarland.cs.st.moskito.infozilla.filters.patch;

import java.util.List;

import de.unisaarland.cs.st.moskito.infozilla.filters.FilterTextRemover;
import de.unisaarland.cs.st.moskito.infozilla.model.patch.Patch;

/**
 * This InfozillaFilter class acts as an interface for the PatchParser class that has to be instantiated before using
 * it.
 * 
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
	 * 
	 * @param text
	 *            the text we should look for patches inside
	 * @return a List of {@link Patch}es.
	 */
	@SuppressWarnings ("unchecked")
	private List<Patch> getPatches(final String text) {
		// Setup Helper classes
		this.textRemover = new FilterTextRemover(text);
		
		// Find Patches
		List<Patch> foundPatches = null;
		if (isRelaxed()) {
			final RelaxedPatchParser pp = new RelaxedPatchParser();
			foundPatches = (List<Patch>) pp.parseForPatches(text);
		} else {
			final PatchParser pp = new PatchParser();
			foundPatches = (List<Patch>) pp.parseForPatches(text);
		}
		
		// InfozillaFilter them out
		for (final Patch patch : foundPatches) {
			this.textRemover.markForDeletion(patch.getStartPosition(), patch.getEndPosition());
		}
		return foundPatches;
	}
	
	public boolean isRelaxed() {
		return this.relaxed;
	}
	
	@Override
	public List<Patch> runFilter(final String inputText) {
		return getPatches(inputText);
	}
	
	public void setRelaxed(final boolean relaxed) {
		this.relaxed = relaxed;
	}
}

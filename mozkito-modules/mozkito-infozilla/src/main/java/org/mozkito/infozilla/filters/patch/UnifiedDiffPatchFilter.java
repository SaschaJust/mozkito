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

import org.mozkito.infozilla.elements.FilterResult;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.patch.Patch;

/**
 * This InfozillaFilter class acts as an interface for the PatchParser class that has to be instantiated before using
 * it.
 * 
 * @author Nicolas Bettenburg
 * 
 */
public class UnifiedDiffPatchFilter extends PatchFilter {
	
	/** The relaxed. */
	private boolean relaxed = false;
	
	/**
	 * Instantiates a new unified diff patch filter.
	 */
	public UnifiedDiffPatchFilter() {
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.InfozillaFilter#apply(java.util.List,
	 *      org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	public void apply(final List<Patch> results,
	                  final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			enhancedReport.setPatches(results);
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * InfozillaFilter a list of {@link Patch}es from a text {@link s}.
	 * 
	 * @param text
	 *            the text we should look for patches inside
	 * @return a List of {@link Patch}es.
	 */
	@SuppressWarnings ("unchecked")
	private List<FilterResult<Patch>> getPatches(final String text) {
		
		// Find Patches
		List<Patch> foundPatches = null;
		if (isRelaxed()) {
			final RelaxedPatchParser pp = new RelaxedPatchParser();
			foundPatches = (List<Patch>) pp.parseForPatches(text);
		} else {
			final PatchParser pp = new PatchParser();
			foundPatches = (List<Patch>) pp.parseForPatches(text);
		}
		
		// // InfozillaFilter them out
		// for (final Patch patch : foundPatches) {
		// this.textRemover.markForDeletion(patch.getStartPosition(), patch.getEndPosition());
		// }
		
		final List<FilterResult<Patch>> list = new ArrayList<>(foundPatches.size());
		for (final Patch patch : foundPatches) {
			if (Logger.logAlways()) {
				Logger.always("Found patch:");
				Logger.always(patch.toString());
			}
			list.add(new FilterResult<Patch>(-1, -1, patch));
		}
		return list;
	}
	
	/**
	 * Checks if is relaxed.
	 * 
	 * @return true, if is relaxed
	 */
	public boolean isRelaxed() {
		return this.relaxed;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.infozilla.filters.InfozillaFilter#runFilter(java.lang.String)
	 */
	@Override
	public List<FilterResult<Patch>> runFilter(final String inputText) {
		return getPatches(inputText);
	}
	
	/**
	 * Sets the relaxed.
	 * 
	 * @param relaxed
	 *            the new relaxed
	 */
	public void setRelaxed(final boolean relaxed) {
		this.relaxed = relaxed;
	}
}

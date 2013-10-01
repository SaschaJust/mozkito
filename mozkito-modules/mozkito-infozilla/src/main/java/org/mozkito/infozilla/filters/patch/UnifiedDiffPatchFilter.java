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

import java.util.LinkedList;
import java.util.List;

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
	 * 
	 * @param enhancedReport
	 *            the enhanced report
	 */
	public UnifiedDiffPatchFilter(final EnhancedReport enhancedReport) {
		super(enhancedReport);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#apply(java.util.List, org.mozkito.infozilla.model.EnhancedReport)
	 */
	@Override
	protected void apply(final List<Patch> results,
	                     final EnhancedReport enhancedReport) {
		PRECONDITIONS: {
			if (results == null) {
				throw new NullPointerException();
			}
			if (enhancedReport == null) {
				throw new NullPointerException();
			}
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
	 * Checks if is relaxed.
	 * 
	 * @return true, if is relaxed
	 */
	public boolean isRelaxed() {
		return this.relaxed;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.infozilla.filters.Filter#runFilter(java.lang.String)
	 */
	@Override
	protected List<Patch> runFilter(final String text) {
		PRECONDITIONS: {
			if (text == null) {
				throw new NullPointerException();
			}
		}
		
		List<Patch> foundPatches = new LinkedList<>();
		
		try {
			// Find Patches
			final IPatchParser pp = isRelaxed()
			                                   ? new RelaxedPatchParser()
			                                   : new PatchParser();
			foundPatches = pp.parseForPatches(text);
			
			return foundPatches;
		} finally {
			POSTCONDITIONS: {
				// assert foundPatches != null;
				// assert (new Regex(PatchParser.HUNK_HEADER_PATTERN).find(text) != null) == !foundPatches.isEmpty();
			}
		}
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

/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
package org.mozkito.causeeffect;

import java.util.Collection;
import java.util.HashSet;

import org.mozkito.causeeffect.ctl.CTLAtomicFormula;
import org.mozkito.causeeffect.ctl.CTLEF;
import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.versions.model.File;


/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class EF_FormulaGenearator extends CTLFormulaGenerator<File> {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.causeeffect.CTLFormulaGenerator#generate(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<CTLFormula> generate(final Collection<File> premises,
	                                       final Collection<File> implications) {
		// PRECONDITIONS
		
		try {
			final Collection<CTLFormula> formulas = new HashSet<CTLFormula>();
			
			for (final File implication : implications) {
				formulas.add(CTLEF.get(CTLAtomicFormula.get(implication.getGeneratedId())));
			}
			return formulas;
		} finally {
			// POSTCONDITIONS
		}
	}
}

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
package de.unisaarland.cs.st.reposuite.ltc;

import java.util.Collection;
import java.util.HashSet;

import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLAG;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLAtomicFormula;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLEF;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLFormula;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLImplication;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class AG_EF_FormulaGenearator extends CTLFormulaGenerator<RCSFile> {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ltc.CTLFormulaGenerator#generate(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<CTLFormula> generate(final Collection<RCSFile> premises,
	                                       final Collection<RCSFile> implications) {
		// PRECONDITIONS
		
		try {
			final Collection<CTLFormula> formulas = new HashSet<CTLFormula>();
			
			for (final RCSFile implication1 : implications) {
				for (final RCSFile implication2 : implications) {
					if (implication1.equals(implication2)) {
						continue;
					}
					CTLFormula formula = CTLAtomicFormula.get(implication2.getGeneratedId());
					formula = CTLEF.get(formula);
					formula = CTLImplication.get(CTLAtomicFormula.get(implication1.getGeneratedId()), formula);
					formula = CTLAG.get(formula);
					formulas.add(formula);
				}
			}
			return formulas;
		} finally {
			// POSTCONDITIONS
		}
	}
}

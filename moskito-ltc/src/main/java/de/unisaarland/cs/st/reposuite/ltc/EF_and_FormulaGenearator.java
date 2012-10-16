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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLAtomicFormula;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLConjunction;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLEF;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLFormula;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class EF_and_FormulaGenearator extends CTLFormulaGenerator<RCSFile> {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.ltc.CTLFormulaGenerator#generate(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<CTLFormula> generate(final Collection<RCSFile> premises,
	                                       final Collection<RCSFile> implications) {
		// PRECONDITIONS
		
		try {
			
			final List<RCSFile> implicationList = new ArrayList<>(implications.size());
			implicationList.addAll(implications);
			
			final Collection<CTLFormula> formulas = new HashSet<CTLFormula>();
			
			final ListIterator<RCSFile> firstIter = implicationList.listIterator();
			while (firstIter.hasNext()) {
				final RCSFile implication1 = firstIter.next();
				final ListIterator<RCSFile> secondIter = implicationList.listIterator(firstIter.nextIndex());
				while (secondIter.hasNext()) {
					final RCSFile implication2 = secondIter.next();
					final CTLFormula f = CTLConjunction.get(CTLAtomicFormula.get(implication1.getGeneratedId()),
					                                        CTLAtomicFormula.get(implication2.getGeneratedId()));
					formulas.add(CTLEF.get(f));
				}
			}
			return formulas;
		} finally {
			// POSTCONDITIONS
		}
	}
}

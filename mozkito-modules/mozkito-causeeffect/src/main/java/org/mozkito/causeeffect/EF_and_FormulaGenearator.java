/***********************************************************************************************************************
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
 **********************************************************************************************************************/
package org.mozkito.causeeffect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.mozkito.causeeffect.ctl.CTLAtomicFormula;
import org.mozkito.causeeffect.ctl.CTLConjunction;
import org.mozkito.causeeffect.ctl.CTLEF;
import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.versions.model.Handle;

/**
 * The Class EF_and_FormulaGenearator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class EF_and_FormulaGenearator extends CTLFormulaGenerator<Handle> {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.causeeffect.CTLFormulaGenerator#generate(java.util.Collection, java.util.Collection)
	 */
	@Override
	public Collection<CTLFormula> generate(final Collection<Handle> premises,
	                                       final Collection<Handle> implications) {
		// PRECONDITIONS
		
		try {
			
			final List<Handle> implicationList = new ArrayList<>(implications.size());
			implicationList.addAll(implications);
			
			final Collection<CTLFormula> formulas = new HashSet<CTLFormula>();
			
			final ListIterator<Handle> firstIter = implicationList.listIterator();
			while (firstIter.hasNext()) {
				final Handle implication1 = firstIter.next();
				final ListIterator<Handle> secondIter = implicationList.listIterator(firstIter.nextIndex());
				while (secondIter.hasNext()) {
					final Handle implication2 = secondIter.next();
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

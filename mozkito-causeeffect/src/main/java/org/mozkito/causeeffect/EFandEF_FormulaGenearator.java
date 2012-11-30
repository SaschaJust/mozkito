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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import org.mozkito.causeeffect.ctl.CTLAtomicFormula;
import org.mozkito.causeeffect.ctl.CTLConjunction;
import org.mozkito.causeeffect.ctl.CTLEF;
import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.versions.model.File;


/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class EFandEF_FormulaGenearator extends CTLFormulaGenerator<File> {
	
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
			
			final List<File> implicationList = new ArrayList<>(implications.size());
			
			final ListIterator<File> firstIter = implicationList.listIterator();
			while (firstIter.hasNext()) {
				final File implication1 = firstIter.next();
				final ListIterator<File> secondIter = implicationList.listIterator(firstIter.nextIndex());
				while (secondIter.hasNext()) {
					final File implication2 = secondIter.next();
					final CTLFormula ef1 = CTLEF.get(CTLAtomicFormula.get(implication1.getGeneratedId()));
					final CTLFormula ef2 = CTLEF.get(CTLAtomicFormula.get(implication2.getGeneratedId()));
					formulas.add(CTLConjunction.get(ef1, ef2));
				}
			}
			return formulas;
		} finally {
			// POSTCONDITIONS
		}
	}
}

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
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.utils.VertexSelector;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.reposuite.ltc.ctl.CTLFormula;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class LTCFormulaFactory {
	
	private final Set<CTLFormulaGenerator<RCSFile>> generators = new HashSet<>();
	
	public Collection<CTLFormula> generateFormulas(final ChangeGenealogy<RCSTransaction> genealogy,
	                                               final RCSTransaction rootVertex,
	                                               final VertexSelector<RCSTransaction> vertexSelector) {
		// generate CTL formulas
		final Collection<CTLFormula> formulas = new HashSet<>();
		for (final CTLFormulaGenerator<RCSFile> generator : this.generators) {
			
			// implication = all changes files of all dependent vertices valid by vertexSelector
			final LinkedList<RCSTransaction> verticesToProcess = new LinkedList<>();
			verticesToProcess.add(rootVertex);
			final Set<RCSFile> implications = new HashSet<>();
			while (!verticesToProcess.isEmpty()) {
				final RCSTransaction vertex = verticesToProcess.poll();
				for (final RCSTransaction dependent : genealogy.getAllDependants(vertex)) {
					if (vertexSelector.selectVertex(dependent)) {
						verticesToProcess.add(dependent);
					}
				}
				implications.addAll(vertex.getChangedFiles());
			}
			
			formulas.addAll(generator.generate(rootVertex.getChangedFiles(), implications));
		}
		return formulas;
	}
	
	@SuppressWarnings ("unchecked")
	public Collection<CTLFormula> generateInnerTransactionFormulas(final ChangeGenealogy<RCSTransaction> genealogy,
	                                                               final RCSTransaction transaction) {
		// generate CTL formulas
		final Collection<CTLFormula> formulas = new HashSet<>();
		for (final CTLFormulaGenerator<RCSFile> generator : this.generators) {
			
			final Collection<RCSFile> implications = transaction.getChangedFiles();
			for (final RCSFile file : implications) {
				final ArrayList<RCSFile> premise = new ArrayList<RCSFile>(1);
				premise.add(file);
				formulas.addAll(generator.generate(premise, CollectionUtils.removeAll(implications, premise)));
			}
		}
		return formulas;
	}
	
	public boolean register(final CTLFormulaGenerator<RCSFile> generator) {
		return this.generators.add(generator);
	}
}

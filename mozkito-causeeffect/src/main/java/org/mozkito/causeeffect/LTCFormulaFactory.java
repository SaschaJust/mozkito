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
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.utils.VertexSelector;
import org.mozkito.versions.model.File;
import org.mozkito.versions.model.Transaction;


/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class LTCFormulaFactory {
	
	private final Set<CTLFormulaGenerator<File>> generators = new HashSet<>();
	
	public Collection<CTLFormula> generateFormulas(final ChangeGenealogy<Transaction> genealogy,
	                                               final Transaction rootVertex,
	                                               final VertexSelector<Transaction> vertexSelector) {
		// generate CTL formulas
		final Collection<CTLFormula> formulas = new HashSet<>();
		for (final CTLFormulaGenerator<File> generator : this.generators) {
			
			// implication = all changes files of all dependent vertices valid by vertexSelector
			final LinkedList<Transaction> verticesToProcess = new LinkedList<>();
			verticesToProcess.add(rootVertex);
			final Set<File> implications = new HashSet<>();
			while (!verticesToProcess.isEmpty()) {
				final Transaction vertex = verticesToProcess.poll();
				for (final Transaction dependent : genealogy.getAllDependants(vertex)) {
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
	public Collection<CTLFormula> generateInnerTransactionFormulas(final ChangeGenealogy<Transaction> genealogy,
	                                                               final Transaction transaction) {
		// generate CTL formulas
		final Collection<CTLFormula> formulas = new HashSet<>();
		for (final CTLFormulaGenerator<File> generator : this.generators) {
			
			final Collection<File> implications = transaction.getChangedFiles();
			for (final File file : implications) {
				final ArrayList<File> premise = new ArrayList<File>(1);
				premise.add(file);
				formulas.addAll(generator.generate(premise, CollectionUtils.removeAll(implications, premise)));
			}
		}
		return formulas;
	}
	
	public boolean register(final CTLFormulaGenerator<File> generator) {
		return this.generators.add(generator);
	}
}

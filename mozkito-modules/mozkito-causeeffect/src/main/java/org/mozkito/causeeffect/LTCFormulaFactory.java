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
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import org.mozkito.causeeffect.ctl.CTLFormula;
import org.mozkito.genealogies.core.ChangeGenealogy;
import org.mozkito.genealogies.utils.VertexSelector;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;

/**
 * A factory for creating LTCFormula objects.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class LTCFormulaFactory {
	
	/** The generators. */
	private final Set<CTLFormulaGenerator<Handle>> generators = new HashSet<>();
	
	/**
	 * Generate formulas.
	 * 
	 * @param genealogy
	 *            the genealogy
	 * @param rootVertex
	 *            the root vertex
	 * @param vertexSelector
	 *            the vertex selector
	 * @return the collection
	 */
	public Collection<CTLFormula> generateFormulas(final ChangeGenealogy<ChangeSet> genealogy,
	                                               final ChangeSet rootVertex,
	                                               final VertexSelector<ChangeSet> vertexSelector) {
		// generate CTL formulas
		final Collection<CTLFormula> formulas = new HashSet<>();
		for (final CTLFormulaGenerator<Handle> generator : this.generators) {
			
			// implication = all changes files of all dependent vertices valid by vertexSelector
			final LinkedList<ChangeSet> verticesToProcess = new LinkedList<>();
			verticesToProcess.add(rootVertex);
			final Set<Handle> implications = new HashSet<>();
			while (!verticesToProcess.isEmpty()) {
				final ChangeSet vertex = verticesToProcess.poll();
				for (final ChangeSet dependent : genealogy.getAllDependents(vertex)) {
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
	
	/**
	 * Generate inner transaction formulas.
	 * 
	 * @param genealogy
	 *            the genealogy
	 * @param changeSet
	 *            the r cs transaction
	 * @return the collection
	 */
	@SuppressWarnings ("unchecked")
	public Collection<CTLFormula> generateInnerTransactionFormulas(final ChangeGenealogy<ChangeSet> genealogy,
	                                                               final ChangeSet changeSet) {
		// generate CTL formulas
		final Collection<CTLFormula> formulas = new HashSet<>();
		for (final CTLFormulaGenerator<Handle> generator : this.generators) {
			
			final Collection<Handle> implications = changeSet.getChangedFiles();
			for (final Handle handle : implications) {
				final ArrayList<Handle> premise = new ArrayList<Handle>(1);
				premise.add(handle);
				formulas.addAll(generator.generate(premise, CollectionUtils.removeAll(implications, premise)));
			}
		}
		return formulas;
	}
	
	/**
	 * Register.
	 * 
	 * @param generator
	 *            the generator
	 * @return true, if successful
	 */
	public boolean register(final CTLFormulaGenerator<Handle> generator) {
		return this.generators.add(generator);
	}
}

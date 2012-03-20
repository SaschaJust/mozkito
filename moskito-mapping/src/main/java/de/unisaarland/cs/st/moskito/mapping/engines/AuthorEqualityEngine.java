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
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.hiari.settings.DynamicArgumentSet;
import net.ownhero.dev.hiari.settings.arguments.DoubleArgument;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * This engine scores according to the equality of the authors of both entities. If the confidence value isn't set
 * explicitly, the default value is used.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AuthorEqualityEngine extends MappingEngine {
	
	private double         scoreAuthorEquality = 0.2d;
	private DoubleArgument confidenceArgument;
	
	@Override
	public void afterParse() {
		setScoreAuthorEquality(this.confidenceArgument.getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return "Scores according to the equality of both entities (at some time in the history).";
	}
	
	/**
	 * @return the scoreAuthorEquality (which is the confidence value for this engine; defaults to the initial value of
	 *         scoreAuthorEquality)
	 */
	private double getScoreAuthorEquality() {
		return this.scoreAuthorEquality;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException {
		this.confidenceArgument = new DoubleArgument(set, "confidence",
		                                             "Score for equal authors in transaction and report comments.",
		                                             this.scoreAuthorEquality + "", Requirement.required);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Mapping score) {
		double confidence = 0d;
		
		if (from.get(FieldKey.AUTHOR).equals(to.get(FieldKey.AUTHOR))) {
			confidence = getScoreAuthorEquality();
		}
		
		addFeature(score, confidence, FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR), from.get(FieldKey.AUTHOR),
		           FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR), to.get(FieldKey.AUTHOR));
	}
	
	/**
	 * @param scoreAuthorEquality
	 *            the scoreAuthorEquality to set
	 */
	private void setScoreAuthorEquality(final double scoreAuthorEquality) {
		this.scoreAuthorEquality = scoreAuthorEquality;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.AUTHOR), new Atom(Index.TO, FieldKey.AUTHOR));
	}
	
}

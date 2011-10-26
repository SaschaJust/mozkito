/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package de.unisaarland.cs.st.reposuite.mapping.engines;

import net.ownhero.dev.andama.settings.DoubleArgument;
import de.unisaarland.cs.st.reposuite.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.requirements.And;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Atom;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Expression;
import de.unisaarland.cs.st.reposuite.mapping.requirements.Index;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;

/**
 * This engine scores according to the equality of the authors of both entities.
 * If the confidence value isn't set explicitly, the default value is used.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class AuthorEqualityEngine extends MappingEngine {
	
	private double scoreAuthorEquality = 0.2d;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores according to the equality of both entities (at some time in the history).";
	}
	
	/**
	 * @return the scoreAuthorEquality (which is the confidence value for this
	 *         engine; defaults to the initial value of scoreAuthorEquality)
	 */
	private double getScoreAuthorEquality() {
		return this.scoreAuthorEquality;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreAuthorEquality((Double) getSettings().getSetting(getOptionName("confidence")).getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#init(de.
	 * unisaarland.cs.st.reposuite.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.reposuite.mapping.settings.MappingArguments,
	 * boolean)
	 */
	@Override
	public void register(final MappingSettings settings,
	                     final MappingArguments arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isRequired && isEnabled());
		arguments.addArgument(new DoubleArgument(settings, getOptionName("confidence"),
		                                         "Score for equal authors in transaction and report comments.",
		                                         this.scoreAuthorEquality + "", isRequired && isEnabled()));
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.reposuite.mapping.model.MapScore)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final MapScore score) {
		if (from.get(FieldKey.AUTHOR).equals(to.get(FieldKey.AUTHOR))) {
			addFeature(score, getScoreAuthorEquality(), FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR),
			           from.get(FieldKey.AUTHOR), FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR),
			           to.get(FieldKey.AUTHOR));
		} else {
			addFeature(score, 0d, FieldKey.AUTHOR.name(), from.get(FieldKey.AUTHOR), from.get(FieldKey.AUTHOR),
			           FieldKey.AUTHOR.name(), to.get(FieldKey.AUTHOR), to.get(FieldKey.AUTHOR));
		}
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
	 * @see
	 * de.unisaarland.cs.st.reposuite.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.FROM, FieldKey.AUTHOR), new Atom(Index.TO, FieldKey.AUTHOR));
	}
	
}

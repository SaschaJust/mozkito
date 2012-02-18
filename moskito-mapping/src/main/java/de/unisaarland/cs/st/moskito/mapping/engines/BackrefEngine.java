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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.DynamicArgumentSet;
import net.ownhero.dev.andama.settings.arguments.DoubleArgument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * This engine scores if the 'to' entity contains a reference to the 'from' entity in the body text.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class BackrefEngine extends MappingEngine {
	
	private double         scoreBackRef = 1d;
	private DoubleArgument confidenceArgument;
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#afterParse()
	 */
	@Override
	public void afterParse() {
		setScoreBackRef(this.confidenceArgument.getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return "Scores if the 'to' entity contains a reference to the 'from' entity in the body text.";
	}
	
	/**
	 * @return the scoreBackRef
	 */
	public double getScoreBackRef() {
		return this.scoreBackRef;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws ArgumentRegistrationException {
		this.confidenceArgument = new DoubleArgument(set, "confidence",
		                                             "Score for backreference in transaction and report.",
		                                             this.scoreBackRef + "", Requirement.required);
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity, de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final Mapping score) {
		final String fullText = element2.getText();
		final String id = element1.get(FieldKey.ID).toString();
		
		double confidence = 0d;
		if (fullText.contains(id.toString())) {
			confidence = getScoreBackRef();
			
		}
		addFeature(score, confidence, FieldKey.ID.name(), id, id, "FULLTEXT", fullText, fullText);
	}
	
	/**
	 * @param scoreBackRef
	 *            the scoreBackRef to set
	 */
	public void setScoreBackRef(final double scoreBackRef) {
		this.scoreBackRef = scoreBackRef;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Atom(Index.FROM, FieldKey.ID);
	}
}

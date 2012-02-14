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
import net.ownhero.dev.andama.settings.arguments.EnumArgument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.mapping.requirements.Or;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportTypeEngine extends MappingEngine {
	
	private double             confidence;
	
	private Type               type;
	
	private EnumArgument<Type> typeArgument;
	
	private DoubleArgument     confidenceArgument;
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#afterParse()
	 */
	@Override
	public void afterParse() {
		setConfidence(this.confidenceArgument.getValue());
		setType(this.typeArgument.getValue());
	}
	
	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report isn't of the specified type.";
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.registerable.ArgumentProvider#initSettings(net.ownhero.dev.andama.settings.
	 * DynamicArgumentSet)
	 */
	@Override
	public boolean initSettings(final DynamicArgumentSet<Boolean> set) throws ArgumentRegistrationException {
		this.confidenceArgument = new DoubleArgument(
		                                             set,
		                                             "confidence",
		                                             "Confidence that is used if the report isn't of the specified type.",
		                                             "-1", Requirement.required);
		this.typeArgument = new EnumArgument<Type>(set, "type", "Type the report has to match, e.g. BUG.", Type.BUG,
		                                           Requirement.required);
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
		if (element1 instanceof MappableReport) {
			if (element1.get(FieldKey.TYPE) != getType()) {
				score.addFeature(getConfidence(), FieldKey.TYPE.name(), element1.get(FieldKey.TYPE).toString(),
				                 element1.get(FieldKey.TYPE).toString(), getUnused(), getUnknown(), getUnknown(),
				                 this.getClass());
			}
		} else
		
		if (element2 instanceof MappableReport) {
			if (element2.get(FieldKey.TYPE) != getType()) {
				score.addFeature(getConfidence(), getUnused(), getUnknown(), getUnknown(), FieldKey.TYPE.name(),
				                 element2.get(FieldKey.TYPE).toString(), element2.get(FieldKey.TYPE).toString(),
				                 this.getClass());
			}
		} else {
			score.addFeature(0, getUnused(), getUnknown(), getUnknown(), getUnused(), getUnknown(), getUnknown(),
			                 this.getClass());
		}
		
	}
	
	/**
	 * @param confidence
	 *            the confidence to set
	 */
	private void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	private void setType(final Type type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Or(new Atom(Index.FROM, Report.class), new Atom(Index.TO, Report.class));
		
	}
	
}

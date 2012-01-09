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
package de.unisaarland.cs.st.moskito.mapping.engines;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.DoubleArgument;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.ioda.JavaUtils;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.model.MapScore;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.mapping.requirements.Or;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class ReportTypeEngine extends MappingEngine {
	
	private double confidence;
	
	private Type   type;
	
	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription
	 * ()
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
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		
		setConfidence((Double) getSettings().getSetting(getOptionName("confidence")).getValue());
		setType((Type) getSettings().getSetting(getOptionName("type")).getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments, boolean)
	 */
	@Override
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet arguments,
	                     final boolean isRequired) {
		super.register(settings, arguments, isEnabled());
		arguments.addArgument(new DoubleArgument(settings, getOptionName("confidence"),
		                                         "Confidence that is used if the report isn't of the specified type.",
		                                         "-1", isEnabled()));
		arguments.addArgument(new EnumArgument(settings, getOptionName("type"),
		                                       "Type the report has to match, e.g. BUG.", null, isEnabled(),
		                                       JavaUtils.enumToArray(Type.BUG)));
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.model.MapScore)
	 */
	@Override
	public void score(final MappableEntity element1,
	                  final MappableEntity element2,
	                  final MapScore score) {
		if (element1 instanceof MappableReport) {
			if (element1.get(FieldKey.TYPE) != getType()) {
				score.addFeature(getConfidence(), unused, unknown, unknown, FieldKey.TYPE.name(),
				                 element1.get(FieldKey.TYPE).toString(), element1.get(FieldKey.TYPE).toString(),
				                 this.getClass());
			}
		}
		
		if (element2 instanceof MappableReport) {
			if (element2.get(FieldKey.TYPE) != getType()) {
				score.addFeature(getConfidence(), unused, unknown, unknown, FieldKey.TYPE.name(),
				                 element2.get(FieldKey.TYPE).toString(), element2.get(FieldKey.TYPE).toString(),
				                 this.getClass());
			}
		}
		
	}
	
	/**
	 * @param confidence
	 *            the confidence to set
	 */
	public void setConfidence(final double confidence) {
		this.confidence = confidence;
	}
	
	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final Type type) {
		this.type = type;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new Or(new Atom(Index.FROM, Report.class), new Atom(Index.TO, Report.class));
		
	}
	
}

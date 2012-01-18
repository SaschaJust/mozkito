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

import org.joda.time.DateTime;

import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CreationOrderEngine extends MappingEngine {
	
	private double scoreReportCreatedAfterTransaction = -1d;
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report was created after the transaction was committed.";
	}
	
	/**
	 * @return the scoreReportCreatedAfterTransaction
	 */
	public double getScoreReportCreatedAfterTransaction() {
		return this.scoreReportCreatedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		setScoreReportCreatedAfterTransaction((Double) getOption("confidence").getSecond().getValue());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#register
	 * (de.unisaarland.cs.st.moskito.mapping.settings.MappingSettings,
	 * de.unisaarland.cs.st.moskito.mapping.settings.MappingArguments, boolean)
	 */
	@Override
	public void register(final AndamaSettings settings,
	                     final AndamaArgumentSet arguments) {
		super.register(settings, arguments);
		registerDoubleOption(settings, arguments, "confidence",
		                     "Score in case the report was created after the transaction.",
		                     this.scoreReportCreatedAfterTransaction + "", true);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#score(de
	 * .unisaarland.cs.st.reposuite.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.mappable.MappableEntity,
	 * de.unisaarland.cs.st.moskito.mapping.model.Mapping)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Mapping score) {
		if (((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).isBefore(((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)))) {
			score.addFeature(getScoreReportCreatedAfterTransaction(), FieldKey.CREATION_TIMESTAMP.name(),
			                 ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			                 ((DateTime) from.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			                 FieldKey.CREATION_TIMESTAMP.name(),
			                 ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString(),
			                 ((DateTime) to.get(FieldKey.CREATION_TIMESTAMP)).toString(), this.getClass());
		}
	}
	
	/**
	 * @param scoreReportCreatedAfterTransaction
	 *            the scoreReportCreatedAfterTransaction to set
	 */
	public void setScoreReportCreatedAfterTransaction(final double scoreReportCreatedAfterTransaction) {
		this.scoreReportCreatedAfterTransaction = scoreReportCreatedAfterTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.ONE, FieldKey.CREATION_TIMESTAMP), new Atom(Index.OTHER,
		                                                                          FieldKey.CREATION_TIMESTAMP));
	}
	
}

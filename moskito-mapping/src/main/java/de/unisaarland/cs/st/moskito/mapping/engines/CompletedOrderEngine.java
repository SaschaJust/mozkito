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
import de.unisaarland.cs.st.moskito.bugs.tracker.model.Report;
import de.unisaarland.cs.st.moskito.mapping.mappable.FieldKey;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableEntity;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableReport;
import de.unisaarland.cs.st.moskito.mapping.mappable.model.MappableTransaction;
import de.unisaarland.cs.st.moskito.mapping.model.Mapping;
import de.unisaarland.cs.st.moskito.mapping.requirements.And;
import de.unisaarland.cs.st.moskito.mapping.requirements.Atom;
import de.unisaarland.cs.st.moskito.mapping.requirements.Expression;
import de.unisaarland.cs.st.moskito.mapping.requirements.Index;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class CompletedOrderEngine extends MappingEngine {
	
	private double scoreReportResolvedBeforeTransaction = -1d;
	
	/**
	 * 
	 */
	public CompletedOrderEngine() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Scores negative if the report was resolved before the transaction was committed.";
	}
	
	/**
	 * @return the scoreReportResolvedBeforeTransaction
	 */
	public double getScoreReportResolvedBeforeTransaction() {
		return this.scoreReportResolvedBeforeTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#init()
	 */
	@Override
	public void init() {
		super.init();
		setScoreReportResolvedBeforeTransaction((Double) getOption("confidence").getSecond().getValue());
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
		                     "Score in case the report was resolved before the transaction.",
		                     this.scoreReportResolvedBeforeTransaction + "", true);
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
		final RCSTransaction transaction = ((MappableTransaction) from).getTransaction();
		final Report report = ((MappableReport) to).getReport();
		double confidence = 0d;
		
		if ((report.getResolutionTimestamp() != null)
		        && transaction.getTimestamp().isAfter(report.getResolutionTimestamp())) {
			confidence = getScoreReportResolvedBeforeTransaction();
		}
		
		addFeature(score, confidence, FieldKey.CREATION_TIMESTAMP.name(), transaction.getTimestamp().toString(),
		           transaction.getTimestamp().toString(), FieldKey.CREATION_TIMESTAMP.name(),
		           report.getResolution() != null
		                                         ? report.getResolutionTimestamp().toString()
		                                         : unknown,
		           report.getResolution() != null
		                                         ? report.getResolutionTimestamp().toString()
		                                         : unknown);
		
	}
	
	/**
	 * @param scoreReportResolvedBeforeTransaction
	 *            the scoreReportResolvedBeforeTransaction to set
	 */
	public void setScoreReportResolvedBeforeTransaction(final double scoreReportResolvedBeforeTransaction) {
		this.scoreReportResolvedBeforeTransaction = scoreReportResolvedBeforeTransaction;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.mapping.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		return new And(new Atom(Index.TO, Report.class), new Atom(Index.FROM, RCSTransaction.class));
	}
	
}

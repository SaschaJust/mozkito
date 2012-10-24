/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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
 *******************************************************************************/
package de.unisaarland.cs.st.mozkito.genealogies.metrics;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.versions.model.RCSFile;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

/**
 * The Class GenealogyMetricAggregationReader.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyMetricAggregationReader extends Source<GenealogyMetricValue> {
	
	/** The node iditerator. */
	private Iterator<String>               nodeIditerator;
	
	/** The output iter. */
	private Iterator<GenealogyMetricValue> outputIter;
	
	/**
	 * Instantiates a new genealogy metric aggregation reader.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param metricValues the metric values
	 * @param persistenceUtil the persistence util
	 */
	public GenealogyMetricAggregationReader(final Group threadGroup, final Settings settings,
	        final Map<String, Map<String, Double>> metricValues, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void preExecution() {
				GenealogyMetricAggregationReader.this.nodeIditerator = metricValues.keySet().iterator();
			}
		};
		
		new ProcessHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void process() {
				
				while ((GenealogyMetricAggregationReader.this.outputIter == null)
				        || (!GenealogyMetricAggregationReader.this.outputIter.hasNext())) {
					
					if (!GenealogyMetricAggregationReader.this.nodeIditerator.hasNext()) {
						setCompleted();
						return;
					}
					
					final String transactionId = GenealogyMetricAggregationReader.this.nodeIditerator.next();
					final RCSTransaction transaction = persistenceUtil.loadById(transactionId, RCSTransaction.class);
					if (transaction != null) {
						final Collection<RCSFile> changedFiles = transaction.getChangedFiles();
						final Collection<GenealogyMetricValue> output = new HashSet<GenealogyMetricValue>();
						
						for (final Entry<String, Double> metricSet : metricValues.get(transactionId).entrySet()) {
							for (final RCSFile file : changedFiles) {
								output.add(new GenealogyMetricValue(metricSet.getKey(), file.getPath(transaction),
								                                    metricSet.getValue()));
							}
						}
						GenealogyMetricAggregationReader.this.outputIter = output.iterator();
					}
				}
				
				final GenealogyMetricValue metricValue = GenealogyMetricAggregationReader.this.outputIter.next();
				
				if (Logger.logDebug()) {
					Logger.debug("Providing GenealogyMetricValue " + metricValue);
				}
				
				providePartialOutputData(metricValue);
				
				if ((!GenealogyMetricAggregationReader.this.outputIter.hasNext())
				        && (!GenealogyMetricAggregationReader.this.nodeIditerator.hasNext())) {
					setCompleted();
				}
			}
		};
	}
}

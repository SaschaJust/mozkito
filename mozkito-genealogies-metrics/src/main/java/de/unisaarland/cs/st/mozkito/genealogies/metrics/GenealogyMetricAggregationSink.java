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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Sink;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;

/**
 * The Class GenealogyMetricAggregationSink.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyMetricAggregationSink extends Sink<GenealogyMetricValue> {
	
	/** The stats. */
	private final Map<String, Map<String, DescriptiveStatistics>> stats = new HashMap<String, Map<String, DescriptiveStatistics>>();
	
	/**
	 * Instantiates a new genealogy metric aggregation sink.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param outputFile the output file
	 * @param persistenceUtil the persistence util
	 */
	public GenealogyMetricAggregationSink(final Group threadGroup, final Settings settings, final File outputFile,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new ProcessHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void process() {
				
				final GenealogyMetricValue input = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Handling input data " + input);
				}
				
				final String nodeId = input.getNodeId();
				final String metricId = input.getMetricId();
				final Double value = input.getValue();
				
				if (!GenealogyMetricAggregationSink.this.stats.containsKey(nodeId)) {
					GenealogyMetricAggregationSink.this.stats.put(nodeId, new HashMap<String, DescriptiveStatistics>());
				}
				if (!GenealogyMetricAggregationSink.this.stats.get(nodeId).containsKey(metricId)) {
					GenealogyMetricAggregationSink.this.stats.get(nodeId).put(metricId, new DescriptiveStatistics());
				}
				
				GenealogyMetricAggregationSink.this.stats.get(nodeId).get(metricId).addValue(value);
				
			}
		};
		
		new PostExecutionHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				// write aggregated metrics to file
				
				try {
					final BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
					
					writer.write("File");
					
					List<String> metricIDs = null;
					
					for (final String filePath : GenealogyMetricAggregationSink.this.stats.keySet()) {
						if (metricIDs == null) {
							metricIDs = new LinkedList<String>();
							for (final String metricId : GenealogyMetricAggregationSink.this.stats.get(filePath)
							                                                                      .keySet()) {
								metricIDs.add(metricId);
								writer.write(",");
								writer.write("Avg" + metricId);
								writer.write(",");
								writer.write("Max" + metricId);
								writer.write(",");
								writer.write("Sum" + metricId);
							}
						}
						
						writer.write(FileUtils.lineSeparator);
						writer.write(filePath);
						
						final Map<String, DescriptiveStatistics> metricValuesForNode = GenealogyMetricAggregationSink.this.stats.get(filePath);
						
						if (metricIDs.size() < metricValuesForNode.size()) {
							if (Logger.logWarn()) {
								Logger.warn("There is an inconsistency in the number of metric values between individual files. This will lead to metric values dropped for affected files.");
							}
						}
						
						for (final String metricId : metricIDs) {
							if (!metricValuesForNode.containsKey(metricId)) {
								writer.write(",");
								writer.write("NA");
								writer.write(",");
								writer.write("NA");
								writer.write(",");
								writer.write("NA");
								if (Logger.logError()) {
									Logger.error("Could not find metric value `" + metricId + "` for file `" + filePath
									        + "`.");
								}
							} else {
								writer.write(",");
								writer.write(String.valueOf((metricValuesForNode.get(metricId).getN() < 1
								                                                                         ? 0
								                                                                         : metricValuesForNode.get(metricId)
								                                                                                              .getMean())));
								writer.write(",");
								writer.write(String.valueOf((metricValuesForNode.get(metricId).getN() < 1
								                                                                         ? 0
								                                                                         : metricValuesForNode.get(metricId)
								                                                                                              .getMax())));
								writer.write(",");
								writer.write(String.valueOf((metricValuesForNode.get(metricId).getN() < 1
								                                                                         ? 0
								                                                                         : metricValuesForNode.get(metricId)
								                                                                                              .getSum())));
							}
						}
					}
					writer.close();
				} catch (final IOException e) {
					throw new UnrecoverableError(e);
				}
				
			}
		};
		
	}
	
}

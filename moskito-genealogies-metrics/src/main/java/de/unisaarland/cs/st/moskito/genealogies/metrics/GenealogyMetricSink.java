/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
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

package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSink;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;

public class GenealogyMetricSink extends AndamaSink<GenealogyMetricValue> {
	
	private final File               outputFile;
	private boolean                  checkedConsistency = false;
	
	Map<String, Map<String, Double>> metricValues       = new HashMap<String, Map<String, Double>>();
	
	public GenealogyMetricSink(final AndamaGroup threadGroup, final AndamaSettings settings, final File outputFile) {
		super(threadGroup, settings, false);
		this.outputFile = outputFile;
		
		new ProcessHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void process() {
				final GenealogyMetricValue metricValue = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Receving metric value: " + metricValue);
				}
				
				if (!GenealogyMetricSink.this.metricValues.containsKey(metricValue.getNodeId())) {
					GenealogyMetricSink.this.metricValues.put(metricValue.getNodeId(), new HashMap<String, Double>());
				}
				final Map<String, Double> nodeMetricValues = GenealogyMetricSink.this.metricValues.get(metricValue.getNodeId());
				
				if (nodeMetricValues.containsKey(metricValue.getMetricId())) {
					if (Logger.logError()) {
						Logger.error("Receiving the very same metric `" + metricValue.getMetricId()
						        + "` for the same node twice. Dropping all values except the first received instance!");
					}
				} else {
					nodeMetricValues.put(metricValue.getMetricId(), metricValue.getValue());
				}
			}
		};
		
		new PostExecutionHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				if (Logger.logInfo()) {
					Logger.info("Checking metric consitency ...");
				}
				if (!GenealogyMetricSink.this.isConsistent()) {
					throw new UnrecoverableError(
					                             "Metric data inconsistent. The metric data is not trust worth and will not be written to disk! Please see error previous error messages");
				}
				if (Logger.logInfo()) {
					Logger.info("done.");
				}
				
				if (Logger.logInfo()) {
					Logger.info("Writing metrics to output file ...");
				}
				GenealogyMetricSink.this.writeToFile();
				if (Logger.logInfo()) {
					Logger.info("done.");
				}
			}
			
		};
		
	}
	
	public Map<String, Map<String, Double>> getMetricValues() {
		return this.metricValues;
	}
	
	public boolean isConsistent() {
		this.checkedConsistency = true;
		int numMetrics = -1;
		String firstNodeId = null;
		int numLines = 0;
		for (final String nodeId : this.metricValues.keySet()) {
			++numLines;
			if (numMetrics == -1) {
				numMetrics = this.metricValues.get(nodeId).size();
				firstNodeId = nodeId;
				continue;
			}
			if (numMetrics != this.metricValues.get(nodeId).size()) {
				if (Logger.logError()) {
					Logger.error("Found " + this.metricValues.get(nodeId).size() + " metric values for node id `"
					        + nodeId + "` but " + numMetrics
					        + " were expected. Metric data not consistent. Don't trust the data!");
					if (numLines < 3) {
						Logger.error("The previous error was caused by the second instance checked. It might be that the first instance was wrong. Instance id of the first entry: `"
						        + firstNodeId + "`");
					}
				}
				return false;
			}
		}
		return true;
		
	}
	
	public void writeToFile() {
		if (!this.checkedConsistency) {
			if (Logger.logWarn()) {
				Logger.warn("You did not check whether the metric data is consistent. This may result in wrong metric data sets written to output file. We strongly recommend data consistency checks. Use metric data at own risk.");
			}
		}
		
		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFile));
			
			writer.write("NodeID");
			
			List<String> metricIDs = null;
			
			for (final String nodeId : this.metricValues.keySet()) {
				if (metricIDs == null) {
					metricIDs = new LinkedList<String>();
					for (final String metricId : this.metricValues.get(nodeId).keySet()) {
						metricIDs.add(metricId);
						writer.write(",");
						writer.write(metricId);
					}
				}
				
				writer.write(FileUtils.lineSeparator);
				writer.write(nodeId);
				
				final Map<String, Double> metricValuesForNode = this.metricValues.get(nodeId);
				
				if (metricIDs.size() < metricValuesForNode.size()) {
					if (Logger.logWarn()) {
						Logger.warn("There is an inconsistency in the number of metric values between individual nodes. This will lead to metric values dropped for affected nodes.");
					}
				}
				
				for (final String metricId : metricIDs) {
					writer.write(",");
					if (!metricValuesForNode.containsKey(metricId)) {
						writer.write("NA");
						if (Logger.logError()) {
							Logger.error("Could not find metric value `" + metricId + "` for node `" + nodeId + "`.");
						}
					} else {
						writer.write(metricValuesForNode.get(metricId).toString());
					}
				}
			}
			writer.close();
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
	}
}

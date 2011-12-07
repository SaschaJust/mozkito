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
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.kisa.Logger;


public class GenealogyMetricSink extends AndamaSink<GenealogyMetricValue> {
	
	private File outputFile;
	private boolean                  checkedConsistency = false;
	
	Map<String, Map<String, Double>> metricValues = new HashMap<String, Map<String, Double>>();
	
	public GenealogyMetricSink(AndamaGroup threadGroup, AndamaSettings settings, File outputFile) {
		super(threadGroup, settings, false);
		this.outputFile = outputFile;
		
		new ProcessHook<GenealogyMetricValue, GenealogyMetricValue>(this) {
			
			@Override
			public void process() {
				GenealogyMetricValue metricValue = getInputData();
				
				if (Logger.logDebug()) {
					Logger.debug("Receving metric value: " + metricValue);
				}
				
				if (!metricValues.containsKey(metricValue.getNodeId())) {
					metricValues.put(metricValue.getNodeId(), new HashMap<String, Double>());
				}
				Map<String, Double> nodeMetricValues = metricValues.get(metricValue.getNodeId());
				
				if (nodeMetricValues.containsKey(metricValue.getMetricId())) {
					if (Logger.logError()) {
						Logger.error("Receiving the very same metric for the same node twice. Dropping all values except the first received instance!");
					}
				} else {
					nodeMetricValues.put(metricValue.getMetricId(), metricValue.getValue());
				}
			}
		};
	}
	
	public boolean isConsistent() {
		checkedConsistency = true;
		int numMetrics = -1;
		String firstNodeId = null;
		int numLines = 0;
		for (String nodeId : metricValues.keySet()) {
			++numLines;
			if (numMetrics == -1) {
				numMetrics = metricValues.get(nodeId).size();
				firstNodeId = nodeId;
				continue;
			}
			if (numMetrics != metricValues.get(nodeId).size()) {
				if (Logger.logError()) {
					Logger.error("Found " + metricValues.get(nodeId).size() + " metric values for node id `" + nodeId
							+ "` but " + numMetrics
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
		if (!checkedConsistency) {
			if (Logger.logWarn()) {
				Logger.warn("You did not check whether the metric data is consistent. This may result in wrong metric data sets written to output file. We strongly recommend data consistency checks. Use metric data at own risk.");
			}
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			
			writer.write("NodeID");
			
			List<String> metricIDs = null;
			
			for (String nodeId : metricValues.keySet()) {
				if(metricIDs == null){
					metricIDs = new LinkedList<String>();
					for(String metricId : metricValues.get(nodeId).keySet()){
						metricIDs.add(metricId);
						writer.write(",");
						writer.write(metricId);
					}
				}
				
				writer.write(FileUtils.lineSeparator);
				writer.write(nodeId);
				
				Map<String, Double> metricValuesForNode = metricValues.get(nodeId);
				
				if (metricIDs.size() < metricValuesForNode.size()) {
					if (Logger.logWarn()) {
						Logger.warn("There is an inconsistency in the number of metric values between individual nodes. This will lead to metric values dropped for affected nodes.");
					}
				}

				for(String metricId : metricIDs){
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
			
		} catch (IOException e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
}

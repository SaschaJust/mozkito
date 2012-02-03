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

package genealogies.metrics;

import java.io.File;
import java.util.Map;

import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricsAggregateToolChain;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricsToolChain;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;

public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		
		final GenealogySettings settings = new GenealogySettings();
		settings.setLoggerArg(true);
		final GenealogyArguments genealogyArgs = settings.setGenealogyArgs(true);
		
		final OutputFileArgument fileMetricsFileArgument = new OutputFileArgument(
		                                                                          settings,
		                                                                          "genealogy.metric.fileAggregate.out",
		                                                                          "Filename that will contain genealogy metrics aggregated to RCSFile level as matrix.",
		                                                                          null, false, true);
		final EnumArgument granularityArg = new EnumArgument(
		                                                     settings,
		                                                     "genealogy.metric.level",
		                                                     "The granularity level the metrics should be computed on.",
		                                                     "CHANGEOPERATION", true, new String[] { "CHANGEOPERATION",
		                                                             "OPERATIONPARTITION", "TRANSACTION" });
		
		final GenealogyMetricsToolChain genealogyMetrics = new GenealogyMetricsToolChain(settings, granularityArg,
		                                                                                 genealogyArgs);
		
		settings.parseArguments();
		
		genealogyMetrics.run();
		
		final File aggregateFile = fileMetricsFileArgument.getValue();
		
		final Map<String, Map<String, Double>> metricsValues = genealogyMetrics.getMetricsValues();
		
		if ((aggregateFile != null) && (!metricsValues.isEmpty())) {
			final CoreChangeGenealogy coreChangeGenealogy = genealogyArgs.getValue();
			if (granularityArg.getValue().equals("TRANSACTION")) {
				final GenealogyMetricsAggregateToolChain aggregateToolChain = new GenealogyMetricsAggregateToolChain(
				                                                                                                     settings,
				                                                                                                     aggregateFile,
				                                                                                                     metricsValues,
				                                                                                                     granularityArg.getValue(),
				                                                                                                     coreChangeGenealogy.getPersistenceUtil());
				aggregateToolChain.run();
				coreChangeGenealogy.getTransactionLayer().close();
				coreChangeGenealogy.close();
			} else {
				if (Logger.logError()) {
					Logger.error("Metric aggregation for granularity " + granularityArg.getValue()
					        + " not supported yet.");
				}
			}
		}
	}
}

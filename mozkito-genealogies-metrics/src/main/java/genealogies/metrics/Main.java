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
 *******************************************************************************/

package genealogies.metrics;

import java.io.File;
import java.util.Map;

import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.instrumentation.KanuniAgent;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricsAggregateToolChain;
import org.mozkito.genealogies.metrics.GenealogyMetricsToolChain;
import org.mozkito.genealogies.metrics.utils.MetricLevel;
import org.mozkito.genealogies.settings.GenealogyOptions;
import org.mozkito.settings.DatabaseOptions;

/**
 * The Class Main.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class Main {
	
	static {
		KanuniAgent.initialize();
	}
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(final String[] args) {
		
		try {
			final Settings settings = new Settings();
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "codeanalysis");
			final GenealogyOptions genealogyOptions = new GenealogyOptions(settings.getRoot(), Requirement.required,
			                                                               databaseOptions);
			
			final OutputFileArgument.Options fileAggregateOutOptions = new OutputFileArgument.Options(
			                                                                                          settings.getRoot(),
			                                                                                          "fileAggregateOut",
			                                                                                          "Filename that will contain genealogy metrics aggregated to File level as matrix.",
			                                                                                          null,
			                                                                                          Requirement.optional,
			                                                                                          true);
			final EnumArgument.Options<MetricLevel> granularityOptions = new EnumArgument.Options<MetricLevel>(
			                                                                                                   settings.getRoot(),
			                                                                                                   "metricLevel",
			                                                                                                   "The granularity level the metrics should be computed on.",
			                                                                                                   MetricLevel.TRANSACTION,
			                                                                                                   Requirement.required);
			
			final InputFileArgument.Options untanglingPartFileOptions = new InputFileArgument.Options(
			                                                                                          settings.getRoot(),
			                                                                                          "untanglingPartitionFile",
			                                                                                          "File containing the untangling partition as create by the mozkito unntangling tool chain.",
			                                                                                          null,
			                                                                                          Requirement.equals(granularityOptions,
			                                                                                                             MetricLevel.UNTANGLINGPARTITION));
			
			final GenealogyMetricsToolChain genealogyMetrics = new GenealogyMetricsToolChain(settings,
			                                                                                 granularityOptions,
			                                                                                 genealogyOptions,
			                                                                                 untanglingPartFileOptions);
			
			genealogyMetrics.setName(genealogyMetrics.getClass().getSimpleName());
			genealogyMetrics.start();
			genealogyMetrics.join();
			
			final File aggregateFile = ArgumentFactory.create(fileAggregateOutOptions).getValue();
			
			final Map<String, Map<String, Double>> metricsValues = genealogyMetrics.getMetricsValues();
			
			if ((aggregateFile != null) && (!metricsValues.isEmpty())) {
				final CoreChangeGenealogy coreChangeGenealogy = genealogyMetrics.getGenealogy();
				if (genealogyMetrics.getGranularity().equals(MetricLevel.TRANSACTION)) {
					final GenealogyMetricsAggregateToolChain aggregateToolChain = new GenealogyMetricsAggregateToolChain(
					                                                                                                     settings,
					                                                                                                     aggregateFile,
					                                                                                                     metricsValues,
					                                                                                                     genealogyMetrics.getGranularity(),
					                                                                                                     coreChangeGenealogy.getPersistenceUtil());
					
					aggregateToolChain.setName(aggregateToolChain.getClass().getSimpleName());
					aggregateToolChain.start();
					aggregateToolChain.join();
					coreChangeGenealogy.getTransactionLayer().close();
					coreChangeGenealogy.close();
				} else {
					if (Logger.logError()) {
						Logger.error("Metric aggregation for granularity " + genealogyMetrics.getGranularity()
						        + " not supported yet.");
					}
				}
			}
		} catch (final InterruptedException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new RuntimeException();
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		} catch (final ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
		}
	}
}

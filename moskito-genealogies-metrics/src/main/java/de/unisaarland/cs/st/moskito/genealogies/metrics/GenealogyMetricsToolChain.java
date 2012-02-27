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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.arguments.EnumArgument;
import net.ownhero.dev.hiari.settings.arguments.OutputFileArgument;
import net.ownhero.dev.hiari.settings.arguments.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.DefaultPartitionGenerator;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core.GenealogyCoreMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core.GenealogyMetricMux;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core.GenealogyMetricThread;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition.GenealogyPartitionMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition.PartitionGenealogyMetricMux;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition.PartitionGenealogyMetricThread;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction.GenealogyTransactionMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction.TransactionGenealogyMetricMux;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction.TransactionGenealogyMetricThread;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.MetricLevel;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;

public class GenealogyMetricsToolChain extends Chain<GenealogySettings> {
	
	private final GenealogyArguments        genealogyArgs;
	private final Pool                      threadPool;
	private CoreChangeGenealogy             genealogy;
	private final EnumArgument<MetricLevel> granularityArg;
	private final OutputFileArgument        outputFileArgument;
	private GenealogyMetricSink             genealogyMetricSink;
	
	public GenealogyMetricsToolChain(final GenealogySettings setting, final EnumArgument<MetricLevel> granularityArg,
	        final GenealogyArguments genealogyArgs) {
		super(setting);
		this.threadPool = new Pool(GenealogyMetricsToolChain.class.getSimpleName(), this);
		try {
			this.genealogyArgs = genealogyArgs;
			this.granularityArg = granularityArg;
			this.outputFileArgument = new OutputFileArgument(setting.getRootArgumentSet(), "genealogy.metric.out",
			                                                 "Filename to write result metric matrix into.", null,
			                                                 Requirement.required, true);
			new StringArgument(
			                   setting.getRootArgumentSet(),
			                   "fix.pattern",
			                   "An regexp string that will be used to detect bug reports within commit message. (Remember to use double slashes)",
			                   null, Requirement.required);
		} catch (final net.ownhero.dev.hiari.settings.registerable.ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e.getMessage(), e);
			}
			throw new Shutdown(e.getMessage(), e);
		}
		
	}
	
	public Map<String, Map<String, Double>> getMetricsValues() {
		return this.genealogyMetricSink.getMetricValues();
	}
	
	@Override
	public void setup() {
		this.genealogy = this.genealogyArgs.getValue();
		
		// allow different genealogy layers
		final MetricLevel granularity = this.granularityArg.getValue();
		
		/*
		 * we always enable all metrics. Otherwise this would require far too many arguments The idea is to load all
		 * classes that extend GenealogyMetric. Each metric registers itself at the GenealogyMetricManager, depending on
		 * the granularity level selected for this run.
		 */
		
		if (granularity.equals(MetricLevel.CHANGEOPERATIONPARTITION)) {
			final PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(
			                                                                                       this.genealogy,
			                                                                                       new DefaultPartitionGenerator(
			                                                                                                                     this.genealogy));
			new PartitionGenealogyReader(this.threadPool.getThreadGroup(), getSettings(), partitionChangeGenealogy);
			new PartitionGenealogyMetricMux(this.threadPool.getThreadGroup(), getSettings());
			
			// start all partition metrics
			try {
				
				final Collection<Class<? extends GenealogyPartitionMetric>> metricClasses = ClassFinder.getClassesExtendingClass(GenealogyPartitionMetric.class.getPackage(),
				                                                                                                                 GenealogyPartitionMetric.class,
				                                                                                                                 Modifier.ABSTRACT
				                                                                                                                         | Modifier.INTERFACE
				                                                                                                                         | Modifier.PRIVATE);
				
				for (final Class<? extends GenealogyPartitionMetric> metricClass : metricClasses) {
					if (!Modifier.isAbstract(metricClass.getModifiers())) {
						final Constructor<? extends GenealogyPartitionMetric> constructor = metricClass.getConstructor(PartitionChangeGenealogy.class);
						if (constructor != null) {
							final GenealogyPartitionMetric metric = constructor.newInstance(partitionChangeGenealogy);
							new PartitionGenealogyMetricThread(this.threadPool.getThreadGroup(), getSettings(), metric);
						}
					}
				}
			} catch (final Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError(e);
			}
			
		} else if (granularity.equals(MetricLevel.TRANSACTION)) {
			final TransactionChangeGenealogy transactionChangeGenealogy = this.genealogy.getTransactionLayer();
			new TransactionGenealogyReader(this.threadPool.getThreadGroup(), getSettings(), transactionChangeGenealogy);
			new TransactionGenealogyMetricMux(this.threadPool.getThreadGroup(), getSettings());
			
			// start all transaction metrics
			
			try {
				final Collection<Class<? extends GenealogyTransactionMetric>> metricClasses = ClassFinder.getClassesExtendingClass(GenealogyTransactionMetric.class.getPackage(),
				                                                                                                                   GenealogyTransactionMetric.class,
				                                                                                                                   Modifier.ABSTRACT
				                                                                                                                           | Modifier.INTERFACE
				                                                                                                                           | Modifier.PRIVATE);
				
				for (final Class<? extends GenealogyTransactionMetric> metricClass : metricClasses) {
					if (!Modifier.isAbstract(metricClass.getModifiers())) {
						final Constructor<? extends GenealogyTransactionMetric> constructor = metricClass.getConstructor(TransactionChangeGenealogy.class);
						if (constructor != null) {
							final GenealogyTransactionMetric metric = constructor.newInstance(transactionChangeGenealogy);
							new TransactionGenealogyMetricThread(this.threadPool.getThreadGroup(), getSettings(),
							                                     metric);
						}
					}
				}
			} catch (final Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError(e);
			}
			
		} else {
			new GenealogyReader(this.threadPool.getThreadGroup(), getSettings(), this.genealogy);
			new GenealogyMetricMux(this.threadPool.getThreadGroup(), getSettings());
			// start all core metrics
			
			try {
				final Collection<Class<? extends GenealogyCoreMetric>> metricClasses = ClassFinder.getClassesExtendingClass(GenealogyCoreMetric.class.getPackage(),
				                                                                                                            GenealogyCoreMetric.class,
				                                                                                                            Modifier.ABSTRACT
				                                                                                                                    | Modifier.INTERFACE
				                                                                                                                    | Modifier.PRIVATE);
				
				for (final Class<? extends GenealogyCoreMetric> metricClass : metricClasses) {
					if (!Modifier.isAbstract(metricClass.getModifiers())) {
						final Constructor<? extends GenealogyCoreMetric> constructor = metricClass.getConstructor(CoreChangeGenealogy.class);
						if (constructor != null) {
							final GenealogyCoreMetric coreMetric = constructor.newInstance(this.genealogy);
							new GenealogyMetricThread(this.threadPool.getThreadGroup(), getSettings(), coreMetric);
						}
					}
				}
			} catch (final Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError(e);
			}
		}
		
		// start a demuxer and a sink that receives all the metric values
		// and stores the overall result matrix
		new GenealogyMetricDemux(this.threadPool.getThreadGroup(), getSettings());
		this.genealogyMetricSink = new GenealogyMetricSink(this.threadPool.getThreadGroup(), getSettings(),
		                                                   this.outputFileArgument.getValue());
	}
}

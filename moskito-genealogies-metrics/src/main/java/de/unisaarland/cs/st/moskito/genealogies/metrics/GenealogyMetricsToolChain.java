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

package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.InputFileArgument;
import net.ownhero.dev.hiari.settings.InputFileArgument.Options;
import net.ownhero.dev.hiari.settings.OutputFileArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.UntanglingMetricsPartitioner;
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
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyOptions;

/**
 * The Class GenealogyMetricsToolChain.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyMetricsToolChain extends Chain<Settings> {
	
	/** The genealogy arguments. */
	private final ArgumentSet<CoreChangeGenealogy, GenealogyOptions> genealogyArguments;
	
	/** The thread pool. */
	private final Pool                                               threadPool;
	
	/** The genealogy. */
	private CoreChangeGenealogy                                      genealogy;
	
	/** The granularity argument. */
	private final EnumArgument<MetricLevel>                          granularityArgument;
	
	/** The output file argument. */
	private final OutputFileArgument                                 outputFileArgument;
	
	/** The genealogy metric sink. */
	private GenealogyMetricSink                                      genealogyMetricSink;
	
	/** The granularity. */
	private MetricLevel                                              granularity;
	
	private InputFileArgument                                        untanglingFileArgument;
	
	/**
	 * Instantiates a new genealogy metrics tool chain.
	 * 
	 * @param setting
	 *            the setting
	 * @param granularityOptions
	 *            the granularity options
	 * @param genealogyOptions
	 *            the genealogy options
	 * @param untanglingPartFileOptions
	 */
	public GenealogyMetricsToolChain(final Settings setting,
	        final EnumArgument.Options<MetricLevel> granularityOptions, final GenealogyOptions genealogyOptions,
	        final Options untanglingPartFileOptions) {
		super(setting);
		this.threadPool = new Pool(GenealogyMetricsToolChain.class.getSimpleName(), this);
		try {
			this.genealogyArguments = ArgumentSetFactory.create(genealogyOptions);
			this.granularityArgument = ArgumentFactory.create(granularityOptions);
			this.untanglingFileArgument = ArgumentFactory.create(untanglingPartFileOptions);
			this.outputFileArgument = ArgumentFactory.create(new OutputFileArgument.Options(
			                                                                                setting.getRoot(),
			                                                                                "metricOut",
			                                                                                "Filename to write result metric matrix into.",
			                                                                                null, Requirement.required,
			                                                                                true));
		} catch (final ArgumentRegistrationException e) {
			throw new UnrecoverableError(e);
		} catch (final SettingsParseError e) {
			throw new UnrecoverableError(e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new UnrecoverableError(e);
		} finally {
			// POSTCONDITION
		}
		
	}
	
	/**
	 * Gets the genealogy.
	 * 
	 * @return the genealogy
	 */
	public CoreChangeGenealogy getGenealogy() {
		return this.genealogy;
	}
	
	/**
	 * Gets the granularity.
	 * 
	 * @return the granularity
	 */
	public MetricLevel getGranularity() {
		return this.granularity;
	}
	
	/**
	 * Gets the metrics values.
	 * 
	 * @return the metrics values
	 */
	public Map<String, Map<String, Double>> getMetricsValues() {
		return this.genealogyMetricSink.getMetricValues();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	@Override
	public void setup() {
		this.genealogy = this.genealogyArguments.getValue();
		
		if (Logger.logInfo()) {
			Logger.info("Found change genealogy with %d vertices and %d edges.", this.genealogy.vertexSize(),
			            this.genealogy.edgeSize());
		}
		
		this.granularity = this.granularityArgument.getValue();
		
		/*
		 * we always enable all metrics. Otherwise this would require far too many arguments The idea is to load all
		 * classes that extend GenealogyMetric. Each metric registers itself at the GenealogyMetricManager, depending on
		 * the granularity level selected for this run.
		 */
		
		switch (this.granularity) {
			case UNTANGLINGPARTITION:
				final UntanglingMetricsPartitioner partitioner = new UntanglingMetricsPartitioner(
				                                                                                  this.untanglingFileArgument.getValue(),
				                                                                                  this.genealogy);
				final PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(this.genealogy,
				                                                                                       partitioner);
				new PartiallyPartitionGenealogyReader(this.threadPool.getThreadGroup(), getSettings(),
				                                      partitionChangeGenealogy, partitioner.getUntanglingPartitions());
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
								new PartitionGenealogyMetricThread(this.threadPool.getThreadGroup(), getSettings(),
								                                   metric);
							}
						}
					}
				} catch (final Exception e) {
					if (Logger.logError()) {
						Logger.error(e);
					}
					throw new UnrecoverableError(e);
				}
				break;
			case CHANGEOPERATIONPARTITION:
				throw new UnrecoverableError("Change Operation Partition not yet supported");
				// final PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(
				// this.genealogy,
				// new DefaultPartitionGenerator(
				// this.genealogy));
				// new PartitionGenealogyReader(this.threadPool.getThreadGroup(), getSettings(),
				// partitionChangeGenealogy);
				// new PartitionGenealogyMetricMux(this.threadPool.getThreadGroup(), getSettings());
				//
				// // start all partition metrics
				// try {
				//
				// final Collection<Class<? extends GenealogyPartitionMetric>> metricClasses =
				// ClassFinder.getClassesExtendingClass(GenealogyPartitionMetric.class.getPackage(),
				// GenealogyPartitionMetric.class,
				// Modifier.ABSTRACT
				// | Modifier.INTERFACE
				// | Modifier.PRIVATE);
				//
				// for (final Class<? extends GenealogyPartitionMetric> metricClass : metricClasses) {
				// if (!Modifier.isAbstract(metricClass.getModifiers())) {
				// final Constructor<? extends GenealogyPartitionMetric> constructor =
				// metricClass.getConstructor(PartitionChangeGenealogy.class);
				// if (constructor != null) {
				// final GenealogyPartitionMetric metric = constructor.newInstance(partitionChangeGenealogy);
				// new PartitionGenealogyMetricThread(this.threadPool.getThreadGroup(), getSettings(), metric);
				// }
				// }
				// }
				// } catch (final Exception e) {
				// if (Logger.logError()) {
				// Logger.error(e);
				// }
				// throw new UnrecoverableError(e);
				// }
			case TRANSACTION:
				final TransactionChangeGenealogy transactionChangeGenealogy = this.genealogy.getTransactionLayer();
				new TransactionGenealogyReader(this.threadPool.getThreadGroup(), getSettings(),
				                               transactionChangeGenealogy);
				new TransactionGenealogyMetricMux(this.threadPool.getThreadGroup(), getSettings());
				
				// start all transaction metrics
				
				try {
					final Collection<Class<? extends GenealogyTransactionMetric>> metricClasses = ClassFinder.getClassesExtendingClass(GenealogyTransactionMetric.class.getPackage(),
					                                                                                                                   GenealogyTransactionMetric.class,
					                                                                                                                   Modifier.ABSTRACT
					                                                                                                                           | Modifier.INTERFACE
					                                                                                                                           | Modifier.PRIVATE);
					
					if (Logger.logDebug()) {
						Logger.debug("Found %d GenealogyMetrics: %s.", metricClasses.size(),
						             net.ownhero.dev.ioda.JavaUtils.collectionToString(metricClasses));
					}
					
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
						Logger.error(e);
					}
					throw new UnrecoverableError(e);
				}
				break;
			default:
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
						Logger.error(e);
					}
					throw new UnrecoverableError(e);
				}
				break;
		
		}
		
		// start a demuxer and a sink that receives all the metric values
		// and stores the overall result matrix
		new GenealogyMetricDemux(this.threadPool.getThreadGroup(), getSettings());
		this.genealogyMetricSink = new GenealogyMetricSink(this.threadPool.getThreadGroup(), getSettings(),
		                                                   this.outputFileArgument.getValue());
	}
}

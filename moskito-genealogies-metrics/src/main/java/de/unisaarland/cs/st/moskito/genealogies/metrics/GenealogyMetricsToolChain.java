package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.OutputFileArgument;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.ioda.ClassFinder;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.DefaultPartitionGenerator;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core.GenealogyCoreMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.partition.GenealogyPartitionMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.transaction.GenealogyTransactionMetric;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogyArguments;
import de.unisaarland.cs.st.moskito.genealogies.settings.GenealogySettings;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyReader;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class GenealogyMetricsToolChain extends AndamaChain {
	
	private GenealogyArguments genealogyArgs;
	private AndamaPool         threadPool;
	private CoreChangeGenealogy genealogy;
	private EnumArgument        granularityArg;
	private OutputFileArgument  outputFileArgument;
	
	private GenealogyMetricSink metricSink;
	
	public GenealogyMetricsToolChain() {
		super(new GenealogySettings());
		GenealogySettings settings = (GenealogySettings) getSettings();
		this.threadPool = new AndamaPool(GenealogyMetricsToolChain.class.getSimpleName(), this);
		genealogyArgs = settings.setGenealogyArgs(true);
		granularityArg = new EnumArgument(settings, "genealogy.metric.level",
				"The granularity level the metrics should be computed on.", "CHANGEOPERATION", true, new String[] {
				"CHANGEOPERATION", "OPERATIONPARTITION", "TRANSACTION" });
		outputFileArgument = new OutputFileArgument(settings, "genealogy.metric.out",
				"Filename to write result metric matrix into.", null,
				true, false);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		this.threadPool.execute();
		if (Logger.logInfo()) {
			Logger.info("Terminating threads.");
		}
		
		if (Logger.logInfo()) {
			Logger.info("Checking metric consitency ...");
		}
		if (!metricSink.isConsistent()) {
			throw new UnrecoverableError(
					"Metric data inconsistent. The metric data is not trust worth and will not be written to disk! Please see error previous error messages");
		}
		if (Logger.logInfo()) {
			Logger.info("done.");
		}
		
		if (Logger.logInfo()) {
			Logger.info("Writing metrics to output file ...");
		}
		metricSink.writeToFile();
		if (Logger.logInfo()) {
			Logger.info("done.");
		}
	}
	
	@Override
	public void setup() {
		genealogy = genealogyArgs.getValue();
		
		//allow different genealogy layers
		String granularity = granularityArg.getValue();
		
		/*
		 * we always enable all metrics. Otherwise this would require far too
		 * many arguments The idea is to load all classes that extend
		 * GenealogyMetric. Each metric registers itself at the
		 * GenealogyMetricManager, depending on the granularity level selected
		 * for this run.
		 */
		
		if (granularity.equals("CHANGEOPERATIONPARTITION")) {
			PartitionChangeGenealogy partitionChangeGenealogy = new PartitionChangeGenealogy(genealogy,
					new DefaultPartitionGenerator(genealogy));
			new GenealogyReader<Collection<JavaChangeOperation>>(this.threadPool.getThreadGroup(), getSettings(),
					partitionChangeGenealogy);
			new GenealogyMetricMux<Collection<JavaChangeOperation>>(this.threadPool.getThreadGroup(), getSettings());
			
			//start all partition metrics
			try {
				
				//TODO is the final 1 correct?
				Collection<Class<? extends GenealogyPartitionMetric>> metricClasses = ClassFinder
						.getClassesExtendingClass(GenealogyPartitionMetric.class.getPackage(),
								GenealogyPartitionMetric.class, 1);
				
				for (Class<? extends GenealogyPartitionMetric> metricClass : metricClasses) {
					if (!Modifier.isAbstract(metricClass.getModifiers())) {
						Constructor<? extends GenealogyPartitionMetric> constructor = metricClass.getConstructor(
								AndamaGroup.class, AndamaSettings.class, ChangeGenealogy.class);
						if (constructor != null) {
							constructor.newInstance(this.threadPool.getThreadGroup(), getSettings(), genealogy);
						}
					}
				}
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError(e);
			}
			
		} else if (granularity.equals("TRANSACTION")) {
			TransactionChangeGenealogy transactionChangeGenealogy = new TransactionChangeGenealogy(genealogy);
			new GenealogyReader<RCSTransaction>(this.threadPool.getThreadGroup(), getSettings(),
					transactionChangeGenealogy);
			new GenealogyMetricMux<RCSTransaction>(this.threadPool.getThreadGroup(), getSettings());
			
			//start all transaction metrics
			
			//TODO is the final 1 correct?
			try {
				Collection<Class<? extends GenealogyTransactionMetric>> metricClasses = ClassFinder
						.getClassesExtendingClass(GenealogyTransactionMetric.class.getPackage(),
								GenealogyTransactionMetric.class, 1);
				
				for (Class<? extends GenealogyTransactionMetric> metricClass : metricClasses) {
					if (!Modifier.isAbstract(metricClass.getModifiers())) {
						Constructor<? extends GenealogyTransactionMetric> constructor = metricClass.getConstructor(
								AndamaGroup.class, AndamaSettings.class, ChangeGenealogy.class);
						if (constructor != null) {
							constructor.newInstance(this.threadPool.getThreadGroup(), getSettings(), genealogy);
						}
					}
				}
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError(e);
			}
			
		} else {
			new GenealogyReader<JavaChangeOperation>(this.threadPool.getThreadGroup(), getSettings(), genealogy);
			new GenealogyMetricMux<JavaChangeOperation>(this.threadPool.getThreadGroup(), getSettings());
			//start all core metrics
			
			//TODO is the final 1 correct?
			try {
				Collection<Class<? extends GenealogyCoreMetric>> metricClasses = ClassFinder.getClassesExtendingClass(
						GenealogyCoreMetric.class.getPackage(), GenealogyCoreMetric.class, 1);
				
				for (Class<? extends GenealogyCoreMetric> metricClass : metricClasses) {
					if (!Modifier.isAbstract(metricClass.getModifiers())) {
						Constructor<? extends GenealogyCoreMetric> constructor = metricClass.getConstructor(
								AndamaGroup.class, AndamaSettings.class, ChangeGenealogy.class);
						if (constructor != null) {
							constructor.newInstance(this.threadPool.getThreadGroup(), getSettings(), genealogy);
						}
					}
				}
			} catch (Exception e) {
				if (Logger.logError()) {
					Logger.error(e.getMessage(), e);
				}
				throw new UnrecoverableError(e);
			}
		}
		
		
		//start a demuxer and a sink that receives all the metric values
		//and stores the overall result matrix
		new GenealogyMetricDemux<GenealogyMetricValue>(this.threadPool.getThreadGroup(), getSettings());
		metricSink = new GenealogyMetricSink(threadPool.getThreadGroup(), getSettings(),
				outputFileArgument.getValue());
	}
	
	@Override
	public void shutdown() {
		
	}
	
}

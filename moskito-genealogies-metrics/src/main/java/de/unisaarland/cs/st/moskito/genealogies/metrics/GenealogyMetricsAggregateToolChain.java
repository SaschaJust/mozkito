package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.io.File;
import java.util.Map;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

public class GenealogyMetricsAggregateToolChain extends AndamaChain {
	
	private final AndamaPool                       threadPool;
	private final File                             aggregateFile;
	private final Map<String, Map<String, Double>> metricValues;
	private final PersistenceUtil                  persistenceUtil;
	
	public GenealogyMetricsAggregateToolChain(final AndamaSettings settings, final File aggregateFile,
	        final Map<String, Map<String, Double>> metricValues, final String granularity,
	        final PersistenceUtil persistenceUtil) {
		super(settings);
		this.threadPool = new AndamaPool(GenealogyMetricsToolChain.class.getSimpleName(), this);
		this.aggregateFile = aggregateFile;
		this.metricValues = metricValues;
		this.persistenceUtil = persistenceUtil;
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
	}
	
	@Override
	public void setup() {
		new GenealogyMetricAggregationReader(this.threadPool.getThreadGroup(), super.getSettings(), this.metricValues,
		                                     this.persistenceUtil);
		new GenealogyMetricAggregationSink(this.threadPool.getThreadGroup(), super.getSettings(), this.aggregateFile,
		                                   this.persistenceUtil);
	}
	
}

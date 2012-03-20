package de.unisaarland.cs.st.moskito.genealogies.metrics;

import java.io.File;
import java.util.Map;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.MetricLevel;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

public class GenealogyMetricsAggregateToolChain extends Chain<Settings> {
	
	private final Pool                             threadPool;
	private final File                             aggregateFile;
	private final Map<String, Map<String, Double>> metricValues;
	private final PersistenceUtil                  persistenceUtil;
	
	public GenealogyMetricsAggregateToolChain(final Settings settings, final File aggregateFile,
	        final Map<String, Map<String, Double>> metricValues, final MetricLevel metricLevel,
	        final PersistenceUtil persistenceUtil) {
		super(settings);
		this.threadPool = new Pool(GenealogyMetricsToolChain.class.getSimpleName(), this);
		this.aggregateFile = aggregateFile;
		this.metricValues = metricValues;
		this.persistenceUtil = persistenceUtil;
	}
	
	@Override
	public void setup() {
		new GenealogyMetricAggregationReader(this.threadPool.getThreadGroup(), super.getSettings(), this.metricValues,
		                                     this.persistenceUtil);
		new GenealogyMetricAggregationSink(this.threadPool.getThreadGroup(), super.getSettings(), this.aggregateFile,
		                                   this.persistenceUtil);
	}
	
}

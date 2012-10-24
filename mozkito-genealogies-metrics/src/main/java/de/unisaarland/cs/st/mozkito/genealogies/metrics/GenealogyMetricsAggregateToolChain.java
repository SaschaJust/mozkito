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

import java.io.File;
import java.util.Map;

import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.Settings;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.utils.MetricLevel;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;

/**
 * The Class GenealogyMetricsAggregateToolChain.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyMetricsAggregateToolChain extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                             threadPool;
	
	/** The aggregate file. */
	private final File                             aggregateFile;
	
	/** The metric values. */
	private final Map<String, Map<String, Double>> metricValues;
	
	/** The persistence util. */
	private final PersistenceUtil                  persistenceUtil;
	
	/**
	 * Instantiates a new genealogy metrics aggregate tool chain.
	 *
	 * @param settings the settings
	 * @param aggregateFile the aggregate file
	 * @param metricValues the metric values
	 * @param metricLevel the metric level
	 * @param persistenceUtil the persistence util
	 */
	public GenealogyMetricsAggregateToolChain(final Settings settings, final File aggregateFile,
	        final Map<String, Map<String, Double>> metricValues, final MetricLevel metricLevel,
	        final PersistenceUtil persistenceUtil) {
		super(settings);
		this.threadPool = new Pool(GenealogyMetricsToolChain.class.getSimpleName(), this);
		this.aggregateFile = aggregateFile;
		this.metricValues = metricValues;
		this.persistenceUtil = persistenceUtil;
	}
	
	/* (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	@Override
	public void setup() {
		new GenealogyMetricAggregationReader(this.threadPool.getThreadGroup(), super.getSettings(), this.metricValues,
		                                     this.persistenceUtil);
		new GenealogyMetricAggregationSink(this.threadPool.getThreadGroup(), super.getSettings(), this.aggregateFile,
		                                   this.persistenceUtil);
	}
	
}

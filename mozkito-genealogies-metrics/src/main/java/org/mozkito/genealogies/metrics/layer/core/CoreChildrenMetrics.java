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

package org.mozkito.genealogies.metrics.layer.core;

import java.util.Collection;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyCoreNode;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.layer.universal.UniversalChildrenMetrics;

/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CoreChildrenMetrics extends GenealogyCoreMetric {
	
	/** The universal metric. */
	UniversalChildrenMetrics<JavaChangeOperation> universalMetric;
	
	/**
	 * Instantiates a new core children metrics.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public CoreChildrenMetrics(final CoreChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalChildrenMetrics<JavaChangeOperation>(genealogy);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalChildrenMetrics.getMetricNames();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyCoreNode item) {
		return this.universalMetric.handle(item.getNode());
	}
	
}

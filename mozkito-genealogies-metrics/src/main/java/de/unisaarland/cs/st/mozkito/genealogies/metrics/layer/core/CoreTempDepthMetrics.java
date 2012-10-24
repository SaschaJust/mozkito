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

package de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.core;

import java.util.Collection;

import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.layer.universal.UniversalTempDepthMetrics;
import de.unisaarland.cs.st.mozkito.genealogies.metrics.utils.DaysBetweenUtils;

/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class CoreTempDepthMetrics extends GenealogyCoreMetric implements DayTimeDiff<JavaChangeOperation> {
	
	/** The universal metric. */
	UniversalTempDepthMetrics<JavaChangeOperation> universalMetric;
	
	/**
	 * Instantiates a new core temp depth metrics.
	 *
	 * @param genealogy the genealogy
	 */
	public CoreTempDepthMetrics(CoreChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalTempDepthMetrics<JavaChangeOperation>(genealogy, this);
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.DayTimeDiff#daysDiff(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int daysDiff(JavaChangeOperation t1,
	                    JavaChangeOperation t2) {
		return DaysBetweenUtils.getDaysBetween(t1, t2);
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalTempDepthMetrics.getMetricNames();
	}
	
	/* (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyCoreNode item) {
		return this.universalMetric.handle(item.getNode());
	}
	
}

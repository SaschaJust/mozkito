/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.genealogies.metrics.layer.core;

import java.util.Collection;
import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.Days;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyCoreNode;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.layer.universal.UniversalDwReachMetric;

/**
 * The Class CoreDependencyMetrics.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class CoreDwReachMetric extends GenealogyCoreMetric {
	
	/** The day diff size. */
	private static int                                  dayDiffSize = 14;
	
	/** The universal metric. */
	private UniversalDwReachMetric<JavaChangeOperation> universalMetric;
	
	/**
	 * Instantiates a new core dw reach metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public CoreDwReachMetric(final CoreChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalDwReachMetric<JavaChangeOperation>(genealogy,
		                                                                       new Comparator<JavaChangeOperation>() {
			                                                                       
			                                                                       @Override
			                                                                       public int compare(final JavaChangeOperation original,
			                                                                                          final JavaChangeOperation t) {
				                                                                       
				                                                                       final DateTime oTime = original.getRevision()
				                                                                                                      .getChangeSet()
				                                                                                                      .getTimestamp();
				                                                                       final DateTime tTime = t.getRevision()
				                                                                                               .getChangeSet()
				                                                                                               .getTimestamp();
				                                                                       
				                                                                       final Days daysBetween = Days.daysBetween(oTime,
				                                                                                                                 tTime);
				                                                                       if (daysBetween.getDays() > CoreDwReachMetric.dayDiffSize) {
					                                                                       return 1;
				                                                                       }
				                                                                       return -1;
			                                                                       }
		                                                                       });
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalDwReachMetric.getMetricNames();
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

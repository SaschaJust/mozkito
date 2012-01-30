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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;
import java.util.Comparator;

import org.joda.time.DateTime;
import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionDwReachMetric extends GenealogyPartitionMetric {
	
	private UniversalDwReachMetric<Collection<JavaChangeOperation>> universalMetric;
	private static int                                              dayDiffSize = 14;
	
	public PartitionDwReachMetric(PartitionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalDwReachMetric<Collection<JavaChangeOperation>>(
		                                                                              genealogy,
		                                                                              new Comparator<Collection<JavaChangeOperation>>() {
			                                                                              
			                                                                              @Override
			                                                                              public int compare(Collection<JavaChangeOperation> original,
			                                                                                                 Collection<JavaChangeOperation> t) {
				                                                                              
				                                                                              DateTime latestOriginal = null;
				                                                                              for (JavaChangeOperation op : original) {
					                                                                              if (latestOriginal == null) {
						                                                                              latestOriginal = op.getRevision()
						                                                                                                 .getTransaction()
						                                                                                                 .getTimestamp();
					                                                                              } else {
						                                                                              DateTime tmp = op.getRevision()
						                                                                                               .getTransaction()
						                                                                                               .getTimestamp();
						                                                                              if (tmp.isAfter(latestOriginal)) {
							                                                                              latestOriginal = tmp;
						                                                                              }
					                                                                              }
				                                                                              }
				                                                                              
				                                                                              DateTime earliestT = null;
				                                                                              for (JavaChangeOperation op : t) {
					                                                                              if (earliestT == null) {
						                                                                              earliestT = op.getRevision()
						                                                                                            .getTransaction()
						                                                                                            .getTimestamp();
					                                                                              } else {
						                                                                              DateTime tmp = op.getRevision()
						                                                                                               .getTransaction()
						                                                                                               .getTimestamp();
						                                                                              if (tmp.isBefore(earliestT)) {
							                                                                              earliestT = tmp;
						                                                                              }
					                                                                              }
				                                                                              }
				                                                                              
				                                                                              Days daysBetween = Days.daysBetween(latestOriginal,
				                                                                                                                  earliestT);
				                                                                              if (daysBetween.getDays() > dayDiffSize) {
					                                                                              return 1;
				                                                                              }
				                                                                              return -1;
			                                                                              }
		                                                                              });
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalDwReachMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

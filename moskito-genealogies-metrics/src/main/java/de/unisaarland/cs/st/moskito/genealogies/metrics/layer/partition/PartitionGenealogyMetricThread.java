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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;

public class PartitionGenealogyMetricThread extends Transformer<GenealogyPartitionNode, GenealogyMetricValue> {
	
	Iterator<GenealogyMetricValue>                             iter              = null;
	static private Map<String, PartitionGenealogyMetricThread> registeredMetrics = new HashMap<String, PartitionGenealogyMetricThread>();
	
	public PartitionGenealogyMetricThread(final Group threadGroup, final Settings settings,
	        final GenealogyPartitionMetric metric) {
		super(threadGroup, settings, false);
		
		for (final String mName : metric.getMetricNames()) {
			if (registeredMetrics.containsKey(mName)) {
				throw new UnrecoverableError("You cannot declare the same method thread twice. A metric with name `"
				        + mName + "` is already registered by class `"
				        + registeredMetrics.get(mName).getClass().getCanonicalName() + "`. Class `"
				        + this.getClass().getCanonicalName() + "` cannot be registered. Please resolve conflict.");
			}
			registeredMetrics.put(mName, this);
		}
		
		new ProcessHook<GenealogyPartitionNode, GenealogyMetricValue>(this) {
			
			/*
			 * (non-Javadoc)
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				if ((PartitionGenealogyMetricThread.this.iter == null)
				        || (!PartitionGenealogyMetricThread.this.iter.hasNext())) {
					final GenealogyPartitionNode inputData = getInputData();
					final Collection<GenealogyMetricValue> mValues = metric.handle(inputData);
					PartitionGenealogyMetricThread.this.iter = mValues.iterator();
					if ((PartitionGenealogyMetricThread.this.iter == null)
					        || (!PartitionGenealogyMetricThread.this.iter.hasNext())) {
						skipData();
						return;
					}
				}
				
				providePartialOutputData(PartitionGenealogyMetricThread.this.iter.next());
				if (!PartitionGenealogyMetricThread.this.iter.hasNext()) {
					setCompleted();
				}
			}
		};
		
		new PostExecutionHook<GenealogyPartitionNode, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				
			}
		};
	}
	
	public void postProcess() {
	}
	
}

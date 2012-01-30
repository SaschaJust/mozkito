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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;

public class TransactionGenealogyMetricThread extends AndamaTransformer<GenealogyTransactionNode, GenealogyMetricValue> {
	
	static private Map<String, TransactionGenealogyMetricThread> registeredMetrics = new HashMap<String, TransactionGenealogyMetricThread>();
	protected Iterator<GenealogyMetricValue>                     iter;
	
	public TransactionGenealogyMetricThread(AndamaGroup threadGroup, AndamaSettings settings,
	        final GenealogyTransactionMetric metric) {
		super(threadGroup, settings, false);
		
		for (String mName : metric.getMetricNames()) {
			if (registeredMetrics.containsKey(mName)) {
				throw new UnrecoverableError("You cannot declare the same method thread twice. A metric with name `"
				        + mName + "` is already registered by class `"
				        + registeredMetrics.get(mName).getClass().getCanonicalName() + "`. Class `"
				        + this.getClass().getCanonicalName() + "` cannot be registered. Please resolve conflict.");
			}
			registeredMetrics.put(mName, this);
		}
		
		new ProcessHook<GenealogyTransactionNode, GenealogyMetricValue>(this) {
			
			/*
			 * (non-Javadoc)
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				if ((iter == null) || (!iter.hasNext())) {
					GenealogyTransactionNode inputData = getInputData();
					Collection<GenealogyMetricValue> mValues = metric.handle(inputData);
					iter = mValues.iterator();
					if ((iter == null) || (!iter.hasNext())) {
						skipData();
						return;
					}
				}
				
				providePartialOutputData(iter.next());
				if (!iter.hasNext()) {
					setCompleted();
				}
			}
		};
		
		new PostExecutionHook<GenealogyTransactionNode, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				
			}
		};
	}
	
	public void postProcess() {
	}
	
}

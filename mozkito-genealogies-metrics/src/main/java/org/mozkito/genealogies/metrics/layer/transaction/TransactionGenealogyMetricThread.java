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

package org.mozkito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyTransactionNode;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Transformer;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

/**
 * The Class TransactionGenealogyMetricThread.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TransactionGenealogyMetricThread extends Transformer<GenealogyTransactionNode, GenealogyMetricValue> {
	
	/** The registered metrics. */
	static private Map<String, TransactionGenealogyMetricThread> registeredMetrics = new HashMap<String, TransactionGenealogyMetricThread>();
	
	/** The iter. */
	protected Iterator<GenealogyMetricValue>                     iter;
	
	/**
	 * Instantiates a new transaction genealogy metric thread.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param metric
	 *            the metric
	 */
	public TransactionGenealogyMetricThread(final Group threadGroup, final Settings settings,
	        final GenealogyTransactionMetric metric) {
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
		
		new ProcessHook<GenealogyTransactionNode, GenealogyMetricValue>(this) {
			
			/*
			 * (non-Javadoc)
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				if ((TransactionGenealogyMetricThread.this.iter == null)
				        || (!TransactionGenealogyMetricThread.this.iter.hasNext())) {
					final GenealogyTransactionNode inputData = getInputData();
					final Collection<GenealogyMetricValue> mValues = metric.handle(inputData);
					TransactionGenealogyMetricThread.this.iter = mValues.iterator();
					if ((TransactionGenealogyMetricThread.this.iter == null)
					        || (!TransactionGenealogyMetricThread.this.iter.hasNext())) {
						skipData();
						return;
					}
				}
				
				providePartialOutputData(TransactionGenealogyMetricThread.this.iter.next());
				if (!TransactionGenealogyMetricThread.this.iter.hasNext()) {
					setCompleted();
				}
			}
		};
	}
	
}

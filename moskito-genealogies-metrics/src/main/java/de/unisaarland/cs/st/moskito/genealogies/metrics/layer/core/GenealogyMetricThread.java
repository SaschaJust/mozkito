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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

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
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.lang.StringUtils;

import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;

public class GenealogyMetricThread extends Transformer<GenealogyCoreNode, GenealogyMetricValue> {
	
	static private Map<String, GenealogyMetricThread> registeredMetrics = new HashMap<String, GenealogyMetricThread>();
	
	private Iterator<GenealogyMetricValue>            iter              = null;
	private String                                    metricName        = "<UNKNOWN>";
	
	public GenealogyMetricThread(final Group threadGroup, final Settings settings, final GenealogyCoreMetric metric) {
		super(threadGroup, settings, false);
		
		this.metricName = StringUtils.join(metric.getMetricNames().toArray(new String[metric.getMetricNames().size()]));
		for (final String mName : metric.getMetricNames()) {
			if (registeredMetrics.containsKey(mName)) {
				throw new UnrecoverableError("You cannot declare the same method thread twice. A metric with name `"
				        + mName + "` is already registered by class `"
				        + registeredMetrics.get(mName).getClass().getCanonicalName() + "`. Class `"
				        + this.getClass().getCanonicalName() + "` cannot be registered. Please resolve conflict.");
			}
			registeredMetrics.put(mName, this);
		}
		
		new ProcessHook<GenealogyCoreNode, GenealogyMetricValue>(this) {
			
			/*
			 * (non-Javadoc)
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				
				if ((GenealogyMetricThread.this.iter == null) || (!GenealogyMetricThread.this.iter.hasNext())) {
					final GenealogyCoreNode inputData = getInputData();
					if (Logger.logDebug()) {
						Logger.debug("Metric " + GenealogyMetricThread.this.metricName + " handling " + inputData);
					}
					
					final Collection<GenealogyMetricValue> mValues = metric.handle(inputData);
					GenealogyMetricThread.this.iter = mValues.iterator();
					if ((GenealogyMetricThread.this.iter == null) || (!GenealogyMetricThread.this.iter.hasNext())) {
						skipData();
						return;
					}
				}
				
				providePartialOutputData(GenealogyMetricThread.this.iter.next());
				if (!GenealogyMetricThread.this.iter.hasNext()) {
					setCompleted();
				}
				
			}
		};
		
		new PostExecutionHook<GenealogyCoreNode, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				
			}
		};
	}
	
	public void postProcess() {
	}
}

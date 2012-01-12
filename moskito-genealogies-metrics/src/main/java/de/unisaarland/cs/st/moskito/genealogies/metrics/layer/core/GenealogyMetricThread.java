package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

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
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyCoreNode;

public class GenealogyMetricThread extends AndamaTransformer<GenealogyCoreNode, GenealogyMetricValue> {
	
	static private Map<String, GenealogyMetricThread> registeredMetrics = new HashMap<String, GenealogyMetricThread>();
	
	private Iterator<GenealogyMetricValue>            iter              = null;
	
	public GenealogyMetricThread(AndamaGroup threadGroup, AndamaSettings settings, final GenealogyCoreMetric metric) {
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
		
		new ProcessHook<GenealogyCoreNode, GenealogyMetricValue>(this) {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				
				if((iter == null) || (!iter.hasNext())){
					GenealogyCoreNode inputData = getInputData();
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
		
		new PostExecutionHook<GenealogyCoreNode, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				
			}
		};
	}
	
	public void postProcess() {
	}
	
}

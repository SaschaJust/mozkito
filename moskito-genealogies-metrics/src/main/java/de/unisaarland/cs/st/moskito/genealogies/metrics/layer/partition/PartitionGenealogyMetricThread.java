package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyPartitionNode;


public abstract class PartitionGenealogyMetricThread extends
AndamaTransformer<GenealogyPartitionNode, GenealogyMetricValue>
implements
        GenealogyMetric<GenealogyPartitionNode> {
	
	static private Map<String, GenealogyMetric<?>> registeredMetrics = new HashMap<String, GenealogyMetric<?>>();
	
	public PartitionGenealogyMetricThread(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		for(String mName : this.getMetricNames()){
			if(registeredMetrics.containsKey(mName)){
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
			 * 
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				GenealogyPartitionNode inputData = getInputData();
				Collection<GenealogyMetricValue> mValues = handle(inputData);
				for (GenealogyMetricValue mValue : mValues) {
					providePartialOutputData(mValue);
				}
				setCompleted();
				
			}
		};
		
		new PostExecutionHook<GenealogyPartitionNode, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				
			}
		};
	}
	
	@Override
	public abstract Collection<String> getMetricNames();
	
	@Override
	public abstract Collection<GenealogyMetricValue> handle(GenealogyPartitionNode item);
	
	public void postProcess() {
	}
	
}

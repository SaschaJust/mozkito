package de.unisaarland.cs.st.moskito.genealogies.metrics;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.ProcessHook;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;


public abstract class GenealogyMetricThread<T> extends AndamaTransformer<GenealogyNode<T>, GenealogyMetricValue>
implements
GenealogyMetric<T> {
	
	public GenealogyMetricThread(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		new ProcessHook<GenealogyNode<T>, GenealogyMetricValue>(this) {
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see net.ownhero.dev.andama.threads.ProcessHook#process()
			 */
			@Override
			public void process() {
				GenealogyNode<T> inputData = getInputData();
				Double result = handle(inputData);
				
				GenealogyMetricValue metricValue = new GenealogyMetricValue(getMetricName(), inputData.getNodeId(),
						result);
				provideOutputData(metricValue);
			}
		};
	}
	
	@Override
	public final String getMetricName() {
		return this.getClass().getCanonicalName();
	}
	
	@Override
	public abstract Double handle(GenealogyNode<T> item);
	
}

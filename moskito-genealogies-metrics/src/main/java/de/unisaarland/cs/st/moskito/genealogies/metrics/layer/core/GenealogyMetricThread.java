package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaTransformer;
import net.ownhero.dev.andama.threads.PostExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyCoreNode;

public abstract class GenealogyMetricThread extends AndamaTransformer<GenealogyCoreNode, GenealogyMetricValue> {
	
	static private Map<String, GenealogyMetricThread> registeredMetrics = new HashMap<String, GenealogyMetricThread>();
	
	public GenealogyMetricThread(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		if (Logger.logDebug()) {
			ParameterizedType type = (ParameterizedType) this.getClass().getGenericSuperclass();
			Type type2 = type.getActualTypeArguments()[0];
			Logger.debug("Loaded: " + this.getClass().toString() + ":"
			        + this.getClass().getGenericSuperclass().toString() + ":" + type + ":" + type2);

		}
		
		for (String mName : this.getMetricNames()) {
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
				GenealogyCoreNode inputData = getInputData();
				Collection<GenealogyMetricValue> mValues = handle(inputData);
				for (GenealogyMetricValue mValue : mValues) {
					providePartialOutputData(mValue);
				}
				setCompleted();
				
			}
		};
		
		new PostExecutionHook<GenealogyCoreNode, GenealogyMetricValue>(this) {
			
			@Override
			public void postExecution() {
				
			}
		};
	}
	
	public abstract Collection<String> getMetricNames();
	
	public abstract Collection<GenealogyMetricValue> handle(GenealogyCoreNode item);
	
	public void postProcess() {
	}
	
}

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;


public abstract class GenealogyCoreMetric extends GenealogyMetricThread {
	
	protected CoreChangeGenealogy genealogy;
	
	public GenealogyCoreMetric(AndamaGroup threadGroup, AndamaSettings settings, CoreChangeGenealogy genealogy) {
		super(threadGroup, settings);
		this.genealogy = genealogy;
	}
}

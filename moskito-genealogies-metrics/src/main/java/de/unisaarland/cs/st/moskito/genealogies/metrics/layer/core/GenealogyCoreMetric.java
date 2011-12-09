package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricThread;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public abstract class GenealogyCoreMetric extends GenealogyMetricThread<JavaChangeOperation> {
	
	protected CoreChangeGenealogy genealogy;
	
	public GenealogyCoreMetric(AndamaGroup threadGroup, AndamaSettings settings, CoreChangeGenealogy genealogy) {
		super(threadGroup, settings);
		this.genealogy = genealogy;
	}
}

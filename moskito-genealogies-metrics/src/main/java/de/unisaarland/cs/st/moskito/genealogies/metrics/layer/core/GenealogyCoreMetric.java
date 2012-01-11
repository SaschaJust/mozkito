package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyCoreNode;


public abstract class GenealogyCoreMetric implements GenealogyMetric<GenealogyCoreNode> {
	
	protected CoreChangeGenealogy genealogy;
	
	public GenealogyCoreMetric(CoreChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

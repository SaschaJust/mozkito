package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.core;

import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyCoreNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric;


public abstract class GenealogyCoreMetric implements GenealogyMetric<GenealogyCoreNode> {
	
	protected CoreChangeGenealogy genealogy;
	
	public GenealogyCoreMetric(CoreChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

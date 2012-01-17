package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.partition;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyPartitionNode;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;


public abstract class GenealogyPartitionMetric implements GenealogyMetric<GenealogyPartitionNode> {
	
	protected ChangeGenealogy<Collection<JavaChangeOperation>> genealogy;
	
	public GenealogyPartitionMetric(PartitionChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

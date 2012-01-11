package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public abstract class GenealogyTransactionMetric implements GenealogyMetric<GenealogyTransactionNode> {
	
	protected ChangeGenealogy<RCSTransaction> genealogy;
	
	public GenealogyTransactionMetric(TransactionChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

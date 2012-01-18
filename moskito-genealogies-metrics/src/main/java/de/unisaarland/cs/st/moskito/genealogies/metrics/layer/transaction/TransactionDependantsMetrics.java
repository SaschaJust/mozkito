package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalAncestorMetrics;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionDependantsMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalAncestorMetrics<RCSTransaction> universalMetric;
	
	public TransactionDependantsMetrics(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalAncestorMetrics<RCSTransaction>(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalAncestorMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

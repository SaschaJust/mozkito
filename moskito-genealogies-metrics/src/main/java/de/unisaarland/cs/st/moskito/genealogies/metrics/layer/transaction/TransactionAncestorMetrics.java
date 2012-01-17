package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalParentsMetrics;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionAncestorMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalParentsMetrics<RCSTransaction> universalMetric;
	
	public TransactionAncestorMetrics(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalParentsMetrics<RCSTransaction>(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalParentsMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

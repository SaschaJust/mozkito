package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInbreedMetrics;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionInbreedMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalInbreedMetrics<RCSTransaction> universalMetric;
	
	public TransactionInbreedMetrics(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalInbreedMetrics<RCSTransaction>(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalInbreedMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

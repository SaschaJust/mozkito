package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInOutDegreeMetrics;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionInOutDegreeMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalInOutDegreeMetrics<RCSTransaction> universalMetric;
	
	public TransactionInOutDegreeMetrics(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalInOutDegreeMetrics<RCSTransaction>(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalInOutDegreeMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

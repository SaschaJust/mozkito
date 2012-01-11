package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalStructuralHolesMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionStructuralHolesMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalStructuralHolesMetrics<RCSTransaction> universalMetric;
	
	public TransactionStructuralHolesMetrics(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalStructuralHolesMetrics<RCSTransaction>(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalStructuralHolesMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

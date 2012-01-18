package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalResponseTimeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionResponseTimeMetrics extends GenealogyTransactionMetric implements DayTimeDiff<RCSTransaction> {
	
	
	private UniversalResponseTimeMetrics<RCSTransaction> universalMetric;
	
	public TransactionResponseTimeMetrics(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalResponseTimeMetrics<RCSTransaction>(genealogy, this);
	}
	
	@Override
	public int daysDiff(RCSTransaction t1, RCSTransaction t2) {
		return DaysBetweenUtils.getDaysBetween(t1, t2);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalResponseTimeMetrics.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}
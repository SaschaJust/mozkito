package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;
import java.util.Comparator;

import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionDwReachMetric extends GenealogyTransactionMetric {
	
	private UniversalDwReachMetric<RCSTransaction> universalMetric;
	
	private static int                             dayDiffSize = 14;
	
	public TransactionDwReachMetric(TransactionChangeGenealogy genealogy) {
		super(genealogy);
		universalMetric = new UniversalDwReachMetric<RCSTransaction>(genealogy, new Comparator<RCSTransaction>() {
			
			@Override
			public int compare(RCSTransaction original, RCSTransaction t) {
				Days daysBetween = Days.daysBetween(original.getTimestamp(), t.getTimestamp());
				if (daysBetween.getDays() > dayDiffSize) {
					return 1;
				}
				return -1;
			}
		});
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return UniversalDwReachMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyTransactionNode item) {
		return universalMetric.handle(item.getNode());
	}
	
}

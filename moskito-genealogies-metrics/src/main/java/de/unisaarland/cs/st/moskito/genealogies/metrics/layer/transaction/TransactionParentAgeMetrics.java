package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;

import org.joda.time.Days;

import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalParentAgeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionParentAgeMetrics extends GenealogyTransactionMetric implements DayTimeDiff<RCSTransaction> {
	
	
	private UniversalParentAgeMetrics<RCSTransaction> universalMetric;
	
	public TransactionParentAgeMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalParentAgeMetrics<RCSTransaction>(genealogy, this);
	}
	
	@Override
	public int daysDiff(RCSTransaction t1, RCSTransaction t2) {
		return Days.daysBetween(t1.getTimestamp(), t2.getTimestamp()).getDays();
	}
	
	@Override
	public Collection<String> getMetricNames() {
		return universalMetric.getMetricNames();
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyNode<RCSTransaction> item) {
		return universalMetric.handle(item.getNode());
	}
	
}
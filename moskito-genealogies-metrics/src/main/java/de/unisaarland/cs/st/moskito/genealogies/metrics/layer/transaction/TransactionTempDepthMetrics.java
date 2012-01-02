package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.DayTimeDiff;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalTempDepthMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.utils.DaysBetweenUtils;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionTempDepthMetrics extends GenealogyTransactionMetric implements DayTimeDiff<RCSTransaction> {
	
	
	private UniversalTempDepthMetrics<RCSTransaction> universalMetric;
	
	public TransactionTempDepthMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalTempDepthMetrics<RCSTransaction>(genealogy, this);
	}
	
	@Override
	public int daysDiff(RCSTransaction t1, RCSTransaction t2) {
		return DaysBetweenUtils.getDaysBetween(t1, t2);
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

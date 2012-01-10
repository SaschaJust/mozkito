package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalInOutDegreeMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionInOutDegreeMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalInOutDegreeMetrics<RCSTransaction> universalMetric;
	
	public TransactionInOutDegreeMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
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

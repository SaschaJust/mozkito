package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalParentsMetrics;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionAncestorMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalParentsMetrics<RCSTransaction> universalMetric;
	
	public TransactionAncestorMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
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

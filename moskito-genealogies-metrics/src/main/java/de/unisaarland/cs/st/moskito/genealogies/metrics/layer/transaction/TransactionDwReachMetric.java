package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.layer.universal.UniversalDwReachMetric;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionDwReachMetric extends GenealogyTransactionMetric {
	
	
	private UniversalDwReachMetric<RCSTransaction> universalMetric;
	
	public TransactionDwReachMetric(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalDwReachMetric<RCSTransaction>(genealogy);
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

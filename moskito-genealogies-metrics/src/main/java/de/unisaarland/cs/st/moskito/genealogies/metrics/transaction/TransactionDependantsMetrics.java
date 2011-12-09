package de.unisaarland.cs.st.moskito.genealogies.metrics.transaction;

import java.util.Collection;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.layer.universal.UniversalDependantsMetrics;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionDependantsMetrics extends GenealogyTransactionMetric {
	
	
	private UniversalDependantsMetrics<RCSTransaction> universalMetric;
	
	public TransactionDependantsMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
		universalMetric = new UniversalDependantsMetrics<RCSTransaction>(genealogy);
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

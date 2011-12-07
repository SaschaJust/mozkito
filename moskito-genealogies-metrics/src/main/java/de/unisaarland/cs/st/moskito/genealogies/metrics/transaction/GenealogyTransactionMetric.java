package de.unisaarland.cs.st.moskito.genealogies.metrics.transaction;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricThread;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public abstract class GenealogyTransactionMetric extends GenealogyMetricThread<RCSTransaction> {
	
	public GenealogyTransactionMetric(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings);
	}
	
}

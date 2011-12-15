package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricThread;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public abstract class GenealogyTransactionMetric extends GenealogyMetricThread<RCSTransaction> {
	
	protected ChangeGenealogy<RCSTransaction> genealogy;
	
	public GenealogyTransactionMetric(AndamaGroup threadGroup, AndamaSettings settings,
	        ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings);
		this.genealogy = genealogy;
	}
	
}

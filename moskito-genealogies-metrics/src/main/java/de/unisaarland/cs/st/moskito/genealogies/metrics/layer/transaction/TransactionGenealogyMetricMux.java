package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaMultiplexer;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyTransactionNode;


public class TransactionGenealogyMetricMux extends AndamaMultiplexer<GenealogyTransactionNode> {
	
	public TransactionGenealogyMetricMux(AndamaGroup threadGroup, AndamaSettings settings) {
		super(threadGroup, settings, false);
		
		
	}
	
}

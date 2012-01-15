package de.unisaarland.cs.st.moskito.genealogies.utils.andama;


import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.layer.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionGenealogyReader extends AndamaSource<GenealogyTransactionNode> {
	
	private Iterator<RCSTransaction> iterator;
	
	public TransactionGenealogyReader(AndamaGroup threadGroup, AndamaSettings settings,
			final TransactionChangeGenealogy changeGenealogy) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyTransactionNode, GenealogyTransactionNode>(this) {
			
			@Override
			public void preExecution() {
				iterator = changeGenealogy.vertexSet().iterator();
			}
		};
		
		new ProcessHook<GenealogyTransactionNode, GenealogyTransactionNode>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					RCSTransaction t = iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + t);
					}
					
					GenealogyTransactionNode node = null;
					if (iterator.hasNext()) {
						node = new GenealogyTransactionNode(t, changeGenealogy.getNodeId(t));
					} else {
						node = new GenealogyTransactionNode(t, changeGenealogy.getNodeId(t), true);
					}
					providePartialOutputData(node);
					if (!iterator.hasNext()) {
						setCompleted();
					}
				}
			}
		};
	}
	
}

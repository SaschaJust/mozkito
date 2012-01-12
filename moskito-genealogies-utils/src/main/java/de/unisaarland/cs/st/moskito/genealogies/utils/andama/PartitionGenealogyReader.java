package de.unisaarland.cs.st.moskito.genealogies.utils.andama;


import java.util.Collection;
import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionGenealogyReader extends AndamaSource<GenealogyPartitionNode> {
	
	private Iterator<Collection<JavaChangeOperation>> iterator;
	
	public PartitionGenealogyReader(AndamaGroup threadGroup, AndamaSettings settings,
			final PartitionChangeGenealogy changeGenealogy) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyPartitionNode, GenealogyPartitionNode>(this) {
			
			@Override
			public void preExecution() {
				iterator = changeGenealogy.vertexSet();
			}
		};
		
		new ProcessHook<GenealogyPartitionNode, GenealogyPartitionNode>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					Collection<JavaChangeOperation> t = iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + t);
					}
					
					GenealogyPartitionNode node = null;
					if (iterator.hasNext()) {
						node = new GenealogyPartitionNode(t, changeGenealogy.getNodeId(t));
					} else {
						node = new GenealogyPartitionNode(t, changeGenealogy.getNodeId(t), true);
					}
					providePartialOutputData(node);
					if(!iterator.hasNext()){
						setCompleted();
					}
				}
			}
		};
	}
	
}

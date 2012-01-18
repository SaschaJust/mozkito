package de.unisaarland.cs.st.moskito.genealogies.metrics;


import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.CoreChangeGenealogy;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class GenealogyReader extends AndamaSource<GenealogyCoreNode> {
	
	private Iterator<JavaChangeOperation> iterator;
	
	public GenealogyReader(AndamaGroup threadGroup, AndamaSettings settings,
			final CoreChangeGenealogy changeGenealogy) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyCoreNode, GenealogyCoreNode>(this) {
			
			@Override
			public void preExecution() {
				iterator = changeGenealogy.vertexSet().iterator();
			}
		};
		
		new ProcessHook<GenealogyCoreNode, GenealogyCoreNode>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					JavaChangeOperation t = iterator.next();
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + t);
					}
					
					GenealogyCoreNode node = null;
					if (iterator.hasNext()) {
						node = new GenealogyCoreNode(t, changeGenealogy.getNodeId(t));
					} else {
						node = new GenealogyCoreNode(t, changeGenealogy.getNodeId(t), true);
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

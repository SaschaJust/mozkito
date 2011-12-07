package de.unisaarland.cs.st.moskito.genealogies.utils.andama;


import java.util.Iterator;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;

public class GenealogyReader<T> extends AndamaSource<GenealogyNode<T>> {
	
	private Iterator<T> iterator;
	
	public GenealogyReader(AndamaGroup threadGroup, AndamaSettings settings,
			final ChangeGenealogy<T> changeGenealogy) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyNode<T>, GenealogyNode<T>>(this) {
			
			@Override
			public void preExecution() {
				iterator = changeGenealogy.vertexSet();
			}
		};
		
		new ProcessHook<GenealogyNode<T>, GenealogyNode<T>>(this) {
			
			@Override
			public void process() {
				if (iterator.hasNext()) {
					T t = iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + t);
					}
					
					GenealogyNode<T> node = new GenealogyNode<T>(t, changeGenealogy.getNodeId(t));
					providePartialOutputData(node);
				} else {
					provideOutputData(null, true);
					setCompleted();
				}
			}
		};
	}
	
}

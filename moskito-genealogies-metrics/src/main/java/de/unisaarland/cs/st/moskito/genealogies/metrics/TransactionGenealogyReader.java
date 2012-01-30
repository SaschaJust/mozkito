/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


package de.unisaarland.cs.st.moskito.genealogies.metrics;


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

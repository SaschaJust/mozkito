/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

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
	
	public GenealogyReader(AndamaGroup threadGroup, AndamaSettings settings, final CoreChangeGenealogy changeGenealogy) {
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

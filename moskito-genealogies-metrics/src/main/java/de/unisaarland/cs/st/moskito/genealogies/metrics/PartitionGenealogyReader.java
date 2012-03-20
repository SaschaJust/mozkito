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

import java.util.Collection;
import java.util.Iterator;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.layer.PartitionChangeGenealogy;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;

public class PartitionGenealogyReader extends Source<GenealogyPartitionNode> {
	
	private Iterator<Collection<JavaChangeOperation>> iterator;
	
	public PartitionGenealogyReader(final Group threadGroup, final Settings settings,
	        final PartitionChangeGenealogy changeGenealogy) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyPartitionNode, GenealogyPartitionNode>(this) {
			
			@Override
			public void preExecution() {
				PartitionGenealogyReader.this.iterator = changeGenealogy.vertexSet().iterator();
			}
		};
		
		new ProcessHook<GenealogyPartitionNode, GenealogyPartitionNode>(this) {
			
			@Override
			public void process() {
				if (PartitionGenealogyReader.this.iterator.hasNext()) {
					final Collection<JavaChangeOperation> t = PartitionGenealogyReader.this.iterator.next();
					
					if (Logger.logInfo()) {
						Logger.info("Providing " + t);
					}
					
					GenealogyPartitionNode node = null;
					if (PartitionGenealogyReader.this.iterator.hasNext()) {
						node = new GenealogyPartitionNode(t, changeGenealogy.getNodeId(t));
					} else {
						node = new GenealogyPartitionNode(t, changeGenealogy.getNodeId(t), true);
					}
					providePartialOutputData(node);
					if (!PartitionGenealogyReader.this.iterator.hasNext()) {
						setCompleted();
					}
				}
			}
		};
	}
	
}

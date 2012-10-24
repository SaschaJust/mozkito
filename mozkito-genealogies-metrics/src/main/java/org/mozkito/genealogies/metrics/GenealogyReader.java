/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/

package org.mozkito.genealogies.metrics;

import java.util.Iterator;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.core.CoreChangeGenealogy;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class GenealogyReader.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class GenealogyReader extends Source<GenealogyCoreNode> {
	
	/** The iterator. */
	private Iterator<JavaChangeOperation> iterator;
	
	/**
	 * Instantiates a new genealogy reader.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 * @param changeGenealogy the change genealogy
	 */
	public GenealogyReader(final Group threadGroup, final Settings settings, final CoreChangeGenealogy changeGenealogy) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<GenealogyCoreNode, GenealogyCoreNode>(this) {
			
			@Override
			public void preExecution() {
				GenealogyReader.this.iterator = changeGenealogy.vertexSet().iterator();
			}
		};
		
		new ProcessHook<GenealogyCoreNode, GenealogyCoreNode>(this) {
			
			@Override
			public void process() {
				if (GenealogyReader.this.iterator.hasNext()) {
					final JavaChangeOperation t = GenealogyReader.this.iterator.next();
					
					if (Logger.logDebug()) {
						Logger.debug("Providing " + t);
					}
					
					GenealogyCoreNode node = null;
					if (GenealogyReader.this.iterator.hasNext()) {
						node = new GenealogyCoreNode(t, changeGenealogy.getNodeId(t));
					} else {
						node = new GenealogyCoreNode(t, changeGenealogy.getNodeId(t), true);
					}
					providePartialOutputData(node);
					if (!GenealogyReader.this.iterator.hasNext()) {
						setCompleted();
					}
				}
			}
		};
	}
	
}

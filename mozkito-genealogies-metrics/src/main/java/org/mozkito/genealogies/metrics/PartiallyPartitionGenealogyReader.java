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
 *******************************************************************************/

package org.mozkito.genealogies.metrics;

import java.util.Collection;
import java.util.Iterator;

import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.genealogies.layer.PartitionChangeGenealogy;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

/**
 * The Class PartitionGenealogyReader.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class PartiallyPartitionGenealogyReader extends Source<GenealogyPartitionNode> {
	
	/** The iterator. */
	private Iterator<ChangeGenealogyLayerNode> iterator;
	
	/**
	 * Instantiates a new partition genealogy reader.
	 * 
	 * @param threadGroup
	 *            the thread group
	 * @param settings
	 *            the settings
	 * @param changeGenealogy
	 *            the change genealogy
	 */
	public PartiallyPartitionGenealogyReader(final Group threadGroup, final Settings settings,
	        final PartitionChangeGenealogy changeGenealogy, final Collection<ChangeGenealogyLayerNode> collection) {
		super(threadGroup, settings, false);
		
		new ProcessHook<GenealogyPartitionNode, GenealogyPartitionNode>(this) {
			
			@Override
			public void process() {
				
				PartiallyPartitionGenealogyReader.this.iterator = collection.iterator();
				while (PartiallyPartitionGenealogyReader.this.iterator.hasNext()) {
					final ChangeGenealogyLayerNode t = PartiallyPartitionGenealogyReader.this.iterator.next();
					Condition.notNull(t, "Change genealogy partition must not be null!");
					if (Logger.logInfo()) {
						Logger.info("Providing ChangeGenealogyLayerNode %s." + t.getNodeId());
					}
					
					if (!PartiallyPartitionGenealogyReader.this.iterator.hasNext()) {
						providePartialOutputData(new GenealogyPartitionNode(t, true));
						setCompleted();
					} else {
						providePartialOutputData(new GenealogyPartitionNode(t));
					}
				}
			}
		};
	}
	
}

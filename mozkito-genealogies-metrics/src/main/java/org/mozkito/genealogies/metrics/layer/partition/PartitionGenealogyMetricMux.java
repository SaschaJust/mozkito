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

package org.mozkito.genealogies.metrics.layer.partition;

import org.mozkito.genealogies.metrics.GenealogyPartitionNode;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.Multiplexer;
import net.ownhero.dev.hiari.settings.Settings;

/**
 * The Class PartitionGenealogyMetricMux.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class PartitionGenealogyMetricMux extends Multiplexer<GenealogyPartitionNode> {
	
	/**
	 * Instantiates a new partition genealogy metric mux.
	 *
	 * @param threadGroup the thread group
	 * @param settings the settings
	 */
	public PartitionGenealogyMetricMux(final Group threadGroup, final Settings settings) {
		super(threadGroup, settings, false);
		
	}
	
}

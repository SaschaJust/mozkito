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

package org.mozkito.genealogies.metrics.layer.partition;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.layer.ChangeGenealogyLayerNode;
import org.mozkito.genealogies.layer.PartitionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetric;
import org.mozkito.genealogies.metrics.GenealogyPartitionNode;

/**
 * The Class GenealogyPartitionMetric.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class GenealogyPartitionMetric implements GenealogyMetric<GenealogyPartitionNode> {
	
	/** The genealogy. */
	protected ChangeGenealogy<ChangeGenealogyLayerNode> genealogy;
	
	/**
	 * Instantiates a new genealogy partition metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public GenealogyPartitionMetric(final PartitionChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

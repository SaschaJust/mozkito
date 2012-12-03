/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.genealogies.metrics.layer.core;

import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyCoreNode;
import org.mozkito.genealogies.metrics.GenealogyMetric;

/**
 * The Class GenealogyCoreMetric.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class GenealogyCoreMetric implements GenealogyMetric<GenealogyCoreNode> {
	
	/** The genealogy. */
	protected CoreChangeGenealogy genealogy;
	
	/**
	 * Instantiates a new genealogy core metric.
	 * 
	 * @param genealogy
	 *            the genealogy
	 */
	public GenealogyCoreMetric(final CoreChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

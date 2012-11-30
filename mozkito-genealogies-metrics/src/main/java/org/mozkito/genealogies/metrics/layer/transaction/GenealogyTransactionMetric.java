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

package org.mozkito.genealogies.metrics.layer.transaction;

import org.mozkito.genealogies.ChangeGenealogy;
import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetric;
import org.mozkito.genealogies.metrics.GenealogyTransactionNode;
import org.mozkito.versions.model.RCSTransaction;


/**
 * The Class GenealogyTransactionMetric.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public abstract class GenealogyTransactionMetric implements GenealogyMetric<GenealogyTransactionNode> {
	
	/** The genealogy. */
	protected ChangeGenealogy<RCSTransaction> genealogy;
	
	/**
	 * Instantiates a new genealogy transaction metric.
	 *
	 * @param genealogy the genealogy
	 */
	public GenealogyTransactionMetric(TransactionChangeGenealogy genealogy) {
		this.genealogy = genealogy;
	}
}

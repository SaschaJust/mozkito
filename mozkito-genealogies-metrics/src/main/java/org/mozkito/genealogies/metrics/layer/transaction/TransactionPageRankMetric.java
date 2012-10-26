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

import java.util.Collection;

import org.mozkito.genealogies.core.TransactionChangeGenealogy;
import org.mozkito.genealogies.metrics.GenealogyMetricValue;
import org.mozkito.genealogies.metrics.GenealogyTransactionNode;
import org.mozkito.genealogies.metrics.layer.universal.UniversalPageRankMetric;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.kisa.Logger;

/**
 * The Class TransactionPageRankMetric.
 *
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class TransactionPageRankMetric extends GenealogyTransactionMetric {
	
	/** The universal metric. */
	private final UniversalPageRankMetric<RCSTransaction> universalMetric;
	
	/**
	 * Instantiates a new transaction page rank metric.
	 *
	 * @param genealogy the genealogy
	 */
	public TransactionPageRankMetric(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
		this.universalMetric = new UniversalPageRankMetric<RCSTransaction>(genealogy);
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#getMetricNames()
	 */
	@Override
	public Collection<String> getMetricNames() {
		return UniversalPageRankMetric.getMetricNames();
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.metrics.GenealogyMetric#handle(java.lang.Object)
	 */
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getCanonicalName() + " handles node " + item.getNodeId());
		}
		return this.universalMetric.handle(item.getNode(), item.isLast());
	}
	
}

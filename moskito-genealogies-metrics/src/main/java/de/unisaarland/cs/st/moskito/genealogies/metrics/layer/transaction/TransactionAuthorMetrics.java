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

package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.genealogies.core.TransactionChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyTransactionNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;

public class TransactionAuthorMetrics extends GenealogyTransactionMetric {
	
	private static final String numDepAuthors    = "NumDepAuthors";
	private static final String numParentAuthors = "NumParentAuthors";
	
	// private static final String avgAuthorPackageDistance = "avgAuthorPackageDistance";
	// private static final String maxAuthorPackageDistance = "maxAuthorPackageDistance";
	// private static final String minAuthorPackageDistance = "minAuthorPackageDistance";
	
	public TransactionAuthorMetrics(final TransactionChangeGenealogy genealogy) {
		super(genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		final List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(numDepAuthors);
		metricNames.add(numParentAuthors);
		// metricNames.add(avgAuthorPackageDistance);
		// metricNames.add(maxAuthorPackageDistance);
		// metricNames.add(minAuthorPackageDistance);
		return metricNames;
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(final GenealogyTransactionNode item) {
		if (Logger.logDebug()) {
			Logger.debug(this.getClass().getCanonicalName() + " handles node " + item.getNodeId());
		}
		final Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		final RCSTransaction transaction = item.getNode();
		final String nodeId = this.genealogy.getNodeId(transaction);
		
		final Set<Long> depAuthors = new HashSet<Long>();
		for (final RCSTransaction dependant : this.genealogy.getAllDependants(transaction)) {
			depAuthors.add(dependant.getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		final Set<Long> parentAuthors = new HashSet<Long>();
		for (final RCSTransaction parent : this.genealogy.getAllParents(transaction)) {
			parentAuthors.add(parent.getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		// final PersistenceUtil persistenceUtil = this.genealogy.getCore().getPersistenceUtil();
		// final Criteria<RCSTransaction> criteria = persistenceUtil.createCriteria(RCSTransaction.class)
		// .eq("persons", transaction.getPersons())
		// .oderByDesc("javaTimestamp");
		// final TreeSet<RCSTransaction> previousTransactions = new TreeSet<RCSTransaction>();
		// previousTransactions.addAll(persistenceUtil.load(criteria));
		//
		// final RCSTransaction previousTransaction = previousTransactions.lower(transaction);
		//
		// final DescriptiveStatistics packageStats = new DescriptiveStatistics();
		// for (final RCSFile file : previousTransaction.getChangedFiles()) {
		//
		// }
		
		return metricValues;
	}
}

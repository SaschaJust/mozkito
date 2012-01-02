package de.unisaarland.cs.st.moskito.genealogies.metrics.layer.transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.threads.AndamaGroup;
import de.unisaarland.cs.st.moskito.genealogies.ChangeGenealogy;
import de.unisaarland.cs.st.moskito.genealogies.metrics.GenealogyMetricValue;
import de.unisaarland.cs.st.moskito.genealogies.utils.andama.GenealogyNode;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionAuthorMetrics extends GenealogyTransactionMetric{
	
	public static String    numDepAuthors    = "changeSize";
	public static String    numParentAuthors = "avgDepChangeSize";
	
	public TransactionAuthorMetrics(AndamaGroup threadGroup, AndamaSettings settings,
			ChangeGenealogy<RCSTransaction> genealogy) {
		super(threadGroup, settings, genealogy);
	}
	
	@Override
	public Collection<String> getMetricNames() {
		List<String> metricNames = new ArrayList<String>(2);
		metricNames.add(numDepAuthors);
		metricNames.add(numParentAuthors);
		return metricNames;
	}
	
	@Override
	public Collection<GenealogyMetricValue> handle(GenealogyNode<RCSTransaction> item) {
		Collection<GenealogyMetricValue> metricValues = new ArrayList<GenealogyMetricValue>(2);
		
		RCSTransaction transaction = item.getNode();
		String nodeId = genealogy.getNodeId(transaction);
		
		Set<Long> depAuthors = new HashSet<Long>();
		for (RCSTransaction dependant : genealogy.getAllDependants(transaction)) {
			depAuthors.add(dependant.getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(numDepAuthors, nodeId, depAuthors.size()));
		
		Set<Long> parentAuthors = new HashSet<Long>();
		for (RCSTransaction parent : genealogy.getAllParents(transaction)) {
			parentAuthors.add(parent.getPersons().getGeneratedId());
		}
		
		metricValues.add(new GenealogyMetricValue(numParentAuthors, nodeId, parentAuthors.size()));
		
		return metricValues;
	}
	
}

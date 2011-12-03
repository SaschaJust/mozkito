package de.unisaarland.cs.st.moskito.genealogies.layer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.unisaarland.cs.st.moskito.genealogies.PartitionGenerator;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;


public class TransactionPartitioner implements
PartitionGenerator<Collection<JavaChangeOperation>, Collection<Collection<JavaChangeOperation>>> {
	
	@Override
	public Collection<Collection<JavaChangeOperation>> partition(Collection<JavaChangeOperation> input) {
		Map<RCSTransaction, Collection<JavaChangeOperation>> map = new HashMap<RCSTransaction, Collection<JavaChangeOperation>>();
		for (JavaChangeOperation operation : input) {
			RCSTransaction transaction = operation.getRevision().getTransaction();
			if (!map.containsKey(transaction)) {
				map.put(transaction, new HashSet<JavaChangeOperation>());
			}
			map.get(transaction).add(operation);
		}
		return map.values();
	}
	
}

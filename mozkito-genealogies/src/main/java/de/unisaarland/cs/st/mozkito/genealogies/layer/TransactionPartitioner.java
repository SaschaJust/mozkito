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

package de.unisaarland.cs.st.mozkito.genealogies.layer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation;
import de.unisaarland.cs.st.mozkito.genealogies.PartitionGenerator;
import de.unisaarland.cs.st.mozkito.versions.model.RCSTransaction;

public class TransactionPartitioner implements
        PartitionGenerator<Collection<JavaChangeOperation>, Collection<PartitionChangeGenealogyNode>> {
	
	@Override
	public Collection<PartitionChangeGenealogyNode> partition(final Collection<JavaChangeOperation> input) {
		final Map<RCSTransaction, Collection<JavaChangeOperation>> map = new HashMap<RCSTransaction, Collection<JavaChangeOperation>>();
		for (final JavaChangeOperation operation : input) {
			final RCSTransaction transaction = operation.getRevision().getTransaction();
			if (!map.containsKey(transaction)) {
				map.put(transaction, new HashSet<JavaChangeOperation>());
			}
			map.get(transaction).add(operation);
		}
		
		final Set<PartitionChangeGenealogyNode> result = new HashSet<>();
		for (final Entry<RCSTransaction, Collection<JavaChangeOperation>> entry : map.entrySet()) {
			result.add(new TransactionChangeGenealogyNode(entry.getKey(), entry.getValue()));
		}
		
		return result;
	}
	
}

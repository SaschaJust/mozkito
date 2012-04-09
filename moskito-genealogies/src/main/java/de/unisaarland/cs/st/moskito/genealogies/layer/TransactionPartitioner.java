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

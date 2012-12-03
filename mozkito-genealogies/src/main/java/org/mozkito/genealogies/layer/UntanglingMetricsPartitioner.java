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

package org.mozkito.genealogies.layer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.genealogies.PartitionGenerator;
import org.mozkito.genealogies.core.CoreChangeGenealogy;
import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.model.RCSTransaction;

import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;

/**
 * The Class UntanglingMetricsPartitioner.
 */
public class UntanglingMetricsPartitioner implements
        PartitionGenerator<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> {
	
	/** The partitions. */
	Map<Long, Collection<JavaChangeOperation>>                                 partitions     = new HashMap<>();
	
	/** The partition names. */
	Map<Collection<JavaChangeOperation>, String>                               partitionNames = new HashMap<>();
	
	/** The partition cache. */
	Map<Collection<JavaChangeOperation>, Collection<ChangeGenealogyLayerNode>> partitionCache = new ConcurrentHashMap<>();
	
	/**
	 * Instantiates a new untangling metrics partitioner.
	 *
	 * @param partitionFile the partition file
	 * @param coreGenealogy the core genealogy
	 */
	public UntanglingMetricsPartitioner(final File partitionFile, final CoreChangeGenealogy coreGenealogy) {
		
		final PersistenceUtil persistenceUtil = coreGenealogy.getPersistenceUtil();
		
		try (final BufferedReader reader = new BufferedReader(new FileReader(partitionFile))) {
			final String header = reader.readLine();
			if (header == null) {
				throw new UnrecoverableError("Partition file must not be empty.");
			}
			String[] lineParts = header.split(",");
			int idIndex = -1;
			int partNumIndex = -1;
			int opIdIndex = -1;
			for (int i = 0; i < lineParts.length; ++i) {
				switch (lineParts[i]) {
					case "ChangeSetID":
						idIndex = i;
						break;
					case "PartitionNumber":
						partNumIndex = i;
						break;
					case "ChangeOperationIDs":
						opIdIndex = i;
						break;
					default:
						break;
				}
			}
			if ((idIndex == -1) || (partNumIndex == -1) || (opIdIndex == -1)) {
				throw new UnrecoverableError(
				                             "The header row of the partitioning file does not contain necessary column names.");
			}
			String line = null;
			while ((line = reader.readLine()) != null) {
				lineParts = line.split(",");
				final String changeSetId = lineParts[idIndex];
				final int partNumber = Integer.valueOf(lineParts[partNumIndex]).intValue();
				final String[] changeOperationIDs = lineParts[opIdIndex].split(":");
				final List<Long> idList = new ArrayList<>(changeOperationIDs.length);
				for (final String opId : changeOperationIDs) {
					idList.add(Long.valueOf(opId));
				}
				
				final Criteria<JavaChangeOperation> criteria = persistenceUtil.createCriteria(JavaChangeOperation.class)
				                                                              .in("id", idList);
				final List<JavaChangeOperation> operations = persistenceUtil.load(criteria);
				if (!operations.isEmpty()) {
					final HashSet<JavaChangeOperation> opSet = new HashSet<JavaChangeOperation>(operations);
					if (coreGenealogy.containsVertex(operations.get(0))) {
						this.partitionNames.put(opSet, changeSetId + ";" + partNumber);
					}
					for (final JavaChangeOperation op : operations) {
						this.partitions.put(op.getId(), opSet);
					}
				}
			}
		} catch (final IOException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Gets the untangling partitions.
	 *
	 * @return the untangling partitions
	 */
	public Collection<ChangeGenealogyLayerNode> getUntanglingPartitions() {
		final Set<ChangeGenealogyLayerNode> result = new HashSet<>();
		for (final Entry<Collection<JavaChangeOperation>, String> entry : this.partitionNames.entrySet()) {
			if (!entry.getKey().isEmpty()) {
				result.add(new PartitionChangeGenealogyNode(entry.getValue(), entry.getKey()));
			}
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see org.mozkito.genealogies.PartitionGenerator#partition(java.lang.Object)
	 */
	@Override
	public Collection<ChangeGenealogyLayerNode> partition(final Collection<JavaChangeOperation> input) {
		
		if (!this.partitionCache.containsKey(input)) {
			final Map<RCSTransaction, Collection<JavaChangeOperation>> map = new HashMap<RCSTransaction, Collection<JavaChangeOperation>>();
			
			for (final JavaChangeOperation operation : input) {
				if (!this.partitions.containsKey(operation.getId())) {
					final RCSTransaction rCSTransaction = operation.getRevision().getTransaction();
					if (!map.containsKey(rCSTransaction)) {
						map.put(rCSTransaction, new HashSet<JavaChangeOperation>());
					}
					map.get(rCSTransaction).add(operation);
				} else {
					this.partitions.get(operation.getId()).add(operation);
				}
			}
			
			final Set<Long> emptyPartitions = new HashSet<>();
			for (final Long id : this.partitions.keySet()) {
				if (this.partitions.get(id).isEmpty()) {
					emptyPartitions.add(id);
				}
			}
			for (final Long id : emptyPartitions) {
				this.partitions.remove(id);
			}
			
			final Set<ChangeGenealogyLayerNode> result = new HashSet<>();
			for (final Entry<RCSTransaction, Collection<JavaChangeOperation>> entry : map.entrySet()) {
				if (!entry.getValue().isEmpty()) {
					result.add(new TransactionChangeGenealogyNode(entry.getKey(), entry.getValue()));
				}
			}
			for (final Entry<Collection<JavaChangeOperation>, String> entry : this.partitionNames.entrySet()) {
				if (!entry.getKey().isEmpty()) {
					result.add(new PartitionChangeGenealogyNode(entry.getValue(), entry.getKey()));
				}
			}
			this.partitionCache.put(input, result);
		}
		return this.partitionCache.get(input);
	}
	
}

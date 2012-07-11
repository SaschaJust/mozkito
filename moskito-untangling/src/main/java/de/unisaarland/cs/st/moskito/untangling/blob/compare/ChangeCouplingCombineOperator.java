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
package de.unisaarland.cs.st.moskito.untangling.blob.compare;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;

import de.unisaarland.cs.st.moskito.changecouplings.ChangeCouplingRuleFactory;
import de.unisaarland.cs.st.moskito.changecouplings.model.FileChangeCoupling;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.ppa.model.JavaChangeOperation;
import de.unisaarland.cs.st.moskito.rcs.model.RCSBranch;
import de.unisaarland.cs.st.moskito.rcs.model.RCSFile;
import de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction;
import de.unisaarland.cs.st.moskito.untangling.blob.ChangeSet;

/**
 * The Class ChangeCouplingCombineOperator.
 * 
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
public class ChangeCouplingCombineOperator implements CombineOperator<ChangeSet> {
	
	private final PersistenceUtil persistenceUtil;
	private final double          minConfidence;
	private final int             minSupport;
	
	public ChangeCouplingCombineOperator(final int minSupport, final double minConfidence,
	        final PersistenceUtil persistenceUtil) {
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.untangling.blob.compare.CombineOperator#canBeCombined(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeSet cl1,
	                             final ChangeSet cl2) {
		// PRECONDITIONS
		
		try {
			final RCSTransaction cl1T = cl1.getTransaction();
			final RCSTransaction cl2T = cl2.getTransaction();
			
			if (Logger.logDebug()) {
				Logger.debug("Trying to combine %s and %s ...", cl1T.getId(), cl2T.getId());
			}
			
			final Set<String> cl1B = cl1T.getBranchNames();
			final Set<String> cl2B = cl2T.getBranchNames();
			
			final Set<Long> cl1Files = new HashSet<>();
			for (final JavaChangeOperation op1 : cl1.getOperations()) {
				cl1Files.add(op1.getRevision().getChangedFile().getGeneratedId());
			}
			
			final Set<Long> cl2Files = new HashSet<>();
			for (final JavaChangeOperation op2 : cl2.getOperations()) {
				cl2Files.add(op2.getRevision().getChangedFile().getGeneratedId());
			}
			
			@SuppressWarnings ("unchecked")
			final Collection<String> branchIntersection = CollectionUtils.intersection(cl1B, cl2B);
			if (branchIntersection.isEmpty()) {
				return false;
			}
			RCSBranch commonBranch = null;
			if (branchIntersection.contains("master")) {
				commonBranch = this.persistenceUtil.loadById("master", RCSBranch.class);
			} else if (branchIntersection.contains("master")) {
				commonBranch = this.persistenceUtil.loadById("trunk", RCSBranch.class);
			} else {
				commonBranch = this.persistenceUtil.loadById(branchIntersection.iterator().next(), RCSBranch.class);
			}
			
			final RCSTransaction head = commonBranch.getHead();
			
			final LinkedList<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(head,
			                                                                                                            this.minSupport,
			                                                                                                            this.minConfidence,
			                                                                                                            this.persistenceUtil);
			for (final FileChangeCoupling coupling : fileChangeCouplings) {
				final Set<RCSFile> premise = coupling.getPremise();
				final Set<Long> premiseIds = new HashSet<>(premise.size());
				for (final RCSFile p : premise) {
					premiseIds.add(p.getGeneratedId());
				}
				final RCSFile implication = coupling.getImplication();
				final Long implicationId = implication.getGeneratedId();
				if ((cl1Files.containsAll(premiseIds)) && (cl2Files.contains(implicationId))) {
					if (Logger.logDebug()) {
						Logger.debug("%s and %s cn be combined using file change coupling %s.", cl1T.getId(),
						             cl2T.getId(), coupling.toString());
					}
					return true;
				}
				if ((cl1Files.containsAll(premiseIds)) && (cl1Files.contains(implicationId))) {
					if (Logger.logDebug()) {
						Logger.debug("%s and %s cn be combined using file change coupling %s.", cl1T.getId(),
						             cl2T.getId(), coupling.toString());
					}
					return true;
				}
			}
			if (Logger.logDebug()) {
				Logger.debug("%s and %s cannot be combined using file change couplings.", cl1T.getId(), cl2T.getId());
			}
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
}

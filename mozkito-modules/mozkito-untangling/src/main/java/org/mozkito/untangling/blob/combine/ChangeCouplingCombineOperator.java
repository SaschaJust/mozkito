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
package org.mozkito.untangling.blob.combine;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import org.mozkito.changecouplings.ChangeCouplingRuleFactory;
import org.mozkito.changecouplings.model.FileChangeCoupling;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.untangling.blob.ChangeOperationSet;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;

/**
 * The Class ChangeCouplingCombineOperator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeCouplingCombineOperator implements CombineOperator<ChangeOperationSet> {
	
	/** The persistence util. */
	private final PersistenceUtil persistenceUtil;
	
	/** The min confidence. */
	private final double          minConfidence;
	
	/** The min support. */
	private final int             minSupport;
	
	/**
	 * Instantiates a new change coupling combine operator.
	 * 
	 * @param minSupport
	 *            the min support
	 * @param minConfidence
	 *            the min confidence
	 * @param persistenceUtil
	 *            the persistence util
	 */
	public ChangeCouplingCombineOperator(final int minSupport, final double minConfidence,
	        final PersistenceUtil persistenceUtil) {
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.untangling.blob.compare.CombineOperator#canBeCombined(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeOperationSet cl1,
	                             final ChangeOperationSet cl2) {
		// PRECONDITIONS
		
		try {
			final ChangeSet cl1T = cl1.getChangeSet();
			final ChangeSet cl2T = cl2.getChangeSet();
			
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
				final JavaChangeOperation tmpFetch = this.persistenceUtil.loadById(op2.getId(),
				                                                                   JavaChangeOperation.class);
				cl2Files.add(tmpFetch.getRevision().getChangedFile().getGeneratedId());
			}
			
			@SuppressWarnings ("unchecked")
			final Collection<String> branchIntersection = CollectionUtils.intersection(cl1B, cl2B);
			if (branchIntersection.isEmpty()) {
				if (Logger.logDebug()) {
					Logger.debug("%s and %s cannot be combined due to empty branch intersection.", cl1T.getId(),
					             cl2T.getId());
				}
				return false;
			}
			final Collection<FileChangeCoupling> fileChangeCouplings = ChangeCouplingRuleFactory.getFileChangeCouplings(cl1T,
			                                                                                                            this.minSupport,
			                                                                                                            this.minConfidence,
			                                                                                                            this.persistenceUtil);
			
			fileChangeCouplings.addAll(ChangeCouplingRuleFactory.getFileChangeCouplings(cl2T, this.minSupport,
			                                                                            this.minConfidence,
			                                                                            this.persistenceUtil));
			
			if (Logger.logDebug()) {
				Logger.debug("Found %s change couplings for transactions %s,%s having a minimal support of %s and a minimal confidence of %s",
				             String.valueOf(fileChangeCouplings.size()), cl1T.getId(), cl2T.getId(),
				             String.valueOf(this.minSupport), String.valueOf(this.minConfidence));
			}
			for (final FileChangeCoupling coupling : fileChangeCouplings) {
				
				final Set<Handle> premise = coupling.getPremise();
				final Set<Long> premiseIds = new HashSet<>(premise.size());
				for (final Handle p : premise) {
					premiseIds.add(p.getGeneratedId());
				}
				final Handle implication = coupling.getImplication();
				final Long implicationId = implication.getGeneratedId();
				
				if (Logger.logTrace()) {
					Logger.trace("Found change coupling {%s} => %s.",
					             StringUtils.join(premiseIds.toArray(new Long[premiseIds.size()])),
					             String.valueOf(implicationId));
				}
				
				if (Logger.logTrace()) {
					Logger.trace("Checking if {%s} contains all premises and if {%s} contains implication.",
					             StringUtils.join(cl1Files.toArray(new Long[cl1Files.size()])),
					             StringUtils.join(cl2Files.toArray(new Long[cl2Files.size()])));
				}
				if ((cl1Files.containsAll(premiseIds)) && (cl2Files.contains(implicationId))) {
					if (Logger.logDebug()) {
						Logger.debug("%s and %s can be combined using file change coupling %s.", cl1T.getId(),
						             cl2T.getId(), coupling.toString());
					}
					return true;
				}
				if (Logger.logTrace()) {
					Logger.trace("Checking if {%s} contains all premises and if {%s} contains implication.",
					             StringUtils.join(cl2Files.toArray(new Long[cl2Files.size()])),
					             StringUtils.join(cl1Files.toArray(new Long[cl1Files.size()])));
				}
				if ((cl2Files.containsAll(premiseIds)) && (cl1Files.contains(implicationId))) {
					if (Logger.logDebug()) {
						Logger.debug("%s and %s can be combined using file change coupling %s.", cl1T.getId(),
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

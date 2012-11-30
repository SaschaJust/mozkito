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
package org.mozkito.untangling.blob.combine;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.DoubleArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.mozkito.changecouplings.ChangeCouplingRuleFactory;
import org.mozkito.changecouplings.model.FileChangeCoupling;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.untangling.blob.ChangeSet;
import org.mozkito.versions.model.File;
import org.mozkito.versions.model.Transaction;


/**
 * The Class ChangeCouplingCombineOperator.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class ChangeCouplingCombineOperator implements CombineOperator<ChangeSet> {
	
	/**
	 * The Class Options.
	 */
	public static class Options extends
	        ArgumentSetOptions<ChangeCouplingCombineOperator, ArgumentSet<ChangeCouplingCombineOperator, Options>> {
		
		private final DatabaseOptions                                 databaseOptions;
		private net.ownhero.dev.hiari.settings.LongArgument.Options   minSupportOptions;
		private net.ownhero.dev.hiari.settings.DoubleArgument.Options minConfidenceOptions;
		
		/**
		 * @param argumentSet
		 * @param name
		 * @param description
		 * @param requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements,
		        final DatabaseOptions databaseOptions) {
			super(argumentSet, "ccCombineOp", "ChangeCouplingCombineOperator options.", requirements);
			this.databaseOptions = databaseOptions;
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public ChangeCouplingCombineOperator init() {
			// PRECONDITIONS
			final PersistenceUtil persistenceUtil = getSettings().getArgumentSet(this.databaseOptions).getValue();
			final Double minConfidence = getSettings().getArgument(this.minConfidenceOptions).getValue();
			final Long minSupport = getSettings().getArgument(this.minSupportOptions).getValue();
			return new ChangeCouplingCombineOperator(minSupport.intValue(), minConfidence.doubleValue(),
			                                         persistenceUtil);
		}
		
		/*
		 * (non-Javadoc)
		 * @see
		 * net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
		 */
		@Override
		public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> argumentSet) throws ArgumentRegistrationException,
		                                                                                    SettingsParseError {
			// PRECONDITIONS
			final Map<String, IOptions<?, ?>> map = new HashMap<>();
			map.put(this.databaseOptions.getName(), this.databaseOptions);
			
			this.minSupportOptions = new LongArgument.Options(
			                                                  argumentSet,
			                                                  "minSupport",
			                                                  "Minimum support for change couplings used to tangle change sets.",
			                                                  3l, Requirement.required);
			map.put(this.minSupportOptions.getName(), this.minSupportOptions);
			this.minConfidenceOptions = new DoubleArgument.Options(
			                                                       argumentSet,
			                                                       "minConfidence",
			                                                       "Minimum confidence for change couplings used to tangle change sets.",
			                                                       0.5, Requirement.required);
			map.put(this.minConfidenceOptions.getName(), this.minConfidenceOptions);
			return map;
		}
	}
	
	private final PersistenceUtil persistenceUtil;
	private final double          minConfidence;
	private final int             minSupport;
	
	protected ChangeCouplingCombineOperator(final int minSupport, final double minConfidence,
	        final PersistenceUtil persistenceUtil) {
		this.minSupport = minSupport;
		this.minConfidence = minConfidence;
		this.persistenceUtil = persistenceUtil;
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.untangling.blob.compare.CombineOperator#canBeCombined(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean canBeCombined(final ChangeSet cl1,
	                             final ChangeSet cl2) {
		// PRECONDITIONS
		
		try {
			final Transaction cl1T = cl1.getTransaction();
			final Transaction cl2T = cl2.getTransaction();
			
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
				
				final Set<File> premise = coupling.getPremise();
				final Set<Long> premiseIds = new HashSet<>(premise.size());
				for (final File p : premise) {
					premiseIds.add(p.getGeneratedId());
				}
				final File implication = coupling.getImplication();
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

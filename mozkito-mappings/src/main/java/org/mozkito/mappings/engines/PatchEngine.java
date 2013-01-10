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
package org.mozkito.mappings.engines;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import difflib.Delta;

import org.mozkito.exceptions.RepositoryOperationException;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableStructuredReport;
import org.mozkito.mappings.mappable.model.MappableTransaction;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.RepositoryStorage;
import org.mozkito.mappings.storages.Storage;
import org.mozkito.versions.Repository;
import org.mozkito.versions.model.RCSFile;
import org.mozkito.versions.model.RCSTransaction;

/**
 * The Class PatchEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PatchEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends ArgumentSetOptions<PatchEngine, ArgumentSet<PatchEngine, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, PatchEngine.TAG, PatchEngine.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public PatchEngine init() {
			// PRECONDITIONS
			
			try {
				return new PatchEngine();
			} finally {
				// POSTCONDITIONS
			}
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
			
			try {
				return new HashMap<String, IOptions<?, ?>>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = Messages.getString("PatchEngine.description"); //$NON-NLS-1$
	                                                                                         
	/** The Constant TAG. */
	private static final String TAG         = "patch";                                      //$NON-NLS-1$
	                                                                                         
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return PatchEngine.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#score(org.mozkito.mappings.mappable.model.MappableEntity,
	 * org.mozkito.mappings.mappable.model.MappableEntity, org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Relation score) {
		// PRECONDITIONS
		
		try {
			final MappableStructuredReport mappableStructuredReport = (MappableStructuredReport) from;
			final EnhancedReport report = mappableStructuredReport.getReport();
			
			final MappableTransaction mappableTransaction = (MappableTransaction) to;
			final RCSTransaction transaction = mappableTransaction.getTransaction();
			
			final RepositoryStorage storage = getStorage(RepositoryStorage.class);
			final Repository repository = storage.getRepository();
			
			final Collection<Patch> patches = report.getPatches();
			
			final List<String> patchedFilesInReport = new java.util.LinkedList<>();
			for (final Patch patch : patches) {
				String originalFile = patch.getOriginalFile();
				
				// TODO determine filepath as greatest overlap of OriginalFile() and ModifiedFile()
				
				final String[] split = originalFile.split("/|\\\\"); //$NON-NLS-1$
				if (split.length > 0) {
					originalFile = split[split.length - 1];
					patchedFilesInReport.add(originalFile);
				}
			}
			
			final Collection<RCSFile> changedFiles = transaction.getChangedFiles();
			final RCSTransaction previousTransaction = transaction.getBranchParent();
			
			double localConfidence = 0.0d;
			
			for (final RCSFile changedFile : changedFiles) {
				final String path = changedFile.getPath(previousTransaction);
				final String[] split = path.split("/"); //$NON-NLS-1$
				if (split.length > 0) {
					final String fileName = split[split.length - 1];
					// TODO determine if filename/path correlates with one in the patchesFiles
					fileName.equals(fileName);
					
					// if so, TODO get the corresponding patch
					final Patch patch = new Patch();
					Collection<Delta> diff;
					try {
						diff = repository.diff(path, previousTransaction.getId(), transaction.getId());
					} catch (final RepositoryOperationException e) {
						throw new UnrecoverableError(e);
					}
					
					// TODO calculate the similarity measure between Patch and Collection<Delta>
					localConfidence = Math.max(localConfidence, similarity(patch, diff));
				}
			}
			
			addFeature(score, localConfidence, "PATCH", JavaUtils.collectionToString(patches), //$NON-NLS-1$
			           "", "DIFF", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * @param patch
	 * @param diff
	 * @return
	 */
	private double similarity(final Patch patch,
	                          final Collection<Delta> diff) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			// return 0;
			throw new RuntimeException("Method 'similarity' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#storageDependency()
	 */
	@Override
	public Set<Class<? extends Storage>> storageDependency() {
		// PRECONDITIONS
		
		try {
			return new HashSet<Class<? extends Storage>>() {
				
				/**
                 * 
                 */
				private static final long serialVersionUID = 1L;
				
				{
					add(RepositoryStorage.class);
				}
			};
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.MappingEngine#supported()
	 */
	@Override
	public Expression supported() {
		// PRECONDITIONS
		
		try {
			return new And(new Atom(Index.FROM, EnhancedReport.class), new Atom(Index.TO, RCSTransaction.class));
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

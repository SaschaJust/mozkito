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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import difflib.Delta;

import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.patch.Patch;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.InfozillaStorage;
import org.mozkito.mappings.storages.RepositoryStorage;
import org.mozkito.mappings.storages.Storage;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;

/**
 * The Class PatchEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class PatchEngine extends Engine {
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("PatchEngine.description"); //$NON-NLS-1$
	                                                                                        
	/** The Constant TAG. */
	public static final String TAG         = "patch";
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	/**
	 * {@inheritDoc}
	 * 
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.model.Relation)
	 */
	@Override
	public void score(final @NotNull Relation relation) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Report report = (Report) relation.getFrom();
			final ChangeSet changeSet = (ChangeSet) relation.getTo();
			
			SANITY: {
				assert report != null;
				assert changeSet != null;
			}
			
			new LinkedList<>();
			
			final InfozillaStorage infozillaStorage = getStorage(InfozillaStorage.class);
			final RepositoryStorage repositoryStorage = getStorage(RepositoryStorage.class);
			
			SANITY: {
				assert infozillaStorage != null;
				assert repositoryStorage != null;
			}
			
			final EnhancedReport enhancedReport = infozillaStorage.getEnhancedReport(report);
			
			if (enhancedReport == null) {
				// we can't do anything here. no inline code or attachments found
				return;
			}
			
			final Repository repository = repositoryStorage.getRepository();
			
			SANITY: {
				assert repository != null;
			}
			
			final Collection<Handle> changedFiles = changeSet.getChangedFiles();
			
			final Collection<Patch> patches = enhancedReport.getPatches();
			
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
			
			final ChangeSet previousTransaction = changeSet.getBranchParent();
			
			double localConfidence = 0.0d;
			
			for (final Handle changedFile : changedFiles) {
				try {
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
							diff = repository.diff(path, previousTransaction.getId(), changeSet.getId());
						} catch (final RepositoryOperationException e) {
							throw new UnrecoverableError(e);
						}
						
						// TODO calculate the similarity measure between Patch and Collection<Delta>
						localConfidence = Math.max(localConfidence, similarity(patch, diff));
					}
				} catch (final NoSuchHandleException e1) {
					// TODO @just please consider the case that handle.getPath does not find the file
				}
			}
			
			addFeature(relation, localConfidence, "PATCH", JavaUtils.collectionToString(patches), //$NON-NLS-1$
			           "", "DIFF", "", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
		} finally {
			POSTCONDITIONS: {
				assert CollectionUtils.exists(relation.getFeatures(), new Predicate() {
					
					/**
					 * {@inheritDoc}
					 * 
					 * @see org.apache.commons.collections.Predicate#evaluate(java.lang.Object)
					 */
					@Override
					public boolean evaluate(final Object object) {
						return ((Feature) object).getEngine().equals(PatchEngine.class);
					}
				});
			}
		}
	}
	
	/**
	 * Similarity.
	 * 
	 * @param patch
	 *            the patch
	 * @param diff
	 *            the diff
	 * @return the double
	 */
	private double similarity(final Patch patch,
	                          final Collection<Delta> diff) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// get assignments in patch
			// get assignments in diff
			// extract variable names from lhs of both
			// return #intersection/#union or #intersection/#variables in patch
			// return 0;
			throw new RuntimeException("Method 'similarity' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#storageDependency()
	 */
	/**
	 * {@inheritDoc}
	 * 
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
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.engines.Engine#supported()
	 */
	@Override
	public Expression supported() {
		// PRECONDITIONS
		
		try {
			return new And(new Atom(Index.FROM, EnhancedReport.class), new Atom(Index.TO, ChangeSet.class));
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

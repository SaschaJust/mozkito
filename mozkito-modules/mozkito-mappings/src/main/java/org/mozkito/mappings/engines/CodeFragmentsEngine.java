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

import java.util.Arrays;
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
import org.mozkito.utilities.io.FileUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;

/**
 * The Class CodeFragmentsEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CodeFragmentsEngine extends Engine {
	
	/** The Constant TAG. */
	public static final String TAG         = "codefragments";                                      //$NON-NLS-1$
	                                                                                                
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("CodeFragmentsEngine.description"); //$NON-NLS-1$
	                                                                                                
	/**
	 * Instantiates a new code fragments engine.
	 */
	public CodeFragmentsEngine() {
		// should only be used by settings or for testing purposes
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return CodeFragmentsEngine.DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Partially contains.
	 * 
	 * @param originalLines
	 *            the original lines
	 * @param revisedLines
	 *            the revised lines
	 * @param codeFragmentList
	 *            the code fragment list
	 * @return the int
	 */
	private int partiallyContains(final List<String> originalLines,
	                              final List<String> revisedLines,
	                              final List<String> codeFragmentList) {
		return 0;
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
			
			final List<String> patchOriginalLines = new LinkedList<>();
			final List<String> patchRevisedLines = new LinkedList<>();
			
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
			
			SANITY: {
				assert changedFiles != null;
			}
			
			for (final Handle handle : changedFiles) {
				try {
					final String path = handle.getPath(changeSet);
					
					SANITY: {
						assert path != null;
					}
					
					Collection<Delta> diff;
					try {
						diff = repository.diff(path, changeSet.getBranchParent().getId(), changeSet.getId());
					} catch (final RepositoryOperationException e) {
						throw new UnrecoverableError(e);
					}
					
					for (final Delta delta : diff) {
						@SuppressWarnings ("unchecked")
						final List<String> lines = (List<String>) delta.getOriginal().getLines();
						patchOriginalLines.addAll(lines);
						@SuppressWarnings ("unchecked")
						final List<String> lines2 = (List<String>) delta.getRevised().getLines();
						patchRevisedLines.addAll(lines2);
					}
				} catch (final NoSuchHandleException e1) {
					// TODO @just please consider the case that handle.getPath does not find the file
				}
			}
			
			final Collection<String> codeFragments = enhancedReport.getCodeFragments();
			
			SANITY: {
				assert codeFragments != null;
			}
			
			final List<String> codeFragmentList = new LinkedList<>();
			
			for (final String codeBlock : codeFragments) {
				final String[] split = codeBlock.split(FileUtils.lineSeparator);
				
				if (split != null) {
					codeFragmentList.addAll(Arrays.asList(split));
				}
			}
			
			// determine maximum confidence
			final double localConfidence = Math.max(0.0d,
			                                        partiallyContains(patchOriginalLines, patchRevisedLines,
			                                                          codeFragmentList));
			
			// add result
			addFeature(relation,
			           localConfidence,
			           "CODEFRAGMENTS", "?", JavaUtils.collectionToString(codeFragments), "PATCH", "?", JavaUtils.collectionToString(patchOriginalLines)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
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
						return ((Feature) object).getEngine().equals(CodeFragmentsEngine.class);
					}
				});
			}
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
					add(InfozillaStorage.class);
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
			return new And(new Atom(Index.ONE, EnhancedReport.class), new Atom(Index.OTHER, ChangeSet.class));
		} finally {
			// POSTCONDITIONS
		}
	}
}

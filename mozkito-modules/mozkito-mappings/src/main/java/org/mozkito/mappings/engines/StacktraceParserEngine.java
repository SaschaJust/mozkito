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

import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.infozilla.model.stacktrace.StacktraceEntry;
import org.mozkito.issues.model.Report;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.InfozillaStorage;
import org.mozkito.mappings.storages.PersistenceStorage;
import org.mozkito.mappings.storages.RepositoryStorage;
import org.mozkito.mappings.storages.Storage;
import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.utilities.commons.JavaUtils;
import org.mozkito.versions.Repository;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class StacktraceParserEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class StacktraceParserEngine extends Engine {
	
	/** The Constant TAG. */
	public static final String TAG         = "stacktraceParser";                                      //$NON-NLS-1$
	                                                                                                   
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("StacktraceParserEngine.description"); //$NON-NLS-1$
	                                                                                                   
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return DESCRIPTION;
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
			final PersistenceStorage persistenceStorage = getStorage(PersistenceStorage.class);
			
			SANITY: {
				assert infozillaStorage != null;
				assert repositoryStorage != null;
				assert persistenceStorage != null;
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
			
			changeSet.getChangedFiles();
			final PersistenceUtil persistenceUtil = persistenceStorage.getUtil();
			final Set<String> subjects = new HashSet<>();
			final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(persistenceUtil,
			                                                                                               changeSet);
			
			for (final JavaChangeOperation operation : changeOperations) {
				if (operation.getChangeType().equals(ChangeType.Modified)) {
					
					final JavaElement javaElement = operation.getChangedElementLocation().getElement();
					if (javaElement instanceof JavaMethodDefinition) {
						final String fullQualifiedName = javaElement.getShortName();
						subjects.add(fullQualifiedName);
					}
				}
			}
			
			final Collection<Stacktrace> stacktraces = enhancedReport.getStacktraces();
			double localConfidence = 0.0d;
			
			STACKTRACES: for (final Stacktrace trace : stacktraces) {
				final List<StacktraceEntry> entries = new LinkedList<>();
				entries.addAll(trace.getTrace());
				Stacktrace cause = trace;
				while ((cause = trace.getCause()) != null) {
					entries.addAll(cause.getTrace());
				}
				// TODO set max depth
				// TODO add package whitelist
				// TODO add package blacklist
				assert entries != null;
				assert entries.iterator().hasNext();
				final StacktraceEntry entry = entries.iterator().next();
				final String methodName = entry.getMethodName();
				if (subjects.contains(methodName)) {
					localConfidence = 1;
					break STACKTRACES;
				}
			}
			
			addFeature(relation, localConfidence, "STACKTRACES", "", "", "JAVA_CHANGE_OPERATION", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			           JavaUtils.collectionToString(subjects));
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
						return ((Feature) object).getEngine().equals(StacktraceParserEngine.class);
					}
				});
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#storageDependency()
	 */
	@Override
	public Set<Class<? extends Storage>> storageDependency() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return new HashSet<Class<? extends Storage>>() {
				
				/**
                 * 
                 */
				private static final long serialVersionUID = 1L;
				
				{
					add(PersistenceStorage.class);
				}
			};
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/*
	 * (non-Javadoc)
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

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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.mallet.util.Strings;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.StringLiteral;

import org.mozkito.codeanalysis.utils.PPAUtils;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.log.Log;
import org.mozkito.infozilla.model.log.LogEntry;
import org.mozkito.mappings.mappable.model.MappableChangeSet;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableStructuredReport;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Feature;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.mappings.storages.RepositoryStorage;
import org.mozkito.mappings.storages.Storage;
import org.mozkito.versions.Repository;
import org.mozkito.versions.exceptions.NoSuchHandleException;
import org.mozkito.versions.exceptions.RepositoryOperationException;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.Handle;

/**
 * The Class LogEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class LogEngine extends Engine {
	
	/**
	 * The Class ConstantStringVisitor.
	 */
	public class ConstantStringVisitor extends ASTVisitor {
		
		/** The strings. */
		private final List<String> strings = new java.util.LinkedList<>();
		
		/**
		 * Gets the strings.
		 * 
		 * @return the strings
		 */
		public List<String> getStrings() {
			// PRECONDITIONS
			
			try {
				return this.strings;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.strings, "Field '%s' in '%s'.", "strings", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.StringLiteral)
		 */
		@Override
		public boolean visit(final StringLiteral node) {
			
			// PRECONDITIONS
			
			try {
				this.strings.add(node.getLiteralValue());
				return super.visit(node);
			} finally {
				// POSTCONDITIONS
			}
		}
	}
	
	/** The Constant DESCRIPTION. */
	public static final String DESCRIPTION = Messages.getString("LogEngine.description"); //$NON-NLS-1$
	                                                                                      
	/** The Constant TAG. */
	public static final String TAG         = "log";                                      //$NON-NLS-1$
	                                                                                      
	/**
	 * Harmonic mean.
	 * 
	 * @param values
	 *            the values
	 * @return the double
	 */
	private static double harmonicMean(final Double[] values) {
		double sum = 0.0;
		
		for (final Double value : values) {
			sum += 1.0 / value;
		}
		
		return values.length / sum;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return LogEngine.DESCRIPTION;
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
			final MappableEntity from = relation.getFrom();
			final MappableEntity to = relation.getTo();
			
			SANITY: {
				assert from != null;
				assert to != null;
			}
			
			final MappableStructuredReport mappableStructuredReport = (MappableStructuredReport) from;
			final EnhancedReport report = mappableStructuredReport.getReport();
			
			final MappableChangeSet mappableChangeSet = (MappableChangeSet) to;
			final ChangeSet transaction = mappableChangeSet.getChangeSet();
			final Collection<Handle> changedFiles = transaction.getChangedFiles();
			final RepositoryStorage storage = getStorage(RepositoryStorage.class);
			final Repository repository = storage.getRepository();
			File fil2e;
			
			try {
				fil2e = repository.checkoutPath("/", transaction.getId()); //$NON-NLS-1$
			} catch (final RepositoryOperationException e) {
				throw new UnrecoverableError(e);
			}
			
			final ConstantStringVisitor visitor = new ConstantStringVisitor();
			final Map<Handle, List<String>> map = new HashMap<>();
			
			for (final Handle file : changedFiles) {
				try {
					final File file3 = new File(fil2e.getAbsolutePath() + FileUtils.fileSeparator
					        + file.getPath(transaction));
					final CompilationUnit cu = PPAUtils.getCUNoPPA(file3);
					cu.accept(visitor);
					if (!visitor.getStrings().isEmpty()) {
						map.put(file, visitor.getStrings());
					}
				} catch (final NoSuchHandleException e1) {
					// TODO @just please consider the case that handle.getPath does not find the file
				}
			}
			
			final Collection<Log> logs = report.getLogs();
			final ArrayList<Double> minDistances = new ArrayList<>(logs.size());
			
			for (final Log log : logs) {
				for (final LogEntry entry : log.getEntities()) {
					final String logMessage = entry.getLine();
					double minValue = logMessage.length();
					for (final List<String> stringList : map.values()) {
						for (final String constantString : stringList) {
							minValue = Math.min(Strings.levenshteinDistance(logMessage, constantString), minValue);
						}
					}
					minDistances.add(minValue);
					
				}
			}
			
			final double localConfidence = harmonicMean(minDistances.toArray(new Double[0]));
			
			addFeature(relation, localConfidence, "LOG", JavaUtils.collectionToString(logs), //$NON-NLS-1$
			           "", "CONSTANT STRINGS", JavaUtils.collectionToString(visitor.getStrings()), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
						return ((Feature) object).getEngine().equals(LogEngine.class);
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
			return new And(new Atom(Index.FROM, EnhancedReport.class), new Atom(Index.TO, ChangeSet.class));
		} finally {
			// POSTCONDITIONS
		}
	}
}

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
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;

import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.codeanalysis.model.JavaElement;
import org.mozkito.codeanalysis.model.JavaMethodDefinition;
import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.infozilla.model.stacktrace.Stacktrace;
import org.mozkito.infozilla.model.stacktrace.StacktraceEntry;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.mappings.mappable.model.MappableStructuredReport;
import org.mozkito.mappings.mappable.model.MappableTransaction;
import org.mozkito.mappings.messages.Messages;
import org.mozkito.mappings.model.Relation;
import org.mozkito.mappings.requirements.And;
import org.mozkito.mappings.requirements.Atom;
import org.mozkito.mappings.requirements.Expression;
import org.mozkito.mappings.requirements.Index;
import org.mozkito.persistence.PPAPersistenceUtil;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class StacktraceParserEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class StacktraceParserEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<StacktraceParserEngine, ArgumentSet<StacktraceParserEngine, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, TAG, DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public StacktraceParserEngine init() {
			// PRECONDITIONS
			
			try {
				return new StacktraceParserEngine();
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
				return new HashMap<>();
			} finally {
				// POSTCONDITIONS
			}
		}
		
	}
	
	/** The Constant TAG. */
	private static final String TAG         = "stacktraceParser";                                      //$NON-NLS-1$
	                                                                                                    
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = Messages.getString("StacktraceParserEngine.description"); //$NON-NLS-1$
	                                                                                                    
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.mappings.engines.Engine#score(org.mozkito.mappings.mappable.model.MappableEntity,
	 * org.mozkito.mappings.mappable.model.MappableEntity, org.mozkito.mappings.model.Relation)
	 */
	@Override
	@NoneNull
	public void score(final MappableEntity from,
	                  final MappableEntity to,
	                  final Relation score) {
		// PRECONDITIONS
		
		try {
			final MappableStructuredReport mappableStructuredReport = (MappableStructuredReport) from;
			final EnhancedReport report = mappableStructuredReport.getReport();
			
			final MappableTransaction mappableTransaction = (MappableTransaction) to;
			final ChangeSet transaction = mappableTransaction.getTransaction();
			final PersistenceUtil persistenceUtil = getPersistenceUtil();
			final Set<String> subjects = new HashSet<>();
			final Collection<JavaChangeOperation> changeOperations = PPAPersistenceUtil.getChangeOperation(persistenceUtil,
			                                                                                               transaction);
			
			for (final JavaChangeOperation operation : changeOperations) {
				if (operation.getChangeType().equals(ChangeType.Modified)) {
					
					final JavaElement javaElement = operation.getChangedElementLocation().getElement();
					if (javaElement instanceof JavaMethodDefinition) {
						final String fullQualifiedName = javaElement.getShortName();
						subjects.add(fullQualifiedName);
					}
				}
			}
			
			final Collection<Stacktrace> stacktraces = report.getStacktraces();
			double localConfidence = 0.0d;
			
			STACKTRACES: for (final Stacktrace trace : stacktraces) {
				final List<? extends StacktraceEntry> entries = trace.getEntries();
				assert entries != null;
				assert entries.iterator().hasNext();
				final StacktraceEntry entry = entries.iterator().next();
				final String methodName = entry.getMethodName();
				if (subjects.contains(methodName)) {
					localConfidence = 1;
					break STACKTRACES;
				}
			}
			
			addFeature(score, localConfidence, "STACKTRACES", "", "", "JAVA_CHANGE_OPERATION", "", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			           JavaUtils.collectionToString(subjects));
		} finally {
			// POSTCONDITIONS
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

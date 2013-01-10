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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.JavaUtils;
import difflib.Delta;

import org.mozkito.exceptions.RepositoryOperationException;
import org.mozkito.infozilla.model.EnhancedReport;
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
 * The Class CodeFragmentsEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class CodeFragmentsEngine extends Engine {
	
	/**
	 * The Class Options.
	 */
	public static final class Options extends
	        ArgumentSetOptions<CodeFragmentsEngine, ArgumentSet<CodeFragmentsEngine, Options>> {
		
		/**
		 * Instantiates a new options.
		 * 
		 * @param argumentSet
		 *            the argument set
		 * @param requirements
		 *            the requirements
		 */
		public Options(final ArgumentSet<?, ?> argumentSet, final Requirement requirements) {
			super(argumentSet, CodeFragmentsEngine.TAG, CodeFragmentsEngine.DESCRIPTION, requirements);
		}
		
		/*
		 * (non-Javadoc)
		 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init()
		 */
		@Override
		public CodeFragmentsEngine init() {
			// PRECONDITIONS
			
			try {
				return new CodeFragmentsEngine();
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
	
	/** The Constant TAG. */
	private static final String TAG         = "codefragments";                                      //$NON-NLS-1$
	                                                                                                 
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = Messages.getString("CodeFragmentsEngine.description"); //$NON-NLS-1$
	                                                                                                 
	/**
	 * Instantiates a new code fragments engine.
	 */
	CodeFragmentsEngine() {
		// should only be used by settings or for testing purposes
	}
	
	/*
	 * (non-Javadoc)
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
	 * @param container
	 *            the container
	 * @param sub
	 *            the sub
	 * @return the int
	 */
	private int partiallyContains(final List<String> container,
	                              final List<String> sub) {
		return 0;
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
			final List<String> patchOriginalLines = new LinkedList<>();
			
			final MappableStructuredReport report = (MappableStructuredReport) from;
			final EnhancedReport enhancedReport = report.getReport();
			final MappableTransaction transaction = (MappableTransaction) to;
			
			final RepositoryStorage repositoryStorage = getStorage(RepositoryStorage.class);
			final Repository repository = repositoryStorage.getRepository();
			final Collection<RCSFile> changedFiles = transaction.getTransaction().getChangedFiles();
			
			for (final RCSFile rcsFile : changedFiles) {
				final String path = rcsFile.getPath(transaction.getTransaction());
				Collection<Delta> diff;
				try {
					diff = repository.diff(path, transaction.getTransaction().getBranchParent().getId(),
					                       transaction.getId());
				} catch (final RepositoryOperationException e) {
					throw new UnrecoverableError(e);
				}
				for (final Delta delta : diff) {
					@SuppressWarnings ("unchecked")
					final List<String> lines = (List<String>) delta.getOriginal().getLines();
					patchOriginalLines.addAll(lines);
				}
			}
			
			final Collection<String> codeFragments = enhancedReport.getCodeFragments();
			
			final List<String> codeFragmentList = new LinkedList<>();
			for (final String codeBlock : codeFragments) {
				final String[] split = codeBlock.split(FileUtils.lineSeparator);
				if (split != null) {
					codeFragmentList.addAll(Arrays.asList(split));
				}
				
			}
			
			final double localConfidence = Math.max(0.0d, partiallyContains(patchOriginalLines, codeFragmentList));
			addFeature(score,
			           localConfidence,
			           "CODEFRAGMENTS", "?", JavaUtils.collectionToString(codeFragments), "PATCH", "?", JavaUtils.collectionToString(patchOriginalLines)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
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
			return new And(new Atom(Index.ONE, EnhancedReport.class), new Atom(Index.OTHER, RCSTransaction.class));
		} finally {
			// POSTCONDITIONS
		}
	}
}

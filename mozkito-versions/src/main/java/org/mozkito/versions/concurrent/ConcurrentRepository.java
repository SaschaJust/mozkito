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

package org.mozkito.versions.concurrent;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.ownhero.dev.hiari.settings.exceptions.InstantiationError;
import net.ownhero.dev.hiari.settings.exceptions.UnrecoverableError;
import net.ownhero.dev.ioda.FileUtils;
import net.ownhero.dev.ioda.FileUtils.FileShutdownAction;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;
import difflib.Delta;

import org.mozkito.exceptions.RepositoryOperationException;
import org.mozkito.versions.BranchFactory;
import org.mozkito.versions.Repository;
import org.mozkito.versions.RevDependencyGraph;
import org.mozkito.versions.elements.AnnotationEntry;
import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.elements.LogEntry;

/**
 * The Class ConcurrentRepository.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ConcurrentRepository extends Repository {
	
	/** The repository. */
	private Repository                            repository;
	
	/** The thread to revision map. */
	private final ConcurrentMap<Long, Repository> threadToRevisionMap   = new ConcurrentHashMap<>();
	
	/** The cleanup threshold. */
	private final int                             cleanupThreshold;
	
	/** The cleanup count. */
	private int                                   cleanupCount          = 0;
	
	/** The Constant DEFAULT_CLEANUP_COUNT. */
	private static final int                      DEFAULT_CLEANUP_COUNT = 5;
	
	/**
	 * Instantiates a new concurrent repository.
	 * 
	 * @param repository
	 *            the repository
	 */
	public ConcurrentRepository(@NotNull final Repository repository) {
		// PRECONDITIONS
		
		try {
			this.repository = repository;
			this.cleanupThreshold = DEFAULT_CLEANUP_COUNT;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.repository, "Field '%s' in '%s'.", "repository", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * Instantiates a new concurrent repository.
	 * 
	 * @param repository
	 *            the repository
	 * @param cleanupThreshold
	 *            the cleanup threshold
	 */
	public ConcurrentRepository(@NotNull final Repository repository, final int cleanupThreshold) {
		// PRECONDITIONS
		
		try {
			this.repository = repository;
			this.cleanupThreshold = cleanupThreshold;
		} finally {
			// POSTCONDITIONS
			Condition.notNull(this.repository, "Field '%s' in '%s'.", "repository", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	@Override
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().annotate(filePath, revision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#checkoutPath(java.lang.String, java.lang.String)
	 */
	@Override
	public File checkoutPath(final String relativeRepoPath,
	                         final String revision) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().checkoutPath(relativeRepoPath, revision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Cleanup.
	 * 
	 * @throws RepositoryOperationException
	 */
	private synchronized void cleanup() throws RepositoryOperationException {
		++this.cleanupCount;
		if ((this.cleanupCount % this.cleanupThreshold) == 0) {
			final Thread[] threads = new Thread[Thread.activeCount()];
			Thread.enumerate(threads);
			final Set<Long> ids = new TreeSet<>();
			
			for (final Thread thread : threads) {
				ids.add(thread.getId());
			}
			
			final List<Long> list = new LinkedList<>();
			for (final Long id : this.threadToRevisionMap.keySet()) {
				if (!ids.contains(id)) {
					list.add(id);
					final Repository orphanedRepository = this.threadToRevisionMap.get(id);
					final File wokingCopyLocation = orphanedRepository.getWorkingCopyLocation();
					
					if (wokingCopyLocation != null) {
						try {
							FileUtils.deleteDirectory(wokingCopyLocation);
						} catch (final IOException e) {
							if (Logger.logWarn()) {
								Logger.warn(e);
							}
							
						}
					}
					
				}
				
			}
			
			for (final Long id : list) {
				this.threadToRevisionMap.remove(id);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Collection<Delta> diff(final String filePath,
	                              final String baseRevision,
	                              final String revisedRevision) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().diff(filePath, baseRevision, revisedRevision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#gatherToolInformation()
	 */
	@Override
	public String gatherToolInformation() {
		// PRECONDITIONS
		
		try {
			return this.repository.gatherToolInformation();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getChangedPaths(java.lang.String)
	 */
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getChangedPaths(revision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	@Override
	public String getFirstRevisionId() throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return this.repository.getFirstRevisionId();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getFormerPathName(java.lang.String, java.lang.String)
	 */
	@Override
	public String getFormerPathName(final String revision,
	                                final String pathName) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getFormerPathName(revision, pathName);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getHandle()
	 */
	@Override
	public final String getHandle() {
		return JavaUtils.getHandle(ConcurrentRepository.class);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#getHEADRevisionId()
	 */
	@Override
	public String getHEADRevisionId() throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getHEADRevisionId();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	private Repository getRepository() {
		final Thread thread = Thread.currentThread();
		
		try {
			if (!this.threadToRevisionMap.containsKey(thread)) {
				cleanup();
				final File dir = FileUtils.createRandomDir("mozkito_concurrent_" + this.repository.getHandle().toLowerCase() + "_" + thread.getId(), null, FileShutdownAction.DELETE); //$NON-NLS-1$ //$NON-NLS-2$
				try {
					final Repository repoClone = this.repository.getClass().newInstance();
					repoClone.setup(this.repository.getUri(), null, dir, this.repository.getMainBranchName());
					
					this.threadToRevisionMap.put(thread.getId(), repoClone);
				} catch (final InstantiationException e) {
					throw new InstantiationError(e, this.repository.getClass(), null, new Object[0]);
				} catch (final RepositoryOperationException | IllegalAccessException e) {
					throw new UnrecoverableError(e);
				}
			}
			
			final Repository threadRepository = this.threadToRevisionMap.get(thread.getId());
			
			Condition.notNull(threadRepository, "Local variable '%s' in '%s'.", "threadRepository", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
			
			return threadRepository;
		} catch (final RepositoryOperationException e1) {
			throw new UnrecoverableError(e1);
		} catch (final IOException e1) {
			throw new UnrecoverableError(e1);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#getRevDependencyGraph()
	 */
	@Override
	public RevDependencyGraph getRevDependencyGraph() throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getRevDependencyGraph();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#getTransactionCount()
	 */
	@Override
	public long getTransactionCount() throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getTransactionCount();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#getTransactionId(long)
	 */
	@Override
	public String getTransactionId(final long index) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getTransactionId(index);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#getTransactionIndex(java.lang.String)
	 */
	@Override
	public long getTransactionIndex(final String transactionId) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getTransactionIndex(transactionId);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#getWorkingCopyLocation()
	 */
	@Override
	public File getWorkingCopyLocation() throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().getWorkingCopyLocation();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws RepositoryOperationException
	 * 
	 * @see org.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	@Override
	public List<LogEntry> log(final String fromRevision,
	                          final String toRevision) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			return getRepository().log(fromRevision, toRevision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, org.mozkito.versions.BranchFactory, java.io.File,
	 *      java.lang.String)
	 */
	@Override
	public void setup(final URI address,
	                  final BranchFactory branchFactory,
	                  final File tmpDir,
	                  final String mainBranchName) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			throw new UnsupportedOperationException("Setup on concurrent repositories isn't supported.");
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String,
	 *      org.mozkito.versions.BranchFactory, java.io.File, java.lang.String)
	 */
	@Override
	public void setup(final URI address,
	                  final String username,
	                  final String password,
	                  final BranchFactory branchFactory,
	                  final File tmpDir,
	                  final String mainBranchName) throws RepositoryOperationException {
		// PRECONDITIONS
		
		try {
			
			throw new UnsupportedOperationException("Setup on concurrent repositories isn't supported.");
		} finally {
			// POSTCONDITIONS
		}
	}
}

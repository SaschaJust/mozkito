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
import java.net.MalformedURLException;
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

import org.mozkito.exceptions.InvalidProtocolType;
import org.mozkito.exceptions.InvalidRepositoryURI;
import org.mozkito.exceptions.UnsupportedProtocolType;
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#annotate(java.lang.String, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public List<AnnotationEntry> annotate(final String filePath,
	                                      final String revision) {
		// PRECONDITIONS
		
		try {
			return getRepository().annotate(filePath, revision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#checkoutPath(java.lang.String, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public File checkoutPath(final String relativeRepoPath,
	                         final String revision) {
		// PRECONDITIONS
		
		try {
			return getRepository().checkoutPath(relativeRepoPath, revision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Cleanup.
	 */
	private synchronized void cleanup() {
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#diff(java.lang.String, java.lang.String, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public Collection<Delta> diff(final String filePath,
	                              final String baseRevision,
	                              final String revisedRevision) {
		// PRECONDITIONS
		
		try {
			return getRepository().diff(filePath, baseRevision, revisedRevision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#gatherToolInformation()
	 */
	/**
	 * {@inheritDoc}
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
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getChangedPaths(java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public Map<String, ChangeType> getChangedPaths(final String revision) {
		// PRECONDITIONS
		
		try {
			return getRepository().getChangedPaths(revision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFirstRevisionId()
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String getFirstRevisionId() {
		// PRECONDITIONS
		
		try {
			return this.repository.getFirstRevisionId();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getFormerPathName(java.lang.String, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String getFormerPathName(final String revision,
	                                final String pathName) {
		// PRECONDITIONS
		
		try {
			return getRepository().getFormerPathName(revision, pathName);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	@Override
	public final String getHandle() {
		return JavaUtils.getHandle(ConcurrentRepository.class);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getHEADRevisionId()
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String getHEADRevisionId() {
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
		
		if (!this.threadToRevisionMap.containsKey(thread)) {
			cleanup();
			final File dir = FileUtils.createRandomDir("mozkito_concurrent_" + this.repository.getHandle().toLowerCase() + "_" + thread.getId(), null, FileShutdownAction.DELETE); //$NON-NLS-1$ //$NON-NLS-2$
			try {
				final Repository repoClone = this.repository.getClass().newInstance();
				repoClone.setup(this.repository.getUri(), null, dir, this.repository.getMainBranchName());
				
				this.threadToRevisionMap.put(thread.getId(), repoClone);
			} catch (final InstantiationException e) {
				throw new InstantiationError(e, this.repository.getClass(), null, new Object[0]);
			} catch (final MalformedURLException | InvalidProtocolType | InvalidRepositoryURI | UnsupportedProtocolType
			        | IllegalAccessException e) {
				throw new UnrecoverableError(e);
			}
		}
		
		final Repository threadRepository = this.threadToRevisionMap.get(thread.getId());
		
		Condition.notNull(threadRepository, "Local variable '%s' in '%s'.", "threadRepository", getHandle()); //$NON-NLS-1$ //$NON-NLS-2$
		
		return threadRepository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getRevDependencyGraph()
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public RevDependencyGraph getRevDependencyGraph() {
		// PRECONDITIONS
		
		try {
			return getRepository().getRevDependencyGraph();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionCount()
	 */
	/**
	 * Gets the transaction count.
	 * 
	 * @return the transaction count
	 */
	@Override
	public long getTransactionCount() {
		// PRECONDITIONS
		
		try {
			return getRepository().getTransactionCount();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionId(long)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public String getTransactionId(final long index) {
		// PRECONDITIONS
		
		try {
			return getRepository().getTransactionId(index);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getTransactionIndex(java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public long getTransactionIndex(final String transactionId) {
		// PRECONDITIONS
		
		try {
			return getRepository().getTransactionIndex(transactionId);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#getWokingCopyLocation()
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public File getWorkingCopyLocation() {
		// PRECONDITIONS
		
		try {
			return getRepository().getWorkingCopyLocation();
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#log(java.lang.String, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public List<LogEntry> log(final String fromRevision,
	                          final String toRevision) {
		// PRECONDITIONS
		
		try {
			return getRepository().log(fromRevision, toRevision);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, org.mozkito.versions.BranchFactory, java.io.File,
	 * java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public void setup(final URI address,
	                  final BranchFactory branchFactory,
	                  final File tmpDir,
	                  final String mainBranchName) throws MalformedURLException,
	                                              InvalidProtocolType,
	                                              InvalidRepositoryURI,
	                                              UnsupportedProtocolType {
		// PRECONDITIONS
		
		try {
			throw new UnsupportedOperationException("Setup on concurrent repositories isn't supported.");
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.versions.Repository#setup(java.net.URI, java.lang.String, java.lang.String,
	 * org.mozkito.versions.BranchFactory, java.io.File, java.lang.String)
	 */
	/**
	 * {@inheritDoc}
	 */
	
	@Override
	public void setup(final URI address,
	                  final String username,
	                  final String password,
	                  final BranchFactory branchFactory,
	                  final File tmpDir,
	                  final String mainBranchName) throws MalformedURLException,
	                                              InvalidProtocolType,
	                                              InvalidRepositoryURI,
	                                              UnsupportedProtocolType {
		// PRECONDITIONS
		
		try {
			
			throw new UnsupportedOperationException("Setup on concurrent repositories isn't supported.");
		} finally {
			// POSTCONDITIONS
		}
	}
}

/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogIterator;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * The {@link RepositoryReader} reads data from a given {@link Repository} and
 * outputs {@link LogEntry} chunks.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryReader extends AndamaSource<LogEntry> {
	
	private LogIterator      logIterator;
	private final Repository repository;
	
	/**
	 * @see RepoSuiteSourceThread
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryReader(final AndamaGroup threadGroup, final RepositorySettings settings,
	        final Repository repository) {
		super(threadGroup, settings, false);
		this.repository = repository;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterExecution()
	 */
	@Override
	public void afterExecution() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#afterProcess()
	 */
	@Override
	public void afterProcess() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		if (Logger.logInfo()) {
			Logger.info("Starting " + getHandle());
			Logger.info("Requesting logs from " + this.repository);
		}
		
		this.repository.getTransactionCount();
		long cacheSize = (Long) this.getSettings().getSetting("cache.size").getValue();
		this.logIterator = (LogIterator) this.repository.log(this.repository.getFirstRevisionId(),
		                                                     this.repository.getEndRevision(), (int) cacheSize);
		
		if (Logger.logInfo()) {
			Logger.info("Created iterator.");
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeProcess()
	 */
	@Override
	public void beforeProcess() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.OnlyOutputConnectable#process()
	 */
	@Override
	public LogEntry process() throws UnrecoverableError, Shutdown {
		if (Logger.logTrace()) {
			Logger.trace("filling queue [" + outputSize() + "]");
		}
		LogEntry entry = this.logIterator.next();
		if (Logger.logTrace()) {
			Logger.trace("with entry: " + entry);
		}
		
		return entry;
	}
}

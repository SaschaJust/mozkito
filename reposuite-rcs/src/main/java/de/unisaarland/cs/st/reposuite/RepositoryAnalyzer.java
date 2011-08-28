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

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaFilter;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.reposuite.rcs.Repository;
import de.unisaarland.cs.st.reposuite.rcs.elements.LogEntry;
import de.unisaarland.cs.st.reposuite.settings.RepositorySettings;

/**
 * The {@link RepositoryAnalyzer} is a null filter, i.e. it does not modify the
 * data, but analyzes it and prints warnings/errors to the STDOUT.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class RepositoryAnalyzer extends AndamaFilter<LogEntry> {
	
	private final List<LogEntry> entries = new LinkedList<LogEntry>();
	private boolean              analyze;
	private final Repository     repository;
	
	/**
	 * @param threadGroup
	 * @param settings
	 * @param repository
	 */
	public RepositoryAnalyzer(final AndamaGroup threadGroup, final RepositorySettings settings,
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
		if (!isShutdown() && this.analyze) {
			this.repository.consistencyCheck(this.entries, ((Boolean) this.getSettings().getSetting("headless")
			                                                              .getValue() == false));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThreadable#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		this.analyze = (this.getSettings().getSetting("repository.analyze") != null)
		        && (this.getSettings().getSetting("repository.analyze").getValue() != null)
		        && (Boolean) this.getSettings().getSetting("repository.analyze").getValue();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * net.ownhero.dev.andama.threads.InputOutputConnectable#process(java.lang
	 * .Object)
	 */
	@Override
	public LogEntry process(final LogEntry data) throws UnrecoverableError, Shutdown {
		
		if (Logger.logDebug()) {
			Logger.debug("Adding " + data + " to analysis.");
		}
		if (this.analyze) {
			this.entries.add(data);
		}
		
		if (Logger.logTrace()) {
			Logger.trace("filling queue [" + outputSize() + "]");
		}
		return data;
	}
}

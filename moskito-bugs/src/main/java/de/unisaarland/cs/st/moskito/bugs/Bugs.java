/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.bugs;

import net.ownhero.dev.andama.model.AndamaChain;
import net.ownhero.dev.andama.model.AndamaPool;
import net.ownhero.dev.andama.settings.BooleanArgument;
import net.ownhero.dev.andama.settings.LoggerArguments;
import net.ownhero.dev.andama.settings.LongArgument;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerArguments;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Bugs extends AndamaChain {
	
	private final AndamaPool        threadPool;
	private final TrackerArguments  trackerArguments;
	private final DatabaseArguments databaseArguments;
	private final LoggerArguments   logSettings;
	
	/**
	 * 
	 */
	public Bugs() {
		super(new TrackerSettings());
		this.threadPool = new AndamaPool(Bugs.class.getSimpleName(), this);
		TrackerSettings settings = (TrackerSettings) getSettings();
		this.trackerArguments = settings.setTrackerArgs(true);
		this.databaseArguments = settings.setDatabaseArgs(false, this.getClass().getSimpleName().toLowerCase());
		this.logSettings = settings.setLoggerArg(true);
		new BooleanArgument(settings, "headless", "Can be enabled when running without graphical interface", "false",
		                    false);
		new LongArgument(settings, "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000", true);
		
		settings.parseArguments();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setup();
		this.threadPool.execute();
		
		if (Logger.logInfo()) {
			Logger.info("Terminating.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		Tracker tracker = this.trackerArguments.getValue();
		this.logSettings.getValue();
		
		new TrackerReader(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerRAWChecker(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerXMLTransformer(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerXMLChecker(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		new TrackerParser(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker);
		
		PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
		if (persistenceUtil != null) {
			new TrackerPersister(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings(), tracker,
			                     persistenceUtil);
		} else {
			new TrackerVoidSink(this.threadPool.getThreadGroup(), (TrackerSettings) getSettings());
		}
	}
	
	@Override
	public void shutdown() {
		this.threadPool.shutdown();
	}
	
}

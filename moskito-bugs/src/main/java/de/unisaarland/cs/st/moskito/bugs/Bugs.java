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

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.exceptions.SettingsParseError;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.settings.arguments.BooleanArgument;
import net.ownhero.dev.andama.settings.arguments.LoggerArguments;
import net.ownhero.dev.andama.settings.arguments.LongArgument;
import net.ownhero.dev.andama.settings.requirements.Optional;
import net.ownhero.dev.andama.settings.requirements.Required;
import de.unisaarland.cs.st.moskito.bugs.tracker.Tracker;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerArguments;
import de.unisaarland.cs.st.moskito.bugs.tracker.settings.TrackerSettings;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.settings.DatabaseArguments;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Bugs extends Chain<TrackerSettings> {
	
	private final Pool              threadPool;
	private final TrackerArguments  trackerArguments;
	private final DatabaseArguments databaseArguments;
	private final LoggerArguments   logSettings;
	
	/**
	 * @throws SettingsParseError
	 * @throws ArgumentRegistrationException
	 * 
	 */
	public Bugs() throws ArgumentRegistrationException {
		super(new TrackerSettings());
		this.threadPool = new Pool(Bugs.class.getSimpleName(), this);
		final TrackerSettings settings = getSettings();
		this.trackerArguments = settings.setTrackerArgs(new Required());
		this.databaseArguments = settings.setDatabaseArgs(new Optional(), this.getClass().getSimpleName().toLowerCase());
		this.logSettings = settings.setLoggerArg(new Required());
		new BooleanArgument(settings.getRootArgumentSet(), "headless",
		                    "Can be enabled when running without graphical interface", "false", new Optional());
		new LongArgument(settings.getRootArgumentSet(), "cache.size",
		                 "determines the cache size (number of logs) that are prefetched during reading", "3000",
		                 new Required());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		final Tracker tracker = this.trackerArguments.getValue();
		this.logSettings.getValue();
		
		new TrackerReader(this.threadPool.getThreadGroup(), getSettings(), tracker);
		new TrackerRAWChecker(this.threadPool.getThreadGroup(), getSettings(), tracker);
		new TrackerXMLTransformer(this.threadPool.getThreadGroup(), getSettings(), tracker);
		new TrackerXMLChecker(this.threadPool.getThreadGroup(), getSettings(), tracker);
		new TrackerParser(this.threadPool.getThreadGroup(), getSettings(), tracker);
		
		final PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
		if (persistenceUtil != null) {
			new TrackerPersister(this.threadPool.getThreadGroup(), getSettings(), tracker, persistenceUtil);
		} else {
			new TrackerVoidSink(this.threadPool.getThreadGroup(), getSettings());
		}
	}
	
}

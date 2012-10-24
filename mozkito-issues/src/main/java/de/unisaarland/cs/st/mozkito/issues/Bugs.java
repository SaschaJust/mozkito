/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.mozkito.issues;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentFactory;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.BooleanArgument;
import net.ownhero.dev.hiari.settings.LongArgument;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.mozkito.issues.tracker.Tracker;
import de.unisaarland.cs.st.mozkito.issues.tracker.settings.TrackerOptions;
import de.unisaarland.cs.st.mozkito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.mozkito.settings.DatabaseOptions;

/**
 * The Class Bugs.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
public class Bugs extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                                    threadPool;
	
	/** The tracker arguments. */
	private ArgumentSet<Tracker, TrackerOptions>          trackerArguments;
	
	/** The database arguments. */
	private ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	/**
	 * Instantiates a new bugs.
	 * 
	 * @param settings
	 *            the settings
	 */
	public Bugs(final Settings settings) {
		super(settings);
		
		this.threadPool = new Pool(Bugs.class.getSimpleName(), this);
		
		try {
			this.trackerArguments = ArgumentSetFactory.create(new TrackerOptions(settings.getRoot(),
			                                                                     Requirement.required));
			this.databaseArguments = ArgumentSetFactory.create(new DatabaseOptions(settings.getRoot(),
			                                                                       Requirement.required, "bugs"));
			
			ArgumentFactory.create(new BooleanArgument.Options(
			                                                   settings.getRoot(),
			                                                   "headless",
			                                                   "Can be enabled when running without graphical interface",
			                                                   false, Requirement.optional));
			ArgumentFactory.create(new LongArgument.Options(
			                                                settings.getRoot(),
			                                                "cacheSize",
			                                                "determines the cache size (number of logs) that are prefetched during reading",
			                                                3000l, Requirement.required));
			Logger.always(settings.toString());
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown(e);
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown(e);
		} catch (final ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.mozkito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		final Tracker tracker = this.trackerArguments.getValue();
		
		final PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
		
		new TrackerReader(this.threadPool.getThreadGroup(), getSettings(), tracker, persistenceUtil);
		// new TrackerRAWChecker(this.threadPool.getThreadGroup(), getSettings(), tracker);
		// new TrackerXMLTransformer(this.threadPool.getThreadGroup(), getSettings(), tracker);
		// new TrackerXMLChecker(this.threadPool.getThreadGroup(), getSettings(), tracker);
		new TrackerParser(this.threadPool.getThreadGroup(), getSettings(), tracker);
		if (persistenceUtil != null) {
			new TrackerPersister(this.threadPool.getThreadGroup(), getSettings(), tracker, persistenceUtil);
		} else {
			new TrackerVoidSink(this.threadPool.getThreadGroup(), getSettings());
		}
	}
}

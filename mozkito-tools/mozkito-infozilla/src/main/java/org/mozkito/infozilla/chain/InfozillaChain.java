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
package org.mozkito.infozilla.chain;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.messages.ErrorEvent;
import net.ownhero.dev.andama.messages.StartupEvent;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.settings.DatabaseOptions;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class MappingChain.
 */
public class InfozillaChain extends Chain<Settings> {
	
	/** The database arguments. */
	private ArgumentSet<PersistenceUtil, DatabaseOptions> databaseArguments;
	
	/** The database options. */
	private DatabaseOptions                               databaseOptions;
	
	/** The thread pool. */
	private final Pool                                    threadPool;
	
	/**
	 * Instantiates a new mapping chain.
	 * 
	 * @param settings
	 *            the settings
	 */
	public InfozillaChain(final Settings settings) {
		super(settings, "infozilla"); //$NON-NLS-1$
		this.threadPool = new Pool(getName(), this);
		
		try {
			this.databaseOptions = new DatabaseOptions(getSettings().getRoot(), Requirement.required, getName());
			this.databaseArguments = ArgumentSetFactory.create(this.databaseOptions);
		} catch (final ArgumentRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			throw new Shutdown(e.getMessage(), e);
		}
		
		Condition.notNull(this.threadPool, "Field '%s' in '%s'.", "threadPool", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public String getClassName() {
		return JavaUtils.getHandle(this);
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @see net.ownhero.dev.andama.model.Chain#setup()
	 */
	@Override
	public void setup() {
		// PRECONDITIONS
		Condition.notNull(this.databaseArguments, "Field '%s' in '%s'.", "databaseArguments", getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
		Logger.updateClassLevels();
		try {
			
			final PersistenceUtil persistenceUtil = this.databaseArguments.getValue();
			
			if (persistenceUtil == null) {
				getEventBus().fireEvent(new ErrorEvent(("MappingChain.dbInit"))); //$NON-NLS-1$
				if (Logger.logError()) {
					Logger.error(getSettings().getHelpString());
				}
				
				shutdown();
				return;
			}
			
			Condition.notNull(persistenceUtil,
			                  "Local variable '%s' in '%s:'.", "persistenceUtil", getClassName(), "setup()"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			final Group group = this.threadPool.getThreadGroup();
			
			// load sources
			new ReportReader(group, getSettings(), persistenceUtil);
			new StacktraceFilter(group, getSettings(), null);
			new VoidSink(group, getSettings());
			
			// final IRCThread t = new IRCThread("mapping");
			// t.start();
			//
			getEventBus().fireEvent(new StartupEvent("Infozilla started")); //$NON-NLS-1$
		} finally {
			// POSTCONDITIONS
		}
	}
}

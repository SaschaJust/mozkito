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
package org.mozkito.persons;

import java.util.Set;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.model.Chain;
import net.ownhero.dev.andama.model.Pool;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetFactory;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.RepositoryToolchain;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persons.engine.MergingEngine;
import org.mozkito.persons.engine.Messages;
import org.mozkito.persons.processing.MergingProcessor;
import org.mozkito.settings.DatabaseOptions;

/**
 * The Class Persons.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Persons extends Chain<Settings> {
	
	/** The thread pool. */
	private final Pool                                             threadPool;
	
	/** The database arguments. */
	private ArgumentSet<PersistenceUtil, DatabaseOptions>          databaseArguments;
	
	/** The persistence util. */
	private PersistenceUtil                                        persistenceUtil;
	
	/** The engines set. */
	private ArgumentSet<Set<MergingEngine>, MergingEngine.Options> enginesSet;
	
	/**
	 * Instantiates a new persons.
	 * 
	 * @deprecated only to be used in tests
	 * @param util
	 *            the util
	 * @throws SettingsParseError
	 *             the settings parse error
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 * @throws ArgumentSetRegistrationException
	 *             the argument set registration exception
	 */
	@Deprecated
	Persons(final PersistenceUtil util) throws SettingsParseError, ArgumentRegistrationException,
	        ArgumentSetRegistrationException {
		super(new Settings());
		this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
		final Settings settings = getSettings();
		this.persistenceUtil = util;
		this.enginesSet = ArgumentSetFactory.create(new MergingEngine.Options(getSettings().getRoot(),
		                                                                      Requirement.required));
		settings.loadByInheritance(MergingEngine.class.getPackage(), settings.getRoot());
		
	}
	
	/**
	 * Instantiates a new persons.
	 * 
	 * @param settings
	 *            the settings
	 * @throws SettingsParseError
	 *             the settings parse error
	 * @throws ArgumentRegistrationException
	 *             the argument registration exception
	 */
	public Persons(final Settings settings) throws SettingsParseError, ArgumentRegistrationException {
		super(settings);
		
		try {
			this.threadPool = new Pool(RepositoryToolchain.class.getSimpleName(), this);
			
			final DatabaseOptions databaseOptions = new DatabaseOptions(settings.getRoot(), Requirement.required,
			                                                            "persistence"); //$NON-NLS-1$
			this.databaseArguments = ArgumentSetFactory.create(databaseOptions);
			
			this.enginesSet = ArgumentSetFactory.create(new MergingEngine.Options(getSettings().getRoot(),
			                                                                      Requirement.required));
			
			settings.loadByInheritance(MergingEngine.class.getPackage(), settings.getRoot());
		} catch (final ArgumentRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown(e.getMessage(), e);
		} catch (final ArgumentSetRegistrationException e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown(e.getMessage(), e);
		} catch (final SettingsParseError e) {
			if (Logger.logError()) {
				Logger.error(e);
			}
			throw new Shutdown(e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.toolchain.RepoSuiteToolchain#setup()
	 */
	@Override
	public void setup() {
		if ((this.persistenceUtil == null) && (this.databaseArguments != null)) {
			this.persistenceUtil = this.databaseArguments.getValue();
		}
		if (this.persistenceUtil == null) {
			if (Logger.logError()) {
				Logger.error(Messages.getString("Persons.Persons.noDatabaseArgs")); //$NON-NLS-1$
			}
			shutdown();
		}
		
		final MergingProcessor processor = new MergingProcessor();
		for (final MergingEngine engine : this.enginesSet.getValue()) {
			processor.addEngine(engine);
			try {
				engine.provide(getSettings().getRoot());
			} catch (final ArgumentRegistrationException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				
			} catch (final ArgumentSetRegistrationException e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
				
			} catch (final SettingsParseError e) {
				if (Logger.logError()) {
					Logger.error(e);
				}
			}
			engine.init();
		}
		
		processor.providePersistenceUtil(this.persistenceUtil);
		
		new PersonsReader(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil);
		new PersonsMerger(this.threadPool.getThreadGroup(), getSettings(), this.persistenceUtil, processor);
	}
}

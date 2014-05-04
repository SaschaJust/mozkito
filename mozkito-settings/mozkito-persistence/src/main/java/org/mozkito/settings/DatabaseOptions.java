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
package org.mozkito.settings;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.ArgumentSetOptions;
import net.ownhero.dev.hiari.settings.EnumArgument;
import net.ownhero.dev.hiari.settings.IOptions;
import net.ownhero.dev.hiari.settings.ISettings;
import net.ownhero.dev.hiari.settings.StringArgument;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.hiari.settings.requirements.Requirement;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.database.DatabaseManager;
import org.mozkito.database.PersistenceUtil;
import org.mozkito.exceptions.ConfigurationException;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;

/**
 * The Class DatabaseOptions.
 * 
 * @author Kim Herzig <herzig@mozkito.org>
 */
public class DatabaseOptions extends ArgumentSetOptions<PersistenceUtil, ArgumentSet<PersistenceUtil, DatabaseOptions>> {
	
	/** The database unit. */
	private StringArgument.Options               databaseUnit;
	
	/** The database options. */
	private EnumArgument.Options<ConnectOptions> databaseOptions;
	
	/** The database type. */
	private EnumArgument.Options<DatabaseType>   databaseType;
	
	/** The database password. */
	private StringArgument.Options               databasePassword;
	
	/** The database host. */
	private StringArgument.Options               databaseHost;
	
	/** The database user. */
	private StringArgument.Options               databaseUser;
	
	/** The database name. */
	private StringArgument.Options               databaseName;
	
	/** The unit. */
	private final String                         unit;
	
	/** The settings. */
	private final ISettings                      settings;
	
	/**
	 * Instantiates a new database options.
	 * 
	 * @param argumentSet
	 *            the argument set
	 * @param requirement
	 *            the requirement
	 * @param unit
	 *            the unit
	 */
	public DatabaseOptions(final ArgumentSet<?, ?> argumentSet, final Requirement requirement, final String unit) {
		super(argumentSet, "database", "Specifies connection options for the database connection.", requirement);
		this.unit = unit;
		this.settings = argumentSet.getSettings();
	}
	
	/**
	 * Gets the database host.
	 * 
	 * @return the databaseHost
	 */
	public final StringArgument.Options getDatabaseHost() {
		return this.databaseHost;
	}
	
	/**
	 * Gets the database name.
	 * 
	 * @return the databaseName
	 */
	public final StringArgument.Options getDatabaseName() {
		return this.databaseName;
	}
	
	/**
	 * Gets the database options.
	 * 
	 * @return the databaseOptions
	 */
	public final EnumArgument.Options<ConnectOptions> getDatabaseOptions() {
		return this.databaseOptions;
	}
	
	/**
	 * Gets the database password.
	 * 
	 * @return the databasePassword
	 */
	public final StringArgument.Options getDatabasePassword() {
		return this.databasePassword;
	}
	
	/**
	 * Gets the database type.
	 * 
	 * @return the databaseType
	 */
	public final EnumArgument.Options<DatabaseType> getDatabaseType() {
		return this.databaseType;
	}
	
	/**
	 * Gets the database unit.
	 * 
	 * @return the databaseUnit
	 */
	public final StringArgument.Options getDatabaseUnit() {
		return this.databaseUnit;
	}
	
	/**
	 * Gets the database user.
	 * 
	 * @return the databaseUser
	 */
	public final StringArgument.Options getDatabaseUser() {
		return this.databaseUser;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#init(java.util.Map)
	 */
	@Override
	public PersistenceUtil init() {
		// PRECONDITIONS
		
		try {
			final StringArgument hostArgument = getSettings().getArgument(this.databaseHost);
			final StringArgument nameArgument = getSettings().getArgument(this.databaseName);
			final StringArgument userArgument = getSettings().getArgument(this.databaseUser);
			final StringArgument passwordArgument = getSettings().getArgument(this.databasePassword);
			final EnumArgument<DatabaseType> typeArgument = getSettings().getArgument(this.databaseType);
			final StringArgument unitArgument = getSettings().getArgument(this.databaseUnit);
			final EnumArgument<ConnectOptions> optionsArgument = getSettings().getArgument(this.databaseOptions);
			
			org.mozkito.database.DatabaseEnvironment dbOptions;
			try {
				dbOptions = new org.mozkito.database.DatabaseEnvironment(typeArgument.getValue(),
				                                                         nameArgument.getValue(),
				                                                         hostArgument.getValue(),
				                                                         userArgument.getValue(),
				                                                         passwordArgument.getValue(),
				                                                         optionsArgument.getValue(),
				                                                         unitArgument.getValue());
			} catch (final ConfigurationException e1) {
				throw new UnrecoverableError(e1);
			}
			
			if (ConnectOptions.DROP_AND_CREATE_DATABASE.equals(optionsArgument.getValue())) {
				try {
					DatabaseManager.dropDatabase(dbOptions);
				} catch (final SQLException ignore) {
					// ignore
				}
				
				try {
					DatabaseManager.createDatabase(dbOptions);
				} catch (final SQLException e) {
					if (Logger.logError()) {
						Logger.error("Could not re-create database.");
					}
					throw new UnrecoverableError(e.getMessage(), e);
				}
				
			}
			try {
				final PersistenceUtil util = DatabaseManager.createUtil(dbOptions);
				this.settings.addInformation("database", util.getToolInformation());
				
				if (Logger.logDebug()) {
					Logger.debug(util.getToolInformation());
				}
				
				return util;
			} catch (final Exception e) {
				if (e.getMessage().contains("database") && e.getMessage().contains("does not exist")) {
					throw new Shutdown(
					                   String.format("The specified database %s on host %s does not exist. Please check you settings.",
					                                 dbOptions.getDatabaseName(), dbOptions.getDatabaseHost()));
				}
				throw new Shutdown(e);
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.ArgumentSetOptions#requirements(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public Map<String, IOptions<?, ?>> requirements(final ArgumentSet<?, ?> set) throws ArgumentRegistrationException,
	                                                                            SettingsParseError {
		// PRECONDITIONS
		
		try {
			final Map<String, IOptions<?, ?>> map = new HashMap<String, IOptions<?, ?>>();
			
			this.databaseName = new StringArgument.Options(set, "name", "Name of the database", null,
			                                               Requirement.required);
			map.put("name", this.databaseName);
			this.databaseUser = new StringArgument.Options(set, "user", "User name for database. Default: miner",
			                                               "miner", Requirement.required);
			map.put("user", this.databaseUser);
			this.databaseHost = new StringArgument.Options(set, "host", "Name of database host. Default: localhost",
			                                               "localhost", Requirement.required);
			map.put("host", this.databaseHost);
			this.databasePassword = new StringArgument.Options(set, "password",
			                                                   "Password for database. Default: miner", "miner",
			                                                   Requirement.required, true);
			map.put("password", this.databasePassword);
			this.databaseType = new EnumArgument.Options<DatabaseType>(set, "type",
			                                                           "Defines the type of the database.",
			                                                           DatabaseType.POSTGRESQL, Requirement.required);
			map.put("type", this.databaseType);
			this.databaseUnit = new StringArgument.Options(set, "unit", "The persistence unit config tag used.",
			                                               this.unit, Requirement.required);
			map.put("unit", this.databaseUnit);
			this.databaseOptions = new EnumArgument.Options<ConnectOptions>(set, "options", "Connection options.",
			                                                                ConnectOptions.VALIDATE_OR_CREATE_SCHEMA,
			                                                                Requirement.required);
			map.put("options", this.databaseOptions);
			
			return map;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

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
package de.unisaarland.cs.st.moskito.settings;

import java.sql.SQLException;

import net.ownhero.dev.andama.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.andama.settings.ArgumentSet;
import net.ownhero.dev.andama.settings.arguments.EnumArgument;
import net.ownhero.dev.andama.settings.arguments.MaskedStringArgument;
import net.ownhero.dev.andama.settings.arguments.StringArgument;
import net.ownhero.dev.andama.settings.requirements.Requirement;
import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.persistence.ConnectOptions;
import de.unisaarland.cs.st.moskito.persistence.DatabaseType;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;

/**
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 * 
 */
public class DatabaseArguments extends ArgumentSet<PersistenceUtil> {
	
	private final StringArgument               databaseUnit;
	private final EnumArgument<ConnectOptions> databaseOptions;
	private final StringArgument               databaseMiddleware;
	private final StringArgument               databaseDriver;
	private final EnumArgument<DatabaseType>   databaseType;
	private final MaskedStringArgument         databasePassword;
	private final StringArgument               databaseHost;
	private final StringArgument               databaseUser;
	private final StringArgument               databaseName;
	
	/**
	 * @param argumentSet
	 * @param isRequired
	 * @throws ArgumentRegistrationException
	 */
	public DatabaseArguments(final ArgumentSet<?> argumentSet, final Requirement requirement, final String unit)
	        throws ArgumentRegistrationException {
		super(argumentSet, "Specifies connection options for the database connection.", requirement);
		this.databaseName = new StringArgument(argumentSet, "database.name", "Name of the database", null, requirement);
		addArgument(this.databaseName);
		this.databaseUser = new MaskedStringArgument(argumentSet, "database.user",
		                                             "User name for database. Default: miner", "miner", requirement);
		addArgument(this.databaseUser);
		this.databaseHost = new StringArgument(argumentSet, "database.host",
		                                       "Name of database host. Default: localhost", "localhost", requirement);
		addArgument(this.databaseHost);
		this.databasePassword = new MaskedStringArgument(argumentSet, "database.password",
		                                                 "Password for database. Default: miner", "miner", requirement);
		addArgument(this.databasePassword);
		this.databaseType = new EnumArgument<DatabaseType>(argumentSet, "database.type",
		                                                   "Defines the type of the database.",
		                                                   DatabaseType.POSTGRESQL, requirement);
		addArgument(this.databaseType);
		this.databaseDriver = new StringArgument(argumentSet, "database.driver", "Default: org.postgresql.Driver",
		                                         "org.postgresql.Driver", requirement);
		addArgument(this.databaseDriver);
		this.databaseMiddleware = new StringArgument(argumentSet, "database.middleware", "Default: OpenJPA", "OpenJPA",
		                                             requirement);
		addArgument(this.databaseMiddleware);
		this.databaseUnit = new StringArgument(argumentSet, "database.unit", "The persistence unit config tag used.",
		                                       unit, requirement);
		addArgument(this.databaseUnit);
		this.databaseOptions = new EnumArgument<ConnectOptions>(argumentSet, "database.options", "Connection options.",
		                                                        ConnectOptions.CREATE, requirement);
		addArgument(this.databaseOptions);
	}
	
	/**
	 * @return the databaseDriver
	 */
	public final StringArgument getDatabaseDriver() {
		return this.databaseDriver;
	}
	
	/**
	 * @return the databaseHost
	 */
	public final StringArgument getDatabaseHost() {
		return this.databaseHost;
	}
	
	/**
	 * @return the databaseMiddleware
	 */
	public final StringArgument getDatabaseMiddleware() {
		return this.databaseMiddleware;
	}
	
	/**
	 * @return the databaseName
	 */
	public final StringArgument getDatabaseName() {
		return this.databaseName;
	}
	
	/**
	 * @return the databaseOptions
	 */
	public final EnumArgument<ConnectOptions> getDatabaseOptions() {
		return this.databaseOptions;
	}
	
	/**
	 * @return the databasePassword
	 */
	public final MaskedStringArgument getDatabasePassword() {
		return this.databasePassword;
	}
	
	/**
	 * @return the databaseType
	 */
	public final EnumArgument<DatabaseType> getDatabaseType() {
		return this.databaseType;
	}
	
	/**
	 * @return the databaseUnit
	 */
	public final StringArgument getDatabaseUnit() {
		return this.databaseUnit;
	}
	
	/**
	 * @return the databaseUser
	 */
	public final StringArgument getDatabaseUser() {
		return this.databaseUser;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.settings.ArgumentSet#init()
	 */
	@Override
	protected boolean init() {
		if (!isInitialized()) {
			synchronized (this) {
				if (!isInitialized()) {
					if (JavaUtils.AnyNull(this.databaseHost.getValue(), this.databaseName.getValue(),
					                      this.databaseUser.getValue(), this.databasePassword.getValue(),
					                      this.databaseType.getValue(), this.databaseDriver.getValue(),
					                      this.databaseUnit.getValue(), this.databaseOptions.getValue(),
					                      this.databaseMiddleware.getValue())) {
						// FIXME this should cause an unrecoverable error
						return false;
					}
					
					if (this.databaseOptions.equals(ConnectOptions.DB_DROP_CREATE)) {
						try {
							PersistenceManager.dropDatabase(this.databaseHost.getValue(), this.databaseName.getValue(),
							                                this.databaseUser.getValue(),
							                                this.databasePassword.getValue(),
							                                this.databaseType.getValue().name(),
							                                this.databaseDriver.getValue());
							PersistenceManager.createDatabase(this.databaseHost.getValue(),
							                                  this.databaseName.getValue(),
							                                  this.databaseUser.getValue(),
							                                  this.databasePassword.getValue(),
							                                  this.databaseType.getValue().name(),
							                                  this.databaseDriver.getValue());
						} catch (final SQLException e) {
							if (Logger.logError()) {
								Logger.error("Could not re-create database.");
							}
							return false;
						}
						
					}
					
					final PersistenceUtil util = PersistenceManager.createUtil(this.databaseHost.getValue(),
					                                                           this.databaseName.getValue(),
					                                                           this.databaseUser.getValue(),
					                                                           this.databasePassword.getValue(),
					                                                           this.databaseType.getValue().name(),
					                                                           this.databaseDriver.getValue(),
					                                                           this.databaseUnit.getValue(),
					                                                           this.databaseOptions.getValue(),
					                                                           this.databaseMiddleware.getValue());
					
					getSettings().addToolInformation(this.databaseMiddleware.getValue(), util.getToolInformation());
					setCachedValue(util);
					return true;
				}
			}
		}
		return true;
	}
}

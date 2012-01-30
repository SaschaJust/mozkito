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
package de.unisaarland.cs.st.moskito.settings;

import java.sql.SQLException;

import net.ownhero.dev.andama.settings.AndamaArgumentSet;
import net.ownhero.dev.andama.settings.AndamaSettings;
import net.ownhero.dev.andama.settings.EnumArgument;
import net.ownhero.dev.andama.settings.MaskedStringArgument;
import net.ownhero.dev.andama.settings.StringArgument;
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
public class DatabaseArguments extends AndamaArgumentSet<PersistenceUtil> {
	
	private final AndamaSettings       settings;
	private final StringArgument       databaseUnit;
	private final EnumArgument         databaseOptions;
	private final StringArgument       databaseMiddleware;
	private final StringArgument       databaseDriver;
	private final EnumArgument         databaseType;
	private final MaskedStringArgument databasePassword;
	private final StringArgument       databaseHost;
	private final StringArgument       databaseUser;
	private final StringArgument       databaseName;
	
	/**
	 * @param settings
	 * @param isRequired
	 */
	public DatabaseArguments(final AndamaSettings settings, final boolean isRequired, final String unit) {
		super();
		this.settings = settings;
		this.databaseName = new StringArgument(settings, "database.name", "Name of the database", null, isRequired);
		addArgument(this.databaseName);
		this.databaseUser = new MaskedStringArgument(settings, "database.user",
		                                             "User name for database. Default: miner", "miner", isRequired);
		addArgument(this.databaseUser);
		this.databaseHost = new StringArgument(settings, "database.host", "Name of database host. Default: localhost",
		                                       "localhost", isRequired);
		addArgument(this.databaseHost);
		this.databasePassword = new MaskedStringArgument(settings, "database.password",
		                                                 "Password for database. Default: miner", "miner", isRequired);
		addArgument(this.databasePassword);
		this.databaseType = new EnumArgument(settings, "database.type", "Possible values: "
		        + JavaUtils.enumToString(DatabaseType.POSTGRESQL), DatabaseType.POSTGRESQL.toString(), isRequired,
		                                     JavaUtils.enumToArray(DatabaseType.POSTGRESQL));
		addArgument(this.databaseType);
		this.databaseDriver = new StringArgument(settings, "database.driver", "Default: org.postgresql.Driver",
		                                         "org.postgresql.Driver", isRequired);
		addArgument(this.databaseDriver);
		this.databaseMiddleware = new StringArgument(settings, "database.middleware", "Default: OpenJPA", "OpenJPA",
		                                             isRequired);
		addArgument(this.databaseMiddleware);
		this.databaseUnit = new StringArgument(settings, "database.unit", "The persistence unit config tag used.",
		                                       unit, true);
		addArgument(this.databaseUnit);
		this.databaseOptions = new EnumArgument(settings, "database.options", "Connection options. Valid values: "
		        + JavaUtils.enumToString(ConnectOptions.CREATE), ConnectOptions.CREATE.name(), true,
		                                        JavaUtils.enumToArray(ConnectOptions.CREATE));
		addArgument(this.databaseOptions);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.settings.AndamaArgumentSet#getValue()
	 */
	@Override
	public PersistenceUtil getValue() {
		getArguments();
		
		if (JavaUtils.AnyNull(this.databaseHost.getValue(), this.databaseName.getValue(), this.databaseUser.getValue(),
		                      this.databasePassword.getValue(), this.databaseType.getValue(),
		                      this.databaseDriver.getValue(), this.databaseUnit.getValue(),
		                      this.databaseOptions.getValue(), this.databaseMiddleware.getValue())) {
			// FIXME this should cause an unrecoverable error
			return null;
		}
		
		if (this.databaseOptions.equals(ConnectOptions.DB_DROP_CREATE)) {
			try {
				PersistenceManager.dropDatabase(this.databaseHost.getValue(), this.databaseName.getValue(),
				                                this.databaseUser.getValue(), this.databasePassword.getValue(),
				                                this.databaseType.getValue(), this.databaseDriver.getValue());
				PersistenceManager.createDatabase(this.databaseHost.getValue(), this.databaseName.getValue(),
				                                  this.databaseUser.getValue(), this.databasePassword.getValue(),
				                                  this.databaseType.getValue(), this.databaseDriver.getValue());
			} catch (final SQLException e) {
				if (Logger.logError()) {
					Logger.error("Could not re-create database.");
				}
				return null;
			}
			
		}
		
		final PersistenceUtil util = PersistenceManager.createUtil(this.databaseHost.getValue(),
		                                                           this.databaseName.getValue(),
		                                                           this.databaseUser.getValue(),
		                                                           this.databasePassword.getValue(),
		                                                           this.databaseType.getValue(),
		                                                           this.databaseDriver.getValue(),
		                                                           this.databaseUnit.getValue(),
		                                                           ConnectOptions.valueOf(this.databaseOptions.getValue()),
		                                                           this.databaseMiddleware.getValue());
		
		this.settings.addToolInformation(this.databaseMiddleware.getValue(), util.getToolInformation());
		return util;
	}
}

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
package org.mozkito.testing.annotation.processors;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;

import net.ownhero.dev.kisa.Logger;

import org.joda.time.DateTime;

import org.mozkito.database.DatabaseEnvironment;
import org.mozkito.database.DatabaseManager;
import org.mozkito.database.PersistenceUtil;
import org.mozkito.exceptions.ConfigurationException;
import org.mozkito.exceptions.TestSettingsError;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.DatabaseType;
import org.mozkito.testing.DatabaseTest;
import org.mozkito.testing.annotation.DatabaseSettings;

/**
 * The Class DatabaseSettingsProcessor.
 */
public class DatabaseSettingsProcessor implements MozkitoSettingsProcessor {
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.testing.annotation.processors. MoskitoSettingsProcessor#setup(java.lang.annotation.Annotation)
	 */
	@Override
	public <T extends DatabaseTest> void setup(final T test,
	                                           final Annotation annotation) {
		final DatabaseSettings settings = (DatabaseSettings) annotation;
		
		// system properties overwrite annotation settings
		String databaseName = System.getProperty("database.name") != null
		                                                                 ? System.getProperty("database.name")
		                                                                 : settings.database();
		final String databaseHost = (System.getProperty("database.host") != null) && settings.remote()
		                                                                                              ? System.getProperty("database.host")
		                                                                                              : settings.hostname();
		final ConnectOptions databaseOptions = System.getProperty("database.options") != null
		                                                                                     ? ConnectOptions.valueOf(System.getProperty("database.options")
		                                                                                                                    .toUpperCase())
		                                                                                     : settings.options();
		final String databasePassword = System.getProperty("database.password") != null
		                                                                               ? System.getProperty("database.password")
		                                                                               : settings.password();
		final DatabaseType databaseType = System.getProperty("database.type") != null
		                                                                             ? DatabaseType.valueOf(System.getProperty("database.type")
		                                                                                                          .toUpperCase())
		                                                                             : settings.type();
		final String databaseUnit = System.getProperty("database.unit") != null
		                                                                       ? System.getProperty("database.unit")
		                                                                       : settings.unit();
		final String databaseUsername = System.getProperty("database.user") != null
		                                                                           ? System.getProperty("database.user")
		                                                                           : settings.username();
		
		if (settings.options().equals(ConnectOptions.DROP_AND_CREATE_DATABASE)) {
			String tag = ManagementFactory.getRuntimeMXBean().getName().toLowerCase();
			tag = tag.replaceAll("\\W", "_");
			tag = tag + "_" + new DateTime().getMillis();
			databaseName = databaseName + '_' + tag;
		}
		
		DatabaseEnvironment options;
		
		try {
			options = new DatabaseEnvironment(databaseType, databaseName, databaseHost, databaseUsername,
			                                  databasePassword, databaseOptions, databaseUnit);
		} catch (final ConfigurationException e1) {
			throw new TestSettingsError(e1);
		}
		
		final PersistenceUtil util;
		
		if (settings.options().equals(ConnectOptions.DROP_AND_CREATE_DATABASE)) {
			
			if (Logger.logInfo()) {
				Logger.info("Setting up database test environment: %s", options);
			}
			
			try {
				if (Logger.logInfo()) {
					Logger.info("Dropping database with options: %s", options);
				}
				DatabaseManager.dropDatabase(options);
			} catch (final SQLException ignore) {
				// ignore
			}
			
			try {
				if (Logger.logInfo()) {
					Logger.info("Creating database with options: %s", options);
				}
				DatabaseManager.createDatabase(options);
			} catch (final SQLException e) {
				throw new TestSettingsError("Could not create database: " + options, e);
			}
			
			try {
				util = DatabaseManager.createUtil(options);
			} catch (final Throwable t) {
				throw new TestSettingsError("Could not initialize database connection: " + options, t);
			}
		} else {
			if (Logger.logInfo()) {
				Logger.info("Setting up database test environment: %s", options);
			}
			
			try {
				util = DatabaseManager.createUtil(options);
			} catch (final Throwable t) {
				throw new TestSettingsError("Could not initialize database connection.", t);
			}
		}
		
		test.setUtil(util);
		test.setOptions(options);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#tearDown(java.lang.annotation.Annotation)
	 */
	@Override
	public <T extends DatabaseTest> void tearDown(final T test,
	                                              final Annotation annotation) {
		
		if (test.getPersistenceUtil() != null) {
			test.getPersistenceUtil().shutdown();
			final DatabaseEnvironment options = test.getOptions();
			if (ConnectOptions.DROP_AND_CREATE_DATABASE.equals(options.getDatabaseOptions())) {
				
				final String dbName = test.getDatabaseName();
				try {
					DatabaseManager.dropDatabase(options);
				} catch (final SQLException e) {
					throw new TestSettingsError("Could not drop database " + dbName, e);
				}
			}
			test.setUtil(null);
		}
	}
	
}

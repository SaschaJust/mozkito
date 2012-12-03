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
import org.mozkito.exceptions.TestSettingsError;
import org.mozkito.persistence.ConnectOptions;
import org.mozkito.persistence.PersistenceManager;
import org.mozkito.persistence.PersistenceUtil;
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
		final String databaseName = System.getProperty("database.name") != null
		                                                                       ? System.getProperty("database.name")
		                                                                       : settings.database();
		final String databaseDriver = System.getProperty("database.driver") != null
		                                                                           ? System.getProperty("database.driver")
		                                                                           : settings.driver();
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
		final String databaseType = System.getProperty("database.type") != null
		                                                                       ? System.getProperty("database.type")
		                                                                       : settings.type();
		final String databaseUnit = System.getProperty("database.unit") != null
		                                                                       ? System.getProperty("database.unit")
		                                                                       : settings.unit();
		final String databaseUsername = System.getProperty("database.user") != null
		                                                                           ? System.getProperty("database.user")
		                                                                           : settings.username();
		
		final PersistenceUtil util;
		
		if (settings.options().equals(ConnectOptions.DB_DROP_CREATE)) {
			String tag = ManagementFactory.getRuntimeMXBean().getName().toLowerCase();
			tag = tag.replaceAll("\\W", "_");
			tag = tag + "_" + new DateTime().getMillis() + "";
			final String dbName = databaseName + '_' + tag;
			test.setDatabaseName(dbName);
			
			if (Logger.logInfo()) {
				Logger.info("Setting up database test environment: 'name:%s', 'driver:%s', 'host:%s', 'options:%s', 'password:******', 'type:%s', 'unit:%s', 'user:******'",
				            dbName, databaseDriver, databaseHost, databaseOptions, databaseType, databaseUnit);
			}
			
			try {
				if (Logger.logInfo()) {
					Logger.always("Dropping database with options: 'name:%s', 'driver:%s', 'host:%s', 'password:******', 'type:%s', 'unit:%s', 'user:******'",
					              dbName, databaseDriver, databaseHost, databaseType, databaseUnit);
				}
				PersistenceManager.dropDatabase(databaseHost, dbName, databaseUsername, databasePassword, databaseType,
				                                databaseDriver);
			} catch (final SQLException ignore) {
				// ignore
			}
			
			try {
				if (Logger.logInfo()) {
					Logger.always("Creating database with options: 'name:%s', 'driver:%s', 'host:%s', 'password:******', 'type:%s', 'unit:%s', 'user:******'",
					              dbName, databaseDriver, databaseHost, databaseType, databaseUnit);
				}
				PersistenceManager.createDatabase(databaseHost, dbName, databaseUsername, databasePassword,
				                                  databaseType, databaseDriver);
			} catch (final SQLException e) {
				throw new TestSettingsError("Could not create database " + dbName, e);
			}
			
			try {
				util = PersistenceManager.createUtil(databaseHost, dbName, databaseUsername, databasePassword,
				                                     databaseType, databaseDriver, databaseUnit, databaseOptions,
				                                     settings.util());
			} catch (final Throwable t) {
				throw new TestSettingsError("Could not initialize database connection.", t);
			}
		} else {
			if (Logger.logInfo()) {
				Logger.info("Setting up database test environment: 'name:%s', 'driver:%s', 'host:%s', 'options:%s', 'password:******', 'type:%s', 'unit:%s', 'user:******'",
				            databaseName, databaseDriver, databaseHost, databaseOptions, databaseType, databaseUnit);
			}
			
			try {
				util = PersistenceManager.createUtil(databaseHost, databaseName, databaseUsername, databasePassword,
				                                     databaseType, databaseDriver, databaseUnit, databaseOptions,
				                                     settings.util());
			} catch (final Throwable t) {
				throw new TestSettingsError("Could not initialize database connection.", t);
			}
		}
		
		test.setUtil(util);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#tearDown(java.lang.annotation.Annotation)
	 */
	@Override
	public <T extends DatabaseTest> void tearDown(final T test,
	                                              final Annotation annotation) {
		final DatabaseSettings settings = (DatabaseSettings) annotation;
		final String databaseDriver = System.getProperty("database.driver") != null
		                                                                           ? System.getProperty("database.driver")
		                                                                           : settings.driver();
		final String databaseHost = System.getProperty("database.host") != null
		                                                                       ? System.getProperty("database.host")
		                                                                       : settings.hostname();
		final String databasePassword = System.getProperty("database.password") != null
		                                                                               ? System.getProperty("database.password")
		                                                                               : settings.password();
		final String databaseType = System.getProperty("database.type") != null
		                                                                       ? System.getProperty("database.type")
		                                                                       : settings.type();
		final String databaseUsername = System.getProperty("database.user") != null
		                                                                           ? System.getProperty("database.user")
		                                                                           : settings.username();
		
		if (test.getPersistenceUtil() != null) {
			test.getPersistenceUtil().shutdown();
		}
		
		if (settings.options().equals(ConnectOptions.DB_DROP_CREATE)) {
			
			final String dbName = test.getDatabaseName();
			try {
				PersistenceManager.dropDatabase(databaseHost, dbName, databaseUsername, databasePassword, databaseType,
				                                databaseDriver);
			} catch (final SQLException e) {
				throw new TestSettingsError("Could not drop database " + dbName, e);
			}
		}
		test.setUtil(null);
	}
	
}

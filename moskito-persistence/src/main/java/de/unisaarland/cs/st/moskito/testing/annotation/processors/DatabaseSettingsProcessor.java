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
 ******************************************************************************/

package de.unisaarland.cs.st.moskito.testing.annotation.processors;

import java.lang.annotation.Annotation;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;

import net.ownhero.dev.kisa.Logger;
import de.unisaarland.cs.st.moskito.exceptions.TestSettingsError;
import de.unisaarland.cs.st.moskito.persistence.ConnectOptions;
import de.unisaarland.cs.st.moskito.persistence.PersistenceManager;
import de.unisaarland.cs.st.moskito.persistence.PersistenceUtil;
import de.unisaarland.cs.st.moskito.testing.MoskitoTest;
import de.unisaarland.cs.st.moskito.testing.annotation.DatabaseSettings;

public class DatabaseSettingsProcessor implements MoskitoSettingsProcessor {
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#setup(java.lang.annotation.Annotation)
	 */
	@Override
	public void setup(final Class<?> aClass,
	                  final Annotation annotation) throws TestSettingsError {
		final DatabaseSettings settings = (DatabaseSettings) annotation;
		final String databaseName = System.getProperty("database.name") != null
		                                                                       ? System.getProperty("database.name")
		                                                                       : settings.database();
		final String databaseDriver = System.getProperty("database.driver") != null
		                                                                           ? System.getProperty("database.driver")
		                                                                           : settings.driver();
		final String databaseHost = System.getProperty("database.host") != null
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
		                                                                       : settings.type();
		final String databaseUsername = System.getProperty("database.user") != null
		                                                                           ? System.getProperty("database.user")
		                                                                           : settings.username();
		
		final PersistenceUtil util;
		
		if (settings.options().equals(ConnectOptions.DB_DROP_CREATE)) {
			String tag = ManagementFactory.getRuntimeMXBean().getName().toLowerCase();
			tag = tag.replaceAll("\\W", "_");
			final String dbName = databaseName + '_' + tag;
			
			if (Logger.logInfo()) {
				Logger.info("Setting up database test environment: 'name:%s', 'driver:%s', 'host:%s', 'options:%s', 'password:******', 'type:%s', 'unit:%s', 'user:******'",
				            dbName, databaseDriver, databaseHost, databaseOptions, databaseType, databaseUnit);
			}
			
			try {
				PersistenceManager.dropDatabase(databaseHost, dbName, databaseUsername, databasePassword, databaseType,
				                                databaseDriver);
			} catch (final SQLException ignore) {
				// ignore
			}
			try {
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
		
		MoskitoTest.setPersistenceUtil(util);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.testing.annotation.processors.
	 * MoskitoSettingsProcessor#tearDown(java.lang.annotation.Annotation)
	 */
	@Override
	public void tearDown(final Class<?> aClass,
	                     final Annotation annotation) throws TestSettingsError {
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
		
		MoskitoTest.getPersistenceUtil().shutdown();
		
		if (settings.options().equals(ConnectOptions.DB_DROP_CREATE)) {
			String tag = ManagementFactory.getRuntimeMXBean().getName().toLowerCase();
			tag = tag.replaceAll("\\W", "_");
			final String dbName = settings.database() + '_' + tag;
			try {
				PersistenceManager.dropDatabase(databaseHost, dbName, databaseUsername, databasePassword, databaseType,
				                                databaseDriver);
			} catch (final SQLException e) {
				throw new TestSettingsError("Could not drop database " + dbName, e);
			}
		}
	}
	
}

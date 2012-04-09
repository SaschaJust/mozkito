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
		PersistenceUtil util;
		
		if (settings.options().equals(ConnectOptions.DB_DROP_CREATE)) {
			String tag = ManagementFactory.getRuntimeMXBean().getName().toLowerCase();
			tag = tag.replaceAll("\\W", "_");
			final String dbName = settings.database() + '_' + tag;
			
			try {
				PersistenceManager.dropDatabase(settings.hostname(), dbName, settings.username(), settings.password(),
				                                settings.type(), settings.driver());
			} catch (final SQLException ignore) {
				// ignore
			}
			try {
				PersistenceManager.createDatabase(settings.hostname(), dbName, settings.username(),
				                                  settings.password(), settings.type(), settings.driver());
			} catch (final SQLException e) {
				throw new TestSettingsError("Could not create database " + settings.database(), e);
			}
			
			util = PersistenceManager.createUtil(settings.hostname(), dbName, settings.username(), settings.password(),
			                                     settings.type(), settings.driver(), settings.unit(),
			                                     settings.options(), settings.util());
		} else {
			util = PersistenceManager.createUtil(settings.hostname(), settings.database(), settings.username(),
			                                     settings.password(), settings.type(), settings.driver(),
			                                     settings.unit(), settings.options(), settings.util());
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
		MoskitoTest.getPersistenceUtil().shutdown();
		
		if (settings.options().equals(ConnectOptions.DB_DROP_CREATE)) {
			String tag = ManagementFactory.getRuntimeMXBean().getName().toLowerCase();
			tag = tag.replaceAll("\\W", "_");
			final String dbName = settings.database() + '_' + tag;
			try {
				PersistenceManager.dropDatabase(settings.hostname(), dbName, settings.username(), settings.password(),
				                                settings.type(), settings.driver());
			} catch (final SQLException e) {
				throw new TestSettingsError("Could not create database " + settings.database(), e);
			}
		}
	}
	
}

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

package org.mozkito.database;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Table;

/**
 * The Class LoaderPool.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class QueryPool {
	
	/** The loaders. */
	private final Map<Class<?>, EntityAdapter<?>> entityAdapters = new HashMap<Class<?>, EntityAdapter<?>>();
	
	/** The persistence util. */
	private Connector                             connector;
	private final EntityCache                     cache          = new EntityCache();
	
	/**
	 * Instantiates a new loader pool.
	 * 
	 * @param connector
	 *            the persistence util
	 */
	public QueryPool(final Connector connector) {
		PRECONDITIONS: {
			if (connector == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			// body
			this.connector = connector;
		} finally {
			POSTCONDITIONS: {
				Condition.notNull(this.connector, "Field '%s' in '%s'.", "this.connector", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Adds the loader.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @param loader
	 *            the loader
	 * @return the loader
	 */
	@SuppressWarnings ("unchecked")
	public <T extends Entity> EntityAdapter<T> addLoader(final Class<T> clazz,
	                                                     final EntityAdapter<T> loader) {
		return (EntityAdapter<T>) this.entityAdapters.put(clazz, loader);
	}
	
	/**
	 * Creates the scheme.
	 * 
	 * @throws DatabaseException
	 *             the database exception
	 */
	public void createScheme() throws DatabaseException {
		SANITY: {
			assert this.entityAdapters != null;
		}
		
		final Set<Table> remainingTables = new HashSet<>();
		final Set<Table> providedTables = new HashSet<>();
		
		EntityAdapter<?> currentAdapter = null;
		for (final Entry<Class<?>, EntityAdapter<?>> entry : this.entityAdapters.entrySet()) {
			currentAdapter = entry.getValue();
			final Layout<?> layout = currentAdapter.getLayout();
			
			remainingTables.addAll(layout.getManagedTables());
		}
		
		Integer previousCount = null;
		while (!remainingTables.isEmpty()) {
			
			if (previousCount != null) {
				if (remainingTables.size() == previousCount) {
					throw new DatabaseException("Cannot resolve circular dependencies. Please fix your layout.");
				}
			}
			
			previousCount = remainingTables.size();
			
			Table nextTable = null;
			NEXT_TABLE: for (final Table table : remainingTables) {
				if (table.dependsOn().isEmpty() || providedTables.containsAll(table.dependsOn())) {
					nextTable = table;
					remainingTables.remove(table);
					break NEXT_TABLE;
				} else {
					if (Logger.logDebug()) {
						Logger.debug("Skipping table %s for now. Dependencies %s not all in provided %s.", table,
						             Table.names(table.dependsOn()), Table.names(providedTables));
					}
				}
			}
			
			if (nextTable == null) {
				throw new DatabaseException("Cannot resolve dependencies or remaining tables "
				        + Table.names(remainingTables));
			}
			
			final List<String> statements = new LinkedList<>();
			statements.add("CREATE " + nextTable.toSQL() + ";");
			try {
				getConnector().beginTransaction();
				final Statement sm = getConnector().createStatement();
				for (final String statement : statements) {
					if (Logger.logDebug()) {
						Logger.debug("SQL: " + statement);
					}
					sm.executeUpdate(statement);
				}
				
				getConnector().commit();
			} catch (final SQLException e) {
				throw new DatabaseException(e);
			}
			
			providedTables.add(nextTable);
		}
	}
	
	/**
	 * Gets the loader.
	 * 
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the clazz
	 * @return the loader
	 */
	@SuppressWarnings ("unchecked")
	public <T extends Entity> EntityAdapter<T> getAdapter(final Class<T> clazz) {
		return (EntityAdapter<T>) this.entityAdapters.get(clazz);
	}
	
	/**
	 * Gets the persistence util.
	 * 
	 * @return the persistenceUtil
	 */
	public Connector getConnector() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.connector;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the entity cache.
	 * 
	 * @return the entity cache
	 */
	public EntityCache getEntityCache() {
		return this.cache;
	}
	
	/**
	 * Sets the persistence util.
	 * 
	 * @param persistenceUtil
	 *            the persistenceUtil to set
	 */
	public void setPersistenceUtil(final Connector persistenceUtil) {
		this.connector = persistenceUtil;
	}
}

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.database.model.Table;

/**
 * The Interface Layout.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 * @param <T>
 *            the generic type
 */
public final class Layout<T extends Entity> {
	
	/**
	 * The Enum TableType.
	 */
	public static enum TableType {
		
		/** The main. */
		MAIN,
		/** The join. */
		JOIN;
	}
	
	/** The join tables. */
	private final Map<String, Table> joinTables = new HashMap<>();
	
	/** The main table. */
	private Table                    mainTable  = null;
	
	private final Class<T>           returnType;
	
	private boolean                  immutable  = false;
	
	/**
	 * Instantiates a new layout.
	 */
	public Layout() {
		try {
			final Method method = getClass().getMethod("provides", new Class<?>[0]);
			SANITY: {
				assert method != null;
			}
			
			@SuppressWarnings ("unchecked")
			final Class<T> clazz = (Class<T>) method.getReturnType();
			
			this.returnType = clazz;
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/**
	 * Instantiates a new layout.
	 * 
	 * @param returnType
	 *            the return type
	 */
	public Layout(final Class<T> returnType) {
		PRECONDITIONS: {
			if (returnType == null) {
				throw new NullPointerException();
			}
		}
		
		this.returnType = returnType;
	}
	
	/**
	 * Adds the table.
	 * 
	 * @param table
	 *            the table
	 * @param type
	 *            the type
	 * @return true, if successful
	 */
	public boolean addTable(final Table table,
	                        final TableType type) {
		PRECONDITIONS: {
			if (table == null) {
				throw new NullPointerException();
			}
			if (type == null) {
				throw new NullPointerException();
			}
			if (this.immutable) {
				throw new IllegalStateException("Layout is immutable.");
			}
		}
		
		switch (type) {
			case MAIN:
				if (this.mainTable != null) {
					return false;
				}
				this.mainTable = table;
				break;
			
			default:
				SANITY: {
					assert this.joinTables != null;
				}
				if (this.joinTables.containsKey(table.name())) {
					return false;
				}
				this.joinTables.put(table.name(), table);
				break;
		}
		
		return true;
	}
	
	/**
	 * Gets the main table.
	 * 
	 * @return the main table
	 */
	public Table getMainTable() {
		SANITY: {
			assert this.mainTable != null;
		}
		
		return this.mainTable;
	}
	
	/**
	 * Gets the managed tables.
	 * 
	 * @return the managed tables
	 */
	public List<Table> getManagedTables() {
		SANITY: {
			assert this.mainTable != null;
			assert this.joinTables != null;
		}
		
		final List<Table> list = new ArrayList<>(this.joinTables.size() + 1);
		list.add(this.mainTable);
		list.addAll(this.joinTables.values());
		
		return list;
	}
	
	/**
	 * Gets the table.
	 * 
	 * @param name
	 *            the name
	 * @return the table
	 */
	public Table getTable(final String name) {
		PRECONDITIONS: {
			if (name == null) {
				throw new NullPointerException();
			}
			if (!this.mainTable.name().equals(name) && !this.joinTables.containsKey(name)) {
				throw new IllegalArgumentException();
			}
		}
		
		if (this.mainTable.name().equals(name)) {
			return this.mainTable;
		} else {
			return this.joinTables.get(name);
		}
	}
	
	/**
     * 
     */
	public void makeImmutable() {
		this.immutable = true;
		for (final Table table : getManagedTables()) {
			table.makeImmutable();
		}
	}
	
	/**
	 * Manages.
	 * 
	 * @return the class
	 */
	public Class<T> provides() {
		return this.returnType;
	}
}

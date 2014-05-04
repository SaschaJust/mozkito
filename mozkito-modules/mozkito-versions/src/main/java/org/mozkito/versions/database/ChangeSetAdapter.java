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

package org.mozkito.versions.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mozkito.database.EntityAdapter;
import org.mozkito.database.QueryPool;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.types.Type;
import org.mozkito.persons.model.Person;
import org.mozkito.versions.model.ChangeSet;

/**
 * The Class ChangeSetQuery.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class ChangeSetAdapter extends EntityAdapter<ChangeSet> {
	
	/**
	 * Instantiates a new change set adapter.
	 * 
	 * @param queryPool
	 *            the query pool
	 */
	protected ChangeSetAdapter(final QueryPool queryPool) {
		super(queryPool);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.EntityAdapter#getId(java.sql.ResultSet)
	 */
	@Override
	public Object getId(final ResultSet idSet) throws DatabaseException {
		PRECONDITIONS: {
			if (idSet == null) {
				throw new NullPointerException();
			}
		}
		
		SANITY: {
			assert ChangeSet.MAIN_TABLE.column(0) != null;
			assert ChangeSet.MAIN_TABLE.primaryKey() != null;
			assert ChangeSet.MAIN_TABLE.primaryKey().getColumns() != null;
			assert ChangeSet.MAIN_TABLE.primaryKey().getColumns().length == 1;
			assert ChangeSet.MAIN_TABLE.primaryKey().getColumns()[0] != null;
			assert ChangeSet.MAIN_TABLE.primaryKey().getColumns()[0].type() != null;
			assert Type.getVarChar(40).equals(Person.MAIN_TABLE.primaryKey().getColumns()[0].type());
			assert Person.MAIN_TABLE.primaryKey().getColumns()[0].equals(Person.MAIN_TABLE.column(0));
		}
		
		try {
			return idSet.getString(1);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.EntityAdapter#loadById(java.lang.Object)
	 */
	@Override
	public ChangeSet loadById(final Object id) throws DatabaseException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'loadById' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.EntityAdapter#saveOrUpdate(org.mozkito.database.Entity)
	 */
	@Override
	public void saveOrUpdate(final ChangeSet entity) throws DatabaseException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'saveOrUpdate' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

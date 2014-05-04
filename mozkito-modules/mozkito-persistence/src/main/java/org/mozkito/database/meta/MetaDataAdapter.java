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

package org.mozkito.database.meta;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mozkito.database.EntityAdapter;
import org.mozkito.database.QueryPool;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;

/**
 * The Class MetaDataQuery.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MetaDataAdapter extends EntityAdapter<MetaData> {
	
	private final Table table = MetaData.LAYOUT.getMainTable();
	
	/**
	 * Instantiates a new meta data query.
	 * 
	 * @param queryPool
	 *            the query pool
	 */
	public MetaDataAdapter(final QueryPool queryPool) {
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
			assert MetaData.TABLE != null;
			assert MetaData.TABLE.primaryKey() != null;
			assert MetaData.TABLE.primaryKey().getColumns() != null;
			assert MetaData.TABLE.primaryKey().getColumns().length == 1;
			assert MetaData.TABLE.primaryKey().getColumns()[0] != null;
			assert MetaData.TABLE.primaryKey().getColumns()[0].type() != null;
			assert Type.getShort().equals(MetaData.TABLE.primaryKey().getColumns()[0]);
			assert MetaData.TABLE.column(0) != null;
			assert MetaData.TABLE.primaryKey().getColumns()[0].equals(MetaData.TABLE.column(0));
		}
		
		try {
			return idSet.getShort(1);
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
	public MetaData loadById(final Object id) throws DatabaseException {
		PRECONDITIONS: {
			// none
		}
		
		try {
			System.err.println(this.table);
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
	public void saveOrUpdate(final MetaData entity) throws DatabaseException {
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

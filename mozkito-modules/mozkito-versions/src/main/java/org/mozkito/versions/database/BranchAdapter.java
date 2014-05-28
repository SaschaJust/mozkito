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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mozkito.database.EntityAdapter;
import org.mozkito.database.QueryPool;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;
import org.mozkito.versions.model.Branch;
import org.mozkito.versions.model.ChangeSet;
import org.mozkito.versions.model.VersionArchive;

/**
 * The Class BranchQuery.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class BranchAdapter extends EntityAdapter<Branch> {
	
	/**
	 * @param queryPool
	 */
	protected BranchAdapter(final QueryPool queryPool) {
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
			assert getLayout().getMainTable().column(0) != null;
			assert getLayout().getMainTable().primaryKey() != null;
			assert getLayout().getMainTable().primaryKey().getColumns() != null;
			assert getLayout().getMainTable().primaryKey().getColumns().length == 1;
			assert getLayout().getMainTable().primaryKey().getColumns()[0] != null;
			assert getLayout().getMainTable().primaryKey().getColumns()[0].type() != null;
			assert Type.getVarChar(255).equals(getLayout().getMainTable().primaryKey().getColumns()[0].type());
			assert getLayout().getMainTable().primaryKey().getColumns()[0].equals(getLayout().getMainTable().column(0));
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
	public Branch loadById(final Object id) throws DatabaseException {
		PRECONDITIONS: {
			if (id == null) {
				throw new NullPointerException();
			}
			if (!String.class.isAssignableFrom(id.getClass())) {
				throw new IllegalArgumentException();
			}
		}
		
		final String localId = (String) id;
		
		try {
			SANITY: {
				assert getLayout().getMainTable().primaryKey().getColumns() != null;
				assert getLayout().getMainTable().primaryKey().getColumns().length == 1; // there exists a key and it's
				                                                                         // no composite key
				assert Type.getVarChar(255).equals(getLayout().getMainTable().primaryKey().getColumnType(0));
				assert getLayout().getMainTable().column("head") != null;
			}
			
			PreparedStatement preparedStatement = getConnector().prepare("SELECT "
			                                                                     + getLayout().getMainTable()
			                                                                                  .primaryKey()
			                                                                     + ", "
			                                                                     + getLayout().getMainTable()
			                                                                                  .column("version_archive")
			                                                                     + getLayout().getMainTable()
			                                                                                  .column("head")
			                                                                     + " FROM "
			                                                                     + getLayout().getMainTable()
			                                                                     + " WHERE "
			                                                                     + getLayout().getMainTable()
			                                                                                  .primaryKey() + " = ?;");
			
			preparedStatement.setString(1, localId);
			ResultSet result = preparedStatement.executeQuery();
			
			if (!result.next()) {
				return null;
			}
			
			SANITY: {
				assert VersionArchive.LAYOUT.getMainTable().primaryKey().getColumns() != null;
				assert VersionArchive.LAYOUT.getMainTable().primaryKey().getColumns().length == 1;
				assert Type.getShort().equals(VersionArchive.LAYOUT.getMainTable().primaryKey().getColumnType(0));
			}
			
			final VersionArchive va = getQueryPool().getAdapter(VersionArchive.class).loadById(result.getShort(1));
			
			final Branch branch = new Branch(va, result.getString(0));
			
			final ChangeSet changeSet = getQueryPool().getAdapter(ChangeSet.class).loadById(result.getString(3));
			branch.setHead(changeSet);
			
			final Table mergeTable = getLayout().getTable("branch_merges");
			preparedStatement = getConnector().prepare("SELECT " + mergeTable.column("id") + ", "
			                                                   + mergeTable.column("merge_changeset") + " FROM "
			                                                   + mergeTable + " WHERE "
			                                                   + mergeTable.column("branch_name") + " = ?;");
			preparedStatement.setString(1, branch.getName());
			
			result = preparedStatement.executeQuery();
			Set<String> mergedIn = null;
			while (result.next()) {
				if (mergedIn == null) {
					mergedIn = new HashSet<>();
				}
				mergedIn.add(result.getString(2));
			}
			
			branch.setMergedIn(mergedIn);
			
			return branch;
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}
	
	/**
	 * Load by version archive.
	 * 
	 * @param id
	 *            the id
	 * @return the iterator
	 * @throws DatabaseException
	 *             the database exception
	 */
	public Iterator<Branch> loadByVersionArchive(final Long id) throws DatabaseException {
		PRECONDITIONS: {
			if (id == null) {
				throw new NullPointerException();
			}
			if (id <= 0) {
				throw new IllegalArgumentException();
			}
		}
		
		final Table table = getLayout().getMainTable();
		SANITY: {
			assert getConnector() != null;
			assert table != null;
		}
		
		try {
			final PreparedStatement preparedStatement = getConnector().prepare("SELECT "
			                                                                           + table.primaryKey()
			                                                                           + " FROM "
			                                                                           + table
			                                                                           + " WHERE "
			                                                                           + table.column("version_archive")
			                                                                           + " = ?;");
			preparedStatement.setLong(1, id);
			final ResultSet idSet = preparedStatement.executeQuery();
			SANITY: {
				assert idSet != null;
			}
			return loadForIds(idSet);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.EntityAdapter#saveOrUpdate(org.mozkito.database.Entity)
	 */
	@Override
	public void saveOrUpdate(final Branch entity) throws DatabaseException {
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

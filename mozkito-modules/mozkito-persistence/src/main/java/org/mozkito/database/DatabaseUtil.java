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

import java.util.Collection;
import java.util.List;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.persistence.DatabaseType;

/**
 * The Class DatabaseUtil.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DatabaseUtil implements PersistenceUtil {
	
	private Connector connector;
	private QueryPool queryPool;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#activeTransaction()
	 */
	@Override
	public boolean activeTransaction() {
		return this.connector.activeTransaction();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws DatabaseException
	 * 
	 * @see org.mozkito.database.PersistenceUtil#beginTransaction()
	 */
	@Override
	public void beginTransaction() throws DatabaseException {
		this.connector.beginTransaction();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws DatabaseException
	 * 
	 * @see org.mozkito.database.PersistenceUtil#commitTransaction()
	 */
	@Override
	public void commitTransaction() throws DatabaseException {
		this.connector.commit();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#createConnector(org.mozkito.database.DatabaseEnvironment)
	 */
	@Override
	public void createConnector(final DatabaseEnvironment options) {
		PRECONDITIONS: {
			if (options == null) {
				throw new NullPointerException();
			}
		}
		
		try {
			this.connector = new Connector(options);
			this.queryPool = new QueryPool(this.connector);
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
		
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#createCriteria(java.lang.Class)
	 */
	@Override
	public <T extends Entity> Criteria<T> createCriteria(final Class<T> clazz) {
		PRECONDITIONS: {
			if (clazz == null) {
				throw new NullPointerException();
			}
		}
		
		final EntityAdapter<T> adapter = this.queryPool.getAdapter(clazz);
		
		if (adapter == null) {
			throw new IllegalArgumentException();
		}
		
		return new CriteriaImpl<>(adapter);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#delete(org.mozkito.database.Entity)
	 */
	@Override
	public void delete(final Entity object) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'delete' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#detach(org.mozkito.database.Entity)
	 */
	@Override
	public void detach(final Entity object) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'detach' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#executeNativeSelectQuery(java.lang.String)
	 */
	@SuppressWarnings ("rawtypes")
	@Override
	public List executeNativeSelectQuery(final String queryString) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'executeNativeSelectQuery' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#executeQuery(java.lang.String)
	 */
	@Override
	public void executeQuery(final String query) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'executeQuery' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#flush()
	 */
	@Override
	public void flush() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'flush' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#getManagedEntities()
	 */
	@Override
	public String getManagedEntities() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getManagedEntities' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#getPool()
	 */
	@Override
	public QueryPool getPool() {
		return this.queryPool;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#getToolInformation()
	 */
	@Override
	public String getToolInformation() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getToolInformation' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#getType()
	 */
	@Override
	public DatabaseType getType() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getType' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws DatabaseException
	 * 
	 * @see org.mozkito.database.PersistenceUtil#load(java.lang.Class)
	 */
	@Override
	public <T extends Entity> List<T> load(final Class<T> clazz) throws DatabaseException {
		PRECONDITIONS: {
			// none
		}
		
		final EntityAdapter<T> adapter = this.queryPool.getAdapter(clazz);
		// TODO
		adapter.load(adapter.getLayout().getMainTable(), null);
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws DatabaseException
	 * 
	 * @see org.mozkito.database.PersistenceUtil#load(org.mozkito.database.Criteria)
	 */
	@Override
	public <T extends Entity> List<T> load(final Criteria<T> criteria) throws DatabaseException {
		PRECONDITIONS: {
			if (criteria == null) {
				throw new NullPointerException();
			}
			if (!(criteria instanceof CriteriaImpl)) {
				throw new IllegalArgumentException();
			}
		}
		
		final CriteriaImpl<T> crit = (CriteriaImpl<T>) criteria;
		
		final EntityAdapter<T> adapter = this.queryPool.getAdapter(crit.queries());
		
		adapter.load(adapter.getLayout().getMainTable(), crit);
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#load(org.mozkito.database.Criteria, int)
	 */
	@Override
	public <T extends Entity> List<T> load(final Criteria<T> criteria,
	                                       final int sizeLimit) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'load' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#loadById(java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T extends Entity> T loadById(final Class<T> clazz,
	                                     final Object id) {
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
	 * @see org.mozkito.database.PersistenceUtil#loadByIds(java.lang.Class, java.util.Collection)
	 */
	@Override
	public <T extends Entity> List<T> loadByIds(final Class<T> clazz,
	                                            final Collection<?> ids) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'loadByIds' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#loadByIds(java.lang.Class, java.lang.Object[])
	 */
	@Override
	public <T extends Entity> List<T> loadByIds(final Class<T> clazz,
	                                            final Object... ids) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'loadByIds' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#rollback()
	 */
	@Override
	public void rollback() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'rollback' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#save(org.mozkito.database.Entity)
	 */
	@Override
	public void save(final Entity object) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'save' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#saveOrUpdate(org.mozkito.database.Entity)
	 */
	@Override
	public void saveOrUpdate(final Entity object) {
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
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#shutdown()
	 */
	@Override
	public void shutdown() {
		PRECONDITIONS: {
			// none
		}
		
		if (this.connector.activeTransaction()) {
			try {
				this.connector.rollback();
			} catch (final DatabaseException e1) {
				// TODO Auto-generated catch block
				
			}
		}
		
		try {
			this.connector.close();
		} catch (final DatabaseException e) {
			// TODO Auto-generated catch block
			
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.PersistenceUtil#update(org.mozkito.database.Entity)
	 */
	@Override
	public void update(final Entity object) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			//
			throw new RuntimeException("Method 'update' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

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

import org.apache.commons.lang.NotImplementedException;

import org.mozkito.persistence.Annotated;

/**
 * The Class DBEntity.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DBEntity implements Annotated {
	
	/** The db query pool. */
	private DBQueryPool dbQueryPool;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		this.dbQueryPool.getEntityCache().unregister(this);
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.persistence.Annotated#getClassName()
	 */
	@Override
	public String getClassName() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			// TODO Auto-generated method stub
			// return null;
			throw new RuntimeException("Method 'getClassName' has not yet been implemented."); //$NON-NLS-1$
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the db query pool.
	 * 
	 * @return the dbQueryPool
	 */
	public DBQueryPool getDbQueryPool() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.dbQueryPool;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	public String getDBTableName() {
		// TODO implement this
		throw new NotImplementedException();
	}
	
	protected Method getIdGetter() {
		return null;
	}
	
	/**
	 * Gets the iD sequence name.
	 * 
	 * @return the iD sequence name
	 */
	public String getIDSequenceName() {
		return null;
	}
	
	protected Method getSetterFromGetter(final Method getter) {
		return null;
	}
	
	/**
	 * Sets the db query pool.
	 * 
	 * @param dbQueryPool
	 *            the dbQueryPool to set
	 */
	public void setDbQueryPool(final DBQueryPool dbQueryPool) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.dbQueryPool = dbQueryPool;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
}

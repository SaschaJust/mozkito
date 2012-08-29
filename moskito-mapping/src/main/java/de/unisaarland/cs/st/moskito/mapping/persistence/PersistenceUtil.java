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
package de.unisaarland.cs.st.moskito.mapping.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.persistence.Query;

import net.ownhero.dev.kanuni.conditions.Condition;
import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.ConnectOptions;
import de.unisaarland.cs.st.moskito.persistence.Criteria;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersistenceUtil implements de.unisaarland.cs.st.moskito.persistence.PersistenceUtil {
	
	public PersistenceUtil(final de.unisaarland.cs.st.moskito.persistence.PersistenceUtil util) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#activeTransaction()
	 */
	@Override
	public boolean activeTransaction() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return false;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#beginTransaction()
	 */
	@Override
	public void beginTransaction() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#commitTransaction()
	 */
	@Override
	public void commitTransaction() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createCriteria(java.lang.Class)
	 */
	@Override
	public <T> Criteria<T> createCriteria(final Class<T> clazz) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createNativeQuery(java.lang.String,
	 * java.lang.Class)
	 */
	@Override
	public <T> Query createNativeQuery(final String query,
	                                   final Class<T> clazz) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createQuery(java.lang.String)
	 */
	@Override
	public Query createQuery(final String query) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createSessionFactory(java.util.Properties)
	 */
	@Override
	public void createSessionFactory(final Properties properties) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#createSessionFactory(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * de.unisaarland.cs.st.moskito.persistence.ConnectOptions)
	 */
	@Override
	public void createSessionFactory(final String host,
	                                 final String database,
	                                 final String user,
	                                 final String password,
	                                 final String type,
	                                 final String driver,
	                                 final String unit,
	                                 final ConnectOptions options) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#delete(de.unisaarland.cs.st.moskito.persistence.Annotated
	 * )
	 */
	@Override
	public void delete(final Annotated object) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#executeNativeQuery(java.lang.String)
	 */
	@Override
	public void executeNativeQuery(final String query) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#executeNativeSelectQuery(java.lang.String)
	 */
	@Override
	public List executeNativeSelectQuery(final String queryString) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#executeQuery(java.lang.String)
	 */
	@Override
	public void executeQuery(final String query) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#exmerge(de.unisaarland.cs.st.moskito.persistence.Annotated
	 * )
	 */
	@Override
	public void exmerge(final Annotated object) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#flush()
	 */
	@Override
	public void flush() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the simple name of the class.
	 * 
	 * @return the simple name of the class.
	 */
	public final String getHandle() {
		// PRECONDITIONS
		
		final StringBuilder builder = new StringBuilder();
		
		try {
			final LinkedList<Class<?>> list = new LinkedList<Class<?>>();
			Class<?> clazz = getClass();
			list.add(clazz);
			
			while ((clazz = clazz.getEnclosingClass()) != null) {
				list.addFirst(clazz);
			}
			
			for (final Class<?> c : list) {
				if (builder.length() > 0) {
					builder.append('.');
				}
				
				builder.append(c.getSimpleName());
			}
			
			return builder.toString();
		} finally {
			// POSTCONDITIONS
			Condition.notNull(builder,
			                  "Local variable '%s' in '%s:%s'.", "builder", getClass().getSimpleName(), "getHandle"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#getToolInformation()
	 */
	@Override
	public String getToolInformation() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#getType()
	 */
	@Override
	public String getType() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#load(de.unisaarland.cs.st.moskito.persistence.Criteria)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#load(de.unisaarland.cs.st.moskito.persistence.Criteria,
	 * int)
	 */
	@Override
	public <T> List<T> load(final Criteria<T> criteria,
	                        final int sizeLimit) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#loadById(java.lang.Object, java.lang.Class)
	 */
	@Override
	public <T extends Annotated> T loadById(final Object id,
	                                        final Class<T> clazz) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
			return null;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#rollbackTransaction()
	 */
	@Override
	public void rollbackTransaction() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#save(de.unisaarland.cs.st.moskito.persistence.Annotated)
	 */
	@Override
	public void save(final Annotated object) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#saveOrUpdate(de.unisaarland.cs.st.moskito.persistence
	 * .Annotated)
	 */
	@Override
	public void saveOrUpdate(final Annotated object) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#shutdown()
	 */
	@Override
	public void shutdown() {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.moskito.persistence.PersistenceUtil#update(de.unisaarland.cs.st.moskito.persistence.Annotated
	 * )
	 */
	@Override
	public void update(final Annotated object) {
		// PRECONDITIONS
		
		try {
			// TODO Auto-generated method stub
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

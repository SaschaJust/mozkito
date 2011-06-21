/*******************************************************************************
 * Copyright (c) 2011 Kim Herzig, Sascha Just.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Kim Herzig, Sascha Just - initial API and implementation
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.reposuite.persistence;

import java.util.List;

import javax.persistence.Query;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public interface PersistenceUtil {
	
	public boolean activeTransaction();
	
	public void beginTransaction();
	
	public void commitTransaction();
	
	public <T> Criteria<T> createCriteria(final Class<T> clazz);
	
	public Query createQuery(final String query);
	
	public void delete(final Annotated object);
	
	public void executeNativeQuery(final String query);
	
	@SuppressWarnings ("rawtypes")
	public List executeNativeSelectQuery(final String queryString);
	
	public void executeQuery(final String query);
	
	public void exmerge(final Annotated object);
	
	public void flush();
	
	public String getToolInformation();
	
	public String getType();
	
	public void globalShutdown();
	
	public <T> List<T> load(final Criteria<T> criteria);
	
	public <T, I> T loadById(final I id,
	                         Class<T> clazz);
	
	public void rollbackTransaction();
	
	public void save(final Annotated object);
	
	public void saveOrUpdate(final Annotated object);
	
	public void shutdown();
	
	public void update(final Annotated object);
}

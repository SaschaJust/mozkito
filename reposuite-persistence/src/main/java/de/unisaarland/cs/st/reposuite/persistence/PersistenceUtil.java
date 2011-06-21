/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

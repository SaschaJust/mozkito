/*******************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
/**
 * 
 */
package de.unisaarland.cs.st.moskito.persistence;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class PersistenceManager {
	
	private final Map<String, Map<String, String>>        nativeQueries = new HashMap<String, Map<String, String>>();
	private final Map<Class<?>, Map<String, Criteria<?>>> storedQueries = new HashMap<Class<?>, Map<String, Criteria<?>>>();
	
	/**
	 * @param util
	 * @param id
	 * @return
	 */
	public String getNativeQuery(final PersistenceUtil util,
	                             final String id) {
		final String databaseType = util.getType().toLowerCase();
		
		if (this.nativeQueries.containsKey(databaseType) && this.nativeQueries.get(databaseType).containsKey(id)) {
			return this.nativeQueries.get(databaseType).get(id);
		} else {
			return null;
		}
	}
	
	/**
	 * @param id
	 * @return
	 */
	@SuppressWarnings ("unchecked")
	public <T> Criteria<T> getStoredQuery(final String id,
	                                      final Class<T> clazz) {
		return (Criteria<T>) this.storedQueries.get(clazz).get(id);
	}
	
	/**
	 * @param type
	 * @param id
	 * @param query
	 * @return
	 */
	public String registerNativeQuery(final String type,
	                                  final String id,
	                                  final String query) {
		final String databaseType = type.toLowerCase();
		if (!this.nativeQueries.containsKey(databaseType)) {
			this.nativeQueries.put(databaseType, new HashMap<String, String>());
		}
		
		final Map<String, String> map = this.nativeQueries.get(databaseType);
		return map.put(id, query);
	}
	
	/**
	 * @param <T>
	 * @param query
	 */
	public <T> void registerPreparedQuery(final PreparedQuery<T> query) {
		
	}
	
	/**
	 * @param id
	 * @param query
	 */
	@SuppressWarnings ("unchecked")
	public <T> void registerQuery(final String id,
	                              final Criteria<T> criteria) {
		final Type actualTypeArgument = ((ParameterizedType) criteria.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<T> actualRawTypeArgument = null;
		if (actualTypeArgument instanceof ParameterizedType) {
			actualRawTypeArgument = (Class<T>) ((ParameterizedType) actualTypeArgument).getRawType();
		} else {
			actualRawTypeArgument = (Class<T>) actualTypeArgument;
		}
		
		if (!this.storedQueries.containsKey(actualRawTypeArgument)) {
			this.storedQueries.put(actualRawTypeArgument, new HashMap<String, Criteria<?>>());
		}
		
		this.storedQueries.get(actualRawTypeArgument).put(id, criteria);
	}
}

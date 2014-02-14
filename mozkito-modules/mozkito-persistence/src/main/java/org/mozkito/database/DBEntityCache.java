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

import java.util.HashMap;
import java.util.Map;

/**
 * The Class DBEntityCache.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class DBEntityCache {
	
	/** The cache. */
	private final Map<DBEntity, Integer> cache = new HashMap<DBEntity, Integer>();
	
	/**
	 * Register.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void register(final DBEntity entity) {
		if (!this.cache.containsKey(entity)) {
			this.cache.put(entity, 0);
		}
		
		final Integer arf = this.cache.get(entity);
		this.cache.put(entity, arf);
	}
	
	/**
	 * Unregister.
	 * 
	 * @param entity
	 *            the entity
	 */
	public void unregister(final DBEntity entity) {
		PRECONDITIONS: {
			if (!this.cache.containsKey(entity)) {
				throw new IllegalArgumentException();
			}
		}
		
		final Integer arf = this.cache.get(entity);
		if (arf == 1) {
			this.cache.remove(entity);
		} else {
			this.cache.put(entity, -1);
		}
	}
	
}

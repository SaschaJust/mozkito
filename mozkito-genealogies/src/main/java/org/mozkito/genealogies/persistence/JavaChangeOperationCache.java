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
package org.mozkito.genealogies.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.mozkito.codeanalysis.model.JavaChangeOperation;
import org.mozkito.persistence.PersistenceUtil;


/**
 * @author Kim Herzig <herzig@mozkito.org>
 * 
 */
public class JavaChangeOperationCache {
	
	private class JavaChangeOperationCacheEntry implements Comparable<JavaChangeOperationCacheEntry> {
		
		private final JavaChangeOperation operation;
		private DateTime                  lastAccess;
		
		public JavaChangeOperationCacheEntry(final JavaChangeOperation operation) {
			this.operation = operation;
			this.lastAccess = new DateTime();
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(final JavaChangeOperationCacheEntry o) {
			return DateTimeComparator.getInstance().compare(this.lastAccess, o.lastAccess);
		}
		
		public JavaChangeOperation getOperation() {
			this.lastAccess = new DateTime();
			return this.operation;
		}
		
		/**
		 * @return
		 */
		public long getOperationId() {
			this.lastAccess = new DateTime();
			return this.operation.getId();
		}
	}
	
	private final Map<Long, JavaChangeOperationCacheEntry> cache = new ConcurrentHashMap<>();
	private final PersistenceUtil                          persistenceUtil;
	
	public JavaChangeOperationCache(final PersistenceUtil persistenceUtil) {
		this.persistenceUtil = persistenceUtil;
	}
	
	public JavaChangeOperation loadById(final long id) {
		if (this.cache.containsKey(id)) {
			return this.cache.get(id).getOperation();
		}
		final JavaChangeOperation op = this.persistenceUtil.loadById(id, JavaChangeOperation.class);
		if (op != null) {
			// TODO make cache size configurable
			if (this.cache.size() > 5000) {
				final List<JavaChangeOperationCacheEntry> cacheList = new ArrayList<>(this.cache.size());
				cacheList.addAll(this.cache.values());
				Collections.sort(cacheList);
				final int deleteIndex = cacheList.size() / 2;
				for (int i = 0; i < deleteIndex; ++i) {
					this.cache.remove(cacheList.get(i).getOperationId());
				}
			}
			this.cache.put(id, new JavaChangeOperationCacheEntry(op));
		}
		return op;
	}
	
}

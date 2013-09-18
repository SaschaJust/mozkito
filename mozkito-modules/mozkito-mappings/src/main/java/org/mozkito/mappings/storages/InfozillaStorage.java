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

package org.mozkito.mappings.storages;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mozkito.infozilla.model.EnhancedReport;
import org.mozkito.issues.model.Report;
import org.mozkito.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class InfozillaStorage extends Storage {
	
	private PersistenceUtil persistenceUtil = null;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#getDescription()
	 */
	@Override
	public String getDescription() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return getClassName();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the enhanced report.
	 * 
	 * @param report
	 *            the report
	 * @return the enhanced report
	 */
	public EnhancedReport getEnhancedReport(final Report report) {
		assert this.persistenceUtil != null;
		
		final List<EnhancedReport> list = this.persistenceUtil.load(this.persistenceUtil.createCriteria(EnhancedReport.class)
		                                                                                .eq("id", report.getId()));
		if (!list.isEmpty()) {
			final Iterator<EnhancedReport> iterator = list.iterator();
			assert iterator.hasNext();
			assert list.size() == 1;
			return iterator.next();
		}
		
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.storages.Storage#loadData()
	 */
	@Override
	public void loadData() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final PersistenceStorage storage = getStorage(PersistenceStorage.class);
			assert storage != null;
			this.persistenceUtil = storage.getUtil();
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.register.Node#storageDependency()
	 */
	@Override
	public Set<Class<? extends Storage>> storageDependency() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			final Set<Class<? extends Storage>> set = new HashSet<>();
			set.add(PersistenceStorage.class);
			return set;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}

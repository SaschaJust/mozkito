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
package de.unisaarland.cs.st.reposuite.mapping;

import java.util.Iterator;
import java.util.List;

import net.ownhero.dev.andama.exceptions.Shutdown;
import net.ownhero.dev.andama.exceptions.UnrecoverableError;
import net.ownhero.dev.andama.threads.AndamaGroup;
import net.ownhero.dev.andama.threads.AndamaSource;
import de.unisaarland.cs.st.reposuite.mapping.model.MapScore;
import de.unisaarland.cs.st.reposuite.mapping.settings.MappingSettings;
import de.unisaarland.cs.st.reposuite.persistence.Criteria;
import de.unisaarland.cs.st.reposuite.persistence.PersistenceUtil;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class MappingReader extends AndamaSource<MapScore> {
	
	private final PersistenceUtil persistenceUtil;
	private Iterator<MapScore>    iterator;
	
	/**
	 * @param threadGroup
	 * @param mappingSettings
	 * @param persistenceUtil
	 */
	public MappingReader(final AndamaGroup threadGroup, final MappingSettings settings,
	        final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		this.persistenceUtil = persistenceUtil;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.AndamaThread#beforeExecution()
	 */
	@Override
	public void beforeExecution() {
		super.beforeExecution();
		
		Criteria<MapScore> criteria = this.persistenceUtil.createCriteria(MapScore.class);
		List<MapScore> list = this.persistenceUtil.load(criteria);
		this.iterator = list.iterator();
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.andama.threads.OnlyOutputConnectable#process()
	 */
	@Override
	public MapScore process() throws UnrecoverableError, Shutdown {
		if (this.iterator.hasNext()) {
			MapScore mapScore = this.iterator.next();
			
			return (mapScore);
		}
		
		return null;
	}
	
}

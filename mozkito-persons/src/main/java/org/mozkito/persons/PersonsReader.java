/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package org.mozkito.persons;

import java.util.List;
import java.util.ListIterator;

import org.mozkito.persistence.Criteria;
import org.mozkito.persistence.PersistenceUtil;
import org.mozkito.persistence.model.PersonContainer;

import net.ownhero.dev.andama.threads.Group;
import net.ownhero.dev.andama.threads.PreExecutionHook;
import net.ownhero.dev.andama.threads.ProcessHook;
import net.ownhero.dev.andama.threads.Source;
import net.ownhero.dev.hiari.settings.Settings;
import net.ownhero.dev.kisa.Logger;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
public class PersonsReader extends Source<PersonContainer> {
	
	private ListIterator<PersonContainer> iterator;
	
	/**
	 * @param threadGroup
	 * @param name
	 * @param settings
	 * @param persistenceUtil
	 */
	public PersonsReader(final Group threadGroup, final Settings settings, final PersistenceUtil persistenceUtil) {
		super(threadGroup, settings, false);
		
		new PreExecutionHook<PersonContainer, PersonContainer>(this) {
			
			@Override
			public void preExecution() {
				final Criteria<PersonContainer> criteria = persistenceUtil.createCriteria(PersonContainer.class);
				final List<PersonContainer> containerList = persistenceUtil.load(criteria);
				
				if (Logger.logDebug()) {
					Logger.debug(String.format(Messages.getString("PersonsReader.analzying"), containerList.size())); //$NON-NLS-1$
				}
				PersonsReader.this.iterator = containerList.listIterator();
			}
		};
		
		new ProcessHook<PersonContainer, PersonContainer>(this) {
			
			@Override
			public void process() {
				if (PersonsReader.this.iterator.hasNext()) {
					final PersonContainer personContainer = PersonsReader.this.iterator.next();
					if (PersonsReader.this.iterator.hasNext()) {
						provideOutputData(personContainer, false);
					} else {
						provideOutputData(personContainer, true);
					}
				} else {
					skipData();
				}
			}
		};
	}
	
}

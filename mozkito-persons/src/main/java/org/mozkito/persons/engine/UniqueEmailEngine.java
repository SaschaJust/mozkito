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
package org.mozkito.persons.engine;

import java.util.LinkedList;
import java.util.List;

import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;

import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.persons.elements.PersonBucket;
import org.mozkito.persons.processing.PersonManager;

/**
 * The Class UniqueEmailEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class UniqueEmailEngine extends MergingEngine {
	
	/** The Constant DESCRIPTION. */
	private static final String DESCRIPTION = Messages.getString("UniqueEmailEngine.description"); //$NON-NLS-1$
	                                                                                               
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persons.engine.MergingEngine#collides(de .unisaarland.cs.st.mozkito.persistence.model.Person,
	 * org.mozkito.persistence.model.PersonContainer, org.mozkito.persons.processing.PersonManager)
	 */
	@Override
	public List<PersonBucket> collides(final Person person,
	                                   final PersonContainer container,
	                                   final PersonManager manager) {
		final List<PersonBucket> buckets = manager.getBuckets(person);
		final List<PersonBucket> list = new LinkedList<PersonBucket>();
		
		for (final PersonBucket bucket : buckets) {
			for (final String email : person.getEmailAddresses()) {
				if (bucket.hasEmail(email)) {
					list.add(bucket);
				}
			}
		}
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persons.engine.MergingEngine#getDescription ()
	 */
	@Override
	public String getDescription() {
		return UniqueEmailEngine.DESCRIPTION;
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#init()
	 */
	@Override
	public void init() {
		// ignore
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ownhero.dev.hiari.settings.SettingsProvider#provide(net.ownhero.dev.hiari.settings.ArgumentSet)
	 */
	@Override
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException,
	                                                              ArgumentSetRegistrationException,
	                                                              SettingsParseError {
		// PRECONDITIONS
		
		try {
			return root;
		} finally {
			// POSTCONDITIONS
		}
	}
	
}

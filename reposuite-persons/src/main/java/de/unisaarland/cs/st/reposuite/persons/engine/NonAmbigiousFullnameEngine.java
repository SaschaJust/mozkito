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
package de.unisaarland.cs.st.reposuite.persons.engine;

import java.util.LinkedList;
import java.util.List;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer;
import de.unisaarland.cs.st.reposuite.persons.elements.PersonBucket;
import de.unisaarland.cs.st.reposuite.persons.processing.PersonManager;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 *
 */
public class NonAmbigiousFullnameEngine extends MergingEngine {
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persons.engine.MergingEngine#collides(
	 * de.unisaarland.cs.st.reposuite.persistence.model.Person,
	 * de.unisaarland.cs.st.reposuite.persistence.model.PersonContainer,
	 * de.unisaarland.cs.st.reposuite.persons.processing.PersonManager,
	 * java.util.Map)
	 */
	@Override
	public List<PersonBucket> collides(final Person person,
	                                   final PersonContainer container,
	                                   final PersonManager manager) {
		List<PersonBucket> buckets = manager.getBuckets(person);
		List<PersonBucket> list = new LinkedList<PersonBucket>();
		
		for (PersonBucket bucket : buckets) {
			boolean found = false;
			if (!person.getUsernames().isEmpty()) {
				for (String username : person.getUsernames()) {
					if (bucket.hasUsername(username)) {
						found = true;
						list.add(bucket);
						break;
					}
				}
			}
			
			if (!found) {
				if (!person.getEmailAddresses().isEmpty()) {
					for (String email : person.getEmailAddresses()) {
						if (bucket.hasEmail(email)) {
							found = true;
							list.add(bucket);
							break;
						}
					}
				}
			}
			
			if (!found) {
				if (person.getEmailAddresses().isEmpty() && person.getUsernames().isEmpty()) {
					found = true;
					list.add(bucket);
				}
			}
		}
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persons.engine.MergingEngine#getDescription
	 * ()
	 */
	@Override
	public String getDescription() {
		return "Finds collision on non-ambious full names.";
	}
	
}

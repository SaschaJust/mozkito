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

package org.mozkito.persons.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jgravatar.GravatarDownloadException;
import net.ownhero.dev.hiari.settings.ArgumentSet;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.ArgumentSetRegistrationException;
import net.ownhero.dev.hiari.settings.exceptions.SettingsParseError;
import net.ownhero.dev.kanuni.conditions.Condition;
import net.ownhero.dev.kisa.Logger;

import org.mozkito.persistence.model.Person;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.persons.elements.PersonBucket;
import org.mozkito.persons.messages.Messages;
import org.mozkito.persons.processing.PersonManager;

/**
 * The Class GravatarEngine.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class GravatarEngine extends MergingEngine {
	
	/**
	 * The Class Gravatar.
	 */
	private static final class Gravatar {
		
		/**
		 * Gets the.
		 * 
		 * @param email
		 *            the email
		 * @return the gravatar
		 */
		public static Gravatar get(final String email) {
			final jgravatar.Gravatar gravatar = new jgravatar.Gravatar();
			byte[] download = null;
			try {
				download = gravatar.download(email);
			} catch (final GravatarDownloadException e) {
				if (Logger.logWarn()) {
					Logger.warn(e);
				}
			}
			
			if (download != null) {
				return new Gravatar(email, 80, download);
			}
			
			return null;
		}
		
		/** The email. */
		private String email;
		
		/** The size. */
		private int    size;
		
		/** The data. */
		private byte[] data;
		
		/**
		 * Instantiates a new gravatar.
		 * 
		 * @param email
		 *            the email
		 * @param size
		 *            the size
		 * @param data
		 *            the data
		 */
		private Gravatar(final String email, final int size, final byte[] data) {
			// PRECONDITIONS
			
			try {
				this.email = email;
				this.size = size;
				this.data = data;
			} finally {
				// POSTCONDITIONS
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Gravatar other = (Gravatar) obj;
			if (!Arrays.equals(this.data, other.data)) {
				return false;
			}
			return true;
		}
		
		/**
		 * Gets the data.
		 * 
		 * @return the data
		 */
		@SuppressWarnings ("unused")
		public byte[] getData() {
			// PRECONDITIONS
			
			try {
				return this.data;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.data, "Field '%s' in '%s'.", "data", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/**
		 * Gets the email.
		 * 
		 * @return the email
		 */
		@SuppressWarnings ("unused")
		public String getEmail() {
			// PRECONDITIONS
			
			try {
				return this.email;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.email, "Field '%s' in '%s'.", "email", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/**
		 * Gets the size.
		 * 
		 * @return the size
		 */
		@SuppressWarnings ("unused")
		public int getSize() {
			// PRECONDITIONS
			
			try {
				return this.size;
			} finally {
				// POSTCONDITIONS
				Condition.notNull(this.size, "Field '%s' in '%s'.", "size", getClass().getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + Arrays.hashCode(this.data);
			return result;
		}
	}
	
	/** The Constant DESCRIPTION. */
	private static final String         DESCRIPTION          = Messages.getString("GravatarEngine.description"); //$NON-NLS-1$
	                                                                                                             
	/** The existing gravatars. */
	private final Map<String, Gravatar> existingGravatars    = new HashMap<>();
	
	/** The not existing gravatars. */
	private final Set<String>           notExistingGravatars = new HashSet<>();
	
	/**
	 * Instantiates a new gravatar engine.
	 */
	public GravatarEngine() {
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persons.engine.MergingEngine#collides(org.mozkito.persistence.model.Person,
	 * org.mozkito.persistence.model.PersonContainer, org.mozkito.persons.processing.PersonManager)
	 */
	@Override
	public List<PersonBucket> collides(final Person person,
	                                   final PersonContainer container,
	                                   final PersonManager manager) {
		// PRECONDITIONS
		
		try {
			final List<PersonBucket> buckets = manager.getBuckets(person);
			final List<PersonBucket> list = new LinkedList<PersonBucket>();
			
			for (final String email : person.getEmailAddresses()) {
				final Gravatar gravatar = getGravatar(email);
				if (gravatar != null) {
					BUCKETS: for (final PersonBucket bucket : buckets) {
						if (bucket.hasEmail(email)) {
							list.add(bucket);
						} else {
							for (final String bEmail : bucket.getEmails()) {
								final Gravatar bGravatar = getGravatar(bEmail);
								if ((bGravatar != null) && gravatar.equals(bGravatar)) {
									list.add(bucket);
									continue BUCKETS;
								}
							}
						}
					}
				}
			}
			return list;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persons.engine.MergingEngine#getDescription()
	 */
	@Override
	public String getDescription() {
		// PRECONDITIONS
		
		try {
			return DESCRIPTION;
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the gravatar.
	 * 
	 * @param email
	 *            the email
	 * @return the gravatar
	 */
	private Gravatar getGravatar(final String email) {
		if (this.existingGravatars.containsKey(email)) {
			return this.existingGravatars.get(email);
		} else if (this.notExistingGravatars.contains(email)) {
			return null;
		} else {
			final Gravatar gravatar = Gravatar.get(email);
			if (gravatar != null) {
				this.existingGravatars.put(email, gravatar);
			} else {
				this.notExistingGravatars.add(email);
			}
			
			return gravatar;
		}
		
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
	public ArgumentSet<?, ?> provide(final ArgumentSet<?, ?> root) throws ArgumentRegistrationException,
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

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
package org.mozkito.persons.model;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.ownhero.dev.kanuni.annotations.bevahiors.NoneNull;
import net.ownhero.dev.kanuni.annotations.simple.NotEmpty;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;
import net.ownhero.dev.kanuni.annotations.string.Trimmed;
import net.ownhero.dev.kanuni.conditions.Condition;

import org.apache.commons.collections.CollectionUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import org.mozkito.persistence.Annotated;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.utilities.commons.JavaUtils;

/**
 * The Class Person.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@Entity
@Table (name = "person")
public class Person implements Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8598414850294255203L;
	
	/**
	 * Merge.
	 * 
	 * @param keeper
	 *            the keeper
	 * @param collisions
	 *            the collisions
	 * @return the person
	 */
	@NoneNull ("When merging multiple Person entities into one person, neither the target person nor the persons under suspect may be null.")
	public static Person merge(final Person keeper,
	                           @NotEmpty ("Merging with an empty collection makes no sense.") @net.ownhero.dev.kanuni.annotations.simple.NoneNull final Collection<Person> collisions) {
		for (final Person merged : collisions) {
			merge(keeper, merged);
		}
		
		return keeper;
	}
	
	/**
	 * Merge.
	 * 
	 * @param keeper
	 *            the keeper
	 * @param from
	 *            the from
	 * @return the person
	 */
	@NoneNull ("When merging two Person entities, neither target nor merged person may be null.")
	public static Person merge(final Person keeper,
	                           final Person from) {
		keeper.addAllEmails(from.getEmailAddresses());
		keeper.addAllFullnames(from.getFullnames());
		keeper.addAllUsernames(from.getUsernames());
		
		return keeper;
	}
	
	/** The generated id. */
	private long        generatedId;
	
	/** The usernames. */
	private Set<String> usernames      = new TreeSet<String>();
	
	/** The email addresses. */
	private Set<String> emailAddresses = new TreeSet<String>();
	
	/** The fullnames. */
	private Set<String> fullnames      = new TreeSet<String>();
	
	/**
	 * @deprecated must only be used by JPA
	 */
	@Deprecated
	public Person() {
		// stub
	}
	
	/**
	 * Instantiates a new person.
	 * 
	 * @param username
	 *            the username
	 * @param fullname
	 *            the fullname
	 * @param email
	 *            the email
	 * @deprecated Use {@link PersonFactory#get(String, String, String) instead.}
	 */
	@Deprecated
	public Person(@Trimmed final String username, @Trimmed final String fullname, @Trimmed final String email) {
		Condition.check((username != null) || (fullname != null) || (email != null),
		                "Creating a person with only (null) values makes no sense."); //$NON-NLS-1$
		addUsername(username);
		addFullname(fullname);
		addEmail(email);
	}
	
	/**
	 * Adds the all emails.
	 * 
	 * @param emails
	 *            the emails
	 * @return true, if successful
	 */
	@Transient
	public boolean addAllEmails(@NotNull @net.ownhero.dev.kanuni.annotations.simple.NoneNull final Set<String> emails) {
		boolean ret = true;
		final Set<String> backup = getEmailAddresses();
		for (final String email : emails) {
			ret &= addEmail(email);
		}
		if (!ret) {
			setEmailAddresses(backup);
		}
		return ret;
	}
	
	/**
	 * Adds the all fullnames.
	 * 
	 * @param fullnames
	 *            the fullnames
	 * @return true, if successful
	 */
	@Transient
	public boolean addAllFullnames(@NotNull @net.ownhero.dev.kanuni.annotations.simple.NoneNull final Set<String> fullnames) {
		boolean ret = false;
		final Set<String> names = getFullnames();
		ret = names.addAll(fullnames);
		setFullnames(names);
		return ret;
	}
	
	/**
	 * Adds the all usernames.
	 * 
	 * @param usernames
	 *            the usernames
	 * @return true, if successful
	 */
	@Transient
	public boolean addAllUsernames(@NotNull final Set<String> usernames) {
		boolean ret = false;
		final Set<String> names = getUsernames();
		ret = names.addAll(usernames);
		setUsernames(names);
		return ret;
	}
	
	/**
	 * Adds the email.
	 * 
	 * @param email
	 *            the email
	 * @return true, if successful
	 */
	@Transient
	public boolean addEmail(@Trimmed final String email) {
		boolean ret = false;
		if (email != null) {
			final Set<String> addresses = getEmailAddresses();
			ret = addresses.add(email);
			setEmailAddresses(addresses);
		}
		return ret;
	}
	
	/**
	 * Adds the fullname.
	 * 
	 * @param fullname
	 *            the fullname
	 * @return true, if successful
	 */
	@Transient
	public boolean addFullname(final String fullname) {
		boolean ret = false;
		if (fullname != null) {
			final Set<String> names = getFullnames();
			ret = names.add(fullname);
			setFullnames(names);
		}
		return ret;
	}
	
	/**
	 * Adds the username.
	 * 
	 * @param username
	 *            the username
	 * @return true, if successful
	 */
	@Transient
	public boolean addUsername(final String username) {
		boolean ret = false;
		if (username != null) {
			final Set<String> names = getUsernames();
			ret = names.add(username);
			setUsernames(names);
		}
		return ret;
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
		final Person other = (Person) obj;
		if (getEmailAddresses() == null) {
			if (other.getEmailAddresses() != null) {
				return false;
			}
		} else if (!getEmailAddresses().equals(other.getEmailAddresses())) {
			return false;
		}
		if (getFullnames() == null) {
			if (other.getFullnames() != null) {
				return false;
			}
		} else if (!getFullnames().equals(other.getFullnames())) {
			return false;
		}
		if (getUsernames() == null) {
			if (other.getUsernames() != null) {
				return false;
			}
		} else if (!getUsernames().equals(other.getUsernames())) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.mozkito.persistence.Annotated#getHandle()
	 */
	@Override
	@Transient
	public final String getClassName() {
		return JavaUtils.getHandle(Person.class);
	}
	
	/**
	 * Gets the email addresses.
	 * 
	 * @return the email addresses
	 */
	@ElementCollection
	public Set<String> getEmailAddresses() {
		return this.emailAddresses;
	}
	
	/**
	 * Gets the fullnames.
	 * 
	 * @return the name
	 */
	@ElementCollection
	public Set<String> getFullnames() {
		return this.fullnames;
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@Index (name = "idx_personid")
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.AUTO)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the usernames.
	 * 
	 * @return the usernames
	 */
	@ElementCollection
	public Set<String> getUsernames() {
		return this.usernames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((getEmailAddresses() == null)
		                                                          ? 0
		                                                          : getEmailAddresses().hashCode());
		result = (prime * result) + ((getFullnames() == null)
		                                                     ? 0
		                                                     : getFullnames().hashCode());
		result = (prime * result) + ((getUsernames() == null)
		                                                     ? 0
		                                                     : getUsernames().hashCode());
		return result;
	}
	
	/**
	 * Matches.
	 * 
	 * @param person
	 *            the person
	 * @return true, if successful
	 */
	@Transient
	public boolean matches(@NotNull final Person person) {
		if (!CollectionUtils.intersection(getEmailAddresses(), person.getEmailAddresses()).isEmpty()) {
			return true;
		} else if (!CollectionUtils.intersection(getUsernames(), person.getUsernames()).isEmpty()) {
			return true;
		} else {
			if ((getUsernames().isEmpty() && person.getEmailAddresses().isEmpty())
			        || (person.getUsernames().isEmpty() && getEmailAddresses().isEmpty())) {
				return !CollectionUtils.intersection(getFullnames(), person.getFullnames()).isEmpty();
			}
			return false;
		}
	}
	
	/**
	 * Sets the email addresses.
	 * 
	 * @param emailAddresses
	 *            the new email addresses
	 */
	protected void setEmailAddresses(final Set<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	/**
	 * Sets the fullnames.
	 * 
	 * @param fullnames
	 *            the fullnames to set
	 */
	protected void setFullnames(final Set<String> fullnames) {
		this.fullnames = fullnames;
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Sets the usernames.
	 * 
	 * @param usernames
	 *            the new usernames
	 */
	protected void setUsernames(final Set<String> usernames) {
		this.usernames = usernames;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getClassName());
		builder.append(" [generatedId="); //$NON-NLS-1$
		builder.append(getGeneratedId());
		builder.append(", usernames="); //$NON-NLS-1$
		builder.append(getUsernames());
		builder.append(", emailAddresses="); //$NON-NLS-1$
		builder.append(getEmailAddresses());
		builder.append(", fullnames="); //$NON-NLS-1$
		builder.append(getFullnames());
		builder.append(", hashcode="); //$NON-NLS-1$
		builder.append(hashCode());
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
	
}

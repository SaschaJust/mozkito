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
package de.unisaarland.cs.st.moskito.persistence.model;

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

import de.unisaarland.cs.st.moskito.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@Table (name = "person")
public class Person implements Annotated {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8598414850294255203L;
	
	/**
	 * @return the simple class name
	 */
	@Transient
	public static String getHandle() {
		return Person.class.getSimpleName();
	}
	
	/**
	 * @param keeper
	 * @param collisions
	 * @return
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
	 * @param keeper
	 * @param from
	 * @return
	 */
	@NoneNull ("When merging two Person entities, neither target nor merged person may be null.")
	public static Person merge(final Person keeper,
	                           final Person from) {
		keeper.addAllEmails(from.getEmailAddresses());
		keeper.addAllFullnames(from.getFullnames());
		keeper.addAllUsernames(from.getUsernames());
		
		return keeper;
	}
	
	private long        generatedId;
	private Set<String> usernames      = new TreeSet<String>();
	private Set<String> emailAddresses = new TreeSet<String>();
	private Set<String> fullnames      = new TreeSet<String>();
	
	/**
	 * Default constructor used by persistence middleware
	 */
	protected Person() {
	}
	
	/**
	 * @param username
	 * @param fullname
	 * @param email
	 */
	public Person(@Trimmed final String username, @Trimmed final String fullname, @Trimmed final String email) {
		Condition.check((username != null) || (fullname != null) || (email != null),
		                "Creating a person with only (null) values makes no sense.");
		addUsername(username);
		addFullname(fullname);
		addEmail(email);
	}
	
	/**
	 * @param emails
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
	 * @param fullnames
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
	 * @param usernames
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
	 * @param email
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
	 * @param fullname
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
	 * @param username
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
		if (!(obj instanceof Person)) {
			return false;
		}
		final Person other = (Person) obj;
		
		return hashCode() == other.hashCode();
	}
	
	/**
	 * @return
	 */
	@ElementCollection
	public Set<String> getEmailAddresses() {
		return this.emailAddresses;
	}
	
	/**
	 * @return the name
	 */
	@ElementCollection
	public Set<String> getFullnames() {
		return this.fullnames;
	}
	
	/**
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
	 * @return
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
		result = (prime * result) + ((this.emailAddresses == null)
		                                                          ? 0
		                                                          : this.emailAddresses.hashCode());
		result = (prime * result) + ((this.fullnames == null)
		                                                     ? 0
		                                                     : this.fullnames.hashCode());
		result = (prime * result) + ((this.usernames == null)
		                                                     ? 0
		                                                     : this.usernames.hashCode());
		return result;
	}	
	
        /**
	 * @param person
	 * @return
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
	 * @param emailAddresses
	 */
	protected void setEmailAddresses(final Set<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	/**
	 * @param fullnames
	 *            the fullnames to set
	 */
	protected void setFullnames(final Set<String> fullnames) {
		this.fullnames = fullnames;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param usernames
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
		builder.append("Person [generatedId=");
		builder.append(getGeneratedId());
		builder.append(", usernames=");
		builder.append(getUsernames());
		builder.append(", emailAddresses=");
		builder.append(getEmailAddresses());
		builder.append(", fullnames=");
		builder.append(getFullnames());
		builder.append(", hashcode=");
		builder.append(hashCode());
		builder.append("]");
		return builder.toString();
	}
	
}

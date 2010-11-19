/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Table;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.utils.Condition;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
@javax.persistence.Table (name = "person")
@Table (indexes = { @Index (name = "idx", columnNames = { "username", "fullname", "email" }) }, appliesTo = "person")
public class Person implements Annotated {
	
	/**
	 * @return the simple class name
	 */
	@Transient
	public static String getHandle() {
		return Person.class.getSimpleName();
	}
	
	public static Person merge(final Collection<Person> candidates) {
		Person moltenCore;
		String email = null;
		String fullname = null;
		String username = null;
		long id = -1;
		Set<String> synonyms = new TreeSet<String>();
		Set<String> emails = new TreeSet<String>();
		
		for (Person tmpPerson : candidates) {
			if ((email == null) && (tmpPerson.getEmail() != null)) {
				email = tmpPerson.getEmail();
			} else if ((email != null) && (tmpPerson.getEmail() != null)) {
				emails.add(tmpPerson.getEmail());
			}
			
			if ((fullname == null) && (tmpPerson.getFullname() != null)) {
				fullname = tmpPerson.getFullname();
			}
			
			if ((username == null) && (tmpPerson.getUsername() != null)) {
				username = tmpPerson.getUsername();
			} else if ((username != null) && (tmpPerson.getUsername() != null)) {
				synonyms.add(tmpPerson.getUsername());
			}
			
			if ((id == -1) && (tmpPerson.getGeneratedId() > -1)) {
				id = tmpPerson.getGeneratedId();
			}
		}
		
		moltenCore = new Person(username, fullname, email);
		moltenCore.addAllEmails(emails);
		moltenCore.addAllSynonyms(synonyms);
		moltenCore.setGeneratedId(id);
		return moltenCore;
	}
	
	private long                    generatedId;
	private String                  email;
	private String                  fullname;
	private TreeSet<RCSTransaction> transactions   = new TreeSet<RCSTransaction>();
	private Set<String>             synonyms       = new TreeSet<String>();
	private Set<String>             emailAddresses = new TreeSet<String>();         ;
	
	private String                  username;
	
	/**
	 * Default constructor used by Hibernate
	 */
	@SuppressWarnings ("unused")
	private Person() {
	}
	
	/**
	 * @param username
	 * @param fullname
	 * @param email
	 */
	public Person(final String username, final String fullname, final String email) {
		Condition.check((username != null) || (fullname != null) || (email != null));
		if (fullname != null) {
			Condition.equals(fullname.trim(), fullname);
		}
		
		if (username != null) {
			Condition.equals(username, username);
		}
		
		if (email != null) {
			Condition.equals(email, email);
		}
		
		this.username = username != null ? username.trim() : null;
		this.fullname = fullname != null ? fullname.trim() : null;
		this.email = email != null ? email.trim() : null;
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * @param emails
	 */
	public void addAllEmails(final Set<String> emails) {
		this.emailAddresses.addAll(emails);
	}
	
	/**
	 * @param synonyms
	 */
	public void addAllSynonyms(final Set<String> synonyms) {
		synonyms.addAll(synonyms);
	}
	
	/**
	 * @param email
	 */
	@Transient
	public void addEmail(final String email) {
		Condition.notNull(email);
		Condition.equals(email != null ? email.trim() : null, email);
		
		this.getEmailAddresses().add(email);
	}
	
	/**
	 * @param fullname
	 */
	@Transient
	public void addSynonym(final String fullname) {
		Condition.notNull(fullname);
		Condition.equals(fullname != null ? fullname.trim() : null, fullname);
		this.synonyms.add(fullname);
	}
	
	/**
	 * @param transaction
	 */
	@Transient
	public void assignTransaction(final RCSTransaction transaction) {
		Condition.notNull(transaction);
		
		this.transactions.add(transaction);
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
		Person other = (Person) obj;
		if (this.email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!this.email.equals(other.email)) {
			return false;
		}
		if (this.fullname == null) {
			if (other.fullname != null) {
				return false;
			}
		} else if (!this.fullname.equals(other.fullname)) {
			return false;
		}
		if (this.username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!this.username.equals(other.username)) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the email
	 */
	@Column (unique = true)
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * @return
	 */
	@ElementCollection
	public Set<String> getEmailAddresses() {
		return this.emailAddresses;
	}
	
	/**
	 * @return the firstCommit
	 */
	@Transient
	public RCSTransaction getFirstCommit() {
		return this.transactions.first();
	}
	
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return this.fullname;
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.AUTO)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the latestCommit
	 */
	@Transient
	public RCSTransaction getLatestCommit() {
		return this.transactions.last();
	}
	
	@Override
	@Transient
	public Collection<Annotated> getSaveFirst() {
		return null;
	}
	
	@ElementCollection
	public Set<String> getSynonyms() {
		return this.synonyms;
	}
	
	/**
	 * @return the transactions
	 */
	@OneToMany (cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	public Set<RCSTransaction> getTransactions() {
		return this.transactions;
	}
	
	/**
	 * @return the username
	 */
	@Column (unique = true)
	public String getUsername() {
		return this.username;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.email == null) ? 0 : this.email.hashCode());
		result = prime * result + ((this.fullname == null) ? 0 : this.fullname.hashCode());
		result = prime * result + ((this.username == null) ? 0 : this.username.hashCode());
		return result;
	}
	
	/**
	 * @param email
	 *            the email to set
	 */
	@SuppressWarnings ("unused")
	private void setEmail(final String email) {
		this.email = email;
	}
	
	@SuppressWarnings ("unused")
	private void setEmailAddresses(final Set<String> emailAddresses) {
		this.emailAddresses = emailAddresses;
	}
	
	/**
	 * @param fullname
	 *            the fullname to set
	 */
	@SuppressWarnings ("unused")
	private void setFullname(final String fullname) {
		this.fullname = fullname;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	protected void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	@SuppressWarnings ("unused")
	private void setSynonyms(final Set<String> synonyms) {
		this.synonyms = synonyms;
	}
	
	/**
	 * @param transactions
	 */
	@SuppressWarnings ("unused")
	private void setTransactions(final Set<RCSTransaction> transactions) {
		this.transactions = new TreeSet<RCSTransaction>(transactions);
	}
	
	/**
	 * @param username
	 *            the username to set
	 */
	@SuppressWarnings ("unused")
	private void setUsername(final String username) {
		this.username = username;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Person [username=" + this.username + ", fullname=" + this.fullname + ", email=" + this.email
		        + ", synonyms=" + JavaUtils.collectionToString(this.synonyms) + ", emailAddresses="
		        + JavaUtils.collectionToString(this.getEmailAddresses()) + "]";
	}
	
}

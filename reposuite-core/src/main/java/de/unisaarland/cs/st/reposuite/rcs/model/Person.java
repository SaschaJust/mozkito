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
		Set<String> names = new TreeSet<String>();
		
		for (Person tmpPerson : candidates) {
			if (tmpPerson.getEmail() != null) {
				if (email == null) {
					email = tmpPerson.getEmail();
				}
				emails.add(tmpPerson.getEmail());
			}
			
			emails.addAll(tmpPerson.getEmailAddresses());
			
			if (tmpPerson.getFullname() != null) {
				if ((fullname == null)) {
					fullname = tmpPerson.getFullname();
				}
				names.add(tmpPerson.getFullname());
			}
			
			names.addAll(tmpPerson.getNames());
			
			if (tmpPerson.getUsername() != null) {
				if (username == null) {
					username = tmpPerson.getUsername();
				}
				synonyms.add(tmpPerson.getUsername());
			}
			
			synonyms.addAll(tmpPerson.getSynonyms());
			
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
	private Set<String>             emailAddresses = new TreeSet<String>();
	private Set<String>             names          = new TreeSet<String>();
	
	private String                  username;
	
	/**
	 * Default constructor used by Hibernate
	 */
	protected Person() {
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
			Condition.equals(username.trim(), username);
		}
		
		if (email != null) {
			Condition.equals(email.trim(), email);
		}
		
		if (username != null) {
			setUsername(username.trim());
		}
		
		if (fullname != null) {
			setFullname(fullname.trim());
			getSynonyms().add(getFullname());
		}
		
		if (email != null) {
			setEmail(email.trim());
			getEmailAddresses().add(getEmail());
		}
		
		if (Logger.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * @param emails
	 */
	public void addAllEmails(final Set<String> emails) {
		Condition.notNull(emails);
		
		this.emailAddresses.addAll(emails);
	}
	
	public void addAllNames(final Set<String> names) {
		Condition.notNull(names);
		
		getNames().addAll(names);
	}
	
	/**
	 * @param synonyms
	 */
	public void addAllSynonyms(final Set<String> synonyms) {
		Condition.notNull(synonyms);
		
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
	public void addName(final String fullname) {
		Condition.notNull(fullname);
		
		this.names.add(fullname);
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
	private long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the latestCommit
	 */
	@Transient
	public RCSTransaction getLatestCommit() {
		return this.transactions.last();
	}
	
	/**
	 * @return the name
	 */
	@ElementCollection
	public Set<String> getNames() {
		return this.names;
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
	
	@Override
	@Transient
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param email
	 *            the email to set
	 */
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
	
	/**
	 * @param names
	 *            the names to set
	 */
	protected void setNames(final Set<String> names) {
		this.names = names;
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
	 * @param transactions
	 *            the transactions to set
	 */
	protected void setTransactions(final TreeSet<RCSTransaction> transactions) {
		this.transactions = transactions;
	}
	
	/**
	 * @param username
	 *            the username to set
	 */
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

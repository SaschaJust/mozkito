/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Set;
import java.util.TreeSet;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.settings.RepoSuiteSettings;
import de.unisaarland.cs.st.reposuite.utils.Logger;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Person implements Annotated {
	
	private String                  username;
	private String                  fullname;
	private String                  email;
	private TreeSet<RCSTransaction> transactions = new TreeSet<RCSTransaction>();
	
	/**
	 * @param username
	 * @param fullname
	 * @param email
	 */
	public Person(final String username, final String fullname, final String email) {
		assert ((username != null) || (fullname != null) || (email != null));
		
		this.username = username;
		this.fullname = fullname;
		this.email = email;
		
		if (RepoSuiteSettings.logTrace()) {
			Logger.trace("Creating " + getHandle() + ": " + this);
		}
	}
	
	/**
	 * @param transaction
	 */
	public void assignTransaction(final RCSTransaction transaction) {
		assert (transaction != null);
		
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
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * @return the firstCommit
	 */
	public RCSTransaction getFirstCommit() {
		return this.transactions.first();
	}
	
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return this.fullname;
	}
	
	String getHandle() {
		return Person.class.getSimpleName();
	}
	
	/**
	 * @return the latestCommit
	 */
	public RCSTransaction getLatestCommit() {
		return this.transactions.last();
	}
	
	/**
	 * @return the transactions
	 */
	public TreeSet<RCSTransaction> getTransactions() {
		return this.transactions;
	}
	
	/**
	 * @return the username
	 */
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
	
	/**
	 * @param fullname
	 *            the fullname to set
	 */
	@SuppressWarnings ("unused")
	private void setFullname(final String fullname) {
		this.fullname = fullname;
	}
	
	/**
	 * @param transactions
	 */
	@SuppressWarnings ("unused")
	private void setTransaction(final Set<RCSTransaction> transactions) {
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
		return "Person [username=" + this.username + ", fullname=" + this.fullname + ", email=" + this.email + "]";
	}
	
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Set;
import java.util.TreeSet;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

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
		this.username = username;
		this.fullname = fullname;
		this.email = email;
	}
	
	/**
	 * @param transaction
	 */
	public void assignTransaction(final RCSTransaction transaction) {
		assert (transaction != null);
		
		this.transactions.add(transaction);
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

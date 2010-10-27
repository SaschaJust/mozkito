/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
public class Person implements Annotated {
	
	private String username;
	
	private String fullname;
	
	private String email;
	
	/**
	 * @param username
	 * @param fullname
	 * @param email
	 */
	public Person(String username, String fullname, String email) {
		this.username = username;
		this.fullname = fullname;
		this.email = email;
	}
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}
	
	/**
	 * @return the fullname
	 */
	public String getFullname() {
		return this.fullname;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @param firstPerson
	 * @param secondPerson
	 * @return
	 */
	public Person merge(Person firstPerson, Person secondPerson) {
		assert ((firstPerson.username == null) || (secondPerson.username == null) || firstPerson.username
		        .equals(secondPerson.username));
		assert ((firstPerson.fullname == null) || (secondPerson.fullname == null) || firstPerson.fullname
		        .equals(secondPerson.fullname));
		assert ((firstPerson.email == null) || (secondPerson.email == null) || firstPerson.email
		        .equals(secondPerson.email));
		
		return new Person((firstPerson.username != null ? firstPerson.username : secondPerson.username),
		        (firstPerson.fullname != null ? firstPerson.fullname : secondPerson.fullname),
		        (firstPerson.email != null ? firstPerson.email : secondPerson.email));
	}
	
	/**
	 * @param email
	 *            the email to set
	 */
	@SuppressWarnings("unused")
	private void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * @param fullname
	 *            the fullname to set
	 */
	@SuppressWarnings("unused")
	private void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
	/**
	 * @param username
	 *            the username to set
	 */
	@SuppressWarnings("unused")
	private void setUsername(String username) {
		this.username = username;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Person [username=" + this.username + ", fullname=" + this.fullname + ", email=" + this.email + "]";
	}
	
}

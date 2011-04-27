package de.unisaarland.cs.st.reposuite.bugs.tracker.google;

import de.unisaarland.cs.st.reposuite.persistence.model.Person;
import de.unisaarland.cs.st.reposuite.utils.Storable;

public class GooglePerson implements Storable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final String      email;
	private final String      username;
	private final String      name;
	
	private boolean           cached;
	
	private String            filename;
	
	public GooglePerson(final String email, final String username, final String name) {
		this.email = email;
		this.username = username;
		this.name = name;
	}
	
	@Override
	public boolean cached() {
		return this.cached;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	@Override
	public String getFilename() {
		return this.filename;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	@Override
	public void setCached(final String filename) {
		this.filename = filename;
		this.cached = true;
	}
	
	public Person toPerson() {
		return new Person(getUsername(), getName(), getEmail());
	}
	
}

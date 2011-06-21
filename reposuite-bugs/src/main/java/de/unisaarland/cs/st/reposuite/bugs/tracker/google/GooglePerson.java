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
package de.unisaarland.cs.st.reposuite.bugs.tracker.google;

import net.ownhero.dev.ioda.interfaces.Storable;
import de.unisaarland.cs.st.reposuite.persistence.model.Person;

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

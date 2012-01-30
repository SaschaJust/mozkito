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
package de.unisaarland.cs.st.moskito.bugs.tracker.google;

import net.ownhero.dev.ioda.interfaces.Storable;
import de.unisaarland.cs.st.moskito.persistence.model.Person;

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

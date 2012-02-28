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
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.ownhero.dev.ioda.JavaUtils;
import net.ownhero.dev.kanuni.annotations.simple.NotNull;

import org.apache.openjpa.persistence.jdbc.Index;

import de.unisaarland.cs.st.moskito.persistence.Annotated;
import de.unisaarland.cs.st.moskito.persistence.Intercepted;

/**
 * The Class PersonContainer.
 * 
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 */
@Entity
public class PersonContainer implements Intercepted<Person>, Annotated {
	
	/** The Constant serialVersionUID. */
	private static final long   serialVersionUID = -5061178255449904475L;
	
	/** The map. */
	private Map<String, Person> map              = new HashMap<String, Person>();
	
	/** The generated id. */
	private long                generatedId;
	
	/**
	 * Instantiates a new person container.
	 */
	public PersonContainer() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.Intercepted#add(java.lang. String, java.lang.Object)
	 */
	@Override
	@Transient
	public Person add(@NotNull final String id,
	                  @NotNull final Person person) {
		final Map<String, Person> map = getMap();
		final Person ret = map.put(id.toLowerCase(), person);
		setMap(map);
		return ret;
	}
	
	/**
	 * Contains.
	 * 
	 * @param key
	 *            the key
	 * @return true, if successful
	 */
	@Transient
	public boolean contains(final String key) {
		return getMap().containsKey(key.toLowerCase());
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.Intercepted#get(java.lang. String)
	 */
	@Override
	@Transient
	public Person get(final String id) {
		return getMap().get(id.toLowerCase());
	}
	
	/**
	 * Gets the generated id.
	 * 
	 * @return the generatedId
	 */
	@Id
	@Index (name = "idx_id")
	@Column (name = "id")
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * Gets the map.
	 * 
	 * @return the map
	 */
	@OneToMany (cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH }, fetch = FetchType.LAZY)
	protected Map<String, Person> getMap() {
		return this.map;
	}
	
	/**
	 * Gets the persons.
	 * 
	 * @return the persons
	 */
	@Transient
	public Collection<Person> getPersons() {
		return getMap().values();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.Intercepted#interceptorTargets ()
	 */
	@Override
	public Collection<Person> interceptorTargets() {
		return getMap().values();
	}
	
	/**
	 * Checks if is empty.
	 * 
	 * @return true, if is empty
	 */
	@Transient
	public boolean isEmpty() {
		return getMap().isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.moskito.persistence.Intercepted#replace(java.lang .Object, java.lang.Object)
	 */
	@Override
	public void replace(final Person from,
	                    final Person to) {
		for (final String key : getMap().keySet()) {
			if (getMap().get(key).getGeneratedId() == from.getGeneratedId()) {
				getMap().put(key, to);
			}
		}
	}
	
	/**
	 * Sets the generated id.
	 * 
	 * @param generatedId
	 *            the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * Sets the map.
	 * 
	 * @param map
	 *            the map to set
	 */
	protected void setMap(final Map<String, Person> map) {
		this.map = map;
	}
	
	/**
	 * Size.
	 * 
	 * @return the int
	 */
	@Transient
	public int size() {
		return getMap().size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("PersonContainer [generatedId=");
		builder.append(getGeneratedId());
		builder.append(", map=");
		builder.append(JavaUtils.mapToString(getMap()));
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Update.
	 * 
	 * @param reference
	 *            the reference
	 */
	public void update(final Person reference) {
		for (final String key : getMap().keySet()) {
			if (getMap().get(key).matches(reference)) {
				getMap().put(key, reference);
			}
		}
	}
}

/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import de.unisaarland.cs.st.reposuite.persistence.Annotated;
import de.unisaarland.cs.st.reposuite.persistence.Intercepted;
import de.unisaarland.cs.st.reposuite.utils.JavaUtils;

/**
 * @author Sascha Just <sascha.just@st.cs.uni-saarland.de>
 * 
 */
@Entity
public class PersonContainer implements Intercepted<Person>, Annotated {
	
	/**
	 * 
	 */
	private static final long   serialVersionUID = -5061178255449904475L;
	private Map<String, Person> map              = new HashMap<String, Person>();
	private long                generatedId;
	
	/**
	 * 
	 */
	public PersonContainer() {
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.Intercepted#add(java.lang.
	 * String, java.lang.Object)
	 */
	@Override
	@Transient
	public void add(final String id, final Person person) {
		this.getMap().put(id, person);
	}
	
	/**
	 * @param key
	 * @return
	 */
	@Transient
	public boolean contains(final String key) {
		return this.map.containsKey(key);
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
		if (!(obj instanceof PersonContainer)) {
			return false;
		}
		PersonContainer other = (PersonContainer) obj;
		if (this.map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!this.map.equals(other.map)) {
			return false;
		}
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.Intercepted#get(java.lang.
	 * String)
	 */
	@Override
	@Transient
	public Person get(final String id) {
		return this.getMap().get(id);
	}
	
	/**
	 * @return the generatedId
	 */
	@Id
	@GeneratedValue (strategy = GenerationType.SEQUENCE)
	public long getGeneratedId() {
		return this.generatedId;
	}
	
	/**
	 * @return the map
	 */
	@ManyToMany (cascade = CascadeType.ALL)
	private Map<String, Person> getMap() {
		return this.map;
	}
	
	/**
	 * @return
	 */
	@Transient
	public Collection<Person> getPersons() {
		return this.map.values();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.map == null) ? 0 : this.map.hashCode());
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.Intercepted#interceptorTargets
	 * ()
	 */
	@Override
	public Collection<Person> interceptorTargets() {
		return this.getMap().values();
	}
	
	/**
	 * @return
	 */
	@Transient
	public boolean isEmpty() {
		return this.map.isEmpty();
	}
	
	/*
	 * (non-Javadoc)
	 * @see
	 * de.unisaarland.cs.st.reposuite.persistence.Intercepted#replace(java.lang
	 * .Object, java.lang.Object)
	 */
	@Override
	public void replace(final Person from, final Person to) {
		for (String key : this.map.keySet()) {
			if (key.equals(from)) {
				getMap().put(key, to);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.unisaarland.cs.st.reposuite.persistence.Annotated#saveFirst()
	 */
	@Override
	public Collection<Annotated> saveFirst() {
		return null;
	}
	
	/**
	 * @param generatedId
	 *            the generatedId to set
	 */
	public void setGeneratedId(final long generatedId) {
		this.generatedId = generatedId;
	}
	
	/**
	 * @param map
	 *            the map to set
	 */
	@SuppressWarnings ("unused")
	private void setMap(final Map<String, Person> map) {
		this.map = map;
	}
	
	/**
	 * @return
	 */
	@Transient
	public int size() {
		return this.map.size();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PersonContainer [map=");
		builder.append(JavaUtils.mapToString(this.map));
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * @param reference
	 */
	public void update(final Person reference) {
		for (String key : this.map.keySet()) {
			if (this.map.get(key).matches(reference)) {
				this.map.put(key, reference);
			}
		}
	}
}

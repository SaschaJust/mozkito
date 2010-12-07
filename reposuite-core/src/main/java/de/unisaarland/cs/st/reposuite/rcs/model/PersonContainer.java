/**
 * 
 */
package de.unisaarland.cs.st.reposuite.rcs.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

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
	public void add(final String id,
	                final Person person) {
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
	@Index (name = "idx_id")
	@Column (name = "id")
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
	public void replace(final Person from,
	                    final Person to) {
		for (String key : this.map.keySet()) {
			if (getMap().get(key).equals(from)) {
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
		builder.append("PersonContainer [generatedId=");
		builder.append(this.generatedId);
		builder.append(", map=");
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

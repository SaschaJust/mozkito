/***********************************************************************************************************************
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
 **********************************************************************************************************************/

package org.mozkito.mappings.test.elements;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.persistence.Transient;

import org.mozkito.mappings.engines.AuthorEqualityEngineTest;
import org.mozkito.mappings.mappable.FieldKey;
import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persons.elements.PersonFactory;
import org.mozkito.persons.model.Person;

/**
 * The Class Dummy.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class Dummy extends MappableEntity {
	
	/** The Constant serialVersionUID. */
	private static final long                serialVersionUID = 6342705499695512947L;
	
	/** The properties. */
	private final Properties                 properties       = new Properties();
	
	private static final Map<String, Person> persons          = new HashMap<>();
	
	private MappableEntity                   entity           = null;
	
	private final PersonFactory              personFactory    = new PersonFactory();
	
	/**
	 * Instantiates a new dummy.
	 * 
	 * @param resource
	 *            the resource
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Dummy(final String resource) throws IOException {
		final String resourceHandle = "/" + AuthorEqualityEngineTest.class.getPackage().getName().replace('.', '/')
		        + "/" + resource + ".dmy";
		final InputStream stream = Dummy.class.getResourceAsStream(resourceHandle);
		
		if (stream != null) {
			this.properties.load(stream);
		} else {
			throw new IOException("Could not find resource: " + resourceHandle);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#get(org.mozkito.mappings.mappable.FieldKey)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	@Transient
	public <T> T get(final FieldKey key) {
		// PRECONDITIONS
		
		try {
			final String value = this.properties.getProperty("field." + key.name().toLowerCase());
			if (value != null) {
				
				switch (key) {
					case AUTHOR:
						final String[] entries = value.split(",");
						assert entries.length == 3;
						if (!persons.containsKey(entries[0])) {
							persons.put(entries[0], this.personFactory.get(entries[0], entries[2], entries[1]));
						}
						
						return (T) persons.get(entries[0]);
					default:
						return (T) value;
						
				}
			} else {
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#get(org.mozkito.mappings.mappable.FieldKey, int)
	 */
	@SuppressWarnings ("unchecked")
	@Override
	@Transient
	public <T> T get(final FieldKey key,
	                 final int index) {
		// PRECONDITIONS
		
		try {
			return (T) this.properties.get("field." + key.name().toLowerCase() + "." + index);
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#getBaseType()
	 */
	@Override
	@Transient
	public Class<?> getBaseType() {
		// PRECONDITIONS
		
		try {
			try {
				return Class.forName((String) this.properties.get("base"));
			} catch (final ClassNotFoundException e) {
				return null;
			}
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * Gets the entity.
	 * 
	 * @return the entity
	 */
	private MappableEntity getEntity() {
		if (this.entity == null) {
			try {
				final String className = MappableEntity.class.getPackage().getName() + ".Mappable"
				        + getBaseType().getSimpleName();
				@SuppressWarnings ("unchecked")
				final Class<? extends MappableEntity> clazz = (Class<? extends MappableEntity>) Class.forName(className);
				this.entity = clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				fail(e.getMessage());
			}
		}
		
		return this.entity;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#getId()
	 */
	@Override
	@Transient
	public String getId() {
		// PRECONDITIONS
		
		try {
			return (String) this.properties.get("id");
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#getText()
	 */
	@Override
	@Transient
	public String getText() {
		// PRECONDITIONS
		
		try {
			return this.properties.getProperty("text");
		} finally {
			// POSTCONDITIONS
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.mappings.mappable.model.MappableEntity#supported()
	 */
	@Override
	@Transient
	public Set<FieldKey> supported() {
		// PRECONDITIONS
		
		try {
			
			return getEntity().supported();
			
		} finally {
			// POSTCONDITIONS
		}
	}
}

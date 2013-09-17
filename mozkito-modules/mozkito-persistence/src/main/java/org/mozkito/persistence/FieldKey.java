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
package org.mozkito.persistence;

import org.joda.time.DateTime;

/**
 * The Enum FieldKey.
 */
public enum FieldKey {
	
	/**
	 * the author of the {@link Entity}, i.e. the one that initially created the original instance encapsulated by this
	 * entity. (Person)
	 */
	AUTHOR,
	
	/** the body text of the {@link Entity} (String). */
	BODY,
	/**
	 * the timestamp of the {@link Entity} that determines when the original entity has been closed/committed/became
	 * immutable/sent. (DateTime)
	 */
	CLOSED_TIMESTAMP,
	/** the person that closed/committed/sent the original entity. */
	CLOSER,
	
	/**
	 * the timestamp of the {@link Entity} that represents the point in time the original instance has been created.
	 * (DateTime)
	 */
	CREATION_TIMESTAMP,
	
	/** the id of the Entity (String). */
	ID,
	/**
	 * the timestamp(s) of the {@link Entity} that determines the time the original entity has been changed (null if
	 * never modified). If requested without index, represents a collection of timestamps for all modifications, a
	 * specific timestamp otherwise. ( <code>Collection<DateTime>/DateTime</code>)
	 */
	MODIFICATION_TIMESTAMP,
	
	/** The RESOLUTIO n_ timestamp. */
	RESOLUTION_TIMESTAMP,
	
	/** The SUMMARY. */
	SUMMARY,
	
	/** The TYPE. */
	TYPE,
	
	/** The DESCRIPTION. */
	DESCRIPTION;
	
	/**
	 * Result type.
	 * 
	 * @return the class
	 */
	public Class<?> resultType() {
		switch (this) {
			case AUTHOR:
			case CLOSER:
				return null;
			case BODY:
			case ID:
			case DESCRIPTION:
			case SUMMARY:
			case TYPE:
				return String.class;
			case CLOSED_TIMESTAMP:
			case CREATION_TIMESTAMP:
			case MODIFICATION_TIMESTAMP:
			case RESOLUTION_TIMESTAMP:
				return DateTime.class;
			default:
				throw new IllegalStateException(name());
		}
	}
}

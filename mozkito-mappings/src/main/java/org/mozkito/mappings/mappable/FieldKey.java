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
package org.mozkito.mappings.mappable;

import java.util.Collection;

import org.mozkito.mappings.mappable.model.MappableEntity;
import org.mozkito.persistence.model.Person;

/**
 * The Enum FieldKey.
 */
public enum FieldKey {
	
	/**
	 * the author of the {@link MappableEntity}, i.e. the one that initially created the original instance encapsulated
	 * by this entity. (Person)
	 */
	AUTHOR,
	
	/** the body text of the {@link MappableEntity} (String). */
	BODY,
	/**
	 * the person(s) that have done modifications to the original entity. If requested without index, represents a
	 * {@link Collection} of {@link Person}s, a specific {@link Person} otherwise. Represents an empty
	 * {@link Collection}/ <code>null</code> if there weren't any changes at all, respectively. (
	 * <code>Collection<Person>/Person</code>)
	 */
	CHANGER,
	/**
	 * the timestamp of the {@link MappableEntity} that determines when the original entity has been
	 * closed/committed/became immutable/sent. (DateTime)
	 */
	CLOSED_TIMESTAMP,
	/** the person that closed/committed/sent the original entity. */
	CLOSER,
	/**
	 * the comment(s)/reply(replies) to an encapsulated entity. If requested without index, represents a
	 * {@link Collection} of corresponding data, a single data object otherwise. Represents an empty {@link Collection}/
	 * <code>null</code> if there weren't any comments/replies at all, respectively. (
	 * <code>Collection<Comment>/Comment</code>)
	 */
	COMMENT,
	/**
	 * the timestamp of the {@link MappableEntity} that represents the point in time the original instance has been
	 * created. (DateTime)
	 */
	CREATION_TIMESTAMP,
	
	/** The FILE. */
	FILE,
	
	/** the id of the MappableEntity (String). */
	ID,
	/**
	 * the timestamp(s) of the {@link MappableEntity} that determines the time the original entity has been changed
	 * (null if never modified). If requested without index, represents a collection of timestamps for all
	 * modifications, a specific timestamp otherwise. ( <code>Collection<DateTime>/DateTime</code>)
	 */
	MODIFICATION_TIMESTAMP,
	
	/** The PATH. @see FieldKey#FILE */
	PATH,
	
	/** The RESOLUTIO n_ timestamp. */
	RESOLUTION_TIMESTAMP,
	
	/** The SUMMARY. */
	SUMMARY,
	
	/** The TYPE. */
	TYPE,
	
	/** The DESCRIPTION */
	DESCRIPTION;
}

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

import java.util.Collection;

/**
 * The Enum IteratableFieldKey.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public enum IterableFieldKey {
	
	/** The comments. */
	COMMENTS,
	/** The files. */
	FILES,
	/** The involved. */
	INVOLVED,
	/**
	 * the person(s) that have done modifications to the original entity. If requested without index, represents a
	 * {@link Collection} of Persons, a specific Person otherwise. Represents an empty {@link Collection}/
	 * <code>null</code> if there weren't any changes at all, respectively. ( <code>Collection<Person>/Person</code>)
	 */
	CHANGER;
	
}

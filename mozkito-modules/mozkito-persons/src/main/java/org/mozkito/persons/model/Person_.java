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

package org.mozkito.persons.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.persons.elements.PersonType;

/**
 * The Class Person_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.persons.model.Person.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Sun Aug 18 20:23:19 CEST 2013")
public class Person_ {
	
	/** The affiliation. */
	public static volatile SetAttribute<Person, String>     affiliation;
	
	/** The email addresses. */
	public static volatile SetAttribute<Person, String>     emailAddresses;
	
	/** The fullnames. */
	public static volatile SetAttribute<Person, String>     fullnames;
	
	/** The generated id. */
	public static volatile SingularAttribute<Person, Long>  generatedId;
	
	/** The location. */
	public static volatile SetAttribute<Person, String>     location;
	
	/** The type. */
	public static volatile SetAttribute<Person, PersonType> type;
	
	/** The usernames. */
	public static volatile SetAttribute<Person, String>     usernames;
}

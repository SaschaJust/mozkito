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

package org.mozkito.persistence.model;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class PersonContainer_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.persistence.model.PersonContainer.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon Jan 07 15:24:20 CET 2013")
public class PersonContainer_ {
	
	/** The generated id. */
	public static volatile SingularAttribute<PersonContainer, Long>      generatedId;
	
	/** The map. */
	public static volatile MapAttribute<PersonContainer, String, Person> map;
}

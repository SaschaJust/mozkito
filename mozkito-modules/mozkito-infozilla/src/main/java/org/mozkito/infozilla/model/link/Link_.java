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

package org.mozkito.infozilla.model.link;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.link.Link.Kind;
import org.mozkito.persons.model.Person;

/**
 * The Class Link_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.link.Link.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 05:19:30 CEST 2013")
public class Link_ {
	
	/** The end position. */
	public static volatile SingularAttribute<Link, Integer>    endPosition;
	
	/** The id. */
	public static volatile SingularAttribute<Link, Integer>    id;
	
	/** The kind. */
	public static volatile SingularAttribute<Link, Kind>       kind;
	
	/** The link description. */
	public static volatile SingularAttribute<Link, String>     linkDescription;
	
	/** The origin. */
	public static volatile SingularAttribute<Link, Attachment> origin;
	
	/** The posted by. */
	public static volatile SingularAttribute<Link, Person>     postedBy;
	
	/** The posted on java. */
	public static volatile SingularAttribute<Link, Date>       postedOnJava;
	
	/** The scheme. */
	public static volatile SingularAttribute<Link, String>     scheme;
	
	/** The start position. */
	public static volatile SingularAttribute<Link, Integer>    startPosition;
	
	/** The string representation. */
	public static volatile SingularAttribute<Link, String>     stringRepresentation;
	
	/** The verified. */
	public static volatile SingularAttribute<Link, Boolean>    verified;
}

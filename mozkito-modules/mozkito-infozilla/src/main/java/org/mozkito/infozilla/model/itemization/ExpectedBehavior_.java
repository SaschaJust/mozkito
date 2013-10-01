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

package org.mozkito.infozilla.model.itemization;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persons.model.Person;

/**
 * The Class ExpectedBehavior_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.itemization.ExpectedBehavior.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 14:05:14 CEST 2013")
public class ExpectedBehavior_ {
	
	/** The end position. */
	public static volatile SingularAttribute<ExpectedBehavior, Integer>    endPosition;
	
	/** The id. */
	public static volatile SingularAttribute<ExpectedBehavior, Integer>    id;
	
	/** The java posted on. */
	public static volatile SingularAttribute<ExpectedBehavior, Date>       javaPostedOn;
	
	/** The origin. */
	public static volatile SingularAttribute<ExpectedBehavior, Attachment> origin;
	
	/** The posted by. */
	public static volatile SingularAttribute<ExpectedBehavior, Person>     postedBy;
	
	/** The start position. */
	public static volatile SingularAttribute<ExpectedBehavior, Integer>    startPosition;
	
	/** The text. */
	public static volatile SingularAttribute<ExpectedBehavior, String>     text;
}

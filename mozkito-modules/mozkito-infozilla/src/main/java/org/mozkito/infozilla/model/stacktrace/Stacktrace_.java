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

package org.mozkito.infozilla.model.stacktrace;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persons.model.Person;

/**
 * The Class Stacktrace_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.stacktrace.Stacktrace.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 05:19:30 CEST 2013")
public class Stacktrace_ {
	
	/** The cause. */
	public static volatile SingularAttribute<Stacktrace, org.mozkito.infozilla.model.stacktrace.Stacktrace> cause;
	
	/** The end position. */
	public static volatile SingularAttribute<Stacktrace, Integer>                                           endPosition;
	
	/** The exception type. */
	public static volatile SingularAttribute<Stacktrace, String>                                            exceptionType;
	
	/** The id. */
	public static volatile SingularAttribute<Stacktrace, Integer>                                           id;
	
	/** The java posted on. */
	public static volatile SingularAttribute<Stacktrace, Date>                                              javaPostedOn;
	
	/** The more. */
	public static volatile SingularAttribute<Stacktrace, Integer>                                           more;
	
	/** The origin. */
	public static volatile SingularAttribute<Stacktrace, Attachment>                                        origin;
	
	/** The posted by. */
	public static volatile SingularAttribute<Stacktrace, Person>                                            postedBy;
	
	/** The reason. */
	public static volatile SingularAttribute<Stacktrace, String>                                            reason;
	
	/** The start position. */
	public static volatile SingularAttribute<Stacktrace, Integer>                                           startPosition;
	
	/** The trace. */
	public static volatile ListAttribute<Stacktrace, StacktraceEntry>                                       trace;
}

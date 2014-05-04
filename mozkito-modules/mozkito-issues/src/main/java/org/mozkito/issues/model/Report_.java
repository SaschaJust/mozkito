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

package org.mozkito.issues.model;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.issues.elements.Priority;
import org.mozkito.issues.elements.Resolution;
import org.mozkito.issues.elements.Severity;
import org.mozkito.issues.elements.Status;
import org.mozkito.issues.elements.Type;
import org.mozkito.persons.model.Person;

/**
 * The Class Report_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.model.Report.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Apr 26 13:58:11 CEST 2013")
public class Report_ {
	
	/** The person container. */
	public static volatile SingularAttribute<Report, Person>       assignedPerson;
	
	/** The attachment entries. */
	public static volatile ListAttribute<Report, AttachmentEntry>  attachmentEntries;
	
	/** The category. */
	public static volatile SingularAttribute<Report, String>       category;
	
	/** The closing person. */
	public static volatile SingularAttribute<Report, Person>       closingPerson;
	
	/** The comments. */
	public static volatile SetAttribute<Report, Comment>           comments;
	
	/** The component. */
	public static volatile SingularAttribute<Report, String>       component;
	
	/** The creation java timestamp. */
	public static volatile SingularAttribute<Report, Date>         creationJavaTimestamp;
	
	/** The description. */
	public static volatile SingularAttribute<Report, String>       description;
	
	/** The hash. */
	public static volatile SingularAttribute<Report, byte[]>       hash;
	
	/** The history. */
	public static volatile SingularAttribute<Report, History>      history;
	
	/** The id. */
	public static volatile SingularAttribute<Report, String>       id;
	
	/** The keywords. */
	public static volatile SetAttribute<Report, String>            keywords;
	
	/** The last fetch java. */
	public static volatile SingularAttribute<Report, Date>         lastFetchJava;
	
	/** The last update java timestamp. */
	public static volatile SingularAttribute<Report, Date>         lastUpdateJavaTimestamp;
	
	/** The priority. */
	public static volatile SingularAttribute<Report, Priority>     priority;
	
	/** The product. */
	public static volatile SingularAttribute<Report, String>       product;
	
	/** The resolution. */
	public static volatile SingularAttribute<Report, Resolution>   resolution;
	
	/** The resolution java timestamp. */
	public static volatile SingularAttribute<Report, Date>         resolutionJavaTimestamp;
	
	/** The resolving person. */
	public static volatile SingularAttribute<Report, Person>       resolvingPerson;
	
	/** The scm fix version. */
	public static volatile SingularAttribute<Report, String>       scmFixVersion;
	
	/** The severity. */
	public static volatile SingularAttribute<Report, Severity>     severity;
	
	/** The siblings. */
	public static volatile SetAttribute<Report, String>            siblings;
	
	/** The status. */
	public static volatile SingularAttribute<Report, Status>       status;
	
	/** The subject. */
	public static volatile SingularAttribute<Report, String>       subject;
	
	/** The submitting person. */
	public static volatile SingularAttribute<Report, Person>       submittingPerson;
	
	/** The summary. */
	public static volatile SingularAttribute<Report, String>       summary;
	
	/** The tracker. */
	public static volatile SingularAttribute<Report, IssueTracker> tracker;
	
	/** The type. */
	public static volatile SingularAttribute<Report, Type>         type;
	
	/** The version. */
	public static volatile SingularAttribute<Report, String>       version;
}

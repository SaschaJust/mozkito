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

package org.mozkito.infozilla.model.patch;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.persons.model.Person;

/**
 * The Class Patch_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.patch.Patch.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 05:19:30 CEST 2013")
public class Patch_ {
	
	/** The end position. */
	public static volatile SingularAttribute<Patch, Integer>    endPosition;
	
	/** The hunks. */
	public static volatile ListAttribute<Patch, PatchHunk>      hunks;
	
	/** The id. */
	public static volatile SingularAttribute<Patch, Integer>    id;
	
	/** The index. */
	public static volatile SingularAttribute<Patch, String>     index;
	
	/** The java posted on. */
	public static volatile SingularAttribute<Patch, Date>       javaPostedOn;
	
	/** The modified file. */
	public static volatile SingularAttribute<Patch, String>     modifiedFile;
	
	/** The new java timestamp. */
	public static volatile SingularAttribute<Patch, Date>       newJavaTimestamp;
	
	/** The old java timestamp. */
	public static volatile SingularAttribute<Patch, Date>       oldJavaTimestamp;
	
	/** The origin. */
	public static volatile SingularAttribute<Patch, Attachment> origin;
	
	/** The original file. */
	public static volatile SingularAttribute<Patch, String>     originalFile;
	
	/** The posted by. */
	public static volatile SingularAttribute<Patch, Person>     postedBy;
	
	/** The start position. */
	public static volatile SingularAttribute<Patch, Integer>    startPosition;
}

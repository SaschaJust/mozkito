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

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.persons.model.PersonContainer;

/**
 * The Class AttachmentEntry_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.model.AttachmentEntry.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Apr 26 13:58:11 CEST 2013")
public class AttachmentEntry_ {
	
	/** The description. */
	public static volatile SingularAttribute<AttachmentEntry, String>          description;
	
	/** The filename. */
	public static volatile SingularAttribute<AttachmentEntry, String>          filename;
	
	/** The id. */
	public static volatile SingularAttribute<AttachmentEntry, String>          id;
	
	/** The java delta ts. */
	public static volatile SingularAttribute<AttachmentEntry, Date>            javaDeltaTS;
	
	/** The java timestamp. */
	public static volatile SingularAttribute<AttachmentEntry, Date>            javaTimestamp;
	
	/** The link. */
	public static volatile SingularAttribute<AttachmentEntry, String>          link;
	
	/** The mime. */
	public static volatile SingularAttribute<AttachmentEntry, String>          mime;
	
	/** The person container. */
	public static volatile SingularAttribute<AttachmentEntry, PersonContainer> personContainer;
	
	/** The size. */
	public static volatile SingularAttribute<AttachmentEntry, Long>            size;
	
}

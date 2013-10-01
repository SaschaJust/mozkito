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

package org.mozkito.infozilla.model.attachment;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.issues.model.AttachmentEntry;

/**
 * The Class Attachment_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.attachment.Attachment.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 04:23:11 CEST 2013")
public class Attachment_ {
	
	/** The data. */
	public static volatile SingularAttribute<Attachment, byte[]>          data;
	
	/** The encoding. */
	public static volatile SingularAttribute<Attachment, String>          encoding;
	
	/** The entry. */
	public static volatile SingularAttribute<Attachment, AttachmentEntry> entry;
	
	/** The filename. */
	public static volatile SingularAttribute<Attachment, String>          filename;
	
	/** The generated id. */
	public static volatile SingularAttribute<Attachment, Long>            generatedId;
	
	/** The md5. */
	public static volatile SingularAttribute<Attachment, byte[]>          md5;
	
	/** The mime. */
	public static volatile SingularAttribute<Attachment, String>          mime;
	
	/** The sha1. */
	public static volatile SingularAttribute<Attachment, byte[]>          sha1;
	
	/** The type. */
	public static volatile SingularAttribute<Attachment, AttachmentType>  type;
}

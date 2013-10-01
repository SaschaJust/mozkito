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

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.model.attachment.Attachment;
import org.mozkito.infozilla.model.itemization.Listing.Type;
import org.mozkito.persons.model.Person;

/**
 * The Class ListingEntry_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.itemization.ListingEntry.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 04:23:11 CEST 2013")
public class ListingEntry_ {
	
	/** The end position. */
	public static volatile SingularAttribute<ListingEntry, Integer>    endPosition;
	
	/** The id. */
	public static volatile SingularAttribute<ListingEntry, Integer>    id;
	
	/** The identifier. */
	public static volatile SingularAttribute<ListingEntry, String>     identifier;
	
	/** The java posted on. */
	public static volatile SingularAttribute<ListingEntry, Date>       javaPostedOn;
	
	/** The ordinal. */
	public static volatile SingularAttribute<ListingEntry, Integer>    ordinal;
	
	/** The origin. */
	public static volatile SingularAttribute<ListingEntry, Attachment> origin;
	
	/** The posted by. */
	public static volatile SingularAttribute<ListingEntry, Person>     postedBy;
	
	/** The start position. */
	public static volatile SingularAttribute<ListingEntry, Integer>    startPosition;
	
	/** The stop. */
	public static volatile SingularAttribute<ListingEntry, String>     stop;
	
	/** The sub listings. */
	public static volatile ListAttribute<ListingEntry, Listing>        subListings;
	
	/** The text. */
	public static volatile SingularAttribute<ListingEntry, String>     text;
	
	/** The type. */
	public static volatile SingularAttribute<ListingEntry, Type>       type;
}

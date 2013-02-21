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

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.persistence.model.DateTimeTuple;
import org.mozkito.persistence.model.EnumTuple;
import org.mozkito.persistence.model.PersonContainer;
import org.mozkito.persistence.model.PersonTuple;
import org.mozkito.persistence.model.StringTuple;

/**
 * The Class HistoryElement_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.model.HistoryElement.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon May 07 15:41:49 CEST 2012")
public class HistoryElement_ {
	
	/** The changed date values. */
	public static volatile MapAttribute<HistoryElement, String, DateTimeTuple> changedDateValues;
	
	/** The changed enum values. */
	public static volatile MapAttribute<HistoryElement, String, EnumTuple>     changedEnumValues;
	
	/** The changed person values. */
	public static volatile MapAttribute<HistoryElement, String, PersonTuple>   changedPersonValues;
	
	/** The changed string values. */
	public static volatile MapAttribute<HistoryElement, String, StringTuple>   changedStringValues;
	
	/** The id. */
	public static volatile SingularAttribute<HistoryElement, Long>             id;
	
	/** The java timestamp. */
	public static volatile SingularAttribute<HistoryElement, Date>             javaTimestamp;
	
	/** The person container. */
	public static volatile SingularAttribute<HistoryElement, PersonContainer>  personContainer;
	
	/** The history. */
	public static volatile SingularAttribute<HistoryElement, History>          history;
}

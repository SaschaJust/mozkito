/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import de.unisaarland.cs.st.moskito.persistence.model.DateTimeTuple;
import de.unisaarland.cs.st.moskito.persistence.model.EnumTuple;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import de.unisaarland.cs.st.moskito.persistence.model.PersonTuple;
import de.unisaarland.cs.st.moskito.persistence.model.StringTuple;
import java.util.Date;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class HistoryElement_.
 *
 * @author Kim Herzig <herzig@cs.uni-saarland.de>
 */
@javax.persistence.metamodel.StaticMetamodel
(value=de.unisaarland.cs.st.moskito.bugs.tracker.model.HistoryElement.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Tue Feb 28 13:12:49 CET 2012")
public class HistoryElement_ {
    
    /** The bug id. */
    public static volatile SingularAttribute<HistoryElement,Long> bugId;
    
    /** The changed date values. */
    public static volatile MapAttribute<HistoryElement,String,DateTimeTuple> changedDateValues;
    
    /** The changed enum values. */
    public static volatile MapAttribute<HistoryElement,String,EnumTuple> changedEnumValues;
    
    /** The changed person values. */
    public static volatile MapAttribute<HistoryElement,String,PersonTuple> changedPersonValues;
    
    /** The changed string values. */
    public static volatile MapAttribute<HistoryElement,String,StringTuple> changedStringValues;
    
    /** The id. */
    public static volatile SingularAttribute<HistoryElement,Long> id;
    
    /** The java timestamp. */
    public static volatile SingularAttribute<HistoryElement,Date> javaTimestamp;
    
    /** The person container. */
    public static volatile SingularAttribute<HistoryElement,PersonContainer> personContainer;
}

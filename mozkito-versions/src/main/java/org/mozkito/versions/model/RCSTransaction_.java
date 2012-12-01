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
package org.mozkito.versions.model;

import java.util.Date;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.persistence.model.PersonContainer;

/**
 * The Class RCSTransaction_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.RCSTransaction.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Mar 02 12:50:26 CET 2012")
public class RCSTransaction_ {
	
	/** The atomic. */
	public static volatile SingularAttribute<RCSTransaction, Boolean>                                   atomic;
	
	/** The branch indices. */
	public static volatile MapAttribute<RCSTransaction, String, Long>                                   branchIndices;
	
	/** The branch parent. */
	public static volatile SingularAttribute<RCSTransaction, org.mozkito.versions.model.RCSTransaction> branchParent;
	
	/** The children. */
	public static volatile SetAttribute<RCSTransaction, org.mozkito.versions.model.RCSTransaction>      children;
	
	/** The id. */
	public static volatile SingularAttribute<RCSTransaction, String>                                    id;
	
	/** The java timestamp. */
	public static volatile SingularAttribute<RCSTransaction, Date>                                      javaTimestamp;
	
	/** The merge parent. */
	public static volatile SingularAttribute<RCSTransaction, org.mozkito.versions.model.RCSTransaction> mergeParent;
	
	/** The message. */
	public static volatile SingularAttribute<RCSTransaction, String>                                    message;
	
	/** The original id. */
	public static volatile SingularAttribute<RCSTransaction, String>                                    originalId;
	
	/** The persons. */
	public static volatile SingularAttribute<RCSTransaction, PersonContainer>                           persons;
	
	/** The revisions. */
	public static volatile CollectionAttribute<RCSTransaction, RCSRevision>                             revisions;
	
	/** The tags. */
	public static volatile SetAttribute<RCSTransaction, String>                                         tags;
}

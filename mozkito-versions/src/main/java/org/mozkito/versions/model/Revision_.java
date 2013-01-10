/*******************************************************************************
 * Copyright 2013 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mozkito.versions.model;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.versions.elements.ChangeType;

/**
 * The Class RCSRevision_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.Revision.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Thu Jan 10 17:52:36 CET 2013")
public class Revision_ {
	
	/** The change type. */
	public static volatile SingularAttribute<Revision, ChangeType> changeType;
	
	/** The changed file. */
	public static volatile SingularAttribute<Revision, Handle>     changedFile;
	
	/** The revision id. */
	public static volatile SingularAttribute<Revision, Long>       revisionId;
	
	/** The transaction. */
	public static volatile SingularAttribute<Revision, ChangeSet>  transaction;
}

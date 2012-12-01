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
package org.mozkito.codeanalysis.model;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.versions.elements.ChangeType;
import org.mozkito.versions.model.RCSRevision;

/**
 * The Class JavaChangeOperation_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.codeanalysis.model.JavaChangeOperation.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Mar 30 11:02:50 CEST 2012")
public class JavaChangeOperation_ {
	
	/** The change type. */
	public static volatile SingularAttribute<JavaChangeOperation, ChangeType>          changeType;
	
	/** The changed element location. */
	public static volatile SingularAttribute<JavaChangeOperation, JavaElementLocation> changedElementLocation;
	
	/** The essential. */
	public static volatile SingularAttribute<JavaChangeOperation, Boolean>             essential;
	
	/** The id. */
	public static volatile SingularAttribute<JavaChangeOperation, Long>                id;
	
	/** The revision. */
	public static volatile SingularAttribute<JavaChangeOperation, RCSRevision>         revision;
	
}

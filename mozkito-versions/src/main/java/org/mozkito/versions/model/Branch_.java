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

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class Branch_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.Branch.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Jan 11 10:58:22 CET 2013")
public class Branch_ {
	
	/** The head. */
	public static volatile SingularAttribute<Branch, ChangeSet> head;
	
	/** The merged in. */
	public static volatile SetAttribute<Branch, String>         mergedIn;
	
	/** The name. */
	public static volatile SingularAttribute<Branch, String>    name;
}

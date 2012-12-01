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

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class RCSBranch_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.RCSBranch.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Mar 02 12:03:07 CET 2012")
public class RCSBranch_ {
	
	/** The head. */
	public static volatile SingularAttribute<RCSBranch, RCSTransaction> head;
	
	/** The merged in. */
	public static volatile SetAttribute<RCSBranch, String>              mergedIn;
	
	/** The name. */
	public static volatile SingularAttribute<RCSBranch, String>         name;
}

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

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class File_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.versions.model.RCSFile.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Thu Mar 01 23:42:05 CET 2012")
public class RCSFile_ {
	
	/** The changed names. */
	public static volatile MapAttribute<RCSFile, String, String> changedNames;
	
	/** The generated id. */
	public static volatile SingularAttribute<RCSFile, Long>      generatedId;
}

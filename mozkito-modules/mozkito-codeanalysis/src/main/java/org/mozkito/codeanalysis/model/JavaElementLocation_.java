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

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class JavaElementLocation_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.codeanalysis.model.JavaElementLocation.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Mar 30 11:02:50 CEST 2012")
public class JavaElementLocation_ {
	
	/** The body start line. */
	public static volatile SingularAttribute<JavaElementLocation, Integer>     bodyStartLine;
	
	/** The comment lines. */
	public static volatile SetAttribute<JavaElementLocation, Integer>          commentLines;
	
	/** The element. */
	public static volatile SingularAttribute<JavaElementLocation, JavaElement> element;
	
	/** The end line. */
	public static volatile SingularAttribute<JavaElementLocation, Integer>     endLine;
	
	/** The file path. */
	public static volatile SingularAttribute<JavaElementLocation, String>      filePath;
	
	/** The id. */
	public static volatile SingularAttribute<JavaElementLocation, Long>        id;
	
	/** The position. */
	public static volatile SingularAttribute<JavaElementLocation, Integer>     position;
	
	/** The start line. */
	public static volatile SingularAttribute<JavaElementLocation, Integer>     startLine;
	
}

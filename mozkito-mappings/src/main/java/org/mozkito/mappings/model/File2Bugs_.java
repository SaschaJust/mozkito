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
package org.mozkito.mappings.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.issues.tracker.model.Report;
import org.mozkito.versions.model.RCSFile;

/**
 * The Class File2Bugs_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.model.File2Bugs.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon Nov 05 13:27:42 CET 2012")
public class File2Bugs_ {
	
	/** The file. */
	public static volatile SingularAttribute<File2Bugs, RCSFile> rCSFile;
	
	/** The reports. */
	public static volatile SetAttribute<File2Bugs, Report>       reports;
}

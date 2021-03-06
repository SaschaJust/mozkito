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

/**
 * The Class IssueTracker_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.model.IssueTracker.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Apr 26 13:58:11 CEST 2013")
public class IssueTracker_ {
	
	/** The generated id. */
	public static volatile SingularAttribute<IssueTracker, Long>      generatedId;
	
	/** The host info. */
	public static volatile SingularAttribute<IssueTracker, String>    hostInfo;
	
	/** The mining java date. */
	public static volatile SingularAttribute<IssueTracker, Date>      miningJavaDate;
	
	/** The mozkito hash. */
	public static volatile SingularAttribute<IssueTracker, String>    mozkitoHash;
	
	/** The mozkito version. */
	public static volatile SingularAttribute<IssueTracker, String>    mozkitoVersion;
	
	/** The reports. */
	public static volatile MapAttribute<IssueTracker, String, Report> reports;
	
	/** The used settings. */
	public static volatile SingularAttribute<IssueTracker, String>    usedSettings;
	
}

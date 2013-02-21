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

package org.mozkito.issues.tracker.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class History_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.tracker.model.History.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon May 07 15:41:49 CEST 2012")
public class History_ {
	
	/** The elements. */
	public static volatile SetAttribute<History, HistoryElement>     elements;
	
	/** The history. */
	public static volatile SingularAttribute<HistoryElement, Report> report;
}

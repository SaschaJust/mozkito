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

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.issues.elements.Type;

/**
 * The Class EnhancedReport_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.issues.model.EnhancedReport.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Apr 26 13:58:11 CEST 2013")
public class EnhancedReport_ {
	
	/** The classified type. */
	public static volatile SingularAttribute<EnhancedReport, Type>   classifiedType;
	
	/** The predicted type. */
	public static volatile SingularAttribute<EnhancedReport, Type>   predictedType;
	
	/** The report. */
	public static volatile SingularAttribute<EnhancedReport, Report> report;
	
}

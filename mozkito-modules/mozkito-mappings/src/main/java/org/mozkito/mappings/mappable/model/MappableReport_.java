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
package org.mozkito.mappings.mappable.model;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.issues.model.Report;

/**
 * The Class MappableReport_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.mappable.model.MappableReport.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Thu Nov 03 18:46:21 CET 2011")
public class MappableReport_ extends MappableEntity_ {
	
	/** The report. */
	public static volatile SingularAttribute<MappableReport, Report> report;
}

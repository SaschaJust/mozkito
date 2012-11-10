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

import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class Feature_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.model.Feature.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon Nov 05 13:27:42 CET 2012")
public class Feature_ {
	
	/** The confidence. */
	public static volatile SingularAttribute<Feature, Double> confidence;
	
	/** The fq class name. */
	public static volatile SingularAttribute<Feature, String> fqClassName;
	
	/** The from field name. */
	public static volatile SingularAttribute<Feature, String> fromFieldName;
	
	/** The from substring. */
	public static volatile SingularAttribute<Feature, String> fromSubstring;
	
	/** The to field name. */
	public static volatile SingularAttribute<Feature, String> toFieldName;
	
	/** The to substring. */
	public static volatile SingularAttribute<Feature, String> toSubstring;
}

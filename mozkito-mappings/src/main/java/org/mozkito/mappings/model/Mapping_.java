/*******************************************************************************
 * Copyright 2012 Kim Herzig, Sascha Just - mozkito.org
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
package org.mozkito.mappings.model;

import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * @author Sascha Just <sascha.just@mozkito.org>
 * 
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.model.Mapping.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Wed Jan 18 19:05:50 CET 2012")
public class Mapping_ {
	
	public static volatile SingularAttribute<Mapping, Composite>  composite;
	public static volatile MapAttribute<Mapping, String, Boolean> filters;
}

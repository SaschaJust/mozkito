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
package org.mozkito.persistence.model;

import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class EnumTuple_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.persistence.model.EnumTuple.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Sep 02 15:41:15 CEST 2011")
public class EnumTuple_ {
	
	/** The enum class name. */
	public static volatile SingularAttribute<EnumTuple, String> enumClassName;
	
	/** The new string value. */
	public static volatile SingularAttribute<EnumTuple, String> newStringValue;
	
	/** The old string value. */
	public static volatile SingularAttribute<EnumTuple, String> oldStringValue;
}

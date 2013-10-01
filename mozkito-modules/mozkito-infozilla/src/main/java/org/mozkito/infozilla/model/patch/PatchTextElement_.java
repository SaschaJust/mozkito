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

package org.mozkito.infozilla.model.patch;

import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.infozilla.model.patch.PatchTextElement.Type;

/**
 * The Class PatchTextElement_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.infozilla.model.patch.PatchTextElement.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Tue Oct 01 02:02:27 CEST 2013")
public class PatchTextElement_ {
	
	/** The id. */
	public static volatile SingularAttribute<PatchTextElement, Integer> id;
	
	/** The text. */
	public static volatile SingularAttribute<PatchTextElement, String>  text;
	
	/** The type. */
	public static volatile SingularAttribute<PatchTextElement, Type>    type;
}

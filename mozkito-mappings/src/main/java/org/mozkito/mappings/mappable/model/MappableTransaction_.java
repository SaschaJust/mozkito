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

import org.mozkito.versions.model.ChangeSet;

/**
 * The Class MappableTransaction_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.mappable.model.MappableTransaction.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Thu Nov 03 18:48:56 CET 2011")
public class MappableTransaction_ extends MappableEntity_ {
	
	/** The transaction. */
	public static volatile SingularAttribute<MappableTransaction, ChangeSet> changeset;
}

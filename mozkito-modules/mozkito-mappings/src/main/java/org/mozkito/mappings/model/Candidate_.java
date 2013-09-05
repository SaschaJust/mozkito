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

import org.mozkito.persistence.model.Artifact;

/**
 * The Class Candidate_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.model.Candidate.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Mon Nov 05 13:27:42 CET 2012")
public class Candidate_ {
	
	/** The from. */
	public static volatile SingularAttribute<Candidate, Artifact> from;
	
	/** The selectors. */
	public static volatile SetAttribute<Candidate, String>              selectors;
	
	/** The to. */
	public static volatile SingularAttribute<Candidate, Artifact> to;
}

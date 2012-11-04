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

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import org.mozkito.mappings.mappable.model.MappableEntity;

/**
 * The MetaModel Candidate_.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.model.Candidate.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Sun Nov 04 12:30:00 CEST 2012")
public class Candidate_ {
	
	/** The from. */
	public static volatile SingularAttribute<Candidate, MappableEntity> from;
	
	/** The to. */
	public static volatile SingularAttribute<Candidate, MappableEntity> to;
	
	/** The active selectors. */
	public static volatile SetAttribute<Candidate, String>              activeSelectors;
	
	/** The voting selectors. */
	public static volatile SetAttribute<Candidate, String>              votingSelectors;
	
}

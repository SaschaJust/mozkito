/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package org.mozkito.mappings.model;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class Relation_.
 */
@javax.persistence.metamodel.StaticMetamodel (value = org.mozkito.mappings.model.Relation.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Wed Jan 18 19:05:50 CET 2012")
public class Relation_ {
	
	/** The class1. */
	public static volatile SingularAttribute<Relation, Candidate> candidate;
	
	/** The features. */
	public static volatile CollectionAttribute<Relation, Feature> features;
	
}

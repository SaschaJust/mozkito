/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package de.unisaarland.cs.st.reposuite.mapping.model;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=de.unisaarland.cs.st.reposuite.mapping.model.MapScore.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Fri Sep 02 15:37:56 CEST 2011")
public class MapScore_ {
    public static volatile SingularAttribute<MapScore,String> class1;
    public static volatile SingularAttribute<MapScore,String> class2;
    public static volatile ListAttribute<MapScore,MappingEngineFeature> features;
    public static volatile SingularAttribute<MapScore,String> fromId;
    public static volatile SingularAttribute<MapScore,String> toId;
    public static volatile SingularAttribute<MapScore,Double> totalConfidence;
}
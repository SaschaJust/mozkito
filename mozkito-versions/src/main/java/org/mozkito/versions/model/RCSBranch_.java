/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package org.mozkito.versions.model;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

/**
 * The Class RCSBranch_.
 */
@javax.persistence.metamodel.StaticMetamodel
(value=org.mozkito.versions.model.RCSBranch.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Fri Mar 02 12:03:07 CET 2012")
public class RCSBranch_ {
    
    /** The head. */
    public static volatile SingularAttribute<RCSBranch,RCSTransaction> head;
    
    /** The merged in. */
    public static volatile SetAttribute<RCSBranch,String> mergedIn;
    
    /** The name. */
    public static volatile SingularAttribute<RCSBranch,String> name;
}
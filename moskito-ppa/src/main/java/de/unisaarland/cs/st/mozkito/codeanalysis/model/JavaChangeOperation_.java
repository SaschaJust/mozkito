/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package de.unisaarland.cs.st.mozkito.codeanalysis.model;

import de.unisaarland.cs.st.mozkito.rcs.elements.ChangeType;
import de.unisaarland.cs.st.mozkito.rcs.model.RCSRevision;

import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=de.unisaarland.cs.st.mozkito.codeanalysis.model.JavaChangeOperation.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Fri Mar 30 11:02:50 CEST 2012")
public class JavaChangeOperation_ {
    public static volatile SingularAttribute<JavaChangeOperation,ChangeType> changeType;
    public static volatile SingularAttribute<JavaChangeOperation,JavaElementLocation> changedElementLocation;
    public static volatile SingularAttribute<JavaChangeOperation,Boolean> essential;
    public static volatile SingularAttribute<JavaChangeOperation,Long> id;
    public static volatile SingularAttribute<JavaChangeOperation,RCSRevision> revision;
}

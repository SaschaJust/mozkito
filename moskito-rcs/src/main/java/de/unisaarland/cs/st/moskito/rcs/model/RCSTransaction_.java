/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package de.unisaarland.cs.st.moskito.rcs.model;

import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;
import java.util.Date;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel
(value=de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Fri Mar 02 12:18:51 CET 2012")
public class RCSTransaction_ {
    public static volatile SingularAttribute<RCSTransaction,Boolean> atomic;
    public static volatile MapAttribute<RCSTransaction,String,Long> branchIndices;
    public static volatile SetAttribute<RCSTransaction,de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction> children;
    public static volatile SingularAttribute<RCSTransaction,String> id;
    public static volatile SingularAttribute<RCSTransaction,Date> javaTimestamp;
    public static volatile SingularAttribute<RCSTransaction,de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction> mergeParent;
    public static volatile SingularAttribute<RCSTransaction,String> message;
    public static volatile SingularAttribute<RCSTransaction,String> originalId;
    public static volatile SingularAttribute<RCSTransaction,PersonContainer> persons;
    public static volatile CollectionAttribute<RCSTransaction,RCSRevision> revisions;
    public static volatile SetAttribute<RCSTransaction,String> tags;
}

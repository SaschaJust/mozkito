/** 
 *  Generated by OpenJPA MetaModel Generator Tool.
**/

package org.mozkito.versions.model;

import java.util.Date;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import org.mozkito.persistence.model.PersonContainer;

@javax.persistence.metamodel.StaticMetamodel
(value=org.mozkito.versions.model.RCSTransaction.class)
@javax.annotation.Generated
(value="org.apache.openjpa.persistence.meta.AnnotationProcessor6",date="Thu Jan 10 17:52:36 CET 2013")
public class RCSTransaction_ {
    public static volatile SingularAttribute<RCSTransaction,Boolean> atomic;
    public static volatile MapAttribute<RCSTransaction,String,Long> branchIndices;
    public static volatile SingularAttribute<RCSTransaction,org.mozkito.versions.model.RCSTransaction> branchParent;
    public static volatile SetAttribute<RCSTransaction,org.mozkito.versions.model.RCSTransaction> children;
    public static volatile SingularAttribute<RCSTransaction,String> id;
    public static volatile SingularAttribute<RCSTransaction,Date> javaTimestamp;
    public static volatile SingularAttribute<RCSTransaction,org.mozkito.versions.model.RCSTransaction> mergeParent;
    public static volatile SingularAttribute<RCSTransaction,String> message;
    public static volatile SingularAttribute<RCSTransaction,String> originalId;
    public static volatile SingularAttribute<RCSTransaction,PersonContainer> persons;
    public static volatile CollectionAttribute<RCSTransaction,RCSRevision> revisions;
    public static volatile SetAttribute<RCSTransaction,String> tags;
}

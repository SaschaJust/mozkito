/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package de.unisaarland.cs.st.moskito.rcs.model;

import java.util.Date;

import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;

@javax.persistence.metamodel.StaticMetamodel (value = de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Thu Mar 01 23:42:05 CET 2012")
public class RCSTransaction_ {
	
	public static volatile SingularAttribute<RCSTransaction, Boolean>                                               atomic;
	public static volatile SetAttribute<RCSTransaction, RCSBranch>                                                  branches;
	public static volatile SetAttribute<RCSTransaction, de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction>      children;
	public static volatile SingularAttribute<RCSTransaction, String>                                                id;
	public static volatile SingularAttribute<RCSTransaction, Date>                                                  javaTimestamp;
	public static volatile SingularAttribute<RCSTransaction, de.unisaarland.cs.st.moskito.rcs.model.RCSTransaction> mergeParent;
	public static volatile SingularAttribute<RCSTransaction, String>                                                message;
	public static volatile SingularAttribute<RCSTransaction, String>                                                originalId;
	public static volatile SingularAttribute<RCSTransaction, PersonContainer>                                       persons;
	public static volatile CollectionAttribute<RCSTransaction, RCSRevision>                                         revisions;
	public static volatile SetAttribute<RCSTransaction, String>                                                     tags;
	public static volatile MapAttribute<RCSTransaction, RCSBranch, String>                                          branchIndices;
}

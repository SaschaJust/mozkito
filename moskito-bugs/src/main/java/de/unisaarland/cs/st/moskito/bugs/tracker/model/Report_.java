/**
 * Generated by OpenJPA MetaModel Generator Tool.
 **/

package de.unisaarland.cs.st.moskito.bugs.tracker.model;

import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Priority;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Resolution;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Severity;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Status;
import de.unisaarland.cs.st.moskito.bugs.tracker.elements.Type;
import de.unisaarland.cs.st.moskito.persistence.model.PersonContainer;

import java.util.Date;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@javax.persistence.metamodel.StaticMetamodel (value = de.unisaarland.cs.st.moskito.bugs.tracker.model.Report.class)
@javax.annotation.Generated (value = "org.apache.openjpa.persistence.meta.AnnotationProcessor6",
                             date = "Fri Sep 02 15:26:32 CEST 2011")
public class Report_ {
	
	public static volatile ListAttribute<Report, AttachmentEntry>     attachmentEntries;
	public static volatile SingularAttribute<Report, String>          category;
	public static volatile SetAttribute<Report, Comment>              comments;
	public static volatile SingularAttribute<Report, String>          component;
	public static volatile SingularAttribute<Report, Date>            creationJavaTimestamp;
	public static volatile SingularAttribute<Report, String>          description;
	public static volatile ListAttribute<Report, Byte>                hash;
	public static volatile SingularAttribute<Report, History>         history;
	public static volatile SingularAttribute<Report, Long>            id;
	public static volatile SingularAttribute<Report, Date>            lastFetchJava;
	public static volatile SingularAttribute<Report, Date>            lastUpdateJavaTimestamp;
	public static volatile SingularAttribute<Report, PersonContainer> personContainer;
	public static volatile SingularAttribute<Report, Priority>        priority;
	public static volatile SingularAttribute<Report, String>          product;
	public static volatile SingularAttribute<Report, Resolution>      resolution;
	public static volatile SingularAttribute<Report, Date>            resolutionJavaTimestamp;
	public static volatile SingularAttribute<Report, Severity>        severity;
	public static volatile SetAttribute<Report, Long>                 siblings;
	public static volatile SingularAttribute<Report, Status>          status;
	public static volatile SingularAttribute<Report, String>          subject;
	public static volatile SingularAttribute<Report, String>          summary;
	public static volatile SingularAttribute<Report, Type>            type;
	public static volatile SingularAttribute<Report, String>          version;
}

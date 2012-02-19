/*
 * An XML document type. Localname: bug Namespace: Java type: BugDocument Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.AssignedToDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.AttachmentDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BugDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.FlagDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.GroupDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.LongDescDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.QaContactDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ReporterDocument;

/**
 * A document containing one bug(@) element.
 * 
 * This is a complex type.
 */
public class BugDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements BugDocument {
	
	/**
	 * An XML bug(@).
	 * 
	 * This is a complex type.
	 */
	public static class BugImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements
	        BugDocument.Bug {
		
		/**
		 * An XML error(@).
		 * 
		 * This is an atomic type that is a restriction of BugDocument$Bug$Error.
		 */
		public static class ErrorImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx implements
		        BugDocument.Bug.Error {
			
			private static final long serialVersionUID = 1L;
			
			public ErrorImpl(final org.apache.xmlbeans.SchemaType sType) {
				super(sType, false);
			}
			
			protected ErrorImpl(final org.apache.xmlbeans.SchemaType sType, final boolean b) {
				super(sType, b);
			}
		}
		
		private static final long                      serialVersionUID      = 1L;
		
		private static final javax.xml.namespace.QName BUGID$0               = new javax.xml.namespace.QName("",
		                                                                                                     "bug_id");
		private static final javax.xml.namespace.QName ALIAS$2               = new javax.xml.namespace.QName("",
		                                                                                                     "alias");
		private static final javax.xml.namespace.QName CREATIONTS$4          = new javax.xml.namespace.QName("",
		                                                                                                     "creation_ts");
		private static final javax.xml.namespace.QName SHORTDESC$6           = new javax.xml.namespace.QName("",
		                                                                                                     "short_desc");
		private static final javax.xml.namespace.QName DELTATS$8             = new javax.xml.namespace.QName("",
		                                                                                                     "delta_ts");
		private static final javax.xml.namespace.QName REPORTERACCESSIBLE$10 = new javax.xml.namespace.QName("",
		                                                                                                     "reporter_accessible");
		private static final javax.xml.namespace.QName CCLISTACCESSIBLE$12   = new javax.xml.namespace.QName("",
		                                                                                                     "cclist_accessible");
		private static final javax.xml.namespace.QName CLASSIFICATIONID$14   = new javax.xml.namespace.QName("",
		                                                                                                     "classification_id");
		private static final javax.xml.namespace.QName CLASSIFICATION$16     = new javax.xml.namespace.QName("",
		                                                                                                     "classification");
		private static final javax.xml.namespace.QName PRODUCT$18            = new javax.xml.namespace.QName("",
		                                                                                                     "product");
		private static final javax.xml.namespace.QName COMPONENT$20          = new javax.xml.namespace.QName("",
		                                                                                                     "component");
		private static final javax.xml.namespace.QName VERSION$22            = new javax.xml.namespace.QName("",
		                                                                                                     "version");
		private static final javax.xml.namespace.QName REPPLATFORM$24        = new javax.xml.namespace.QName("",
		                                                                                                     "rep_platform");
		private static final javax.xml.namespace.QName OPSYS$26              = new javax.xml.namespace.QName("",
		                                                                                                     "op_sys");
		private static final javax.xml.namespace.QName BUGSTATUS$28          = new javax.xml.namespace.QName("",
		                                                                                                     "bug_status");
		private static final javax.xml.namespace.QName RESOLUTION$30         = new javax.xml.namespace.QName("",
		                                                                                                     "resolution");
		private static final javax.xml.namespace.QName DUPID$32              = new javax.xml.namespace.QName("",
		                                                                                                     "dup_id");
		private static final javax.xml.namespace.QName SEEALSO$34            = new javax.xml.namespace.QName("",
		                                                                                                     "see_also");
		private static final javax.xml.namespace.QName BUGFILELOC$36         = new javax.xml.namespace.QName("",
		                                                                                                     "bug_file_loc");
		private static final javax.xml.namespace.QName STATUSWHITEBOARD$38   = new javax.xml.namespace.QName("",
		                                                                                                     "status_whiteboard");
		private static final javax.xml.namespace.QName KEYWORDS$40           = new javax.xml.namespace.QName("",
		                                                                                                     "keywords");
		private static final javax.xml.namespace.QName PRIORITY$42           = new javax.xml.namespace.QName("",
		                                                                                                     "priority");
		private static final javax.xml.namespace.QName BUGSEVERITY$44        = new javax.xml.namespace.QName("",
		                                                                                                     "bug_severity");
		private static final javax.xml.namespace.QName TARGETMILESTONE$46    = new javax.xml.namespace.QName("",
		                                                                                                     "target_milestone");
		private static final javax.xml.namespace.QName DEPENDSON$48          = new javax.xml.namespace.QName("",
		                                                                                                     "dependson");
		private static final javax.xml.namespace.QName BLOCKED$50            = new javax.xml.namespace.QName("",
		                                                                                                     "blocked");
		private static final javax.xml.namespace.QName EVERCONFIRMED$52      = new javax.xml.namespace.QName("",
		                                                                                                     "everconfirmed");
		private static final javax.xml.namespace.QName REPORTER$54           = new javax.xml.namespace.QName("",
		                                                                                                     "reporter");
		private static final javax.xml.namespace.QName ASSIGNEDTO$56         = new javax.xml.namespace.QName("",
		                                                                                                     "assigned_to");
		private static final javax.xml.namespace.QName CC$58                 = new javax.xml.namespace.QName("", "cc");
		private static final javax.xml.namespace.QName ESTIMATEDTIME$60      = new javax.xml.namespace.QName("",
		                                                                                                     "estimated_time");
		private static final javax.xml.namespace.QName REMAININGTIME$62      = new javax.xml.namespace.QName("",
		                                                                                                     "remaining_time");
		private static final javax.xml.namespace.QName ACTUALTIME$64         = new javax.xml.namespace.QName("",
		                                                                                                     "actual_time");
		private static final javax.xml.namespace.QName DEADLINE$66           = new javax.xml.namespace.QName("",
		                                                                                                     "deadline");
		private static final javax.xml.namespace.QName QACONTACT$68          = new javax.xml.namespace.QName("",
		                                                                                                     "qa_contact");
		private static final javax.xml.namespace.QName VOTES$70              = new javax.xml.namespace.QName("",
		                                                                                                     "votes");
		private static final javax.xml.namespace.QName TOKEN$72              = new javax.xml.namespace.QName("",
		                                                                                                     "token");
		private static final javax.xml.namespace.QName GROUP$74              = new javax.xml.namespace.QName("",
		                                                                                                     "group");
		private static final javax.xml.namespace.QName FLAG$76               = new javax.xml.namespace.QName("", "flag");
		private static final javax.xml.namespace.QName LONGDESC$78           = new javax.xml.namespace.QName("",
		                                                                                                     "long_desc");
		private static final javax.xml.namespace.QName ATTACHMENT$80         = new javax.xml.namespace.QName("",
		                                                                                                     "attachment");
		private static final javax.xml.namespace.QName ERROR$82              = new javax.xml.namespace.QName("",
		                                                                                                     "error");
		
		public BugImpl(final org.apache.xmlbeans.SchemaType sType) {
			super(sType);
		}
		
		/**
		 * Appends the value as the last "blocked" element
		 */
		public void addBlocked(final java.lang.String blocked) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(BLOCKED$50);
				target.setStringValue(blocked);
			}
		}
		
		/**
		 * Appends the value as the last "cc" element
		 */
		public void addCc(final java.lang.String cc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(CC$58);
				target.setStringValue(cc);
			}
		}
		
		/**
		 * Appends the value as the last "dependson" element
		 */
		public void addDependson(final java.lang.String dependson) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(DEPENDSON$48);
				target.setStringValue(dependson);
			}
		}
		
		/**
		 * Appends the value as the last "keywords" element
		 */
		public void addKeywords(final java.lang.String keywords) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(KEYWORDS$40);
				target.setStringValue(keywords);
			}
		}
		
		/**
		 * Appends and returns a new empty "assigned_to" element
		 */
		public AssignedToDocument.AssignedTo addNewAssignedTo() {
			synchronized (monitor()) {
				check_orphaned();
				AssignedToDocument.AssignedTo target = null;
				target = (AssignedToDocument.AssignedTo) get_store().add_element_user(ASSIGNEDTO$56);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "attachment" element
		 */
		public AttachmentDocument.Attachment addNewAttachment() {
			synchronized (monitor()) {
				check_orphaned();
				AttachmentDocument.Attachment target = null;
				target = (AttachmentDocument.Attachment) get_store().add_element_user(ATTACHMENT$80);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "blocked" element
		 */
		public org.apache.xmlbeans.XmlString addNewBlocked() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(BLOCKED$50);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "cc" element
		 */
		public org.apache.xmlbeans.XmlString addNewCc() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(CC$58);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "dependson" element
		 */
		public org.apache.xmlbeans.XmlString addNewDependson() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(DEPENDSON$48);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "flag" element
		 */
		public FlagDocument.Flag addNewFlag() {
			synchronized (monitor()) {
				check_orphaned();
				FlagDocument.Flag target = null;
				target = (FlagDocument.Flag) get_store().add_element_user(FLAG$76);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "group" element
		 */
		public GroupDocument.Group addNewGroup() {
			synchronized (monitor()) {
				check_orphaned();
				GroupDocument.Group target = null;
				target = (GroupDocument.Group) get_store().add_element_user(GROUP$74);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "keywords" element
		 */
		public org.apache.xmlbeans.XmlString addNewKeywords() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(KEYWORDS$40);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "long_desc" element
		 */
		public LongDescDocument.LongDesc addNewLongDesc() {
			synchronized (monitor()) {
				check_orphaned();
				LongDescDocument.LongDesc target = null;
				target = (LongDescDocument.LongDesc) get_store().add_element_user(LONGDESC$78);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty "qa_contact" element
		 */
		public QaContactDocument.QaContact addNewQaContact() {
			synchronized (monitor()) {
				check_orphaned();
				QaContactDocument.QaContact target = null;
				target = (QaContactDocument.QaContact) get_store().add_element_user(QACONTACT$68);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty "reporter" element
		 */
		public ReporterDocument.Reporter addNewReporter() {
			synchronized (monitor()) {
				check_orphaned();
				ReporterDocument.Reporter target = null;
				target = (ReporterDocument.Reporter) get_store().add_element_user(REPORTER$54);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "see_also" element
		 */
		public org.apache.xmlbeans.XmlString addNewSeeAlso() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(SEEALSO$34);
				return target;
			}
		}
		
		/**
		 * Appends the value as the last "see_also" element
		 */
		public void addSeeAlso(final java.lang.String seeAlso) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(SEEALSO$34);
				target.setStringValue(seeAlso);
			}
		}
		
		/**
		 * Gets the "actual_time" element
		 */
		public java.lang.String getActualTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ACTUALTIME$64, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "alias" element
		 */
		public java.lang.String getAlias() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ALIAS$2, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "assigned_to" element
		 */
		public AssignedToDocument.AssignedTo getAssignedTo() {
			synchronized (monitor()) {
				check_orphaned();
				AssignedToDocument.AssignedTo target = null;
				target = (AssignedToDocument.AssignedTo) get_store().find_element_user(ASSIGNEDTO$56, 0);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets array of all "attachment" elements
		 */
		public AttachmentDocument.Attachment[] getAttachmentArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(ATTACHMENT$80, targetList);
				final AttachmentDocument.Attachment[] result = new AttachmentDocument.Attachment[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets ith "attachment" element
		 */
		public AttachmentDocument.Attachment getAttachmentArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				AttachmentDocument.Attachment target = null;
				target = (AttachmentDocument.Attachment) get_store().find_element_user(ATTACHMENT$80, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets array of all "blocked" elements
		 */
		public java.lang.String[] getBlockedArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(BLOCKED$50, targetList);
				final java.lang.String[] result = new java.lang.String[targetList.size()];
				for (int i = 0, len = targetList.size(); i < len; i++) {
					result[i] = ((org.apache.xmlbeans.SimpleValue) targetList.get(i)).getStringValue();
				}
				return result;
			}
		}
		
		/**
		 * Gets ith "blocked" element
		 */
		public java.lang.String getBlockedArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BLOCKED$50, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "bug_file_loc" element
		 */
		public java.lang.String getBugFileLoc() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGFILELOC$36, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "bug_id" element
		 */
		public java.lang.String getBugId() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGID$0, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "bug_severity" element
		 */
		public java.lang.String getBugSeverity() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGSEVERITY$44, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "bug_status" element
		 */
		public java.lang.String getBugStatus() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGSTATUS$28, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets array of all "cc" elements
		 */
		public java.lang.String[] getCcArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(CC$58, targetList);
				final java.lang.String[] result = new java.lang.String[targetList.size()];
				for (int i = 0, len = targetList.size(); i < len; i++) {
					result[i] = ((org.apache.xmlbeans.SimpleValue) targetList.get(i)).getStringValue();
				}
				return result;
			}
		}
		
		/**
		 * Gets ith "cc" element
		 */
		public java.lang.String getCcArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CC$58, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "cclist_accessible" element
		 */
		public java.lang.String getCclistAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CCLISTACCESSIBLE$12, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "classification" element
		 */
		public java.lang.String getClassification() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CLASSIFICATION$16, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "classification_id" element
		 */
		public java.lang.String getClassificationId() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CLASSIFICATIONID$14, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "component" element
		 */
		public java.lang.String getComponent() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(COMPONENT$20, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "creation_ts" element
		 */
		public java.lang.String getCreationTs() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CREATIONTS$4, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "deadline" element
		 */
		public java.lang.String getDeadline() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DEADLINE$66, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "delta_ts" element
		 */
		public java.lang.String getDeltaTs() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DELTATS$8, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets array of all "dependson" elements
		 */
		public java.lang.String[] getDependsonArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(DEPENDSON$48, targetList);
				final java.lang.String[] result = new java.lang.String[targetList.size()];
				for (int i = 0, len = targetList.size(); i < len; i++) {
					result[i] = ((org.apache.xmlbeans.SimpleValue) targetList.get(i)).getStringValue();
				}
				return result;
			}
		}
		
		/**
		 * Gets ith "dependson" element
		 */
		public java.lang.String getDependsonArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DEPENDSON$48, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "dup_id" element
		 */
		public java.lang.String getDupId() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DUPID$32, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "error" attribute
		 */
		public BugDocument.Bug.Error.Enum getError() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(ERROR$82);
				if (target == null) {
					return null;
				}
				return (BugDocument.Bug.Error.Enum) target.getEnumValue();
			}
		}
		
		/**
		 * Gets the "estimated_time" element
		 */
		public java.lang.String getEstimatedTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ESTIMATEDTIME$60, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "everconfirmed" element
		 */
		public java.lang.String getEverconfirmed() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(EVERCONFIRMED$52, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets array of all "flag" elements
		 */
		public FlagDocument.Flag[] getFlagArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(FLAG$76, targetList);
				final FlagDocument.Flag[] result = new FlagDocument.Flag[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets ith "flag" element
		 */
		public FlagDocument.Flag getFlagArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				FlagDocument.Flag target = null;
				target = (FlagDocument.Flag) get_store().find_element_user(FLAG$76, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets array of all "group" elements
		 */
		public GroupDocument.Group[] getGroupArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(GROUP$74, targetList);
				final GroupDocument.Group[] result = new GroupDocument.Group[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets ith "group" element
		 */
		public GroupDocument.Group getGroupArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				GroupDocument.Group target = null;
				target = (GroupDocument.Group) get_store().find_element_user(GROUP$74, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets array of all "keywords" elements
		 */
		public java.lang.String[] getKeywordsArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(KEYWORDS$40, targetList);
				final java.lang.String[] result = new java.lang.String[targetList.size()];
				for (int i = 0, len = targetList.size(); i < len; i++) {
					result[i] = ((org.apache.xmlbeans.SimpleValue) targetList.get(i)).getStringValue();
				}
				return result;
			}
		}
		
		/**
		 * Gets ith "keywords" element
		 */
		public java.lang.String getKeywordsArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(KEYWORDS$40, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets array of all "long_desc" elements
		 */
		public LongDescDocument.LongDesc[] getLongDescArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(LONGDESC$78, targetList);
				final LongDescDocument.LongDesc[] result = new LongDescDocument.LongDesc[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets ith "long_desc" element
		 */
		public LongDescDocument.LongDesc getLongDescArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				LongDescDocument.LongDesc target = null;
				target = (LongDescDocument.LongDesc) get_store().find_element_user(LONGDESC$78, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets the "op_sys" element
		 */
		public java.lang.String getOpSys() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(OPSYS$26, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "priority" element
		 */
		public java.lang.String getPriority() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(PRIORITY$42, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "product" element
		 */
		public java.lang.String getProduct() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(PRODUCT$18, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "qa_contact" element
		 */
		public QaContactDocument.QaContact getQaContact() {
			synchronized (monitor()) {
				check_orphaned();
				QaContactDocument.QaContact target = null;
				target = (QaContactDocument.QaContact) get_store().find_element_user(QACONTACT$68, 0);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets the "remaining_time" element
		 */
		public java.lang.String getRemainingTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(REMAININGTIME$62, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "reporter" element
		 */
		public ReporterDocument.Reporter getReporter() {
			synchronized (monitor()) {
				check_orphaned();
				ReporterDocument.Reporter target = null;
				target = (ReporterDocument.Reporter) get_store().find_element_user(REPORTER$54, 0);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets the "reporter_accessible" element
		 */
		public java.lang.String getReporterAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(REPORTERACCESSIBLE$10, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "rep_platform" element
		 */
		public java.lang.String getRepPlatform() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(REPPLATFORM$24, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "resolution" element
		 */
		public java.lang.String getResolution() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(RESOLUTION$30, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets array of all "see_also" elements
		 */
		public java.lang.String[] getSeeAlsoArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(SEEALSO$34, targetList);
				final java.lang.String[] result = new java.lang.String[targetList.size()];
				for (int i = 0, len = targetList.size(); i < len; i++) {
					result[i] = ((org.apache.xmlbeans.SimpleValue) targetList.get(i)).getStringValue();
				}
				return result;
			}
		}
		
		/**
		 * Gets ith "see_also" element
		 */
		public java.lang.String getSeeAlsoArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(SEEALSO$34, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "short_desc" element
		 */
		public java.lang.String getShortDesc() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(SHORTDESC$6, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "status_whiteboard" element
		 */
		public java.lang.String getStatusWhiteboard() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(STATUSWHITEBOARD$38, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "target_milestone" element
		 */
		public java.lang.String getTargetMilestone() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(TARGETMILESTONE$46, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "token" element
		 */
		public java.lang.String getToken() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(TOKEN$72, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "version" element
		 */
		public java.lang.String getVersion() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(VERSION$22, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "votes" element
		 */
		public java.lang.String getVotes() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(VOTES$70, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Inserts the value as the ith "blocked" element
		 */
		public void insertBlocked(final int i,
		                          final java.lang.String blocked) {
			synchronized (monitor()) {
				check_orphaned();
				final org.apache.xmlbeans.SimpleValue target = (org.apache.xmlbeans.SimpleValue) get_store().insert_element_user(BLOCKED$50,
				                                                                                                                 i);
				target.setStringValue(blocked);
			}
		}
		
		/**
		 * Inserts the value as the ith "cc" element
		 */
		public void insertCc(final int i,
		                     final java.lang.String cc) {
			synchronized (monitor()) {
				check_orphaned();
				final org.apache.xmlbeans.SimpleValue target = (org.apache.xmlbeans.SimpleValue) get_store().insert_element_user(CC$58,
				                                                                                                                 i);
				target.setStringValue(cc);
			}
		}
		
		/**
		 * Inserts the value as the ith "dependson" element
		 */
		public void insertDependson(final int i,
		                            final java.lang.String dependson) {
			synchronized (monitor()) {
				check_orphaned();
				final org.apache.xmlbeans.SimpleValue target = (org.apache.xmlbeans.SimpleValue) get_store().insert_element_user(DEPENDSON$48,
				                                                                                                                 i);
				target.setStringValue(dependson);
			}
		}
		
		/**
		 * Inserts the value as the ith "keywords" element
		 */
		public void insertKeywords(final int i,
		                           final java.lang.String keywords) {
			synchronized (monitor()) {
				check_orphaned();
				final org.apache.xmlbeans.SimpleValue target = (org.apache.xmlbeans.SimpleValue) get_store().insert_element_user(KEYWORDS$40,
				                                                                                                                 i);
				target.setStringValue(keywords);
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "attachment" element
		 */
		public AttachmentDocument.Attachment insertNewAttachment(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				AttachmentDocument.Attachment target = null;
				target = (AttachmentDocument.Attachment) get_store().insert_element_user(ATTACHMENT$80, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "blocked" element
		 */
		public org.apache.xmlbeans.XmlString insertNewBlocked(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().insert_element_user(BLOCKED$50, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "cc" element
		 */
		public org.apache.xmlbeans.XmlString insertNewCc(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().insert_element_user(CC$58, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "dependson" element
		 */
		public org.apache.xmlbeans.XmlString insertNewDependson(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().insert_element_user(DEPENDSON$48, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "flag" element
		 */
		public FlagDocument.Flag insertNewFlag(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				FlagDocument.Flag target = null;
				target = (FlagDocument.Flag) get_store().insert_element_user(FLAG$76, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "group" element
		 */
		public GroupDocument.Group insertNewGroup(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				GroupDocument.Group target = null;
				target = (GroupDocument.Group) get_store().insert_element_user(GROUP$74, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "keywords" element
		 */
		public org.apache.xmlbeans.XmlString insertNewKeywords(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().insert_element_user(KEYWORDS$40, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "long_desc" element
		 */
		public LongDescDocument.LongDesc insertNewLongDesc(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				LongDescDocument.LongDesc target = null;
				target = (LongDescDocument.LongDesc) get_store().insert_element_user(LONGDESC$78, i);
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "see_also" element
		 */
		public org.apache.xmlbeans.XmlString insertNewSeeAlso(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().insert_element_user(SEEALSO$34, i);
				return target;
			}
		}
		
		/**
		 * Inserts the value as the ith "see_also" element
		 */
		public void insertSeeAlso(final int i,
		                          final java.lang.String seeAlso) {
			synchronized (monitor()) {
				check_orphaned();
				final org.apache.xmlbeans.SimpleValue target = (org.apache.xmlbeans.SimpleValue) get_store().insert_element_user(SEEALSO$34,
				                                                                                                                 i);
				target.setStringValue(seeAlso);
			}
		}
		
		/**
		 * True if has "actual_time" element
		 */
		public boolean isSetActualTime() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(ACTUALTIME$64) != 0;
			}
		}
		
		/**
		 * True if has "alias" element
		 */
		public boolean isSetAlias() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(ALIAS$2) != 0;
			}
		}
		
		/**
		 * True if has "assigned_to" element
		 */
		public boolean isSetAssignedTo() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(ASSIGNEDTO$56) != 0;
			}
		}
		
		/**
		 * True if has "bug_file_loc" element
		 */
		public boolean isSetBugFileLoc() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(BUGFILELOC$36) != 0;
			}
		}
		
		/**
		 * True if has "bug_severity" element
		 */
		public boolean isSetBugSeverity() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(BUGSEVERITY$44) != 0;
			}
		}
		
		/**
		 * True if has "bug_status" element
		 */
		public boolean isSetBugStatus() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(BUGSTATUS$28) != 0;
			}
		}
		
		/**
		 * True if has "cclist_accessible" element
		 */
		public boolean isSetCclistAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(CCLISTACCESSIBLE$12) != 0;
			}
		}
		
		/**
		 * True if has "classification" element
		 */
		public boolean isSetClassification() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(CLASSIFICATION$16) != 0;
			}
		}
		
		/**
		 * True if has "classification_id" element
		 */
		public boolean isSetClassificationId() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(CLASSIFICATIONID$14) != 0;
			}
		}
		
		/**
		 * True if has "component" element
		 */
		public boolean isSetComponent() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(COMPONENT$20) != 0;
			}
		}
		
		/**
		 * True if has "creation_ts" element
		 */
		public boolean isSetCreationTs() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(CREATIONTS$4) != 0;
			}
		}
		
		/**
		 * True if has "deadline" element
		 */
		public boolean isSetDeadline() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(DEADLINE$66) != 0;
			}
		}
		
		/**
		 * True if has "delta_ts" element
		 */
		public boolean isSetDeltaTs() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(DELTATS$8) != 0;
			}
		}
		
		/**
		 * True if has "dup_id" element
		 */
		public boolean isSetDupId() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(DUPID$32) != 0;
			}
		}
		
		/**
		 * True if has "error" attribute
		 */
		public boolean isSetError() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().find_attribute_user(ERROR$82) != null;
			}
		}
		
		/**
		 * True if has "estimated_time" element
		 */
		public boolean isSetEstimatedTime() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(ESTIMATEDTIME$60) != 0;
			}
		}
		
		/**
		 * True if has "everconfirmed" element
		 */
		public boolean isSetEverconfirmed() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(EVERCONFIRMED$52) != 0;
			}
		}
		
		/**
		 * True if has "op_sys" element
		 */
		public boolean isSetOpSys() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(OPSYS$26) != 0;
			}
		}
		
		/**
		 * True if has "priority" element
		 */
		public boolean isSetPriority() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(PRIORITY$42) != 0;
			}
		}
		
		/**
		 * True if has "product" element
		 */
		public boolean isSetProduct() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(PRODUCT$18) != 0;
			}
		}
		
		/**
		 * True if has "qa_contact" element
		 */
		public boolean isSetQaContact() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(QACONTACT$68) != 0;
			}
		}
		
		/**
		 * True if has "remaining_time" element
		 */
		public boolean isSetRemainingTime() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(REMAININGTIME$62) != 0;
			}
		}
		
		/**
		 * True if has "reporter" element
		 */
		public boolean isSetReporter() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(REPORTER$54) != 0;
			}
		}
		
		/**
		 * True if has "reporter_accessible" element
		 */
		public boolean isSetReporterAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(REPORTERACCESSIBLE$10) != 0;
			}
		}
		
		/**
		 * True if has "rep_platform" element
		 */
		public boolean isSetRepPlatform() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(REPPLATFORM$24) != 0;
			}
		}
		
		/**
		 * True if has "resolution" element
		 */
		public boolean isSetResolution() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(RESOLUTION$30) != 0;
			}
		}
		
		/**
		 * True if has "short_desc" element
		 */
		public boolean isSetShortDesc() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(SHORTDESC$6) != 0;
			}
		}
		
		/**
		 * True if has "status_whiteboard" element
		 */
		public boolean isSetStatusWhiteboard() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(STATUSWHITEBOARD$38) != 0;
			}
		}
		
		/**
		 * True if has "target_milestone" element
		 */
		public boolean isSetTargetMilestone() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(TARGETMILESTONE$46) != 0;
			}
		}
		
		/**
		 * True if has "token" element
		 */
		public boolean isSetToken() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(TOKEN$72) != 0;
			}
		}
		
		/**
		 * True if has "version" element
		 */
		public boolean isSetVersion() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(VERSION$22) != 0;
			}
		}
		
		/**
		 * True if has "votes" element
		 */
		public boolean isSetVotes() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(VOTES$70) != 0;
			}
		}
		
		/**
		 * Removes the ith "attachment" element
		 */
		public void removeAttachment(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(ATTACHMENT$80, i);
			}
		}
		
		/**
		 * Removes the ith "blocked" element
		 */
		public void removeBlocked(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(BLOCKED$50, i);
			}
		}
		
		/**
		 * Removes the ith "cc" element
		 */
		public void removeCc(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(CC$58, i);
			}
		}
		
		/**
		 * Removes the ith "dependson" element
		 */
		public void removeDependson(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(DEPENDSON$48, i);
			}
		}
		
		/**
		 * Removes the ith "flag" element
		 */
		public void removeFlag(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(FLAG$76, i);
			}
		}
		
		/**
		 * Removes the ith "group" element
		 */
		public void removeGroup(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(GROUP$74, i);
			}
		}
		
		/**
		 * Removes the ith "keywords" element
		 */
		public void removeKeywords(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(KEYWORDS$40, i);
			}
		}
		
		/**
		 * Removes the ith "long_desc" element
		 */
		public void removeLongDesc(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(LONGDESC$78, i);
			}
		}
		
		/**
		 * Removes the ith "see_also" element
		 */
		public void removeSeeAlso(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(SEEALSO$34, i);
			}
		}
		
		/**
		 * Sets the "actual_time" element
		 */
		public void setActualTime(final java.lang.String actualTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ACTUALTIME$64, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(ACTUALTIME$64);
				}
				target.setStringValue(actualTime);
			}
		}
		
		/**
		 * Sets the "alias" element
		 */
		public void setAlias(final java.lang.String alias) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ALIAS$2, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(ALIAS$2);
				}
				target.setStringValue(alias);
			}
		}
		
		/**
		 * Sets the "assigned_to" element
		 */
		public void setAssignedTo(final AssignedToDocument.AssignedTo assignedTo) {
			generatedSetterHelperImpl(assignedTo, ASSIGNEDTO$56, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets array of all "attachment" element WARNING: This method is not atomicaly synchronized.
		 */
		public void setAttachmentArray(final AttachmentDocument.Attachment[] attachmentArray) {
			check_orphaned();
			arraySetterHelper(attachmentArray, ATTACHMENT$80);
		}
		
		/**
		 * Sets ith "attachment" element
		 */
		public void setAttachmentArray(final int i,
		                               final AttachmentDocument.Attachment attachment) {
			synchronized (monitor()) {
				check_orphaned();
				AttachmentDocument.Attachment target = null;
				target = (AttachmentDocument.Attachment) get_store().find_element_user(ATTACHMENT$80, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(attachment);
			}
		}
		
		/**
		 * Sets ith "blocked" element
		 */
		public void setBlockedArray(final int i,
		                            final java.lang.String blocked) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BLOCKED$50, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.setStringValue(blocked);
			}
		}
		
		/**
		 * Sets array of all "blocked" element
		 */
		public void setBlockedArray(final java.lang.String[] blockedArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(blockedArray, BLOCKED$50);
			}
		}
		
		/**
		 * Sets the "bug_file_loc" element
		 */
		public void setBugFileLoc(final java.lang.String bugFileLoc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGFILELOC$36, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(BUGFILELOC$36);
				}
				target.setStringValue(bugFileLoc);
			}
		}
		
		/**
		 * Sets the "bug_id" element
		 */
		public void setBugId(final java.lang.String bugId) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGID$0, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(BUGID$0);
				}
				target.setStringValue(bugId);
			}
		}
		
		/**
		 * Sets the "bug_severity" element
		 */
		public void setBugSeverity(final java.lang.String bugSeverity) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGSEVERITY$44, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(BUGSEVERITY$44);
				}
				target.setStringValue(bugSeverity);
			}
		}
		
		/**
		 * Sets the "bug_status" element
		 */
		public void setBugStatus(final java.lang.String bugStatus) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGSTATUS$28, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(BUGSTATUS$28);
				}
				target.setStringValue(bugStatus);
			}
		}
		
		/**
		 * Sets ith "cc" element
		 */
		public void setCcArray(final int i,
		                       final java.lang.String cc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CC$58, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.setStringValue(cc);
			}
		}
		
		/**
		 * Sets array of all "cc" element
		 */
		public void setCcArray(final java.lang.String[] ccArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(ccArray, CC$58);
			}
		}
		
		/**
		 * Sets the "cclist_accessible" element
		 */
		public void setCclistAccessible(final java.lang.String cclistAccessible) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CCLISTACCESSIBLE$12, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(CCLISTACCESSIBLE$12);
				}
				target.setStringValue(cclistAccessible);
			}
		}
		
		/**
		 * Sets the "classification" element
		 */
		public void setClassification(final java.lang.String classification) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CLASSIFICATION$16, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(CLASSIFICATION$16);
				}
				target.setStringValue(classification);
			}
		}
		
		/**
		 * Sets the "classification_id" element
		 */
		public void setClassificationId(final java.lang.String classificationId) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CLASSIFICATIONID$14, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(CLASSIFICATIONID$14);
				}
				target.setStringValue(classificationId);
			}
		}
		
		/**
		 * Sets the "component" element
		 */
		public void setComponent(final java.lang.String component) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(COMPONENT$20, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(COMPONENT$20);
				}
				target.setStringValue(component);
			}
		}
		
		/**
		 * Sets the "creation_ts" element
		 */
		public void setCreationTs(final java.lang.String creationTs) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(CREATIONTS$4, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(CREATIONTS$4);
				}
				target.setStringValue(creationTs);
			}
		}
		
		/**
		 * Sets the "deadline" element
		 */
		public void setDeadline(final java.lang.String deadline) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DEADLINE$66, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(DEADLINE$66);
				}
				target.setStringValue(deadline);
			}
		}
		
		/**
		 * Sets the "delta_ts" element
		 */
		public void setDeltaTs(final java.lang.String deltaTs) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DELTATS$8, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(DELTATS$8);
				}
				target.setStringValue(deltaTs);
			}
		}
		
		/**
		 * Sets ith "dependson" element
		 */
		public void setDependsonArray(final int i,
		                              final java.lang.String dependson) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DEPENDSON$48, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.setStringValue(dependson);
			}
		}
		
		/**
		 * Sets array of all "dependson" element
		 */
		public void setDependsonArray(final java.lang.String[] dependsonArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(dependsonArray, DEPENDSON$48);
			}
		}
		
		/**
		 * Sets the "dup_id" element
		 */
		public void setDupId(final java.lang.String dupId) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(DUPID$32, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(DUPID$32);
				}
				target.setStringValue(dupId);
			}
		}
		
		/**
		 * Sets the "error" attribute
		 */
		public void setError(final BugDocument.Bug.Error.Enum error) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(ERROR$82);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_attribute_user(ERROR$82);
				}
				target.setEnumValue(error);
			}
		}
		
		/**
		 * Sets the "estimated_time" element
		 */
		public void setEstimatedTime(final java.lang.String estimatedTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ESTIMATEDTIME$60, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(ESTIMATEDTIME$60);
				}
				target.setStringValue(estimatedTime);
			}
		}
		
		/**
		 * Sets the "everconfirmed" element
		 */
		public void setEverconfirmed(final java.lang.String everconfirmed) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(EVERCONFIRMED$52, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(EVERCONFIRMED$52);
				}
				target.setStringValue(everconfirmed);
			}
		}
		
		/**
		 * Sets array of all "flag" element WARNING: This method is not atomicaly synchronized.
		 */
		public void setFlagArray(final FlagDocument.Flag[] flagArray) {
			check_orphaned();
			arraySetterHelper(flagArray, FLAG$76);
		}
		
		/**
		 * Sets ith "flag" element
		 */
		public void setFlagArray(final int i,
		                         final FlagDocument.Flag flag) {
			synchronized (monitor()) {
				check_orphaned();
				FlagDocument.Flag target = null;
				target = (FlagDocument.Flag) get_store().find_element_user(FLAG$76, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(flag);
			}
		}
		
		/**
		 * Sets array of all "group" element WARNING: This method is not atomicaly synchronized.
		 */
		public void setGroupArray(final GroupDocument.Group[] groupArray) {
			check_orphaned();
			arraySetterHelper(groupArray, GROUP$74);
		}
		
		/**
		 * Sets ith "group" element
		 */
		public void setGroupArray(final int i,
		                          final GroupDocument.Group group) {
			synchronized (monitor()) {
				check_orphaned();
				GroupDocument.Group target = null;
				target = (GroupDocument.Group) get_store().find_element_user(GROUP$74, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(group);
			}
		}
		
		/**
		 * Sets ith "keywords" element
		 */
		public void setKeywordsArray(final int i,
		                             final java.lang.String keywords) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(KEYWORDS$40, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.setStringValue(keywords);
			}
		}
		
		/**
		 * Sets array of all "keywords" element
		 */
		public void setKeywordsArray(final java.lang.String[] keywordsArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(keywordsArray, KEYWORDS$40);
			}
		}
		
		/**
		 * Sets ith "long_desc" element
		 */
		public void setLongDescArray(final int i,
		                             final LongDescDocument.LongDesc longDesc) {
			synchronized (monitor()) {
				check_orphaned();
				LongDescDocument.LongDesc target = null;
				target = (LongDescDocument.LongDesc) get_store().find_element_user(LONGDESC$78, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(longDesc);
			}
		}
		
		/**
		 * Sets array of all "long_desc" element WARNING: This method is not atomicaly synchronized.
		 */
		public void setLongDescArray(final LongDescDocument.LongDesc[] longDescArray) {
			check_orphaned();
			arraySetterHelper(longDescArray, LONGDESC$78);
		}
		
		/**
		 * Sets the "op_sys" element
		 */
		public void setOpSys(final java.lang.String opSys) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(OPSYS$26, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(OPSYS$26);
				}
				target.setStringValue(opSys);
			}
		}
		
		/**
		 * Sets the "priority" element
		 */
		public void setPriority(final java.lang.String priority) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(PRIORITY$42, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(PRIORITY$42);
				}
				target.setStringValue(priority);
			}
		}
		
		/**
		 * Sets the "product" element
		 */
		public void setProduct(final java.lang.String product) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(PRODUCT$18, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(PRODUCT$18);
				}
				target.setStringValue(product);
			}
		}
		
		/**
		 * Sets the "qa_contact" element
		 */
		public void setQaContact(final QaContactDocument.QaContact qaContact) {
			generatedSetterHelperImpl(qaContact, QACONTACT$68, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets the "remaining_time" element
		 */
		public void setRemainingTime(final java.lang.String remainingTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(REMAININGTIME$62, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(REMAININGTIME$62);
				}
				target.setStringValue(remainingTime);
			}
		}
		
		/**
		 * Sets the "reporter" element
		 */
		public void setReporter(final ReporterDocument.Reporter reporter) {
			generatedSetterHelperImpl(reporter, REPORTER$54, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets the "reporter_accessible" element
		 */
		public void setReporterAccessible(final java.lang.String reporterAccessible) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(REPORTERACCESSIBLE$10, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(REPORTERACCESSIBLE$10);
				}
				target.setStringValue(reporterAccessible);
			}
		}
		
		/**
		 * Sets the "rep_platform" element
		 */
		public void setRepPlatform(final java.lang.String repPlatform) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(REPPLATFORM$24, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(REPPLATFORM$24);
				}
				target.setStringValue(repPlatform);
			}
		}
		
		/**
		 * Sets the "resolution" element
		 */
		public void setResolution(final java.lang.String resolution) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(RESOLUTION$30, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(RESOLUTION$30);
				}
				target.setStringValue(resolution);
			}
		}
		
		/**
		 * Sets ith "see_also" element
		 */
		public void setSeeAlsoArray(final int i,
		                            final java.lang.String seeAlso) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(SEEALSO$34, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.setStringValue(seeAlso);
			}
		}
		
		/**
		 * Sets array of all "see_also" element
		 */
		public void setSeeAlsoArray(final java.lang.String[] seeAlsoArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(seeAlsoArray, SEEALSO$34);
			}
		}
		
		/**
		 * Sets the "short_desc" element
		 */
		public void setShortDesc(final java.lang.String shortDesc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(SHORTDESC$6, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(SHORTDESC$6);
				}
				target.setStringValue(shortDesc);
			}
		}
		
		/**
		 * Sets the "status_whiteboard" element
		 */
		public void setStatusWhiteboard(final java.lang.String statusWhiteboard) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(STATUSWHITEBOARD$38, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(STATUSWHITEBOARD$38);
				}
				target.setStringValue(statusWhiteboard);
			}
		}
		
		/**
		 * Sets the "target_milestone" element
		 */
		public void setTargetMilestone(final java.lang.String targetMilestone) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(TARGETMILESTONE$46, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(TARGETMILESTONE$46);
				}
				target.setStringValue(targetMilestone);
			}
		}
		
		/**
		 * Sets the "token" element
		 */
		public void setToken(final java.lang.String token) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(TOKEN$72, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(TOKEN$72);
				}
				target.setStringValue(token);
			}
		}
		
		/**
		 * Sets the "version" element
		 */
		public void setVersion(final java.lang.String version) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(VERSION$22, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(VERSION$22);
				}
				target.setStringValue(version);
			}
		}
		
		/**
		 * Sets the "votes" element
		 */
		public void setVotes(final java.lang.String votes) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(VOTES$70, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(VOTES$70);
				}
				target.setStringValue(votes);
			}
		}
		
		/**
		 * Returns number of "attachment" element
		 */
		public int sizeOfAttachmentArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(ATTACHMENT$80);
			}
		}
		
		/**
		 * Returns number of "blocked" element
		 */
		public int sizeOfBlockedArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(BLOCKED$50);
			}
		}
		
		/**
		 * Returns number of "cc" element
		 */
		public int sizeOfCcArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(CC$58);
			}
		}
		
		/**
		 * Returns number of "dependson" element
		 */
		public int sizeOfDependsonArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(DEPENDSON$48);
			}
		}
		
		/**
		 * Returns number of "flag" element
		 */
		public int sizeOfFlagArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(FLAG$76);
			}
		}
		
		/**
		 * Returns number of "group" element
		 */
		public int sizeOfGroupArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(GROUP$74);
			}
		}
		
		/**
		 * Returns number of "keywords" element
		 */
		public int sizeOfKeywordsArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(KEYWORDS$40);
			}
		}
		
		/**
		 * Returns number of "long_desc" element
		 */
		public int sizeOfLongDescArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(LONGDESC$78);
			}
		}
		
		/**
		 * Returns number of "see_also" element
		 */
		public int sizeOfSeeAlsoArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(SEEALSO$34);
			}
		}
		
		/**
		 * Unsets the "actual_time" element
		 */
		public void unsetActualTime() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(ACTUALTIME$64, 0);
			}
		}
		
		/**
		 * Unsets the "alias" element
		 */
		public void unsetAlias() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(ALIAS$2, 0);
			}
		}
		
		/**
		 * Unsets the "assigned_to" element
		 */
		public void unsetAssignedTo() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(ASSIGNEDTO$56, 0);
			}
		}
		
		/**
		 * Unsets the "bug_file_loc" element
		 */
		public void unsetBugFileLoc() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(BUGFILELOC$36, 0);
			}
		}
		
		/**
		 * Unsets the "bug_severity" element
		 */
		public void unsetBugSeverity() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(BUGSEVERITY$44, 0);
			}
		}
		
		/**
		 * Unsets the "bug_status" element
		 */
		public void unsetBugStatus() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(BUGSTATUS$28, 0);
			}
		}
		
		/**
		 * Unsets the "cclist_accessible" element
		 */
		public void unsetCclistAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(CCLISTACCESSIBLE$12, 0);
			}
		}
		
		/**
		 * Unsets the "classification" element
		 */
		public void unsetClassification() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(CLASSIFICATION$16, 0);
			}
		}
		
		/**
		 * Unsets the "classification_id" element
		 */
		public void unsetClassificationId() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(CLASSIFICATIONID$14, 0);
			}
		}
		
		/**
		 * Unsets the "component" element
		 */
		public void unsetComponent() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(COMPONENT$20, 0);
			}
		}
		
		/**
		 * Unsets the "creation_ts" element
		 */
		public void unsetCreationTs() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(CREATIONTS$4, 0);
			}
		}
		
		/**
		 * Unsets the "deadline" element
		 */
		public void unsetDeadline() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(DEADLINE$66, 0);
			}
		}
		
		/**
		 * Unsets the "delta_ts" element
		 */
		public void unsetDeltaTs() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(DELTATS$8, 0);
			}
		}
		
		/**
		 * Unsets the "dup_id" element
		 */
		public void unsetDupId() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(DUPID$32, 0);
			}
		}
		
		/**
		 * Unsets the "error" attribute
		 */
		public void unsetError() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_attribute(ERROR$82);
			}
		}
		
		/**
		 * Unsets the "estimated_time" element
		 */
		public void unsetEstimatedTime() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(ESTIMATEDTIME$60, 0);
			}
		}
		
		/**
		 * Unsets the "everconfirmed" element
		 */
		public void unsetEverconfirmed() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(EVERCONFIRMED$52, 0);
			}
		}
		
		/**
		 * Unsets the "op_sys" element
		 */
		public void unsetOpSys() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(OPSYS$26, 0);
			}
		}
		
		/**
		 * Unsets the "priority" element
		 */
		public void unsetPriority() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(PRIORITY$42, 0);
			}
		}
		
		/**
		 * Unsets the "product" element
		 */
		public void unsetProduct() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(PRODUCT$18, 0);
			}
		}
		
		/**
		 * Unsets the "qa_contact" element
		 */
		public void unsetQaContact() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(QACONTACT$68, 0);
			}
		}
		
		/**
		 * Unsets the "remaining_time" element
		 */
		public void unsetRemainingTime() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(REMAININGTIME$62, 0);
			}
		}
		
		/**
		 * Unsets the "reporter" element
		 */
		public void unsetReporter() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(REPORTER$54, 0);
			}
		}
		
		/**
		 * Unsets the "reporter_accessible" element
		 */
		public void unsetReporterAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(REPORTERACCESSIBLE$10, 0);
			}
		}
		
		/**
		 * Unsets the "rep_platform" element
		 */
		public void unsetRepPlatform() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(REPPLATFORM$24, 0);
			}
		}
		
		/**
		 * Unsets the "resolution" element
		 */
		public void unsetResolution() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(RESOLUTION$30, 0);
			}
		}
		
		/**
		 * Unsets the "short_desc" element
		 */
		public void unsetShortDesc() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(SHORTDESC$6, 0);
			}
		}
		
		/**
		 * Unsets the "status_whiteboard" element
		 */
		public void unsetStatusWhiteboard() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(STATUSWHITEBOARD$38, 0);
			}
		}
		
		/**
		 * Unsets the "target_milestone" element
		 */
		public void unsetTargetMilestone() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(TARGETMILESTONE$46, 0);
			}
		}
		
		/**
		 * Unsets the "token" element
		 */
		public void unsetToken() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(TOKEN$72, 0);
			}
		}
		
		/**
		 * Unsets the "version" element
		 */
		public void unsetVersion() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(VERSION$22, 0);
			}
		}
		
		/**
		 * Unsets the "votes" element
		 */
		public void unsetVotes() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(VOTES$70, 0);
			}
		}
		
		/**
		 * Gets (as xml) the "actual_time" element
		 */
		public org.apache.xmlbeans.XmlString xgetActualTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ACTUALTIME$64, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "alias" element
		 */
		public org.apache.xmlbeans.XmlString xgetAlias() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ALIAS$2, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) array of all "blocked" elements
		 */
		public org.apache.xmlbeans.XmlString[] xgetBlockedArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(BLOCKED$50, targetList);
				final org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets (as xml) ith "blocked" element
		 */
		public org.apache.xmlbeans.XmlString xgetBlockedArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BLOCKED$50, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "bug_file_loc" element
		 */
		public org.apache.xmlbeans.XmlString xgetBugFileLoc() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGFILELOC$36, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "bug_id" element
		 */
		public org.apache.xmlbeans.XmlString xgetBugId() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGID$0, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "bug_severity" element
		 */
		public org.apache.xmlbeans.XmlString xgetBugSeverity() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGSEVERITY$44, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "bug_status" element
		 */
		public org.apache.xmlbeans.XmlString xgetBugStatus() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGSTATUS$28, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) array of all "cc" elements
		 */
		public org.apache.xmlbeans.XmlString[] xgetCcArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(CC$58, targetList);
				final org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets (as xml) ith "cc" element
		 */
		public org.apache.xmlbeans.XmlString xgetCcArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CC$58, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "cclist_accessible" element
		 */
		public org.apache.xmlbeans.XmlString xgetCclistAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CCLISTACCESSIBLE$12, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "classification" element
		 */
		public org.apache.xmlbeans.XmlString xgetClassification() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CLASSIFICATION$16, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "classification_id" element
		 */
		public org.apache.xmlbeans.XmlString xgetClassificationId() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CLASSIFICATIONID$14, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "component" element
		 */
		public org.apache.xmlbeans.XmlString xgetComponent() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(COMPONENT$20, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "creation_ts" element
		 */
		public org.apache.xmlbeans.XmlString xgetCreationTs() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CREATIONTS$4, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "deadline" element
		 */
		public org.apache.xmlbeans.XmlString xgetDeadline() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DEADLINE$66, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "delta_ts" element
		 */
		public org.apache.xmlbeans.XmlString xgetDeltaTs() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DELTATS$8, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) array of all "dependson" elements
		 */
		public org.apache.xmlbeans.XmlString[] xgetDependsonArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(DEPENDSON$48, targetList);
				final org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets (as xml) ith "dependson" element
		 */
		public org.apache.xmlbeans.XmlString xgetDependsonArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DEPENDSON$48, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "dup_id" element
		 */
		public org.apache.xmlbeans.XmlString xgetDupId() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DUPID$32, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "error" attribute
		 */
		public BugDocument.Bug.Error xgetError() {
			synchronized (monitor()) {
				check_orphaned();
				BugDocument.Bug.Error target = null;
				target = (BugDocument.Bug.Error) get_store().find_attribute_user(ERROR$82);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "estimated_time" element
		 */
		public org.apache.xmlbeans.XmlString xgetEstimatedTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ESTIMATEDTIME$60, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "everconfirmed" element
		 */
		public org.apache.xmlbeans.XmlString xgetEverconfirmed() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(EVERCONFIRMED$52, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) array of all "keywords" elements
		 */
		public org.apache.xmlbeans.XmlString[] xgetKeywordsArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(KEYWORDS$40, targetList);
				final org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets (as xml) ith "keywords" element
		 */
		public org.apache.xmlbeans.XmlString xgetKeywordsArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(KEYWORDS$40, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "op_sys" element
		 */
		public org.apache.xmlbeans.XmlString xgetOpSys() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(OPSYS$26, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "priority" element
		 */
		public org.apache.xmlbeans.XmlString xgetPriority() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(PRIORITY$42, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "product" element
		 */
		public org.apache.xmlbeans.XmlString xgetProduct() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(PRODUCT$18, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "remaining_time" element
		 */
		public org.apache.xmlbeans.XmlString xgetRemainingTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(REMAININGTIME$62, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "reporter_accessible" element
		 */
		public org.apache.xmlbeans.XmlString xgetReporterAccessible() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(REPORTERACCESSIBLE$10, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "rep_platform" element
		 */
		public org.apache.xmlbeans.XmlString xgetRepPlatform() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(REPPLATFORM$24, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "resolution" element
		 */
		public org.apache.xmlbeans.XmlString xgetResolution() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(RESOLUTION$30, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) array of all "see_also" elements
		 */
		public org.apache.xmlbeans.XmlString[] xgetSeeAlsoArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(SEEALSO$34, targetList);
				final org.apache.xmlbeans.XmlString[] result = new org.apache.xmlbeans.XmlString[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets (as xml) ith "see_also" element
		 */
		public org.apache.xmlbeans.XmlString xgetSeeAlsoArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(SEEALSO$34, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "short_desc" element
		 */
		public org.apache.xmlbeans.XmlString xgetShortDesc() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(SHORTDESC$6, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "status_whiteboard" element
		 */
		public org.apache.xmlbeans.XmlString xgetStatusWhiteboard() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(STATUSWHITEBOARD$38, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "target_milestone" element
		 */
		public org.apache.xmlbeans.XmlString xgetTargetMilestone() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(TARGETMILESTONE$46, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "token" element
		 */
		public org.apache.xmlbeans.XmlString xgetToken() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(TOKEN$72, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "version" element
		 */
		public org.apache.xmlbeans.XmlString xgetVersion() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(VERSION$22, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "votes" element
		 */
		public org.apache.xmlbeans.XmlString xgetVotes() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(VOTES$70, 0);
				return target;
			}
		}
		
		/**
		 * Sets (as xml) the "actual_time" element
		 */
		public void xsetActualTime(final org.apache.xmlbeans.XmlString actualTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ACTUALTIME$64, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(ACTUALTIME$64);
				}
				target.set(actualTime);
			}
		}
		
		/**
		 * Sets (as xml) the "alias" element
		 */
		public void xsetAlias(final org.apache.xmlbeans.XmlString alias) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ALIAS$2, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(ALIAS$2);
				}
				target.set(alias);
			}
		}
		
		/**
		 * Sets (as xml) ith "blocked" element
		 */
		public void xsetBlockedArray(final int i,
		                             final org.apache.xmlbeans.XmlString blocked) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BLOCKED$50, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(blocked);
			}
		}
		
		/**
		 * Sets (as xml) array of all "blocked" element
		 */
		public void xsetBlockedArray(final org.apache.xmlbeans.XmlString[] blockedArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(blockedArray, BLOCKED$50);
			}
		}
		
		/**
		 * Sets (as xml) the "bug_file_loc" element
		 */
		public void xsetBugFileLoc(final org.apache.xmlbeans.XmlString bugFileLoc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGFILELOC$36, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(BUGFILELOC$36);
				}
				target.set(bugFileLoc);
			}
		}
		
		/**
		 * Sets (as xml) the "bug_id" element
		 */
		public void xsetBugId(final org.apache.xmlbeans.XmlString bugId) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGID$0, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(BUGID$0);
				}
				target.set(bugId);
			}
		}
		
		/**
		 * Sets (as xml) the "bug_severity" element
		 */
		public void xsetBugSeverity(final org.apache.xmlbeans.XmlString bugSeverity) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGSEVERITY$44, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(BUGSEVERITY$44);
				}
				target.set(bugSeverity);
			}
		}
		
		/**
		 * Sets (as xml) the "bug_status" element
		 */
		public void xsetBugStatus(final org.apache.xmlbeans.XmlString bugStatus) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGSTATUS$28, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(BUGSTATUS$28);
				}
				target.set(bugStatus);
			}
		}
		
		/**
		 * Sets (as xml) ith "cc" element
		 */
		public void xsetCcArray(final int i,
		                        final org.apache.xmlbeans.XmlString cc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CC$58, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(cc);
			}
		}
		
		/**
		 * Sets (as xml) array of all "cc" element
		 */
		public void xsetCcArray(final org.apache.xmlbeans.XmlString[] ccArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(ccArray, CC$58);
			}
		}
		
		/**
		 * Sets (as xml) the "cclist_accessible" element
		 */
		public void xsetCclistAccessible(final org.apache.xmlbeans.XmlString cclistAccessible) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CCLISTACCESSIBLE$12, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(CCLISTACCESSIBLE$12);
				}
				target.set(cclistAccessible);
			}
		}
		
		/**
		 * Sets (as xml) the "classification" element
		 */
		public void xsetClassification(final org.apache.xmlbeans.XmlString classification) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CLASSIFICATION$16, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(CLASSIFICATION$16);
				}
				target.set(classification);
			}
		}
		
		/**
		 * Sets (as xml) the "classification_id" element
		 */
		public void xsetClassificationId(final org.apache.xmlbeans.XmlString classificationId) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CLASSIFICATIONID$14, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(CLASSIFICATIONID$14);
				}
				target.set(classificationId);
			}
		}
		
		/**
		 * Sets (as xml) the "component" element
		 */
		public void xsetComponent(final org.apache.xmlbeans.XmlString component) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(COMPONENT$20, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(COMPONENT$20);
				}
				target.set(component);
			}
		}
		
		/**
		 * Sets (as xml) the "creation_ts" element
		 */
		public void xsetCreationTs(final org.apache.xmlbeans.XmlString creationTs) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(CREATIONTS$4, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(CREATIONTS$4);
				}
				target.set(creationTs);
			}
		}
		
		/**
		 * Sets (as xml) the "deadline" element
		 */
		public void xsetDeadline(final org.apache.xmlbeans.XmlString deadline) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DEADLINE$66, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(DEADLINE$66);
				}
				target.set(deadline);
			}
		}
		
		/**
		 * Sets (as xml) the "delta_ts" element
		 */
		public void xsetDeltaTs(final org.apache.xmlbeans.XmlString deltaTs) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DELTATS$8, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(DELTATS$8);
				}
				target.set(deltaTs);
			}
		}
		
		/**
		 * Sets (as xml) ith "dependson" element
		 */
		public void xsetDependsonArray(final int i,
		                               final org.apache.xmlbeans.XmlString dependson) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DEPENDSON$48, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(dependson);
			}
		}
		
		/**
		 * Sets (as xml) array of all "dependson" element
		 */
		public void xsetDependsonArray(final org.apache.xmlbeans.XmlString[] dependsonArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(dependsonArray, DEPENDSON$48);
			}
		}
		
		/**
		 * Sets (as xml) the "dup_id" element
		 */
		public void xsetDupId(final org.apache.xmlbeans.XmlString dupId) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(DUPID$32, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(DUPID$32);
				}
				target.set(dupId);
			}
		}
		
		/**
		 * Sets (as xml) the "error" attribute
		 */
		public void xsetError(final BugDocument.Bug.Error error) {
			synchronized (monitor()) {
				check_orphaned();
				BugDocument.Bug.Error target = null;
				target = (BugDocument.Bug.Error) get_store().find_attribute_user(ERROR$82);
				if (target == null) {
					target = (BugDocument.Bug.Error) get_store().add_attribute_user(ERROR$82);
				}
				target.set(error);
			}
		}
		
		/**
		 * Sets (as xml) the "estimated_time" element
		 */
		public void xsetEstimatedTime(final org.apache.xmlbeans.XmlString estimatedTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ESTIMATEDTIME$60, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(ESTIMATEDTIME$60);
				}
				target.set(estimatedTime);
			}
		}
		
		/**
		 * Sets (as xml) the "everconfirmed" element
		 */
		public void xsetEverconfirmed(final org.apache.xmlbeans.XmlString everconfirmed) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(EVERCONFIRMED$52, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(EVERCONFIRMED$52);
				}
				target.set(everconfirmed);
			}
		}
		
		/**
		 * Sets (as xml) ith "keywords" element
		 */
		public void xsetKeywordsArray(final int i,
		                              final org.apache.xmlbeans.XmlString keywords) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(KEYWORDS$40, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(keywords);
			}
		}
		
		/**
		 * Sets (as xml) array of all "keywords" element
		 */
		public void xsetKeywordsArray(final org.apache.xmlbeans.XmlString[] keywordsArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(keywordsArray, KEYWORDS$40);
			}
		}
		
		/**
		 * Sets (as xml) the "op_sys" element
		 */
		public void xsetOpSys(final org.apache.xmlbeans.XmlString opSys) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(OPSYS$26, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(OPSYS$26);
				}
				target.set(opSys);
			}
		}
		
		/**
		 * Sets (as xml) the "priority" element
		 */
		public void xsetPriority(final org.apache.xmlbeans.XmlString priority) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(PRIORITY$42, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(PRIORITY$42);
				}
				target.set(priority);
			}
		}
		
		/**
		 * Sets (as xml) the "product" element
		 */
		public void xsetProduct(final org.apache.xmlbeans.XmlString product) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(PRODUCT$18, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(PRODUCT$18);
				}
				target.set(product);
			}
		}
		
		/**
		 * Sets (as xml) the "remaining_time" element
		 */
		public void xsetRemainingTime(final org.apache.xmlbeans.XmlString remainingTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(REMAININGTIME$62, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(REMAININGTIME$62);
				}
				target.set(remainingTime);
			}
		}
		
		/**
		 * Sets (as xml) the "reporter_accessible" element
		 */
		public void xsetReporterAccessible(final org.apache.xmlbeans.XmlString reporterAccessible) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(REPORTERACCESSIBLE$10, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(REPORTERACCESSIBLE$10);
				}
				target.set(reporterAccessible);
			}
		}
		
		/**
		 * Sets (as xml) the "rep_platform" element
		 */
		public void xsetRepPlatform(final org.apache.xmlbeans.XmlString repPlatform) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(REPPLATFORM$24, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(REPPLATFORM$24);
				}
				target.set(repPlatform);
			}
		}
		
		/**
		 * Sets (as xml) the "resolution" element
		 */
		public void xsetResolution(final org.apache.xmlbeans.XmlString resolution) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(RESOLUTION$30, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(RESOLUTION$30);
				}
				target.set(resolution);
			}
		}
		
		/**
		 * Sets (as xml) ith "see_also" element
		 */
		public void xsetSeeAlsoArray(final int i,
		                             final org.apache.xmlbeans.XmlString seeAlso) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(SEEALSO$34, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(seeAlso);
			}
		}
		
		/**
		 * Sets (as xml) array of all "see_also" element
		 */
		public void xsetSeeAlsoArray(final org.apache.xmlbeans.XmlString[] seeAlsoArray) {
			synchronized (monitor()) {
				check_orphaned();
				arraySetterHelper(seeAlsoArray, SEEALSO$34);
			}
		}
		
		/**
		 * Sets (as xml) the "short_desc" element
		 */
		public void xsetShortDesc(final org.apache.xmlbeans.XmlString shortDesc) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(SHORTDESC$6, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(SHORTDESC$6);
				}
				target.set(shortDesc);
			}
		}
		
		/**
		 * Sets (as xml) the "status_whiteboard" element
		 */
		public void xsetStatusWhiteboard(final org.apache.xmlbeans.XmlString statusWhiteboard) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(STATUSWHITEBOARD$38, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(STATUSWHITEBOARD$38);
				}
				target.set(statusWhiteboard);
			}
		}
		
		/**
		 * Sets (as xml) the "target_milestone" element
		 */
		public void xsetTargetMilestone(final org.apache.xmlbeans.XmlString targetMilestone) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(TARGETMILESTONE$46, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(TARGETMILESTONE$46);
				}
				target.set(targetMilestone);
			}
		}
		
		/**
		 * Sets (as xml) the "token" element
		 */
		public void xsetToken(final org.apache.xmlbeans.XmlString token) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(TOKEN$72, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(TOKEN$72);
				}
				target.set(token);
			}
		}
		
		/**
		 * Sets (as xml) the "version" element
		 */
		public void xsetVersion(final org.apache.xmlbeans.XmlString version) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(VERSION$22, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(VERSION$22);
				}
				target.set(version);
			}
		}
		
		/**
		 * Sets (as xml) the "votes" element
		 */
		public void xsetVotes(final org.apache.xmlbeans.XmlString votes) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(VOTES$70, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(VOTES$70);
				}
				target.set(votes);
			}
		}
	}
	
	private static final long                      serialVersionUID = 1L;
	
	private static final javax.xml.namespace.QName BUG$0            = new javax.xml.namespace.QName("", "bug");
	
	public BugDocumentImpl(final org.apache.xmlbeans.SchemaType sType) {
		super(sType);
	}
	
	/**
	 * Appends and returns a new empty "bug" element
	 */
	public BugDocument.Bug addNewBug() {
		synchronized (monitor()) {
			check_orphaned();
			BugDocument.Bug target = null;
			target = (BugDocument.Bug) get_store().add_element_user(BUG$0);
			return target;
		}
	}
	
	/**
	 * Gets the "bug" element
	 */
	public BugDocument.Bug getBug() {
		synchronized (monitor()) {
			check_orphaned();
			BugDocument.Bug target = null;
			target = (BugDocument.Bug) get_store().find_element_user(BUG$0, 0);
			if (target == null) {
				return null;
			}
			return target;
		}
	}
	
	/**
	 * Sets the "bug" element
	 */
	public void setBug(final BugDocument.Bug bug) {
		generatedSetterHelperImpl(bug, BUG$0, 0,
		                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
	}
}

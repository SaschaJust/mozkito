/*
 * An XML document type. Localname: bugzilla Namespace: Java type: BugzillaDocument Automatically generated - do not
 * modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BugDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BugzillaDocument;

/**
 * A document containing one bugzilla(@) element.
 * 
 * This is a complex type.
 */
public class BugzillaDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements
        BugzillaDocument {
	
	/**
	 * An XML bugzilla(@).
	 * 
	 * This is a complex type.
	 */
	public static class BugzillaImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements
	        BugzillaDocument.Bugzilla {
		
		private static final long                      serialVersionUID = 1L;
		
		private static final javax.xml.namespace.QName BUG$0            = new javax.xml.namespace.QName("", "bug");
		
		private static final javax.xml.namespace.QName VERSION$2        = new javax.xml.namespace.QName("", "version");
		private static final javax.xml.namespace.QName URLBASE$4        = new javax.xml.namespace.QName("", "urlbase");
		private static final javax.xml.namespace.QName MAINTAINER$6     = new javax.xml.namespace.QName("",
		                                                                                                "maintainer");
		private static final javax.xml.namespace.QName EXPORTER$8       = new javax.xml.namespace.QName("", "exporter");
		
		public BugzillaImpl(final org.apache.xmlbeans.SchemaType sType) {
			super(sType);
		}
		
		/**
		 * Appends and returns a new empty value (as xml) as the last "bug" element
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
		 * Appends and returns a new empty "exporter" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType addNewExporter() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().add_attribute_user(EXPORTER$8);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty "maintainer" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType addNewMaintainer() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().add_attribute_user(MAINTAINER$6);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty "urlbase" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType addNewUrlbase() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().add_attribute_user(URLBASE$4);
				return target;
			}
		}
		
		/**
		 * Appends and returns a new empty "version" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType addNewVersion() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().add_attribute_user(VERSION$2);
				return target;
			}
		}
		
		/**
		 * Gets array of all "bug" elements
		 */
		public BugDocument.Bug[] getBugArray() {
			synchronized (monitor()) {
				check_orphaned();
				final java.util.List targetList = new java.util.ArrayList();
				get_store().find_all_element_users(BUG$0, targetList);
				final BugDocument.Bug[] result = new BugDocument.Bug[targetList.size()];
				targetList.toArray(result);
				return result;
			}
		}
		
		/**
		 * Gets ith "bug" element
		 */
		public BugDocument.Bug getBugArray(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				BugDocument.Bug target = null;
				target = (BugDocument.Bug) get_store().find_element_user(BUG$0, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				return target;
			}
		}
		
		/**
		 * Gets the "exporter" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType getExporter() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().find_attribute_user(EXPORTER$8);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets the "maintainer" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType getMaintainer() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().find_attribute_user(MAINTAINER$6);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets the "urlbase" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType getUrlbase() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().find_attribute_user(URLBASE$4);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets the "version" attribute
		 */
		public org.apache.xmlbeans.XmlAnySimpleType getVersion() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlAnySimpleType target = null;
				target = (org.apache.xmlbeans.XmlAnySimpleType) get_store().find_attribute_user(VERSION$2);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Inserts and returns a new empty value (as xml) as the ith "bug" element
		 */
		public BugDocument.Bug insertNewBug(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				BugDocument.Bug target = null;
				target = (BugDocument.Bug) get_store().insert_element_user(BUG$0, i);
				return target;
			}
		}
		
		/**
		 * True if has "exporter" attribute
		 */
		public boolean isSetExporter() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().find_attribute_user(EXPORTER$8) != null;
			}
		}
		
		/**
		 * Removes the ith "bug" element
		 */
		public void removeBug(final int i) {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(BUG$0, i);
			}
		}
		
		/**
		 * Sets array of all "bug" element WARNING: This method is not atomicaly synchronized.
		 */
		public void setBugArray(final BugDocument.Bug[] bugArray) {
			check_orphaned();
			arraySetterHelper(bugArray, BUG$0);
		}
		
		/**
		 * Sets ith "bug" element
		 */
		public void setBugArray(final int i,
		                        final BugDocument.Bug bug) {
			synchronized (monitor()) {
				check_orphaned();
				BugDocument.Bug target = null;
				target = (BugDocument.Bug) get_store().find_element_user(BUG$0, i);
				if (target == null) {
					throw new IndexOutOfBoundsException();
				}
				target.set(bug);
			}
		}
		
		/**
		 * Sets the "exporter" attribute
		 */
		public void setExporter(final org.apache.xmlbeans.XmlAnySimpleType exporter) {
			generatedSetterHelperImpl(exporter, EXPORTER$8, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets the "maintainer" attribute
		 */
		public void setMaintainer(final org.apache.xmlbeans.XmlAnySimpleType maintainer) {
			generatedSetterHelperImpl(maintainer, MAINTAINER$6, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets the "urlbase" attribute
		 */
		public void setUrlbase(final org.apache.xmlbeans.XmlAnySimpleType urlbase) {
			generatedSetterHelperImpl(urlbase, URLBASE$4, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets the "version" attribute
		 */
		public void setVersion(final org.apache.xmlbeans.XmlAnySimpleType version) {
			generatedSetterHelperImpl(version, VERSION$2, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Returns number of "bug" element
		 */
		public int sizeOfBugArray() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(BUG$0);
			}
		}
		
		/**
		 * Unsets the "exporter" attribute
		 */
		public void unsetExporter() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_attribute(EXPORTER$8);
			}
		}
	}
	
	private static final long                      serialVersionUID = 1L;
	
	private static final javax.xml.namespace.QName BUGZILLA$0       = new javax.xml.namespace.QName("", "bugzilla");
	
	public BugzillaDocumentImpl(final org.apache.xmlbeans.SchemaType sType) {
		super(sType);
	}
	
	/**
	 * Appends and returns a new empty "bugzilla" element
	 */
	public BugzillaDocument.Bugzilla addNewBugzilla() {
		synchronized (monitor()) {
			check_orphaned();
			BugzillaDocument.Bugzilla target = null;
			target = (BugzillaDocument.Bugzilla) get_store().add_element_user(BUGZILLA$0);
			return target;
		}
	}
	
	/**
	 * Gets the "bugzilla" element
	 */
	public BugzillaDocument.Bugzilla getBugzilla() {
		synchronized (monitor()) {
			check_orphaned();
			BugzillaDocument.Bugzilla target = null;
			target = (BugzillaDocument.Bugzilla) get_store().find_element_user(BUGZILLA$0, 0);
			if (target == null) {
				return null;
			}
			return target;
		}
	}
	
	/**
	 * Sets the "bugzilla" element
	 */
	public void setBugzilla(final BugzillaDocument.Bugzilla bugzilla) {
		generatedSetterHelperImpl(bugzilla, BUGZILLA$0, 0,
		                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
	}
}

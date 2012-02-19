/*
 * An XML document type. Localname: long_desc Namespace: Java type: LongDescDocument Automatically generated - do not
 * modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.LongDescDocument;
import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.WhoDocument;

/**
 * A document containing one long_desc(@) element.
 * 
 * This is a complex type.
 */
public class LongDescDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements
        LongDescDocument {
	
	/**
	 * An XML long_desc(@).
	 * 
	 * This is a complex type.
	 */
	public static class LongDescImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements
	        LongDescDocument.LongDesc {
		
		/**
		 * An XML isprivate(@).
		 * 
		 * This is an atomic type that is a restriction of LongDescDocument$LongDesc$Isprivate.
		 */
		public static class IsprivateImpl extends org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx
		        implements LongDescDocument.LongDesc.Isprivate {
			
			private static final long serialVersionUID = 1L;
			
			public IsprivateImpl(final org.apache.xmlbeans.SchemaType sType) {
				super(sType, false);
			}
			
			protected IsprivateImpl(final org.apache.xmlbeans.SchemaType sType, final boolean b) {
				super(sType, b);
			}
		}
		
		private static final long                      serialVersionUID = 1L;
		
		private static final javax.xml.namespace.QName COMMENTID$0      = new javax.xml.namespace.QName("", "commentid");
		private static final javax.xml.namespace.QName ATTACHID$2       = new javax.xml.namespace.QName("", "attachid");
		private static final javax.xml.namespace.QName WHO$4            = new javax.xml.namespace.QName("", "who");
		private static final javax.xml.namespace.QName BUGWHEN$6        = new javax.xml.namespace.QName("", "bug_when");
		private static final javax.xml.namespace.QName WORKTIME$8       = new javax.xml.namespace.QName("", "work_time");
		private static final javax.xml.namespace.QName THETEXT$10       = new javax.xml.namespace.QName("", "thetext");
		private static final javax.xml.namespace.QName ISPRIVATE$12     = new javax.xml.namespace.QName("", "isprivate");
		
		public LongDescImpl(final org.apache.xmlbeans.SchemaType sType) {
			super(sType);
		}
		
		/**
		 * Appends and returns a new empty "who" element
		 */
		public WhoDocument.Who addNewWho() {
			synchronized (monitor()) {
				check_orphaned();
				WhoDocument.Who target = null;
				target = (WhoDocument.Who) get_store().add_element_user(WHO$4);
				return target;
			}
		}
		
		/**
		 * Gets the "attachid" element
		 */
		public java.lang.String getAttachid() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ATTACHID$2, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "bug_when" element
		 */
		public java.lang.String getBugWhen() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGWHEN$6, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "commentid" element
		 */
		public java.lang.String getCommentid() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(COMMENTID$0, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "isprivate" attribute
		 */
		public LongDescDocument.LongDesc.Isprivate.Enum getIsprivate() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(ISPRIVATE$12);
				if (target == null) {
					return null;
				}
				return (LongDescDocument.LongDesc.Isprivate.Enum) target.getEnumValue();
			}
		}
		
		/**
		 * Gets the "thetext" element
		 */
		public java.lang.String getThetext() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(THETEXT$10, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * Gets the "who" element
		 */
		public WhoDocument.Who getWho() {
			synchronized (monitor()) {
				check_orphaned();
				WhoDocument.Who target = null;
				target = (WhoDocument.Who) get_store().find_element_user(WHO$4, 0);
				if (target == null) {
					return null;
				}
				return target;
			}
		}
		
		/**
		 * Gets the "work_time" element
		 */
		public java.lang.String getWorkTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(WORKTIME$8, 0);
				if (target == null) {
					return null;
				}
				return target.getStringValue();
			}
		}
		
		/**
		 * True if has "attachid" element
		 */
		public boolean isSetAttachid() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(ATTACHID$2) != 0;
			}
		}
		
		/**
		 * True if has "work_time" element
		 */
		public boolean isSetWorkTime() {
			synchronized (monitor()) {
				check_orphaned();
				return get_store().count_elements(WORKTIME$8) != 0;
			}
		}
		
		/**
		 * Sets the "attachid" element
		 */
		public void setAttachid(final java.lang.String attachid) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(ATTACHID$2, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(ATTACHID$2);
				}
				target.setStringValue(attachid);
			}
		}
		
		/**
		 * Sets the "bug_when" element
		 */
		public void setBugWhen(final java.lang.String bugWhen) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(BUGWHEN$6, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(BUGWHEN$6);
				}
				target.setStringValue(bugWhen);
			}
		}
		
		/**
		 * Sets the "commentid" element
		 */
		public void setCommentid(final java.lang.String commentid) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(COMMENTID$0, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(COMMENTID$0);
				}
				target.setStringValue(commentid);
			}
		}
		
		/**
		 * Sets the "isprivate" attribute
		 */
		public void setIsprivate(final LongDescDocument.LongDesc.Isprivate.Enum isprivate) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(ISPRIVATE$12);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_attribute_user(ISPRIVATE$12);
				}
				target.setEnumValue(isprivate);
			}
		}
		
		/**
		 * Sets the "thetext" element
		 */
		public void setThetext(final java.lang.String thetext) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(THETEXT$10, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(THETEXT$10);
				}
				target.setStringValue(thetext);
			}
		}
		
		/**
		 * Sets the "who" element
		 */
		public void setWho(final WhoDocument.Who who) {
			generatedSetterHelperImpl(who, WHO$4, 0,
			                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
		}
		
		/**
		 * Sets the "work_time" element
		 */
		public void setWorkTime(final java.lang.String workTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.SimpleValue target = null;
				target = (org.apache.xmlbeans.SimpleValue) get_store().find_element_user(WORKTIME$8, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.SimpleValue) get_store().add_element_user(WORKTIME$8);
				}
				target.setStringValue(workTime);
			}
		}
		
		/**
		 * Unsets the "attachid" element
		 */
		public void unsetAttachid() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(ATTACHID$2, 0);
			}
		}
		
		/**
		 * Unsets the "work_time" element
		 */
		public void unsetWorkTime() {
			synchronized (monitor()) {
				check_orphaned();
				get_store().remove_element(WORKTIME$8, 0);
			}
		}
		
		/**
		 * Gets (as xml) the "attachid" element
		 */
		public org.apache.xmlbeans.XmlString xgetAttachid() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ATTACHID$2, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "bug_when" element
		 */
		public org.apache.xmlbeans.XmlString xgetBugWhen() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGWHEN$6, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "commentid" element
		 */
		public org.apache.xmlbeans.XmlString xgetCommentid() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(COMMENTID$0, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "isprivate" attribute
		 */
		public LongDescDocument.LongDesc.Isprivate xgetIsprivate() {
			synchronized (monitor()) {
				check_orphaned();
				LongDescDocument.LongDesc.Isprivate target = null;
				target = (LongDescDocument.LongDesc.Isprivate) get_store().find_attribute_user(ISPRIVATE$12);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "thetext" element
		 */
		public org.apache.xmlbeans.XmlString xgetThetext() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(THETEXT$10, 0);
				return target;
			}
		}
		
		/**
		 * Gets (as xml) the "work_time" element
		 */
		public org.apache.xmlbeans.XmlString xgetWorkTime() {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(WORKTIME$8, 0);
				return target;
			}
		}
		
		/**
		 * Sets (as xml) the "attachid" element
		 */
		public void xsetAttachid(final org.apache.xmlbeans.XmlString attachid) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(ATTACHID$2, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(ATTACHID$2);
				}
				target.set(attachid);
			}
		}
		
		/**
		 * Sets (as xml) the "bug_when" element
		 */
		public void xsetBugWhen(final org.apache.xmlbeans.XmlString bugWhen) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(BUGWHEN$6, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(BUGWHEN$6);
				}
				target.set(bugWhen);
			}
		}
		
		/**
		 * Sets (as xml) the "commentid" element
		 */
		public void xsetCommentid(final org.apache.xmlbeans.XmlString commentid) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(COMMENTID$0, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(COMMENTID$0);
				}
				target.set(commentid);
			}
		}
		
		/**
		 * Sets (as xml) the "isprivate" attribute
		 */
		public void xsetIsprivate(final LongDescDocument.LongDesc.Isprivate isprivate) {
			synchronized (monitor()) {
				check_orphaned();
				LongDescDocument.LongDesc.Isprivate target = null;
				target = (LongDescDocument.LongDesc.Isprivate) get_store().find_attribute_user(ISPRIVATE$12);
				if (target == null) {
					target = (LongDescDocument.LongDesc.Isprivate) get_store().add_attribute_user(ISPRIVATE$12);
				}
				target.set(isprivate);
			}
		}
		
		/**
		 * Sets (as xml) the "thetext" element
		 */
		public void xsetThetext(final org.apache.xmlbeans.XmlString thetext) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(THETEXT$10, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(THETEXT$10);
				}
				target.set(thetext);
			}
		}
		
		/**
		 * Sets (as xml) the "work_time" element
		 */
		public void xsetWorkTime(final org.apache.xmlbeans.XmlString workTime) {
			synchronized (monitor()) {
				check_orphaned();
				org.apache.xmlbeans.XmlString target = null;
				target = (org.apache.xmlbeans.XmlString) get_store().find_element_user(WORKTIME$8, 0);
				if (target == null) {
					target = (org.apache.xmlbeans.XmlString) get_store().add_element_user(WORKTIME$8);
				}
				target.set(workTime);
			}
		}
	}
	
	private static final long                      serialVersionUID = 1L;
	
	private static final javax.xml.namespace.QName LONGDESC$0       = new javax.xml.namespace.QName("", "long_desc");
	
	public LongDescDocumentImpl(final org.apache.xmlbeans.SchemaType sType) {
		super(sType);
	}
	
	/**
	 * Appends and returns a new empty "long_desc" element
	 */
	public LongDescDocument.LongDesc addNewLongDesc() {
		synchronized (monitor()) {
			check_orphaned();
			LongDescDocument.LongDesc target = null;
			target = (LongDescDocument.LongDesc) get_store().add_element_user(LONGDESC$0);
			return target;
		}
	}
	
	/**
	 * Gets the "long_desc" element
	 */
	public LongDescDocument.LongDesc getLongDesc() {
		synchronized (monitor()) {
			check_orphaned();
			LongDescDocument.LongDesc target = null;
			target = (LongDescDocument.LongDesc) get_store().find_element_user(LONGDESC$0, 0);
			if (target == null) {
				return null;
			}
			return target;
		}
	}
	
	/**
	 * Sets the "long_desc" element
	 */
	public void setLongDesc(final LongDescDocument.LongDesc longDesc) {
		generatedSetterHelperImpl(longDesc, LONGDESC$0, 0,
		                          org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);
	}
}

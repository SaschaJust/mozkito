/*
 * An XML document type.
 * Localname: bug_when
 * Namespace: 
 * Java type: BugWhenDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BugWhenDocument;

/**
 * A document containing one bug_when(@) element.
 *
 * This is a complex type.
 */
public class BugWhenDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements BugWhenDocument
{
    private static final long serialVersionUID = 1L;
    
    public BugWhenDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BUGWHEN$0 = 
        new javax.xml.namespace.QName("", "bug_when");
    
    
    /**
     * Gets the "bug_when" element
     */
    public java.lang.String getBugWhen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGWHEN$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "bug_when" element
     */
    public org.apache.xmlbeans.XmlString xgetBugWhen()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGWHEN$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "bug_when" element
     */
    public void setBugWhen(java.lang.String bugWhen)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGWHEN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BUGWHEN$0);
            }
            target.setStringValue(bugWhen);
        }
    }
    
    /**
     * Sets (as xml) the "bug_when" element
     */
    public void xsetBugWhen(org.apache.xmlbeans.XmlString bugWhen)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGWHEN$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUGWHEN$0);
            }
            target.set(bugWhen);
        }
    }
}

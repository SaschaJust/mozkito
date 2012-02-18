/*
 * An XML document type.
 * Localname: bug_id
 * Namespace: 
 * Java type: BugIdDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BugIdDocument;

/**
 * A document containing one bug_id(@) element.
 *
 * This is a complex type.
 */
public class BugIdDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements BugIdDocument
{
    private static final long serialVersionUID = 1L;
    
    public BugIdDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BUGID$0 = 
        new javax.xml.namespace.QName("", "bug_id");
    
    
    /**
     * Gets the "bug_id" element
     */
    public java.lang.String getBugId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "bug_id" element
     */
    public org.apache.xmlbeans.XmlString xgetBugId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "bug_id" element
     */
    public void setBugId(java.lang.String bugId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BUGID$0);
            }
            target.setStringValue(bugId);
        }
    }
    
    /**
     * Sets (as xml) the "bug_id" element
     */
    public void xsetBugId(org.apache.xmlbeans.XmlString bugId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUGID$0);
            }
            target.set(bugId);
        }
    }
}

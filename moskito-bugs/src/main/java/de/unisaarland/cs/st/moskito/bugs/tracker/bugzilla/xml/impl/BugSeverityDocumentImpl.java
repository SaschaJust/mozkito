/*
 * An XML document type.
 * Localname: bug_severity
 * Namespace: 
 * Java type: BugSeverityDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BugSeverityDocument;

/**
 * A document containing one bug_severity(@) element.
 *
 * This is a complex type.
 */
public class BugSeverityDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements BugSeverityDocument
{
    private static final long serialVersionUID = 1L;
    
    public BugSeverityDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BUGSEVERITY$0 = 
        new javax.xml.namespace.QName("", "bug_severity");
    
    
    /**
     * Gets the "bug_severity" element
     */
    public java.lang.String getBugSeverity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGSEVERITY$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "bug_severity" element
     */
    public org.apache.xmlbeans.XmlString xgetBugSeverity()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGSEVERITY$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "bug_severity" element
     */
    public void setBugSeverity(java.lang.String bugSeverity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGSEVERITY$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BUGSEVERITY$0);
            }
            target.setStringValue(bugSeverity);
        }
    }
    
    /**
     * Sets (as xml) the "bug_severity" element
     */
    public void xsetBugSeverity(org.apache.xmlbeans.XmlString bugSeverity)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGSEVERITY$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUGSEVERITY$0);
            }
            target.set(bugSeverity);
        }
    }
}

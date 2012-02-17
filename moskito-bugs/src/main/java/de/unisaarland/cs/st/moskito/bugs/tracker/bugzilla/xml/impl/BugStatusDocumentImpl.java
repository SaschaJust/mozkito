/*
 * An XML document type.
 * Localname: bug_status
 * Namespace: 
 * Java type: BugStatusDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.impl;
/**
 * A document containing one bug_status(@) element.
 *
 * This is a complex type.
 */
public class BugStatusDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements BugStatusDocument
{
    private static final long serialVersionUID = 1L;
    
    public BugStatusDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BUGSTATUS$0 = 
        new javax.xml.namespace.QName("", "bug_status");
    
    
    /**
     * Gets the "bug_status" element
     */
    public java.lang.String getBugStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGSTATUS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "bug_status" element
     */
    public org.apache.xmlbeans.XmlString xgetBugStatus()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGSTATUS$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "bug_status" element
     */
    public void setBugStatus(java.lang.String bugStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BUGSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BUGSTATUS$0);
            }
            target.setStringValue(bugStatus);
        }
    }
    
    /**
     * Sets (as xml) the "bug_status" element
     */
    public void xsetBugStatus(org.apache.xmlbeans.XmlString bugStatus)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BUGSTATUS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BUGSTATUS$0);
            }
            target.set(bugStatus);
        }
    }
}

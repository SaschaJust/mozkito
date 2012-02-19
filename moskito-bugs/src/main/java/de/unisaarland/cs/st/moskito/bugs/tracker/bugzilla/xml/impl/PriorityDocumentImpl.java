/*
 * An XML document type.
 * Localname: priority
 * Namespace: 
 * Java type: PriorityDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.PriorityDocument;

/**
 * A document containing one priority(@) element.
 *
 * This is a complex type.
 */
public class PriorityDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements PriorityDocument
{
    private static final long serialVersionUID = 1L;
    
    public PriorityDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PRIORITY$0 = 
        new javax.xml.namespace.QName("", "priority");
    
    
    /**
     * Gets the "priority" element
     */
    public java.lang.String getPriority()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRIORITY$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "priority" element
     */
    public org.apache.xmlbeans.XmlString xgetPriority()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRIORITY$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "priority" element
     */
    public void setPriority(java.lang.String priority)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PRIORITY$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PRIORITY$0);
            }
            target.setStringValue(priority);
        }
    }
    
    /**
     * Sets (as xml) the "priority" element
     */
    public void xsetPriority(org.apache.xmlbeans.XmlString priority)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PRIORITY$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PRIORITY$0);
            }
            target.set(priority);
        }
    }
}

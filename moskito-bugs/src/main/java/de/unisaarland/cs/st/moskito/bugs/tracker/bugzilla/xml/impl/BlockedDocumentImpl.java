/*
 * An XML document type.
 * Localname: blocked
 * Namespace: 
 * Java type: BlockedDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.BlockedDocument;

/**
 * A document containing one blocked(@) element.
 *
 * This is a complex type.
 */
public class BlockedDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements BlockedDocument
{
    private static final long serialVersionUID = 1L;
    
    public BlockedDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName BLOCKED$0 = 
        new javax.xml.namespace.QName("", "blocked");
    
    
    /**
     * Gets the "blocked" element
     */
    public java.lang.String getBlocked()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BLOCKED$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "blocked" element
     */
    public org.apache.xmlbeans.XmlString xgetBlocked()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BLOCKED$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "blocked" element
     */
    public void setBlocked(java.lang.String blocked)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(BLOCKED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(BLOCKED$0);
            }
            target.setStringValue(blocked);
        }
    }
    
    /**
     * Sets (as xml) the "blocked" element
     */
    public void xsetBlocked(org.apache.xmlbeans.XmlString blocked)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(BLOCKED$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(BLOCKED$0);
            }
            target.set(blocked);
        }
    }
}

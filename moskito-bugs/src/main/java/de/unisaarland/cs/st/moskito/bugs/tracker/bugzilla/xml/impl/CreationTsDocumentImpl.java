/*
 * An XML document type.
 * Localname: creation_ts
 * Namespace: 
 * Java type: CreationTsDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.CreationTsDocument;

/**
 * A document containing one creation_ts(@) element.
 *
 * This is a complex type.
 */
public class CreationTsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements CreationTsDocument
{
    private static final long serialVersionUID = 1L;
    
    public CreationTsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CREATIONTS$0 = 
        new javax.xml.namespace.QName("", "creation_ts");
    
    
    /**
     * Gets the "creation_ts" element
     */
    public java.lang.String getCreationTs()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CREATIONTS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "creation_ts" element
     */
    public org.apache.xmlbeans.XmlString xgetCreationTs()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CREATIONTS$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "creation_ts" element
     */
    public void setCreationTs(java.lang.String creationTs)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CREATIONTS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CREATIONTS$0);
            }
            target.setStringValue(creationTs);
        }
    }
    
    /**
     * Sets (as xml) the "creation_ts" element
     */
    public void xsetCreationTs(org.apache.xmlbeans.XmlString creationTs)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CREATIONTS$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CREATIONTS$0);
            }
            target.set(creationTs);
        }
    }
}

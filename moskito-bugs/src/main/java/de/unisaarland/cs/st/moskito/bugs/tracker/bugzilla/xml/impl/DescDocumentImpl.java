/*
 * An XML document type.
 * Localname: desc
 * Namespace: 
 * Java type: DescDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.DescDocument;

/**
 * A document containing one desc(@) element.
 *
 * This is a complex type.
 */
public class DescDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements DescDocument
{
    private static final long serialVersionUID = 1L;
    
    public DescDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DESC$0 = 
        new javax.xml.namespace.QName("", "desc");
    
    
    /**
     * Gets the "desc" element
     */
    public java.lang.String getDesc()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESC$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "desc" element
     */
    public org.apache.xmlbeans.XmlString xgetDesc()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESC$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "desc" element
     */
    public void setDesc(java.lang.String desc)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DESC$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DESC$0);
            }
            target.setStringValue(desc);
        }
    }
    
    /**
     * Sets (as xml) the "desc" element
     */
    public void xsetDesc(org.apache.xmlbeans.XmlString desc)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DESC$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DESC$0);
            }
            target.set(desc);
        }
    }
}

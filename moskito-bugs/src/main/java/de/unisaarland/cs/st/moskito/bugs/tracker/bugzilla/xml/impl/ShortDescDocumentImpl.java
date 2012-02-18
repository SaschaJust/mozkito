/*
 * An XML document type.
 * Localname: short_desc
 * Namespace: 
 * Java type: ShortDescDocument
 *
 * Automatically generated - do not modify.
 */
package de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.impl;

import de.unisaarland.cs.st.moskito.bugs.tracker.bugzilla.xml.ShortDescDocument;

/**
 * A document containing one short_desc(@) element.
 *
 * This is a complex type.
 */
public class ShortDescDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements ShortDescDocument
{
    private static final long serialVersionUID = 1L;
    
    public ShortDescDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SHORTDESC$0 = 
        new javax.xml.namespace.QName("", "short_desc");
    
    
    /**
     * Gets the "short_desc" element
     */
    public java.lang.String getShortDesc()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SHORTDESC$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "short_desc" element
     */
    public org.apache.xmlbeans.XmlString xgetShortDesc()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHORTDESC$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "short_desc" element
     */
    public void setShortDesc(java.lang.String shortDesc)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SHORTDESC$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SHORTDESC$0);
            }
            target.setStringValue(shortDesc);
        }
    }
    
    /**
     * Sets (as xml) the "short_desc" element
     */
    public void xsetShortDesc(org.apache.xmlbeans.XmlString shortDesc)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SHORTDESC$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SHORTDESC$0);
            }
            target.set(shortDesc);
        }
    }
}
